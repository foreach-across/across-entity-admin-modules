package com.foreach.across.modules.experimental.webutility.viewprocessor;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.experimental.webutility.support.TableLinker;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

/**
 * Use the property as a link to the update / detail view.
 * <p>
 * Example usage that turns the property `siteName` in a link to the update view:
 *
 * <pre>
 *      .viewProcessor( vp -> vp.createBean( TablePropertyLinkViewProcessor.class )
 *           .configure( processor -> processor.property( "siteName" ).showEditIcon( false ) ) )
 * </pre>
 * </p>
 */
public class TablePropertyLinkViewProcessor extends EntityViewProcessorAdapter
{

	private final TableLinker tableLinker;
	private String property;
	private boolean showEditIcon = true;

	public TablePropertyLinkViewProcessor( EntityViewLinks entityViewLinks ) {
		this.tableLinker = new TableLinker( entityViewLinks );
	}

	public TablePropertyLinkViewProcessor property( String property ) {
		this.property = property;
		return this;
	}

	public TablePropertyLinkViewProcessor showEditIcon( boolean showIcon ) {
		this.showEditIcon = showIcon;
		return this;
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		SortableTableBuilder sortableTableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );

		if ( sortableTableBuilder != null ) {
			tableLinker.createLinkOnProperty( sortableTableBuilder, property, entityViewRequest.getEntityViewContext().getAllowableActions().contains(
					AllowableAction.UPDATE ) ? TableLinker.LinkDestination.UPDATE : TableLinker.LinkDestination.DETAIL );

			if ( !showEditIcon ) {
				sortableTableBuilder.valueRowProcessor( ( ctx, row ) ->
						                                        ContainerViewElementUtils
								                                        .find( row, EntityListActionsProcessor.CELL_NAME, TableViewElement.Cell.class )
								                                        .ifPresent( actions ->
										                                                    actions.getChildren().stream()
										                                                           .filter( c -> "edit".equals( c.get( HtmlViewElement.Functions
												                                                                                               .attribute(
														                                                                                               "data-em-button-role" ) ) ) )
										                                                           .findFirst().ifPresent( actions::removeChild )
								                                        ) );
			}
		}
	}
}