package com.foreach.across.modules.experimental.modals.support;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.experimental.modals.support.action.ActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ModalLoadAttribute implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>
{
	/**
	 * Selector for the target modal element that should be used.
	 */
	@NonNull
	@JsonProperty
	private String target;

	@NonNull
	@JsonProperty
	private List<ActionAttribute> content = new ArrayList<>();

	@JsonProperty
	private boolean renderAfterContentLoaded = true;

	public ModalLoadAttribute content( ActionAttribute... actions ) {
		this.content.addAll( Arrays.asList( actions ) );
		return this;
	}

	public static ModalLoadAttribute modalLoadAttribute() {
		return new ModalLoadAttribute();
	}

	@Override
	public void applyTo( HtmlViewElement target ) {
		target.setAttribute( "data-modal-load", this );
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement element ) {
		element.set( this );
	}
}
