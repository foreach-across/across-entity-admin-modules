package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.http.HttpMethod;

import java.util.function.BiFunction;

import static com.foreach.across.modules.experimental.modals.support.action.RequestActionAttribute.requestAction;
import static com.foreach.across.modules.experimental.modals.support.action.RequestActionHandlerAttribute.requestActionHandler;
import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionHandlerAttribute.*;

/**
 * Configures the referenced {@link ViewElement} to submit a given modal on click.
 * The customization is only applied if a {@link com.foreach.across.modules.experimental.modals.support.ModalConfigurers#MODAL_ORIGIN_HEADER} header
 * is present that references the id of the modal.
 * </p>
 * If a validation error occurs after submission, the body of the modal will be refreshed to show the updated state.
 * If the form within the modal is succesfully submitted, the modal will close and refresh the table on the current view.
 */
@Accessors(fluent = true, chain = true)
public class ModalSubmitAndRefreshTableViewProcessor extends ModalFormViewProcessor<ModalSubmitAndRefreshTableViewProcessor>
{
	@Setter
	@NonNull
	private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url;

	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		element.set( requestAction()
				             .url( url.apply( linkBuilder, builderContext ) )
				             .method( HttpMethod.POST )
				             .partial( "::body" )
				             .form( getModalSelector() + " .modal-body form" )
				             .success(
						             clearHandler( getModalSelector() + " .modal-body" ),
						             responseContentHandler()
								             .target( getModalSelector() + " .modal-body" ),
						             removeHandler( getModalSelector() + " .modal-body .em-form-actions" ),
						             initializeFormElements( getModalSelector() + " .modal-body" )
				             )
				             .redirect(
						             requestActionHandler()
								             .partial( "::itemsTable" )
								             .target( ".em-sortableTable-panel" ),
						             closeModalHandler( getModalSelector() ),
						             initializeFormElements( ".em-sortableTable-panel" )
				             ) );
	}
}
