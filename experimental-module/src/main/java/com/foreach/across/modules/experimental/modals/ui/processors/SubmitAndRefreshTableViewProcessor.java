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

@Accessors(fluent = true, chain = true)
public class SubmitAndRefreshTableViewProcessor extends ModalFormViewProcessor<SubmitAndRefreshTableViewProcessor>
{
	@Setter
	@NonNull
	private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> url;

	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		element.set( requestAction()
				             .url( url.apply( linkBuilder, builderContext ) )
				             .method( HttpMethod.POST )
				             .partial( "::entityForm" )
				             .form( getModalSelector() + " .modal-body form" )
				             .success(
						             clearHandler( getModalSelector() + " .modal-body" ),
						             requestContentHandler()
								             .target( getModalSelector() + " .modal-body" ),
						             removeHandler( getModalSelector() + " .modal-body .em-form-actions" ),
						             initializeFormElements( getModalSelector() + ".modal-body" )
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
