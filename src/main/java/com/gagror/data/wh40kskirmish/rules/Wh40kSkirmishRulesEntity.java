package com.gagror.data.wh40kskirmish.rules;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.gagror.data.AbstractEditableEntity;
import com.gagror.data.group.GroupEntity;
import com.gagror.data.wh40kskirmish.rules.gangs.Wh40kSkirmishGangTypeEntity;
import com.gagror.data.wh40kskirmish.rules.items.Wh40kSkirmishItemCategoryEntity;
import com.gagror.data.wh40kskirmish.rules.skills.Wh40kSkirmishSkillCategoryEntity;
import com.gagror.data.wh40kskirmish.rules.territory.Wh40kSkirmishTerritoryCategoryEntity;

@NoArgsConstructor
@ToString(of={}, callSuper=true)
@Entity
@Table(name="wh40ksk_rules")
public class Wh40kSkirmishRulesEntity extends AbstractEditableEntity {

	@OneToOne(optional=false)
	@Getter
	GroupEntity group;

	@OneToMany(mappedBy="rules", fetch=FetchType.LAZY)
	@Getter
	private Set<Wh40kSkirmishGangTypeEntity> gangTypes;

	@OneToMany(mappedBy="rules", fetch=FetchType.LAZY)
	@Getter
	private Set<Wh40kSkirmishTerritoryCategoryEntity> territoryCategories;

	@OneToMany(mappedBy="rules", fetch=FetchType.LAZY)
	@Getter
	private Set<Wh40kSkirmishSkillCategoryEntity> skillCategories;

	@OneToMany(mappedBy="rules", fetch=FetchType.LAZY)
	@Getter
	private Set<Wh40kSkirmishItemCategoryEntity> itemCategories;

	// TODO Make initial money per gang configurable

	// TODO Make name of currency in game configurable

	public Wh40kSkirmishRulesEntity(final GroupEntity group) {
		this.group = group;
	}
}