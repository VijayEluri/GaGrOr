package com.gagror.service.social;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.thymeleaf.util.StringUtils;

import com.gagror.data.AbstractEntity;
import com.gagror.data.Identifiable;
import com.gagror.data.account.AccountEditInput;
import com.gagror.data.account.AccountEntity;
import com.gagror.data.account.AccountRepository;
import com.gagror.service.AbstractIdentifiablePersister;
import com.gagror.service.accesscontrol.AccessControlService;

@Service
@CommonsLog
public class EditAccountPersister extends AbstractIdentifiablePersister<AccountEditInput, AccountEntity, AbstractEntity> {

	@Autowired
	AccountRepository accountRepository;

	@Autowired
	AccessControlService accessControlService;

	@Override
	protected void validateForm(final AccountEditInput form, final BindingResult bindingResult) {
		final boolean editingOwnAccount = isEditingOwnAccount(form);
		if(! StringUtils.isEmptyOrWhitespace(form.getPassword())
				|| ! StringUtils.isEmptyOrWhitespace(form.getPasswordRepeat())) {
			// Only validate password input if there was any input
			if(! form.getPassword().equals(form.getPasswordRepeat())) {
				log.warn(String.format("Attempt to edit account %d failed, password repeat didn't match", form.getId()));
				form.addErrorPasswordMismatch(bindingResult);
			}
			if(accessControlService.isPasswordTooWeak(form.getPassword())) {
				log.warn(String.format("Attempt to edit account %d failed, password was too weak", form.getId()));
				form.addErrorPasswordTooWeak(bindingResult);
			}
		}
		if(! editingOwnAccount
				&& ! accessControlService.getRequestAccountEntity().getAccountType().getMayEdit().contains(form.getAccountType())) {
			log.warn(String.format("Attempt to edit account %d failed, attempted to set disallowed account type %s", form.getId(), form.getAccountType()));
			form.addErrorDisallowedAccountType(bindingResult);
		}
	}

	@Override
	protected boolean isCreateNew(final AccountEditInput form) {
		return false;
	}

	@Override
	protected AccountEntity loadExisting(final AccountEditInput form, final AbstractEntity context) {
		return accountRepository.load(form.getId());
	}

	@Override
	protected void validateFormVsExistingState(final AccountEditInput form, final BindingResult bindingResult, final AccountEntity entity) {
		final boolean editingOwnAccount = isEditingOwnAccount(form);
		if(editingOwnAccount
				&& ! entity.getName().equals(form.getName())
				&& accountRepository.exists(form.getName())) {
			log.warn(String.format("Attempt to edit account %d failed, username was busy", form.getId()));
			form.addErrorUsernameBusy(bindingResult);
		}
	}

	@Override
	protected void updateValues(final AccountEditInput form, final AccountEntity entity) {
		final boolean editingOwnAccount = isEditingOwnAccount(entity);
		if(editingOwnAccount) {
			/*
			 * Editing username for other accounts is forbidden, since it would be very confusing.
			 * That user would be thrown out and unable to log in again.
			 */
			entity.setName(form.getName());
		}
		if(! StringUtils.isEmptyOrWhitespace(form.getPassword())) {
			entity.setPassword(accessControlService.encodePassword(form.getPassword()));
		}
		if(! editingOwnAccount) {
			// Cannot edit account type or flags of one's own account
			entity.setAccountType(form.getAccountType());
			entity.setActive(form.isActive());
			entity.setLocked(form.isLocked());
		}
	}

	@Override
	protected void postPersistenceUpdate(final AccountEntity entity) {
		if(isEditingOwnAccount(entity)) {
			accessControlService.logInAs(entity);
		}
	}

	private boolean isEditingOwnAccount(final Identifiable<Long> editingAccount) {
		return accessControlService.getRequestAccountEntity().hasId(editingAccount.getId());
	}
}
