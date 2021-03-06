package com.gagror.service.wh40kskirmish.rules;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.gagror.data.DataNotFoundException;
import com.gagror.data.wh40kskirmish.rules.items.ItemCategoryEntity;
import com.gagror.data.wh40kskirmish.rules.items.ItemCategoryRepository;
import com.gagror.data.wh40kskirmish.rules.items.ItemTypeEntity;
import com.gagror.data.wh40kskirmish.rules.items.ItemTypeInput;
import com.gagror.data.wh40kskirmish.rules.items.ItemTypeRepository;
import com.gagror.service.AbstractIdentifiablePersister;

@Service
public class ItemTypePersister
extends AbstractIdentifiablePersister<ItemTypeInput, ItemTypeEntity, ItemCategoryEntity> {

	@Autowired
	ItemCategoryRepository itemCategoryRepository;

	@Autowired
	ItemTypeRepository itemTypeRepository;

	@Override
	protected void validateForm(final ItemTypeInput form, final BindingResult bindingResult) {
		// Nothing to do that isn't verified by annotations already
	}

	@Override
	protected ItemCategoryEntity loadContext(final ItemTypeInput form) {
		return itemCategoryRepository.load(form.getGroupId(), form.getItemCategoryId());
	}

	@Override
	protected void validateFormVsContext(
			final ItemTypeInput form,
			final BindingResult bindingResult,
			final ItemCategoryEntity context) {
		for(final ItemCategoryEntity itemCategory : context.getRules().getItemCategories()) {
			for(final ItemTypeEntity itemType : itemCategory.getItemTypes()) {
				if(itemType.getName().equals(form.getName())
						&& ! itemType.hasId(form.getId())) {
					form.addErrorNameMustBeUniqueWithinGroup(bindingResult);
				}
			}
		}
	}

	@Override
	protected ItemTypeEntity loadExisting(final ItemTypeInput form, final ItemCategoryEntity context) {
		for(final ItemTypeEntity itemType : context.getItemTypes()) {
			if(itemType.hasId(form.getId())) {
				return itemType;
			}
		}
		throw new DataNotFoundException(String.format("Item type %d (item category %d, group %d)", form.getId(), form.getItemCategoryId(), form.getGroupId()));
	}

	@Override
	protected ItemTypeEntity createNew(final ItemTypeInput form, final ItemCategoryEntity context) {
		return new ItemTypeEntity(context);
	}

	@Override
	protected void updateValues(final ItemTypeInput form, final ItemTypeEntity entity) {
		entity.setName(form.getName());
	}

	@Override
	protected ItemTypeEntity makePersistent(final ItemTypeEntity entity) {
		return itemTypeRepository.persist(entity);
	}
}
