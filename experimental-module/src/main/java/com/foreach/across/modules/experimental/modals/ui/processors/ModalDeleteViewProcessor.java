package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.webutility.support.action.ActionAttribute;
import com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.AccessLevel;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.Map;
import java.util.function.Function;

import static com.foreach.across.modules.entity.views.util.EntityViewElementUtils.currentEntity;
import static com.foreach.across.modules.experimental.modals.support.ModalConfigurers.MODAL_ORIGIN_HEADER;
import static com.foreach.across.modules.experimental.webutility.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.webutility.support.action.ResponseContentHandlerAttribute.responseContentHandler;
import static com.foreach.across.modules.experimental.webutility.support.action.SimpleActionHandlerAttribute.*;

public class ModalDeleteViewProcessor extends ModalFormViewProcessor<ModalDeleteViewProcessor>
{
	@Setter(AccessLevel.PROTECTED)
	private Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> actionCustomizer = ModalActionCustomizationContext::action;

	/**
	 * Supports customizing the default action attribute that is registered to fetch the modal content.
	 * Offers more advanced configuration as well as overriding of previously configured methods.
	 * </p>
	 * The customizer should return the final action that should be used. Overrides applied through the customizer always take precedence.
	 */
	public ModalDeleteViewProcessor action( Function<ModalActionCustomizationContext<RequestActionAttribute>, ActionAttribute> actionCustomizer ) {
		this.actionCustomizer = actionCustomizer;
		return this;
	}

	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		String url = linkBuilder.forInstance( currentEntity( builderContext ) ).deleteView().toUriString();
		RequestActionAttribute action = requestAction()
				.url( url )
				.method( HttpMethod.GET )
				.partial( "content" )
				.requestConfig( Map.of( "headers", Map.of( MODAL_ORIGIN_HEADER, getModalSelector() ) ) )
				.success(
						clearHandler( getModalSelector() + " .modal-title" ),
						clearHandler( getModalSelector() + " .modal-footer" ),
						clearHandler( getModalSelector() + " .modal-body" ),
						responseContentHandler()
								.source( "." + PageContentStructure.CSS_BODY_SECTION )
								.target( getModalSelector() + " .modal-body" ),
						responseContentHandler()
								.source( ".page-header" )
								.target( getModalSelector() + " .modal-title" ),
						moveHandler()
								.source( getModalSelector() + " .modal-body .em-form-actions" )
								.target( getModalSelector() + " .modal-footer" ),
						initializeFormElements( getModalSelector() )
				);
		ModalActionCustomizationContext<RequestActionAttribute> ctx =
				new ModalActionCustomizationContext<>( getModalSelector(), ( x ) -> getModalSelector() + " " + x, action, builderContext );
		element.set( actionCustomizer.apply( ctx ) );
	}
}
