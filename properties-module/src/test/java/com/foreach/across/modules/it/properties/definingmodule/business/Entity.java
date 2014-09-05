package com.foreach.across.modules.it.properties.definingmodule.business;

/**
 * @author Arne Vandamme
 */
public class Entity
{
	private long id;

	public Entity( long id ) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId( long id ) {
		this.id = id;
	}
}
