package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

import java.util.concurrent.atomic.AtomicInteger;

public class EditableValueListViewControlsProcessor extends EntityViewProcessorAdapter
{
	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		SortableTableBuilder tableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );
		if ( tableBuilder != null ) {
			AtomicInteger idPrefix = new AtomicInteger( 1 );
			tableBuilder.valueRowProcessor( ( builderContext, element ) -> ContainerViewElementUtils.findAll( element, FormInputElement.class )
			                                                                                        .forEach( fie -> {
				                                                                                        String currentId = fie.getHtmlId();
				                                                                                        fie.setHtmlId(
						                                                                                        idPrefix.getAndIncrement() + "-" + currentId );
			                                                                                        } ) );
		}
	}
}
