package com.gagror.data.wh40kskirmish.gangs;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public class FighterViewOutput extends FighterReferenceOutput {

	@Getter
	private final int movement;

	@Getter
	private final int weaponSkill;

	@Getter
	private final int ballisticSkill;

	@Getter
	private final int strength;

	@Getter
	private final int toughness;

	@Getter
	private final int wounds;

	@Getter
	private final int initiative;

	@Getter
	private final int attacks;

	@Getter
	private final int leadership;

	protected FighterViewOutput(
			final FighterEntity entity,
			final int movement,
			final int weaponSkill,
			final int ballisticSkill,
			final int strength,
			final int toughness,
			final int wounds,
			final int initiative,
			final int attacks,
			final int leadership) {
		super(entity);
		this.movement = movement;
		this.weaponSkill = weaponSkill;
		this.ballisticSkill = ballisticSkill;
		this.strength = strength;
		this.toughness = toughness;
		this.wounds = wounds;
		this.initiative = initiative;
		this.attacks = attacks;
		this.leadership = leadership;
	}

	@RequiredArgsConstructor
	public static class Builder {
		final FighterEntity entity;

		@Getter
		@Setter
		private int movement;

		@Getter
		@Setter
		private int weaponSkill;

		@Getter
		@Setter
		private int ballisticSkill;

		@Getter
		@Setter
		private int strength;

		@Getter
		@Setter
		private int toughness;

		@Getter
		@Setter
		private int wounds;

		@Getter
		@Setter
		private int initiative;

		@Getter
		@Setter
		private int attacks;

		@Getter
		@Setter
		private int leadership;

		public FighterViewOutput build() {
			return new FighterViewOutput(
					entity,
					movement,
					weaponSkill,
					ballisticSkill,
					strength,
					toughness,
					wounds,
					initiative,
					attacks,
					leadership);
		}
	}
}
