package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.bootstrapui.attributes.BootstrapDataAttributes;
import com.foreach.across.modules.bootstrapui.elements.ButtonViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.support.AttributeWitherFunction;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.thymeleaf.util.StringUtils;

import java.util.Optional;

@Accessors(fluent = true, chain = true)
public class ModalItemActionViewProcessor extends AbstractModalViewProcessor<ModalItemActionViewProcessor>
{
	@NonNull
	@Setter
	private String actionRole;

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		SortableTableBuilder sortableTableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );
		EntityViewLinkBuilder linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();

		if ( sortableTableBuilder != null ) {
			sortableTableBuilder.valueRowProcessor( ( ctx, element ) -> {
				Optional<TableViewElement.Cell> actions = element.find( EntityListActionsProcessor.CELL_NAME, TableViewElement.Cell.class );
				if ( actions.isPresent() ) {
					TableViewElement.Cell rowActions = actions.get();
					rowActions.findAll( ButtonViewElement.class, this::shouldConfigureRowAction )
					          .forEach( btnViewElement -> configureViewElement( btnViewElement, linkBuilder, ctx ) );

				}
			} );
			containerBuilder.add( createModal() );
		}
	}

	protected boolean shouldConfigureRowAction( ButtonViewElement ve ) {
		AttributeWitherFunction<String> actionSelector = BootstrapDataAttributes.data.of( "em-button-role" );
		HtmlViewElement.class.isAssignableFrom( ve.getClass() );
		return actionSelector.test( ve ) && StringUtils.equals( actionRole, actionSelector.getValueFrom( ve ) );
	}
}
