package com.gagror.data.wh40kskirmish.rules.skills;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.gagror.data.AbstractIdentifiableNamedInput;
import com.gagror.data.group.GroupIdentifiable;

@NoArgsConstructor
public class SkillInput
extends AbstractIdentifiableNamedInput<SkillOutput>
implements GroupIdentifiable {

	@Getter
	@Setter
	private Long groupId;

	@Getter
	@Setter
	private Long skillCategoryId;

	public SkillInput(final SkillOutput currentState) {
		super(currentState);
		setGroupId(currentState.getSkillCategory().getGroup().getId());
		setSkillCategoryId(currentState.getSkillCategory().getId());
	}

	public SkillInput(final Long groupId, final Long skillCategoryId) {
		setGroupId(groupId);
		setSkillCategoryId(skillCategoryId);
	}
}
