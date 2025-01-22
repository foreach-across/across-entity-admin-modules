package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

public class EditableValueViewActionsViewProcessor extends EntityViewProcessorAdapter
{
	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();
		ContainerViewElementUtils.remove( container, "btn-save" );
		ContainerViewElementUtils.remove( container, "btn-update" );
		ContainerViewElementUtils.find( container, "btn-cancel", ButtonViewElement.class )
		                         .ifPresent(
				                         btn -> btn.setName( "btn-back" )
				                                   .setText( messages.messageWithFallback( "actions.back" ) )
		                         );

	}
}
