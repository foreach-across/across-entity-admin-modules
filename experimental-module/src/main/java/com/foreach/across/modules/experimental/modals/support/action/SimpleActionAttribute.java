package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

public class SimpleActionAttribute extends ActionAttribute<SimpleActionAttribute>
{
	public static final String ACTION = "exm:simple";

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> handlers = new LinkedList<>();

	public SimpleActionAttribute handlers( Collection<ActionHandlerAttribute> handlers ) {
		this.handlers = new LinkedList<>( handlers );
		return this;
	}

	public SimpleActionAttribute handlers( ActionHandlerAttribute... handlers ) {
		this.handlers.addAll( Arrays.asList( handlers ) );
		return this;
	}

	public SimpleActionAttribute() {
		action( ACTION );
		event( "click" );
	}

	public static SimpleActionAttribute simpleAction() {
		return new SimpleActionAttribute();
	}
}
