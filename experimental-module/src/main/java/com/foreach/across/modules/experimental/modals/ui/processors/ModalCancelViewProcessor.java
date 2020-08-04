package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionAttribute.simpleAction;
import static com.foreach.across.modules.experimental.modals.support.action.SimpleActionHandlerAttribute.closeModalHandler;

/**
 * Configures the referenced {@link ViewElement} to close a given modal on click.
 * The customization is only applied if a {@link com.foreach.across.modules.experimental.modals.support.ModalConfigurers#MODAL_ORIGIN_HEADER} header
 * is present that references the id of the modal. The modal corresponding to the id will be closed.
 */
public class ModalCancelViewProcessor extends ModalFormViewProcessor<ModalCancelViewProcessor>
{
	@Override
	protected void configureViewElement( ViewElement element, EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext ) {
		element.set( simpleAction().handlers( closeModalHandler( getModalSelector() ) ) );
	}
}
