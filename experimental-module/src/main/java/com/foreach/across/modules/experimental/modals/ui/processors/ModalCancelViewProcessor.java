package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionAttribute.simpleAction;
import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionHandlerAttribute.closeModalHandler;

public class ModalCancelViewProcessor extends ModalFormViewProcessor<ModalCancelViewProcessor>
{
	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		element.set( simpleAction()
				             .handlers(
						             closeModalHandler()
								             .target( getModalSelector() )
				             ) );
	}
}
