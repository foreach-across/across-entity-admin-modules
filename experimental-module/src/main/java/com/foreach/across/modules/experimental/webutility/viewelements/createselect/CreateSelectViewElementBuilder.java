package com.foreach.across.modules.experimental.webutility.viewelements.createselect;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.modals.ui.components.ModalViewElementBuilder;
import com.foreach.across.modules.experimental.webutility.icons.WebUtilityModuleIcons;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.support.action.ActionHandlerAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.experimental.modals.support.ModalLoadAttribute.modalLoadAttribute;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.requestActionHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionAttribute.simpleAction;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@RequiredArgsConstructor
public class CreateSelectViewElementBuilder extends ViewElementBuilderSupport
{
	private final ViewElementBuilder selectControlViewElementBuilder;
	private final EntityViewLinks entityViewLinks;

	private Consumer<ButtonViewElementBuilder> createNewButtonCustomizer = ( btn ) -> {
	};
	private BiFunction<EntityViewLinks, EntityPropertyDescriptor, String> urlProvider =
			( links, descriptor ) -> links.linkTo( descriptor.getPropertyType() ).createView().toUriString();

	/**
	 * Supports customization for the create new button. If no customization is provided, a bootstrap primary button
	 * containing an icon is rendered.
	 */
	public CreateSelectViewElementBuilder customizeCreateNewButton( Consumer<ButtonViewElementBuilder> createNewButtonCustomizer ) {
		this.createNewButtonCustomizer = createNewButtonCustomizer;
		return this;
	}

	/**
	 * Supports customizing the url to the create view, from which the {@code .pcs-body-section} element is loaded into the modal.
	 * If no customization is provided, a link will be build to the create view of the entity type of the property.
	 */
	public CreateSelectViewElementBuilder createViewUrl( BiFunction<EntityViewLinks, EntityPropertyDescriptor, String> urlProvider ) {
		this.urlProvider = urlProvider;
		return this;
	}

	@Override
	protected MutableViewElement createElement( ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor entityPropertyDescriptor =
				builderContext.getAttribute( EntityPropertyDescriptor.class.getName(), EntityPropertyDescriptor.class );

		String baseSelector = entityPropertyDescriptor.getName().replace( ".", "-" ) + "-create-select";
		String wrapperId = baseSelector + "-wrapper";
		NodeViewElementBuilder wrappedElement = html.builders
				.div( css( "create-select-wrapper" ) )
				.htmlId( wrapperId )
				.name( wrapperId );

		String controlElementName = baseSelector + "-control";
		NodeViewElementBuilder controlWrapper = html.builders
				.div( css( "create-select-control" ), css.display.flex )
				.htmlId( controlElementName )
				.name( controlElementName );

		String createViewUrl = urlProvider.apply( entityViewLinks, entityPropertyDescriptor );
		String modalName = baseSelector + "-modal";
		ViewElement selectControlViewElement = this.selectControlViewElementBuilder.build( builderContext );
		controlWrapper.add( selectControlViewElement )
		              .add( createNewButton( entityPropertyDescriptor, modalName, createViewUrl, builderContext ) );

		String selectControlName = selectControlViewElement instanceof FormControlElementSupport fces
				? fces.getControlName()
				: selectControlViewElement.getName();
		wrappedElement.add( controlWrapper,
		                    createModal( modalName, controlElementName, selectControlName, createViewUrl, entityPropertyDescriptor, builderContext ) );

		addWebResources( builderContext );
		return wrappedElement.build( builderContext );
	}

	private ViewElementBuilder createNewButton( EntityPropertyDescriptor entityPropertyDescriptor,
	                                            String modalName,
	                                            String createViewUrl, ViewElementBuilderContext builderContext ) {
		ButtonViewElementBuilder createNewBuilder =
				bootstrap.builders.button( css.margin.left.s2 )
				                  .name( "create-select-" + entityPropertyDescriptor.getName() )
				                  .attribute( "aria-label", "Create new" )
				                  .style( Style.PRIMARY )
				                  .text( " " + builderContext
						                  .resolveText( "#{properties." + entityPropertyDescriptor.getName() + ".createSelect.actions.add=Create new}" ) )
				                  .icon( WebUtilityModuleIcons.webUtilityModuleIcons.components.createSelect.addItem() )
				                  .iconLeft()
				                  .iconOnly();

		createNewBuilder.with( data( "toggle", "modal" ), data( "target", modalName ) )
		                .with(
				                modalLoadAttribute()
						                .target( "#" + modalName )
						                .content(
								                requestAction()
										                .url( createViewUrl )
										                .partial( "content" )
										                .requestConfig( Map.of( "headers",
										                                        Map.of( ModalConfigurers.MODAL_ORIGIN_HEADER, modalName ) ) )
										                .success(
												                clearHandler( "#" + modalName + " .modal-title" ),
												                clearHandler( "#" + modalName + " .modal-body" ),
												                responseContentHandler()
														                .source( "." + PageContentStructure.CSS_BODY_SECTION )
														                .target( "#" + modalName + " .modal-body" ),
												                responseContentHandler()
														                .source( ".page-header" )
														                .target( "#" + modalName + " .modal-title" ),
												                removeHandler( "#" + modalName + " .modal-body .em-form-actions" ),
												                initializeFormElements( "#" + modalName + " .modal-body" )
										                )
						                )
		                );

		createNewButtonCustomizer.accept( createNewBuilder );
		return createNewBuilder;
	}

	/**
	 * Create a modal to support creating a new instance and automatically selects the created instance afterwards.
	 */
	protected ModalViewElementBuilder createModal( String modalName,
	                                               String viewElementName, String controlName,
	                                               String urlOfCreateView, EntityPropertyDescriptor descriptor, ViewElementBuilderContext builderContext ) {
		return new ModalViewElementBuilder()
				.name( modalName )
				.centered( true )
				.header( html.builders.container()
				                      .add( html.builders.div( css.modal.title ) )
				                      .add( html.builders.button()
				                                         .attribute( "type", "button" )
				                                         .with( css.close )
				                                         .data( "dismiss", "modal" )
				                                         .attribute( "aria-label", "Close" )
				                                         .add( IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "times" )
				                                                      .set( AcrossBootstrapStyles.css.text.danger )
				                                                      .setAttribute( "aria-hidden", true ) )
				                      )
				)
				.body()
				.footer( html.builders.div( css.of( "em-form-actions" ) )
				                      .add( modalSaveButton( urlOfCreateView, modalName, viewElementName, controlName, descriptor, builderContext ),
				                            modalCancelButton( modalName, descriptor, builderContext ) ) );
	}

	/**
	 * Creates a button that closes the modal identified by {@code modalName}.
	 */
	private ButtonViewElementBuilder modalCancelButton( String modalName, EntityPropertyDescriptor descriptor, ViewElementBuilderContext builderContext ) {
		return bootstrap.builders.button()
		                         .link()
		                         .data( "em-button-role", "cancel" )
		                         .name( "btn-cancel" )
		                         .htmlId( "btn-cancel" )
		                         .text( builderContext.resolveText( "#{properties." + descriptor.getName() + ".createSelect.actions.cancel=Cancel}" ) )
		                         .with( simpleAction().handlers( closeModalHandler( "#" + modalName ) ) );
	}

	/**
	 * Save button to submit the modal identified by {@code modalName}. Submits the current form and rerenders the body in case of validation errors.
	 * If the save was successful, updates the select control with the created value.
	 */
	private ButtonViewElementBuilder modalSaveButton( String urlOfCreateView,
	                                                  String modalName,
	                                                  String viewElementName, String controlName,
	                                                  EntityPropertyDescriptor descriptor,
	                                                  ViewElementBuilderContext messages ) {
		return bootstrap.builders.button()
		                         .data( "em-button-role", "save" )
		                         .name( "btn-save" )
		                         .htmlId( "btn-save" )
		                         .type( ButtonViewElement.Type.BUTTON_SUBMIT )
		                         .style( Style.PRIMARY )
		                         .text( messages.resolveText( "#{properties." + descriptor.getName() + ".createSelect.actions.save=Save}" ) )
		                         .with( RequestActionAttribute.requestAction()
		                                                      .url( urlOfCreateView )
		                                                      .method( HttpMethod.POST )
		                                                      .partial( "::body" )
		                                                      .form( "#" + modalName + " .modal-body form" )
		                                                      .success( new ActionHandlerAttribute[] {
				                                                      clearHandler( "#" + modalName + " .modal-body" ),
				                                                      responseContentHandler().target( "#" + modalName + " .modal-body" ),
				                                                      removeHandler( "#" + modalName + " .modal-body .em-form-actions" ),
				                                                      initializeFormElements( "#" + modalName + " .modal-body" )
		                                                      } )
		                                                      .redirect(
				                                                      responseUrlIdResolver( controlName ),
				                                                      requestActionHandler()
						                                                      .partial( "::" + viewElementName )
						                                                      .target( "#" + viewElementName ),
				                                                      closeModalHandler( "#" + modalName ),
				                                                      initializeFormElements( "#" + viewElementName ) ) );
	}

	/**
	 * Add the resources needed for the modal to open
	 */
	private void addWebResources( ViewElementBuilderContext builderContext ) {
		WebResourceRegistry webResourceRegistry = builderContext.getAttribute( WebResourceRegistry.class.getName(), WebResourceRegistry.class );
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );
		webResourceRegistry.apply(
				WebResourceRule.addPackage( WebUtilityModuleWebResources.NAME ),
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/modal-loader.js" ) )
				               .withKey( "modal-loader-js" )
				               .after( WebUtilityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
	}
}