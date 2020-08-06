package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.modals.ui.components.ModalViewElementBuilder;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Map;
import java.util.function.BiFunction;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.experimental.modals.support.ModalLoadAttribute.modalLoadAttribute;
import static com.foreach.across.modules.experimental.modals.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionHandlerAttribute.*;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Registers the necessary webresources for modal and ajax-based loading support, and defines a few utility methods to configure the loading of a modal.
 *
 * @param <T> inherited type
 */
public abstract class AbstractModalViewProcessor<T extends AbstractModalViewProcessor> extends EntityViewProcessorAdapter
{
	@NonNull
	@Getter
	private String modalId;

	@NonNull
	@Setter
	private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url;

	@Setter
	private String partial = null;

	public T modalId( String modalId ) {
		this.modalId = modalId;
		return (T) this;
	}

	public T url( BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url ) {
		this.url = url;
		return (T) this;
	}

	public T partial( String partial ) {
		this.partial = partial;
		return (T) this;
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/experimental-module.js" ) )
				               .withKey( "experimental-module" )
				               .after( EntityModuleWebResources.NAME )
				               .before( "modal-loader-js" )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/action-loader.js" ) )
				               .withKey( "request-executor" )
				               .after( EntityModuleWebResources.NAME )
				               .before( "modal-loader-js" )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/modal-loader.js" ) )
				               .withKey( "modal-loader-js" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
	}

	protected void configureViewElement( ViewElement viewElement,
	                                     EntityViewLinkBuilder linkViewBuilder,
	                                     ViewElementBuilderContext builderContext ) {
		viewElement.set( data( "toggle", "modal" ), data( "target", modalSelector() ) )
		           .set(
				           modalLoadAttribute()
						           .target( modalSelector() )
						           .content(
								           requestAction()
										           .url( url.apply( linkViewBuilder, builderContext ) )
										           .partial( partial )
										           .requestConfig( Map.of( "headers", Map.of( ModalConfigurers.MODAL_ORIGIN_HEADER, modalId ) ) )
										           .success(
												           clearHandler( modalTarget( ".modal-title" ) ),
												           clearHandler( modalTarget( ".modal-footer" ) ),
												           clearHandler( modalTarget( ".modal-body" ) ),
												           responseContentHandler()
														           .source( "." + PageContentStructure.CSS_BODY_SECTION )
														           .target( modalTarget( ".modal-body" ) ),
												           responseContentHandler()
														           .source( ".page-header" )
														           .target( modalTarget( ".modal-title" ) ),
												           moveHandler()
														           .source( modalTarget( ".modal-body .em-form-actions" ) )
														           .target( modalTarget( ".modal-footer" ) ),
												           initializeFormElements( modalSelector() )
										           )
						           )
		           );
	}

	protected ModalViewElementBuilder createModal() {
		return new ModalViewElementBuilder()
				.name( modalId )
				.centered( true )
				.header( modalHeader() )
				.body()
				.footer();
	}

	protected ContainerViewElementBuilder modalHeader() {
		return html.builders.container()
		                    .add( html.builders.div( css.modal.title ) )
		                    .add( html.builders.button()
		                                       .attribute( "type", "button" )
		                                       .with( css.close )
		                                       .data( "dismiss", "modal" )
		                                       .attribute( "aria-label", "Close" )
		                                       .add( IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
		                                                    .set( AcrossBootstrapStyles.css.text.danger )
		                                                    .setAttribute( "aria-hidden", true ) )
		                    );
	}

	protected String modalTarget( String target ) {
		return modalSelector() + " " + target;
	}

	protected String modalSelector() {
		return "#" + getModalId();
	}
}
