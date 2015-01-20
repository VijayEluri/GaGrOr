package com.gagror.data.wh40kskirmish.rules.territory;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.gagror.data.AbstractEditableNamedEntity;

@NoArgsConstructor
@ToString(of={}, callSuper=true)
@Entity
@Table(name="wh40ksk_territorytype")
public class Wh40kSkirmishTerritoryTypeEntity extends AbstractEditableNamedEntity {

	@ManyToOne(optional=false)
	@JoinColumn(nullable=false, insertable=true, updatable=false)
	@Getter
	private Wh40kSkirmishTerritoryCategoryEntity territoryCategory;

	public Wh40kSkirmishTerritoryTypeEntity(final Wh40kSkirmishTerritoryCategoryEntity territoryCategory) {
		this.territoryCategory = territoryCategory;
		// Add the new entity to the referencing collection
		territoryCategory.getTerritoryTypes().add(this);
	}
}