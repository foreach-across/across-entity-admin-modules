package com.foreach.across.modules.experimental.webutility.viewelements.createselect;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.styles.AcrossBootstrapStyles;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.modals.ui.components.ModalViewElementBuilder;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.support.action.ActionHandlerAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.google.common.collect.ImmutableMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.experimental.modals.support.ModalLoadAttribute.modalLoadAttribute;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionHandlerAttribute.UPDATE_ID_VALUE;
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
	private final ViewElementBuilder originalViewElementBuilder;

	@Override
	protected MutableViewElement createElement( ViewElementBuilderContext builderContext ) {
		NodeViewElementBuilder wrappedElement = html.builders
				.div( css( "create-select-wrapper" ) )
				.name( "create-select-wrapper" );

		ViewElement originalElement = originalViewElementBuilder.build( builderContext );
		originalElement.set( css( "original-element" ) );
		wrappedElement.add( originalElement );

		createPlusButtonWithModal( builderContext, wrappedElement, originalElement );

		addWebResources( builderContext );

		return wrappedElement.build( builderContext );
	}

	/**
	 * Add a plus button besides the original element. When clicking on the plus button a modal opens to add a new
	 * element to the select list.
	 */
	private void createPlusButtonWithModal( ViewElementBuilderContext builderContext, NodeViewElementBuilder wrappedElement, ViewElement element ) {
		EntityPropertyDescriptor entityPropertyDescriptor = builderContext.getAttribute( EntityPropertyDescriptor.class.getName(),
		                                                                                 EntityPropertyDescriptor.class );
		EntityViewRequest entityViewRequest = builderContext.getAttribute( "entityViewRequest", EntityViewRequest.class );
		EntityViewLinkBuilder entityViewLinkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
		String urlOfCreateView = entityViewLinkBuilder.root().linkTo( entityPropertyDescriptor.getPropertyType() ).createView().toUriString();

		ButtonViewElement plusButton = bootstrap.builders.button()
		                                                 .name( "create-select-" + entityPropertyDescriptor.getName() )
		                                                 .css( "ml-2" )
		                                                 .attribute( "type", "button" )
		                                                 .attribute( "aria-label", "Add" )
		                                                 .style( Style.PRIMARY )
		                                                 .add( IconSet.iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "plus" ) )
		                                                 .build( builderContext );
		String modelName = "create-modal-" + entityPropertyDescriptor.getName();

		addAttributesToOpenModal( plusButton, modelName, urlOfCreateView );

		String viewElementName = element.getName();
		String propertyControlName = viewElementName;

		if ( element instanceof FormGroupElement ) {
			FormGroupElement formGroupElement = (FormGroupElement) element;

			ViewElement control = ( (FormGroupElement) element ).getControl();
			if ( control instanceof FormControlElementSupport ) {
				propertyControlName = ( (FormControlElementSupport) control ).getControlName();
			}
			else {
				propertyControlName = control.getName();
			}

			formGroupElement.setControl( html.div( css( "create-select-inner-wrapper d-flex" ) ).addChild( control )
			                                 .addChild( plusButton ) );
		}
		else {
			wrappedElement.add( plusButton );
		}

		wrappedElement.add( createModal( modelName, viewElementName, propertyControlName, urlOfCreateView, builderContext ) );
	}

	/**
	 * Add all the necessary attributes to the plusButton so it opens a modal to the create form of the entity
	 * the button is for.
	 */
	protected void addAttributesToOpenModal( ViewElement plusButton,
	                                         String modelName,
	                                         String urlOfCreateView ) {
		plusButton.set( data( "toggle", "modal" ), data( "target", modelName ) )
		          .set(
				          modalLoadAttribute()
						          .target( "#" + modelName )
						          .content(
								          requestAction()
										          .url( urlOfCreateView )
										          .partial( "content" )
										          .requestConfig( ImmutableMap.of( "headers",
										                                           ImmutableMap.of( ModalConfigurers.MODAL_ORIGIN_HEADER, modelName ) ) )
										          .success(
												          clearHandler( "#" + modelName + " .modal-title" ),
												          clearHandler( "#" + modelName + " .modal-body" ),
												          responseContentHandler()
														          .source( "." + PageContentStructure.CSS_BODY_SECTION )
														          .target( "#" + modelName + " .modal-body" ),
												          responseContentHandler()
														          .source( ".page-header" )
														          .target( "#" + modelName + " .modal-title" ),
												          removeHandler( "#" + modelName + " .modal-body .em-form-actions" ),
												          initializeFormElements( "#" + modelName + " .modal-body" )
										          )
						          )
		          );
	}

	/**
	 * Create a modal to support creating a new instance and automatically selects the created instance afterwards.
	 */
	protected ModalViewElementBuilder createModal( String modalName,
	                                               String viewElementName, String controlName,
	                                               String urlOfCreateView, ViewElementBuilderContext builderContext ) {
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
				                      .add( buildModalSaveButton( urlOfCreateView, modalName, viewElementName, controlName, builderContext ),
				                            buildModalCancelButton( modalName, builderContext ) ) );
	}

	/**
	 * Creates a button that closes the modal identified by {@code modalName}.
	 */
	private ButtonViewElementBuilder buildModalCancelButton( String modalName, ViewElementBuilderContext builderContext ) {
		EntityViewRequest entityViewRequest = builderContext.getAttribute( "entityViewRequest", EntityViewRequest.class );
		EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();

		return bootstrap.builders.button()
		                         .link()
		                         .data( "em-button-role", "cancel" )
		                         .name( "btn-cancel" )
		                         .htmlId( "btn-cancel" )
		                         .text( messages.messageWithFallback( "actions.cancel" ) )
		                         .with( simpleAction().handlers( closeModalHandler( "#" + modalName ) ) );
	}

	/**
	 * Save button to submit the modal identified by {@code modalName}. Submits the current form and rerenders the body in case of validation errors.
	 * If the save was successful, updates the select control with the created value.
	 */
	private ButtonViewElementBuilder buildModalSaveButton( String urlOfCreateView,
	                                                       String modalName,
	                                                       String viewElementName, String controlName,
	                                                       ViewElementBuilderContext builderContext ) {
		EntityViewRequest entityViewRequest = builderContext.getAttribute( "entityViewRequest", EntityViewRequest.class );
		EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();

		return bootstrap.builders.button()
		                         .data( "em-button-role", "save" )
		                         .name( "btn-save" )
		                         .htmlId( "btn-save" )
		                         .type( ButtonViewElement.Type.BUTTON_SUBMIT )
		                         .style( Style.PRIMARY )
		                         .text( messages.messageWithFallback( "actions.save" ) )
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
				                                                      requestActionHandler()
						                                                      .additionalQueryParameter( controlName, UPDATE_ID_VALUE )
						                                                      .partial( "::" + viewElementName )
						                                                      .target( ".original-element" ),
				                                                      closeModalHandler( "#" + modalName ),
				                                                      // todo
				                                                      initializeFormElements( ".original-element" ) ) );
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