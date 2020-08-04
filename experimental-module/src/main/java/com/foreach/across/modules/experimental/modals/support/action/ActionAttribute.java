package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Defines one or more steps which should be executed when the specified DOM Event is triggered on the corresponding element.
 * After the execution of a step, the result can be handled by one or more {@link ActionHandlerAttribute}s.
 *
 * @param <T> inheriting type
 */
@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ActionAttribute<T extends ActionAttribute> implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>
{
	/**
	 * Name of the action handler.
	 */
	@NonNull
	@JsonProperty
	private String action;

	/**
	 * DOM Event on which this action should be triggered. If no event is specified, the configuration will be applied to the {@link ViewElement},
	 * but no logic will be registered.
	 */
	@JsonProperty
	private String event;

	public T action( String action ) {
		this.action = action;
		return (T) this;
	}

	public T event( String event ) {
		this.event = event;
		return (T) this;
	}

	@Override
	public void applyTo( HtmlViewElement target ) {
		target.setAttribute( "data-action-load", this );
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement element ) {
		element.set( this );
	}
}
