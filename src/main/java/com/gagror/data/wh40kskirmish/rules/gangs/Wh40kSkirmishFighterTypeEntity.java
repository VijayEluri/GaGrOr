package com.gagror.data.wh40kskirmish.rules.gangs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import com.gagror.data.AbstractEditableNamedEntity;

@NoArgsConstructor
@ToString(of={}, callSuper=true)
@Entity
@Table(name="wh40ksk_fightertype")
public class Wh40kSkirmishFighterTypeEntity extends AbstractEditableNamedEntity {

	@ManyToOne(optional=false)
	@JoinColumn(nullable=false, insertable=true, updatable=false)
	@Getter
	private Wh40kSkirmishRaceEntity race;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingMovement;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingWeaponSkill;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingBallisticSkill;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingStrength;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingToughness;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingWounds;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingInitiative;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingAttacks;

	@Column(nullable = false, insertable = true, updatable = true)
	@Getter
	@Setter
	private int startingLeadership;

	public Wh40kSkirmishFighterTypeEntity(final Wh40kSkirmishRaceEntity race) {
		this.race = race;;
		// Add the new entity to the referencing collection
		race.getFighterTypes().add(this);
	}
}
