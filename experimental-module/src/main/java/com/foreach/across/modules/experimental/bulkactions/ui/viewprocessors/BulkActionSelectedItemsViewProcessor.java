package com.foreach.across.modules.experimental.bulkactions.ui.viewprocessors;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Renders a Span element that displays the total amount of currently selected items with the bulk actions.
 * The content of the HtmlElement is resolved in the frontend.
 * <p>
 * You can provide your own elements by providing a html element with the data attribute `bulk-action-total-selected-text`.
 *
 * @param <T>
 */
@Getter
@Setter
@Accessors(fluent = true)
public class BulkActionSelectedItemsViewProcessor<T> extends EntityViewProcessorAdapter
{
	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		ContainerViewElementUtils.find( container, "itemsTable-panel-heading", NodeViewElement.class ).ifPresent( panelHeading -> {
			NodeViewElement headingSelectionInformation = html.builders
					.div( css( "small" ) )
					.name( "bulk-action-total-items-selected" )
					.add(
							html.builders
									.span( data( "bulk-action-total-selected-text",
									             builderContext.getMessage( "bulkActionTotalSelected", "You have % items selected." ) ) )
					)
					.add(
							bootstrap.builders
									.link( data( "bulk-action-total-selected-clear", "" ) )
									.text( builderContext.getMessage( "bulkActionClearSelection", "Clear selection" ) )
					)
					.build( builderContext );
			panelHeading.addChild( headingSelectionInformation
			);
		} );
	}
}
