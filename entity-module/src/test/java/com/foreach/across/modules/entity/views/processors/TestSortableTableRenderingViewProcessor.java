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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
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
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestSortableTableRenderingViewProcessor
{
	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private EntityView entityView;

	@Mock
	private EntityViewElementBuilderHelper builderHelper;

	@Mock
	private ViewElementBuilderMap builderMap;

	@Mock
	private SortableTableBuilder sortableTable;

	@Mock
	private Page page;

	@InjectMocks
	private SortableTableRenderingViewProcessor processor;

	@Before
	public void setUp() throws Exception {
		ViewElementBuilderContextHolder.setViewElementBuilderContext( mock( ViewElementBuilderContext.class ) );

		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( builderHelper.createSortableTableBuilder( viewContext ) ).thenReturn( sortableTable );
		when( entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class ) ).thenReturn( page );
	}

	@After
	public void tearDown() throws Exception {
		ViewElementBuilderContextHolder.clearViewElementBuilderContext();
	}

	@Test
	public void noTableIsBuiltIfNoPageIsPresent() {
		when( entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class ) ).thenReturn( null );

		processor.createViewElementBuilders( viewRequest, entityView, builderMap );
		verify( entityView ).getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class );
		verifyNoMoreInteractions( builderMap );
	}

	@Test
	public void defaultSortableTable() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		processor.createViewElementBuilders( viewRequest, entityView, builderMap );

		verify( sortableTable ).properties( EntityPropertySelector.of( EntityPropertySelector.READABLE ) );
		verify( sortableTable ).items( page );
		verify( sortableTable ).tableName( "itemsTable" );
		verify( sortableTable ).formName( null );
		verify( sortableTable, never() ).sortableOn( anyCollectionOf( String.class ) );
		verify( sortableTable ).showResultNumber( true );
		verify( sortableTable, never() ).headerRowProcessor( any() );
		verify( sortableTable, never() ).valueRowProcessor( any() );

		verify( builderMap ).put( SortableTableRenderingViewProcessor.TABLE_BUILDER, sortableTable );
	}

	@Test
	public void customSettingsAndDefaultActions() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		List<?> items = new ArrayList<>();
		when( entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class ) ).thenReturn( items );

		processor.setFormName( "form-name" );
		processor.setTableName( "table-name" );
		processor.setPropertySelector( EntityPropertySelector.of( "one", "two" ) );
		processor.setIncludeDefaultActions( true );
		processor.setShowResultNumber( false );
		processor.setSortableProperties( Collections.singleton( "two" ) );

		processor.createViewElementBuilders( viewRequest, entityView, builderMap );

		verify( sortableTable ).properties( EntityPropertySelector.of( "one", "two" ) );
		verify( sortableTable ).items( EntityUtils.asPage( items ) );
		verify( sortableTable ).tableName( "table-name" );
		verify( sortableTable ).formName( "form-name" );
		verify( sortableTable ).sortableOn( Collections.singleton( "two" ) );
		verify( sortableTable ).showResultNumber( false );
		verify( sortableTable ).headerRowProcessor( any( EntityListActionsProcessor.class ) );
		verify( sortableTable ).valueRowProcessor( any( EntityListActionsProcessor.class ) );

		verify( builderMap ).put( SortableTableRenderingViewProcessor.TABLE_BUILDER, sortableTable );
	}

	@Test
	public void summaryViewNotAddedBecauseNotExisting() {
		Page page = mock( Page.class );
		when( entityView.getAttribute( AbstractEntityFetchingViewProcessor.DEFAULT_ATTRIBUTE_NAME, Iterable.class ) ).thenReturn( page );
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		processor.setSummaryViewName( "summary-view-name" );
		processor.createViewElementBuilders( viewRequest, entityView, builderMap );

		verify( sortableTable, never() ).headerRowProcessor( any() );
		verify( sortableTable, never() ).valueRowProcessor( any() );
	}

	@Test
	public void summaryViewAddedForConfiguration() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( entityConfiguration.hasView( "summary-view-name" ) ).thenReturn( true );

		processor.setSummaryViewName( "summary-view-name" );
		processor.createViewElementBuilders( viewRequest, entityView, builderMap );

		verify( sortableTable, never() ).headerRowProcessor( any() );
		verify( sortableTable ).valueRowProcessor( any( EntitySummaryViewActionProcessor.class ) );
	}

	@Test
	public void summaryViewAddedForAssociation() {
		when( viewContext.isForAssociation() ).thenReturn( true );
		EntityAssociation entityAssociation = mock( EntityAssociation.class );
		when( viewContext.getEntityAssociation() ).thenReturn( entityAssociation );
		when( entityAssociation.hasView( "summary-view-name" ) ).thenReturn( true );

		processor.setSummaryViewName( "summary-view-name" );
		processor.createViewElementBuilders( viewRequest, entityView, builderMap );

		verify( sortableTable, never() ).headerRowProcessor( any() );
		verify( sortableTable ).valueRowProcessor( any( EntitySummaryViewActionProcessor.class ) );
	}

	@Test
	public void byDefaultNoWebResourcesRegistered() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		WebResourceRegistry registry = mock( WebResourceRegistry.class );
		processor.registerWebResources( viewRequest, entityView, registry );
		verifyNoMoreInteractions( registry );
	}

	@Test
	public void webResourcesRegisteredForConfiguration() {
		WebResourceRegistry registry = mock( WebResourceRegistry.class );
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( entityConfiguration.hasView( "summary-view-name" ) ).thenReturn( true );

		processor.setSummaryViewName( "summary-view-name" );
		processor.registerWebResources( viewRequest, entityView, registry );

		verify( registry ).add( WebResource.JAVASCRIPT_PAGE_END, "/static/entity/js/expandable.js", WebResource.VIEWS );
	}

	@Test
	public void webResourcesRegisteredForAssociation() {
		WebResourceRegistry registry = mock( WebResourceRegistry.class );
		when( viewContext.isForAssociation() ).thenReturn( true );
		EntityAssociation entityAssociation = mock( EntityAssociation.class );
		when( viewContext.getEntityAssociation() ).thenReturn( entityAssociation );
		when( entityAssociation.hasView( "summary-view-name" ) ).thenReturn( true );

		processor.setSummaryViewName( "summary-view-name" );
		processor.registerWebResources( viewRequest, entityView, registry );

		verify( registry ).add( WebResource.JAVASCRIPT_PAGE_END, "/static/entity/js/expandable.js", WebResource.VIEWS );
	}
}
