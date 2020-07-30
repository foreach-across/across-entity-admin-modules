package com.foreach.across.modules.experimental.modals.support.old;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.modals.support.action.PartialActionHandlerAttribute;
import com.foreach.across.modules.web.resource.WebResourceUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.function.Consumer;

/**
 * Enable automatic refreshing of multiple partial snippet on an element.
 *
 * @see PartialActionHandlerAttribute
 */
@Setter
@Accessors(chain = true, fluent = true)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class MultiPartialLoadAttribute implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>, Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder>, ContentLoadAttribute
{
	/**
	 * Type of the {@link ContentLoadAttribute}. Used to differentiate with how the data should be reloaded.
	 */
	@NonNull
	@JsonProperty
	@Setter(value = AccessLevel.NONE)
	private String type = "partial:multi";

	/**
	 * Collection of {@link PartialActionHandlerAttribute}s that should be reloaded when a change occurs on this element.
	 */
	@NonNull
	@Getter
	@JsonProperty
	private List<PartialActionHandlerAttribute> partials;

	/**
	 * Apply the partial refresh attribute to a html node.
	 */
	@Override
	public void applyTo( HtmlViewElement node ) {
		partials.stream()
		        .filter( partial -> partial.url() != null )
		        .forEach( partial -> {
			        String url = partial.url();
			        partial.url( WebResourceUtils.currentLinkBuilder().map( webAppLinkBuilder -> webAppLinkBuilder.buildLink( url ) ).orElse( url ) );
		        } );

		// set the attribute to this object, which will be serialized as json
		node.setAttribute( "data-multi-partial-load", this );

	}

	/**
	 * Postprocess a regular html node and enable the partial refresh.
	 */
	@Override
	public void postProcess( ViewElementBuilderContext builderContext, HtmlViewElement node ) {
		node.set( this );
	}

	/**
	 * Configures the partial refresh on the CONTROL element mode of the property.
	 */
	@Override
	public void accept( EntityPropertyRegistryBuilder.PropertyDescriptorBuilder property ) {
		property.viewElementPostProcessor( ViewElementMode.CONTROL, this );
	}
}
