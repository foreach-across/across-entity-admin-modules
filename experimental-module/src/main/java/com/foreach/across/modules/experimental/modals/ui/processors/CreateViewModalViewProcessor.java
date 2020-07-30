package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.modals.support.action.PartialActionHandlerAttribute;
import com.foreach.across.modules.experimental.modals.support.action.RequestActionAttribute;
import com.foreach.across.modules.experimental.modals.support.action.RequestActionHandlerAttribute;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import org.springframework.http.HttpMethod;

public class CreateViewModalViewProcessor extends EntityViewProcessorAdapter
{
	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityViewLinkBuilder linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
		ContainerViewElementUtils.find( container, "btn-save", ButtonViewElement.class )
		                         .ifPresent(
				                         btn -> {
					                         String modalSelector = "#" + CreateModalViewProcessor.CREATE_MODAL_ID;
					                         btn.set(
							                         new RequestActionAttribute()
									                         .url( linkBuilder.createView().toUriString() )
									                         .method( HttpMethod.POST )
									                         .form( modalSelector + " .modal-body form" )
									                         .redirect(
											                         new PartialActionHandlerAttribute()
													                         .partial( "::itemsTable" )
													                         .target( ".em-sortableTable-panel" ),
											                         new RequestActionHandlerAttribute()
													                         .type( RequestActionHandlerAttribute.Type.CLOSE )
													                         .target( modalSelector )
									                         )

					                         );
				                         }
		                         );
	}
}
