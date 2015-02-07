package com.gagror.service.wh40kskirmish.rules;

import lombok.extern.apachecommons.CommonsLog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.gagror.data.DataNotFoundException;
import com.gagror.data.group.GroupEntity;
import com.gagror.data.group.GroupRepository;
import com.gagror.data.group.WrongGroupTypeException;
import com.gagror.data.wh40kskirmish.rules.Wh40kSkirmishRulesEntity;
import com.gagror.data.wh40kskirmish.rules.items.ItemCategoryEntity;
import com.gagror.data.wh40kskirmish.rules.items.ItemCategoryInput;
import com.gagror.data.wh40kskirmish.rules.items.ItemCategoryRepository;
import com.gagror.service.AbstractPersister;

@Service
@CommonsLog
public class ItemCategoryPersister
extends AbstractPersister<ItemCategoryInput, ItemCategoryEntity, Wh40kSkirmishRulesEntity> {

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	ItemCategoryRepository itemCategoryRepository;

	@Override
	protected void validateForm(final ItemCategoryInput form, final BindingResult bindingResult) {
		// Nothing to do that isn't verified by annotations already
	}

	@Override
	protected Wh40kSkirmishRulesEntity loadContext(final ItemCategoryInput form) {
		final GroupEntity group = groupRepository.load(form.getGroupId());
		if(null == group.getWh40kSkirmishRules()) {
			throw new WrongGroupTypeException(group);
		}
		return group.getWh40kSkirmishRules();
	}

	@Override
	protected void validateFormVsContext(
			final ItemCategoryInput form,
			final BindingResult bindingResult,
			final Wh40kSkirmishRulesEntity context) {
		for(final ItemCategoryEntity itemCategory : context.getItemCategories()) {
			if(itemCategory.getName().equals(form.getName())
					&& ! itemCategory.hasId(form.getId())) {
				form.addErrorNameMustBeUniqueWithinGroup(bindingResult);
			}
		}
	}

	@Override
	protected boolean isCreateNew(final ItemCategoryInput form) {
		return !form.isPersistent();
	}

	@Override
	protected ItemCategoryEntity loadExisting(final ItemCategoryInput form, final Wh40kSkirmishRulesEntity context) {
		for(final ItemCategoryEntity itemCategory : context.getItemCategories()) {
			if(itemCategory.hasId(form.getId())) {
				return itemCategory;
			}
		}
		throw new DataNotFoundException(String.format("Item category %d (group %d)", form.getId(), form.getGroupId()));
	}

	@Override
	protected void validateFormVsExistingState(
			final ItemCategoryInput form,
			final BindingResult bindingResult,
			final ItemCategoryEntity entity) {
		if(! form.getVersion().equals(entity.getVersion())) {
			log.warn(String.format("Attempt to edit item category %d failed, simultaneous edit detected", form.getId()));
			form.addErrorSimultaneuosEdit(bindingResult);
		}
	}

	@Override
	protected ItemCategoryEntity createNew(final ItemCategoryInput form, final Wh40kSkirmishRulesEntity context) {
		return new ItemCategoryEntity(context);
	}

	@Override
	protected void updateValues(final ItemCategoryInput form, final ItemCategoryEntity entity) {
		entity.setName(form.getName());
	}

	@Override
	protected ItemCategoryEntity makePersistent(final ItemCategoryEntity entity) {
		return itemCategoryRepository.persist(entity);
	}
}
