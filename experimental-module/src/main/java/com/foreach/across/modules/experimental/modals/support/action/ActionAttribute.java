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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class ActionAttribute<T extends ActionAttribute> implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>
{
	@NonNull
	@JsonProperty
	private String action;

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> success = new LinkedList<>();

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> redirect = new LinkedList<>();

	@JsonProperty
	private LinkedList<ActionHandlerAttribute> error = new LinkedList<>();

	public T action( String action ) {
		this.action = action;
		return (T) this;
	}

	public T success( Collection<ActionHandlerAttribute> success ) {
		this.success = new LinkedList<>( success );
		return (T) this;
	}

	public T success( ActionHandlerAttribute... success ) {
		this.success.addAll( Arrays.asList( success ) );
		return (T) this;
	}

	public T redirect( Collection<ActionHandlerAttribute> redirect ) {
		this.redirect = new LinkedList<>( redirect );
		return (T) this;
	}

	public T redirect( ActionHandlerAttribute... redirect ) {
		this.redirect.addAll( Arrays.asList( redirect ) );
		return (T) this;
	}

	public T error( Collection<ActionHandlerAttribute> error ) {
		this.error = new LinkedList<>( error );
		return (T) this;
	}

	public T error( ActionHandlerAttribute... error ) {
		this.error.addAll( Arrays.asList( error ) );
		return (T) this;
	}

	@Override
	public void applyTo( HtmlViewElement target ) {
		target.setAttribute( "data-action-executor", this );
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement element ) {
		element.set( this );
	}
}
