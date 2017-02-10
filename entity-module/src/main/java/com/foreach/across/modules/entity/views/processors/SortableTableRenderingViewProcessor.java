/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntitySummaryViewActionProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Collection;

/**
 * Renders a list of items as a sortable table using a {@link com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder}.
 * Expects either a {@link org.springframework.data.domain.Page} or {@link java.util.List} attribute to be available on
 * the {@link com.foreach.across.modules.entity.views.EntityView} under the name {@link #ATTRIBUTE_ITEMS}.
 * If no {@link Page} is available as that attribute, the table will not get created.
 * <p/>
 * Will create a {@link com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder} and store it as {@link #TABLE_BUILDER}
 * so following processors can modify it.  During rendering the builder will be added to the general container.
 * <p/>
 * In addition to regular rendering of table results, this processor also supports adding the default
 * {@link com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor} and optionally enabling a summary view
 * if the {@link com.foreach.across.modules.entity.registry.EntityConfiguration} has a view with the specified {@link #setSummaryViewName(String)}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class SortableTableRenderingViewProcessor extends EntityViewProcessorAdapter
{
	public static final String ATTRIBUTE_ITEMS = "items";
	public static final String TABLE_BUILDER = "sortableTableBuilder";

	private BootstrapUiFactory bootstrapUiFactory;
	private EntityViewElementBuilderHelper builderHelper;

	@Setter
	private boolean showResultNumber = true;

	@Setter
	private boolean includeDefaultActions = false;

	@Setter
	private String summaryViewName;

	@Setter
	private String tableName = "itemsTable";

	@Setter
	private String formName;

	/**
	 * Collection of properties that should be sortable in the UI.
	 * If {@code null} default sorting will be enabled on the table pased on the properties being rendered.
	 *
	 * @see SortableTableBuilder#defaultSorting()
	 */
	@Setter
	private Collection<String> sortableProperties = null;

	/**
	 * Selector for the properties that should be rendered on the table.
	 */
	@Setter
	private EntityPropertySelector propertySelector = EntityPropertySelector.of( EntityPropertySelector.ALL );

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		Iterable<?> items = entityView.getAttribute( ATTRIBUTE_ITEMS, Iterable.class );

		if ( items != null ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			SortableTableBuilder tableBuilder = builderHelper.createSortableTableBuilder( entityViewContext );
			tableBuilder.properties( propertySelector );
			tableBuilder.tableName( tableName );
			tableBuilder.formName( formName );
			tableBuilder.showResultNumber( showResultNumber );
			if ( sortableProperties != null ) {
				tableBuilder.sortableOn( sortableProperties );
			}

			if ( items instanceof Page ) {
				tableBuilder.items( (Page) items );
			}
			else {
				tableBuilder.items( EntityUtils.asPage( items ) );
			}

			registerDefaultListActions( entityViewContext, tableBuilder );
			registerSummaryView( entityViewContext, tableBuilder );

			builderMap.put( TABLE_BUILDER, tableBuilder );
		}
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		if ( hasSummaryView( entityViewRequest.getEntityViewContext() ) ) {
			webResourceRegistry.add( WebResource.JAVASCRIPT_PAGE_END, "/js/entity/expandable.js", WebResource.VIEWS );
		}
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		if ( builderMap.containsKey( TABLE_BUILDER ) ) {
			containerBuilder.add( builderMap.get( TABLE_BUILDER ) );
		}
	}

	private void registerSummaryView( EntityViewContext entityViewContext, SortableTableBuilder tableBuilder ) {
		if ( hasSummaryView( entityViewContext ) ) {
			tableBuilder.valueRowProcessor( new EntitySummaryViewActionProcessor( summaryViewName ) );
		}
	}

	private boolean hasSummaryView( EntityViewContext entityViewContext ) {
		return summaryViewName != null &&
				( entityViewContext.isForAssociation()
						? entityViewContext.getEntityAssociation().hasView( summaryViewName )
						: entityViewContext.getEntityConfiguration().hasView( summaryViewName ) );
	}

	private void registerDefaultListActions( EntityViewContext entityViewContext, SortableTableBuilder tableBuilder ) {
		if ( includeDefaultActions ) {
			EntityListActionsProcessor actionsProcessor = new EntityListActionsProcessor( bootstrapUiFactory,
			                                                                              entityViewContext.getEntityConfiguration(),
			                                                                              entityViewContext.getLinkBuilder(),
			                                                                              entityViewContext.getEntityMessages() );
			tableBuilder.headerRowProcessor( actionsProcessor );
			tableBuilder.valueRowProcessor( actionsProcessor );
		}
	}

	@Autowired
	void setBuilderHelper( EntityViewElementBuilderHelper builderHelper ) {
		this.builderHelper = builderHelper;
	}

	@Autowired
	void setBootstrapUiFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUiFactory = bootstrapUiFactory;
	}
}
