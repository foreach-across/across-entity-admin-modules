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
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntitySummaryViewActionProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
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
import java.util.Map;
import java.util.Objects;

/**
 * Renders a list of items as a sortable table using a {@link com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder}.
 * Expects either a {@link org.springframework.data.domain.Page} or {@link java.util.List} attribute to be available on
 * the {@link com.foreach.across.modules.entity.views.EntityView} under the name {@link AbstractEntityFetchingViewProcessor#DEFAULT_ATTRIBUTE_NAME}.
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
	public static final String TABLE_BUILDER = "sortableTableBuilder";

	private EntityViewElementBuilderHelper builderHelper;

	/**
	 * Should the result number be shown as first column in the table?
	 * Defaults to {@code true}.
	 */
	@Setter
	private boolean showResultNumber = true;

	/**
	 * Should default actions be included for every entity?
	 * These are an update and delete button and depend on the principal having
	 * the {@link AllowableAction#UPDATE} or {@link AllowableAction#DELETE} respectively.
	 * <p/>
	 * Defaults to {@code false}.
	 */
	@Setter
	private boolean includeDefaultActions = false;

	/**
	 * Name of the summary view.  If this is set and the view exists, a summary detail view will be available for every entity row.
	 * Defaults to {@link EntityView#SUMMARY_VIEW_NAME}.
	 */
	@Setter
	private String summaryViewName = EntityView.SUMMARY_VIEW_NAME;

	/**
	 * Set the name of the table, added as data attributes in the markup.
	 * <p/>
	 * Defaults to {@code itemsTable}.
	 */
	@Setter
	private String tableName = "itemsTable";

	/**
	 * Set the name of the form that should be submitted when changing the page or sorting a column.
	 */
	@Setter
	private String formName;

	/**
	 * Collection of properties that should be sortable in the UI.
	 * If {@code null} default sorting will be enabled on the table passed on the properties being rendered.
	 *
	 * @see SortableTableBuilder#defaultSorting()
	 */
	@Setter
	private Collection<String> sortableProperties = null;

	private EntityPropertySelector propertySelector = EntityPropertySelector.of( EntityPropertySelector.READABLE );

	/**
	 * ViewElement mode for the value rows.
	 */
	@Setter
	private ViewElementMode viewElementMode = ViewElementMode.LIST_VALUE;

	/**
	 * Set the selector for the properties that should be rendered on the table.
	 */
	public void setPropertySelector( EntityPropertySelector propertySelector ) {
		this.propertySelector = this.propertySelector.combine( propertySelector );
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		Iterable<?> items = entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class );

		if ( items != null ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			SortableTableBuilder tableBuilder = builderHelper.createSortableTableBuilder( entityViewContext );
			tableBuilder.properties( propertySelector );
			tableBuilder.tableName( tableName );
			tableBuilder.formName( formName );
			tableBuilder.showResultNumber( showResultNumber );
			tableBuilder.setValueViewElementMode( viewElementMode );

			if ( sortableProperties != null ) {
				tableBuilder.sortableOn( sortableProperties );
			}

			if ( items instanceof Page ) {
				tableBuilder.items( (Page) items );
			}
			else {
				tableBuilder.items( EntityUtils.asPage( items ) );
			}

			registerDefaultListActions( entityViewRequest, tableBuilder );
			registerSummaryView( entityViewContext, tableBuilder );

			builderMap.put( TABLE_BUILDER, tableBuilder );
		}
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		if ( hasSummaryView( entityViewRequest.getEntityViewContext() ) ) {
			webResourceRegistry.add( WebResource.JAVASCRIPT_PAGE_END, "/static/entity/js/expandable.js", WebResource.VIEWS );
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
			tableBuilder.valueRowProcessor( new EntitySummaryViewActionProcessor( entityViewContext.getLinkBuilder(), summaryViewName ) );
		}
	}

	private boolean hasSummaryView( EntityViewContext entityViewContext ) {
		return summaryViewName != null &&
				( entityViewContext.isForAssociation()
						? entityViewContext.getEntityAssociation().hasView( summaryViewName )
						: entityViewContext.getEntityConfiguration().hasView( summaryViewName ) );
	}

	private void registerDefaultListActions( EntityViewRequest entityViewRequest, SortableTableBuilder tableBuilder ) {
		if ( includeDefaultActions ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			EntityConfiguration entityConfiguration = entityViewContext.getEntityConfiguration();
			EntityListActionsProcessor actionsProcessor = new EntityListActionsProcessor( entityConfiguration,
			                                                                              entityViewContext.getLinkBuilder(),
			                                                                              entityViewContext.getEntityMessages() );
			actionsProcessor.setLinkToDetailView( shouldLinkToDetailView( entityViewRequest, entityConfiguration ) );
			tableBuilder.headerRowProcessor( actionsProcessor );
			tableBuilder.valueRowProcessor( actionsProcessor );
		}
	}

	private boolean shouldLinkToDetailView( EntityViewRequest entityViewRequest, EntityConfiguration entityConfiguration ) {
		Map<String, Object> configurationAttributes = entityViewRequest.getConfigurationAttributes();

		if ( configurationAttributes.containsKey( EntityAttributes.LINK_TO_DETAIL_VIEW ) ) {
			return Boolean.TRUE.equals( configurationAttributes.get( EntityAttributes.LINK_TO_DETAIL_VIEW ) );
		}

		return Boolean.TRUE.equals( entityConfiguration.getAttribute( EntityAttributes.LINK_TO_DETAIL_VIEW ) );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		SortableTableRenderingViewProcessor that = (SortableTableRenderingViewProcessor) o;
		return showResultNumber == that.showResultNumber &&
				includeDefaultActions == that.includeDefaultActions &&
				Objects.equals( summaryViewName, that.summaryViewName ) &&
				Objects.equals( tableName, that.tableName ) &&
				Objects.equals( formName, that.formName ) &&
				Objects.equals( sortableProperties, that.sortableProperties ) &&
				Objects.equals( propertySelector, that.propertySelector ) &&
				Objects.equals( viewElementMode, that.viewElementMode );
	}

	@Override
	public int hashCode() {
		return Objects.hash( showResultNumber, includeDefaultActions, summaryViewName, tableName, formName, sortableProperties, propertySelector,
		                     viewElementMode );
	}

	@Autowired
	void setBuilderHelper( EntityViewElementBuilderHelper builderHelper ) {
		this.builderHelper = builderHelper;
	}
}
