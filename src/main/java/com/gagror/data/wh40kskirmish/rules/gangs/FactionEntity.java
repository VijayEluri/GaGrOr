package com.gagror.data.wh40kskirmish.rules.gangs;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.gagror.data.AbstractEditableNamedEntity;
import com.gagror.data.group.GroupEntity;
import com.gagror.data.group.GroupOwned;
import com.gagror.data.wh40kskirmish.gangs.GangEntity;

@NoArgsConstructor
@ToString(of={}, callSuper=true)
@Entity
@Table(name="wh40ksk_faction")
public class FactionEntity extends AbstractEditableNamedEntity implements GroupOwned {

	@ManyToOne(optional=false)
	@JoinColumn(nullable=false, insertable=true, updatable=false)
	@Getter
	private GangTypeEntity gangType;

	@OneToMany(mappedBy="faction", fetch=FetchType.LAZY)
	@Getter
	private Set<GangEntity> gangs;

	public FactionEntity(final GangTypeEntity gangType) {
		this.gangType = gangType;
		// Add the new entity to the referencing collection
		gangType.getFactions().add(this);
	}

	@Override
	public GroupEntity getGroup() {
		return getGangType().getGroup();
	}
}
