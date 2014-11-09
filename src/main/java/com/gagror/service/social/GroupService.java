package com.gagror.service.social;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import com.gagror.data.account.AccountEntity;
import com.gagror.data.account.AccountReferenceOutput;
import com.gagror.data.account.AccountRepository;
import com.gagror.data.account.ContactEntity;
import com.gagror.data.account.ContactRepository;
import com.gagror.data.account.ContactType;
import com.gagror.data.group.GroupEntity;
import com.gagror.data.group.GroupInviteInput;
import com.gagror.data.group.GroupListOutput;
import com.gagror.data.group.GroupMemberEntity;
import com.gagror.data.group.GroupMemberRepository;
import com.gagror.data.group.GroupReferenceOutput;
import com.gagror.data.group.GroupRepository;
import com.gagror.data.group.GroupViewMembersOutput;
import com.gagror.data.group.MemberType;
import com.gagror.service.accesscontrol.AccessControlService;

@Service
@Transactional
@CommonsLog
public class GroupService {

	@Autowired
	AccessControlService accessControlService;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	GroupMemberRepository groupMemberRepository;

	@Autowired
	ContactRepository contactRepository;

	public List<GroupListOutput> loadGroupList() {
		log.debug("Loading group list");
		final List<GroupListOutput> output = new ArrayList<>();
		for(final GroupMemberEntity member : accessControlService.getRequestAccountEntity().getGroupMemberships()) {
			if(member.getMemberType().isMember()) {
				output.add(new GroupListOutput(member));
			}
		}
		Collections.sort(output);
		return output;
	}

	public List<GroupListOutput> loadInvitationsList() {
		log.debug("Loading invitations list");
		final List<GroupListOutput> output = new ArrayList<>();
		for(final GroupMemberEntity member : accessControlService.getRequestAccountEntity().getGroupMemberships()) {
			if(member.getMemberType().isInvitation()) {
				output.add(new GroupListOutput(member));
			}
		}
		Collections.sort(output);
		return output;
	}

	public GroupReferenceOutput viewGroup(final Long groupId) {
		final GroupEntity group = loadGroup(groupId);
		log.debug(String.format("Loaded group %s for viewing", group));
		final AccountEntity currentUser = accessControlService.getRequestAccountEntity();
		for(final GroupMemberEntity membership : group.getGroupMemberships()) {
			if(membership.getAccount().equals(currentUser)) {
				return new GroupReferenceOutput(membership);
			}
		}
		return new GroupReferenceOutput(group);
	}

	public GroupViewMembersOutput viewGroupMembers(final Long groupId) {
		final GroupEntity group = loadGroup(groupId);
		log.debug(String.format("Loaded group %s for viewing", group));
		final AccountEntity currentUser = accessControlService.getRequestAccountEntity();
		for(final GroupMemberEntity membership : group.getGroupMemberships()) {
			if(membership.getAccount().equals(currentUser)) {
				return new GroupViewMembersOutput(membership);
			}
		}
		throw new IllegalArgumentException(String.format("Could not load members for group %d, request account is not a member", groupId));
	}

	private GroupEntity loadGroup(final Long groupId) {
		final GroupEntity group = groupRepository.findOne(groupId);
		if(null == group) {
			throw new IllegalArgumentException(String.format("Failed to load group %d", groupId));
		}
		return group;
	}

	public List<AccountReferenceOutput> loadPossibleUsersToInvite(final Long groupId) {
		final GroupEntity group = loadGroup(groupId);
		// Find the group of users who are already invited or members
		final Set<AccountEntity> groupMemberAccounts = findGroupMemberAccounts(group, false);
		// Find the possible users
		final List<AccountReferenceOutput> output = new ArrayList<>();
		for(final ContactEntity contact : accessControlService.getRequestAccountEntity().getContacts()) {
			// Filter out group members, and contacts who have not been accepted
			if(! groupMemberAccounts.contains(contact.getContact()) && contact.getContactType().isContact()) {
				output.add(new AccountReferenceOutput(contact.getContact()));
			}
		}
		Collections.sort(output);
		return output;
	}

	private Set<AccountEntity> findGroupMemberAccounts(final GroupEntity group, final boolean onlyFullMembers) {
		final Set<AccountEntity> groupMemberAccounts = new HashSet<>();
		for(final GroupMemberEntity membership : group.getGroupMemberships()) {
			if(! onlyFullMembers || membership.getMemberType().isMember()) {
				groupMemberAccounts.add(membership.getAccount());
			}
		}
		return groupMemberAccounts;
	}

	public void sendInvitations(final GroupInviteInput groupInviteForm, final BindingResult bindingResult) {
		final GroupEntity group = loadGroup(groupInviteForm.getId());
		final Set<AccountEntity> groupMemberAccounts = findGroupMemberAccounts(group, false);
		final Set<AccountEntity> contacts = new HashSet<>();
		for(final ContactEntity contact : accessControlService.getRequestAccountEntity().getContacts()) {
			if(contact.getContactType().isContact()) {
				contacts.add(contact.getContact());
			}
		}
		for(final Long invited : groupInviteForm.getSelected()) {
			final AccountEntity account = accountRepository.findById(invited);
			if(null == account) {
				throw new IllegalArgumentException(String.format("Failed to load invited account %d", invited));
			}
			if(! contacts.contains(account)) {
				throw new IllegalArgumentException(String.format("Invited account %d is not a contact, cannot invite", invited));
			}
			if(! groupMemberAccounts.contains(account)) {
				groupMemberRepository.save(new GroupMemberEntity(
						group,
						account,
						MemberType.INVITED));
			}
		}
	}

	public void accept(final Long memberId) {
		final GroupMemberEntity invitation = findInvitation(memberId);
		if(null != invitation) {
			invitation.setMemberType(MemberType.MEMBER);
			// Add group members as contacts
			// TODO Gather functionality to add and mirror contacts in a single place (perhaps create a separate contact service?)
			final AccountEntity requestAccount = accessControlService.getRequestAccountEntity();
			for(final AccountEntity groupMember : findGroupMemberAccounts(invitation.getGroup(), true)) {
				if(! requestAccount.equals(groupMember)) {
					boolean foundContact = false;
					for(final ContactEntity contact : requestAccount.getContacts()) {
						if(contact.getContact().equals(groupMember)) {
							foundContact = true;
							if(contact.getContactType().isRequest()) {
								// Auto-accept and mirror the requested contact
								log.debug(String.format("Auto-accepting and mirroring requested contact: %s", contact));
								contact.setContactType(ContactType.AUTOMATIC);
								contactRepository.save(new ContactEntity(groupMember, ContactType.AUTOMATIC, requestAccount));
							}
						}
					}
					for(final ContactEntity incoming : requestAccount.getIncomingContacts()) {
						if(incoming.getOwner().equals(groupMember) && incoming.getContactType().isRequest()) {
							foundContact = true;
							// Auto-accept and mirror the incoming contact request
							log.debug(String.format("Auto-accepting and mirroring incoming contact request: %s", incoming));
							incoming.setContactType(ContactType.AUTOMATIC);
							contactRepository.save(new ContactEntity(requestAccount, ContactType.AUTOMATIC, groupMember));
						}
					}
					if(! foundContact) {
						// Create and mirror the contact with the non-contact user
						log.debug(String.format("Creating and mirroring contact for %s and %s", requestAccount, groupMember));
						contactRepository.save(new ContactEntity(requestAccount, ContactType.AUTOMATIC, groupMember));
						contactRepository.save(new ContactEntity(groupMember, ContactType.AUTOMATIC, requestAccount));
					}
				}
			}
		}
		// If invitation does no longer exist, just silently ignore it
	}

	public void decline(final Long memberId) {
		final GroupMemberEntity invitation = findInvitation(memberId);
		if(null != invitation) {
			invitation.getAccount().getGroupMemberships().remove(invitation);
			invitation.getGroup().getGroupMemberships().remove(invitation);
			groupMemberRepository.delete(invitation);
		}
		// If invitation does no longer exist, just silently ignore it
	}

	private GroupMemberEntity findInvitation(final Long memberId) {
		for(final GroupMemberEntity membership : accessControlService.getRequestAccountEntity().getGroupMemberships()) {
			if(memberId.equals(membership.getId()) && membership.getMemberType().isInvitation()) {
				return membership;
			}
		}
		return null;
	}

	public void leave(final Long groupId) {
		for(final GroupMemberEntity membership : accessControlService.getRequestAccountEntity().getGroupMemberships()) {
			if(groupId.equals(membership.getGroup().getId())) {
				// If the request account is the only owner, we cannot leave the group
				if(membership.getMemberType().isOwner()) {
					int countOwners = 0;
					for(final GroupMemberEntity member : membership.getGroup().getGroupMemberships()) {
						if(member.getMemberType().isOwner()) {
							countOwners++;
						}
					}
					if(1 == countOwners) {
						throw new IllegalArgumentException(String.format("Only owner, cannot leave group %s", membership.getGroup()));
					}
				}
				// Remove the membership
				deleteMembership(membership);
				return;
			}
		}
	}

	private void deleteMembership(final GroupMemberEntity membership) {
		membership.getGroup().getGroupMemberships().remove(membership);
		membership.getAccount().getGroupMemberships().remove(membership);
		groupMemberRepository.delete(membership);
	}

	public void promote(final Long groupId, final Long accountId) {
		final GroupMemberEntity member = findAnotherGroupMemberFromMemberships(groupId, accountId);
		if(null == member || ! member.getMemberType().isMember()) {
			throw new IllegalArgumentException(String.format("Cannot promote, account %d is not a member of group %d", accountId, groupId));
		}
		member.setMemberType(MemberType.OWNER);
	}

	public void demote(final Long groupId, final Long accountId) {
		final GroupMemberEntity member = findAnotherGroupMemberFromMemberships(groupId, accountId);
		if(null == member || ! member.getMemberType().isMember()) {
			throw new IllegalArgumentException(String.format("Cannot demote, account %d is not a member of group %d", accountId, groupId));
		}
		member.setMemberType(MemberType.MEMBER);
	}

	public void remove(final Long groupId, final Long accountId) {
		final GroupMemberEntity member = findAnotherGroupMemberFromMemberships(groupId, accountId);
		if(null == member) {
			// Silently ignore that the account could not be found in the group, that's the end result we want anyway
			return;
		}
		deleteMembership(member);
	}

	private GroupMemberEntity findAnotherGroupMemberFromMemberships(final Long groupId, final Long accountId) {
		final AccountEntity requestAccount = accessControlService.getRequestAccountEntity();
		if(accountId.equals(requestAccount.getId())) {
			throw new IllegalArgumentException("This action can only be performed on other accounts");
		}
		GroupEntity group = null;
		for(final GroupMemberEntity membership : requestAccount.getGroupMemberships()) {
			if(groupId.equals(membership.getGroup().getId())) {
				group = membership.getGroup();
				break;
			}
		}
		if(null == group) {
			throw new IllegalArgumentException(String.format("Request account is not a member of group %d", groupId));
		}
		for(final GroupMemberEntity membership : group.getGroupMemberships()) {
			if(accountId.equals(membership.getAccount().getId())) {
				return membership;
			}
		}
		// Member not found
		return null;
	}
}