package com.gagror.data.wh40kskirmish.rules.items;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.gagror.data.AbstractIdentifiableNamedInput;

@NoArgsConstructor
public class ItemTypeInput extends AbstractIdentifiableNamedInput<Long, ItemTypeOutput> {

	@Getter
	@Setter
	private Long groupId;

	@Getter
	@Setter
	private Long itemCategoryId;

	public ItemTypeInput(final ItemTypeOutput currentState) {
		super(currentState);
		setGroupId(currentState.getItemCategory().getGroup().getId());
		setItemCategoryId(currentState.getItemCategory().getId());
	}

	public ItemTypeInput(final Long groupId, final Long itemCategoryId) {
		setGroupId(groupId);
		setItemCategoryId(itemCategoryId);
	}
}