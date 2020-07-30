package com.foreach.across.modules.experimental.modals.support.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

@Getter
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataRequestAttribute implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>
{
	@JsonProperty
	private String type = "partial:single";

	@JsonProperty
	@NonNull
	private String url;
	/**
	 * HTTP method that should be used for the AJAX request.
	 * Defaults to {@link HttpMethod#GET}.
	 * <strong>Note that any client-side form method will be ignored as this property takes precedence.</strong>
	 */
	@JsonProperty
	private HttpMethod method = HttpMethod.GET;

	@Override
	public void applyTo( HtmlViewElement target ) {
		// set the attribute to this object, which will be serialized as json
		target.setAttribute( "data-request", this );
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement element ) {
		element.set( this );
	}
}
