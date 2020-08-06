package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

/**
 * Configures the referenced {@link ViewElement} to open a given modal and load its content on click.
 */
public class ModalCreateButtonListViewProcessor extends AbstractModalViewProcessor<ModalCreateButtonListViewProcessor>
{
	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		EntityViewLinkBuilder linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
		ContainerViewElementUtils.find( container, "btn-create", ButtonViewElement.class )
		                         .ifPresent(
				                         btn -> {
					                         configureViewElement( btn, linkBuilder, builderContext );
					                         container.addChild( createModal().build( builderContext ) );
				                         }
		                         );
	}
}
