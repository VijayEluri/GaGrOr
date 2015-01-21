package com.gagror.data.wh40kskirmish.rules.gangs;

import lombok.Getter;

public class Wh40kSkirmishFighterTypeOutput extends Wh40kSkirmishFighterTypeReferenceOutput {

	@Getter
	private final Wh40kSkirmishRaceOutput race;

	@Getter
	private final int startingMovement;

	@Getter
	private final int startingWeaponSkill;

	@Getter
	private final int startingBallisticSkill;

	@Getter
	private final int startingStrength;

	@Getter
	private final int startingToughness;

	@Getter
	private final int startingWounds;

	@Getter
	private final int startingInitiative;

	@Getter
	private final int startingAttacks;

	@Getter
	private final int startingLeadership;

	public Wh40kSkirmishFighterTypeOutput(
			final Wh40kSkirmishFighterTypeEntity entity,
			final Wh40kSkirmishRaceOutput race) {
		super(entity);
		this.race = race;
		startingMovement = entity.getStartingMovement();
		startingWeaponSkill = entity.getStartingWeaponSkill();
		startingBallisticSkill = entity.getStartingBallisticSkill();
		startingStrength = entity.getStartingStrength();
		startingToughness = entity.getStartingToughness();
		startingWounds = entity.getStartingWounds();
		startingInitiative = entity.getStartingInitiative();
		startingAttacks = entity.getStartingAttacks();
		startingLeadership = entity.getStartingLeadership();
	}
}
