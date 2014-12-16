package com.gagror.data;

import java.util.Date;

import lombok.Getter;

public abstract class AbstractEntityOutput
extends AbstractIdentifiable {

	@Getter
	private final Long id;

	@Getter
	private final Date created;

	// TODO Make subclasses extend AbstractEditableEntityOutput and AbstractEditableNamedEntityOutput

	protected AbstractEntityOutput(final AbstractEntity entity) {
		id = entity.getId();
		created = entity.getCreated();
	}

	@Override
	public String toString() {
		return String.format("id=%d", getId());
	}
}
