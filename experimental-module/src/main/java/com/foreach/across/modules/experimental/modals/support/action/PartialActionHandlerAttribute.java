package com.foreach.across.modules.experimental.modals.support.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.web.resource.WebResourceUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.function.Consumer;

/**
 * Enable automatic partial snippet refreshing on an element.
 * <p/>
 * This will add a {@code data-partial-load} html attribute to the html node, which will trigger
 * a client-side (AJAX) request based on a control value change. Works by default with all elements
 * that publish a {@code bootstrapui.change} event (which is all default bootstrap form controls).
 * <p/>
 * Which request is executed and where the resulting output is sent to depends on the value of the different
 * properties. Read the javadoc below for more details and see also the {@code partial-loader.js} javascript
 * extension for the client-side code.
 * <p/>
 * This configurer can either be used with {@link HtmlViewElement#set(ViewElement.WitherSetter[])},
 * as a {@link ViewElementPostProcessor} or for automatic {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}
 * configuration to activate partial refresh for the {@link ViewElementMode#CONTROL}.
 * <p/>
 * Example usage:
 * <pre>{@code
 *  .properties( props -> props.property( "productFeatureType" )
 *                             .viewElementPostProcessor( ViewElementMode.FILTER_CONTROL,
 * 				                                          refreshPartial( "itemsTable" )
 * 						                                           .target( ".em-sortableTable-panel" )
 * 						                                           .form( "form" )
 * 						                                           .url(  entityViewLinks.linkTo(ProductFeatureValue.class ) ) )
 * )
 * }</pre>
 * <p/>
 * At minimum a partial name is required. If it is the only thing specified (neither {@link #form(String)} nor {@link #url(String)} is set,
 * the form closest to the control will be fetched and its {@code action} will be used as the target url (but using the {@link #method(HttpMethod)}
 * that was configured. If both form and url are set, only the form data of the form will be serialized and sent to the target url.
 * <p/>
 * If no {@link #target(String)} is specified, a target element with attribute {@code data-partial-target=NAME_OF_THE_PARTIAL} will be updated.
 * If it also does not exist, the original control will be replaced instead. As soon as a target is specified, it will be used instead.
 */
@Setter
@Accessors(chain = true, fluent = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@SuppressWarnings("unused")
public class PartialActionHandlerAttribute extends ActionHandlerAttribute<PartialActionHandlerAttribute> implements ViewElement.WitherSetter<HtmlViewElement>, ViewElementPostProcessor<HtmlViewElement>, Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder>
{

	public PartialActionHandlerAttribute() {
		type( "exm:partial" );
	}

	/**
	 * Name of the view element that should be rendered as partial.
	 * Only the output of this view element will be then be set on the {@link #target(String)} element.
	 */
	@NonNull
	@JsonProperty
	private String partial;

	/**
	 * CSS3 selector to the form whose data should be sent to the {@link #url(String)}.
	 * If no url property is configured, the form action will also be used as url.
	 */
	@JsonProperty
	private String form;

	/**
	 * HTTP method that should be used for the AJAX request.
	 * Defaults to {@link HttpMethod#GET}.
	 * <strong>Note that any client-side form method will be ignored as this property takes precedence.</strong>
	 */
	@JsonProperty
	private HttpMethod method = HttpMethod.GET;

	/**
	 * URL to which the partial request should go. If not set, the url from the {@link #form(String)} will be used
	 * (or from the closest form if {@link #form(String)} was also not specified.
	 */
	@Getter
	@JsonProperty
	private String url;

	/**
	 * CSS3 selector of the element that should be replaced by the partial output.
	 * If the target element has an attribute {@code data-partial-target}, only the content of the target will be
	 * replaced. Else the target element in its entirety will be replaced by the partial output.
	 */
	@JsonProperty
	private String target;

	/**
	 * Apply the partial refresh attribute to a html node.
	 */
	@Override
	public void applyTo( HtmlViewElement node ) {
		if ( url != null ) {
			// todo: this is a workaround: support path prefixes in the configured url but replace them once
			//  a possible improvement might be to support either a supplier or to always replace them but then set a Map as attribute
			//  or create a clone (instead of updating the original fixed value)
			url = WebResourceUtils.currentLinkBuilder().map( webAppLinkBuilder -> webAppLinkBuilder.buildLink( url ) ).orElse( url );
		}

		// set the attribute to this object, which will be serialized as json
		node.setAttribute( "data-partial-load", this );

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
