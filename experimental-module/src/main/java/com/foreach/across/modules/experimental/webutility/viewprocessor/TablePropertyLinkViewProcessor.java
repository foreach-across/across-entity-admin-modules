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
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

import java.util.function.Function;

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
	private boolean showIcon = true;
	private Function<EntityViewRequest, TableLinker.LinkDestination> linkDestination;
	private Function<Object, String> targetLink;

	public TablePropertyLinkViewProcessor( EntityViewLinks entityViewLinks ) {
		this.tableLinker = new TableLinker( entityViewLinks );
	}

	public TablePropertyLinkViewProcessor property( String property ) {
		this.property = property;
		return this;
	}

	public TablePropertyLinkViewProcessor showIcon( boolean showIcon ) {
		this.showIcon = showIcon;
		return this;
	}

	/**
	 * Set the {@link TableLinker.LinkDestination} that the generator uses to build the link.
	 * Note that this will be ignored when you provided a {@link #targetLink}
	 */
	public TablePropertyLinkViewProcessor resolveLinkDestination( Function<EntityViewRequest, TableLinker.LinkDestination> linkDestination ) {
		this.linkDestination = linkDestination;
		return this;
	}

	/**
	 * Provide a function that will be called for every row in the table to determine the link to go to when clicking on the property.
	 * Provides the rowEntity as parameter that can be used to build the targetLink for each row.
	 * <p>
	 * When no targetLink is specified, a sensible default will be used to determine the best possible target link
	 */
	public TablePropertyLinkViewProcessor targetLink( Function<Object, String> targetLink ) {
		this.targetLink = targetLink;
		return this;
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		SortableTableBuilder sortableTableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );

		if ( sortableTableBuilder != null ) {
			if ( targetLink == null ) {
				targetLink = tableLinker.buildTargetLink( entityViewRequest, linkDestination );
			}

			tableLinker.createLinkOnProperty( sortableTableBuilder, property, targetLink );

			if ( !showIcon ) {
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

				sortableTableBuilder.valueRowProcessor( ( ctx, row ) ->
						                                        ContainerViewElementUtils
								                                        .find( row, EntityListActionsProcessor.CELL_NAME, TableViewElement.Cell.class )
								                                        .ifPresent( actions ->
										                                                    actions.getChildren().stream()
										                                                           .filter( c -> "view".equals( c.get( HtmlViewElement.Functions
												                                                                                               .attribute(
														                                                                                               "data-em-button-role" ) ) ) )
										                                                           .findFirst().ifPresent( actions::removeChild )
								                                        ) );
			}
		}
	}

}