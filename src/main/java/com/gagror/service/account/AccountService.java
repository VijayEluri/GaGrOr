package com.gagror.service.account;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.thymeleaf.util.StringUtils;

import com.gagror.data.account.AccountEditInput;
import com.gagror.data.account.AccountEditOutput;
import com.gagror.data.account.AccountEntity;
import com.gagror.data.account.AccountReferenceOutput;
import com.gagror.data.account.AccountRepository;
import com.gagror.service.accesscontrol.AccessControlService;

@Service
@Transactional
@CommonsLog
public class AccountService {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccessControlService accessControlService;

	public AccountEditOutput loadAccountForEditing(final Long accountId) {
		final AccountEntity entity = accountRepository.findById(accountId);
		if(null != entity) {
			log.debug(String.format("Loaded account ID %d (%s) for editing", accountId, entity.getUsername()));
			return new AccountEditOutput(entity);
		} else {
			log.warn(String.format("Failed to load account ID %d for editing", accountId));
			return null;
		}
	}

	public void saveAccount(final AccountEditInput editAccountForm, final BindingResult bindingResult) {
		// Find the account to update
		final AccountEntity entity = accountRepository.findById(editAccountForm.getId());
		if(null == entity) {
			throw new IllegalStateException(String.format("Failed to find edited account ID %d when saving", editAccountForm.getId()));
		}
		final boolean editingOwnAccount = entity.getId().equals(accessControlService.getRequestAccountEntity().getId());
		// Validate input before updating the entity
		if((! StringUtils.isEmptyOrWhitespace(editAccountForm.getPassword())
				|| ! StringUtils.isEmptyOrWhitespace(editAccountForm.getPasswordRepeat()))
				&& ! editAccountForm.getPassword().equals(editAccountForm.getPasswordRepeat())) {
			editAccountForm.addErrorPasswordMismatch(bindingResult);
		}
		if(editingOwnAccount
				&& ! entity.getUsername().equals(editAccountForm.getUsername())
				&& null != accountRepository.findByUsername(editAccountForm.getUsername())) {
			editAccountForm.addErrorUsernameBusy(bindingResult);
		}
		if(! editAccountForm.getVersion().equals(entity.getVersion())) {
			editAccountForm.addErrorSimultaneuosEdit(bindingResult);
		}
		// Stop if errors have been detected
		if(bindingResult.hasErrors()) {
			return;
		}
		// Everything is OK, update the entity
		if(editingOwnAccount) {
			entity.setUsername(editAccountForm.getUsername());
		}
		entity.setPassword(accessControlService.encodePassword(editAccountForm.getPassword()));
		// TODO Support editing account type (but not for yourself?)
		// If the currently logged in user was edited, make sure that the user is still logged in
		if(editingOwnAccount) {
			accessControlService.logInAs(entity);
		}
	}

	public List<AccountReferenceOutput> loadContacts() {
		log.debug("Loading contacts");
		// TODO Add the concept of each user's address book (i.e. don't list every account)
		final Iterable<AccountEntity> entities = accountRepository.findAll(new Sort("username"));
		final List<AccountReferenceOutput> output = new ArrayList<>();
		for(final AccountEntity entity : entities) {
			output.add(new AccountReferenceOutput(entity));
		}
		return output;
	}
}
