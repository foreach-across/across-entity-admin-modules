package com.foreach.across.modules.experimental.webutility.support.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * {@link ActionAttribute} that is used to perform a series of handlers
 */
@Getter
public class SimpleActionAttribute<SELF extends SimpleActionAttribute<SELF>> extends ActionAttribute<SELF>
{
	public static final String ACTION = "exm:simple";

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> handlers = new LinkedList<>();

	public SELF handlers( Collection<ActionHandlerAttribute> handlers ) {
		this.handlers = new LinkedList<>( handlers );
		return self();
	}

	public SELF handlers( ActionHandlerAttribute... handlers ) {
		this.handlers.addAll( Arrays.asList( handlers ) );
		return self();
	}

	protected SimpleActionAttribute() {
		action( ACTION );
		event( "click" );
	}

	@SuppressWarnings("unchecked")
	public static <T extends SimpleActionAttribute<T>> SimpleActionAttribute<T> simpleAction() {
		return (T) new SimpleActionAttribute();
	}
}
