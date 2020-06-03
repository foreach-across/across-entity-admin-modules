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

package it.com.foreach.across.modules.entity.views.bootstrapui.util;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.util.PagingMessages;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.*;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.entity.views.support.EntityMessages.RESULTS_FOUND;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration(classes = TestSortableTableBuilder.Config.class)
public class TestSortableTableBuilder extends AbstractViewElementTemplateTest
{
	private static final String TABLE_WITH_RESULT_NUMBER = "<div class='table-responsive'>" +
			"<table class='em-sortableTable-table table table-hover' " +
			"data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
			"data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='1'>" +
			"<thead>" +
			"<tr><th class='result-number'>#</th><th data-tbl-field='propertyOne'>Property name</th></tr>" +
			"</thead>" +
			"<tbody>" +
			"<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
			"</tbody>" +
			"</table>" +
			"</div>";

	private static final String TABLE_WITH_RESULT_NUMBER_AND_FORM = "<div class='table-responsive'>" +
			"<table class='em-sortableTable-table table table-hover' " +
			"data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
			"data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='1' data-tbl-form='my-form'>" +
			"<thead>" +
			"<tr><th class='result-number'>#</th><th data-tbl-field='propertyOne'>Property name</th></tr>" +
			"</thead>" +
			"<tbody>" +
			"<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
			"</tbody>" +
			"</table>" +
			"</div>";

	private static final String TABLE_WITHOUT_RESULT_NUMBER = "<div class='table-responsive'>" +
			"<table class='em-sortableTable-table table table-hover' " +
			"data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
			"data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='1'>" +
			"<thead>" +
			"<tr><th data-tbl-field='propertyOne'>Property name</th></tr>" +
			"</thead>" +
			"<tbody>" +
			"<tr class='odd'><td data-tbl-field='propertyOne'>Property value</td></tr>" +
			"</tbody>" +
			"</table>" +
			"</div>";

	@Autowired
	private EntityViewElementBuilderService viewElementBuilderService;

	private EntityConfiguration entityConfiguration;
	private SortableTableBuilder tableBuilder;
	private EntityPropertyDescriptor descriptor;

	@Before
	public void before() {
		reset( viewElementBuilderService );

		tableBuilder = new SortableTableBuilder( viewElementBuilderService );

		entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "entity" );

		descriptor = mock( EntityPropertyDescriptor.class );

		tableBuilder
				.entityConfiguration( entityConfiguration )
				.noSorting()
				.properties( Collections.singleton( descriptor ) );

		tableBuilder.items( Collections.singletonList( "test" ) );

		PagingMessages messages = mock( PagingMessages.class );
		when( messages.resultsFound( any( Page.class ), any() ) ).thenReturn( "xx results" );
		tableBuilder.pagingMessages( messages );

		when( descriptor.getName() ).thenReturn( "propertyOne" );

		Sort.Order order = new Sort.Order( "sortOnMe" );
		when( descriptor.getAttribute( Sort.Order.class ) ).thenReturn( order );

		when( viewElementBuilderService.getElementBuilder( descriptor,
		                                                   ViewElementMode.LIST_LABEL ) )
				.thenReturn( new TextViewElementBuilder().text( "Property name" ) );

		when( viewElementBuilderService.getElementBuilder( descriptor,
		                                                   ViewElementMode.LIST_VALUE ) )
				.thenReturn( new TextViewElementBuilder().text( "Property value" ) );
	}

	@Test
	public void tableOnlyDoesNotRequirePagingMessages() {
		assertSame( tableBuilder, tableBuilder.tableOnly().pagingMessages( null ) );

		expect( TABLE_WITH_RESULT_NUMBER );

		verify( entityConfiguration, times( 1 ) ).getEntityMessageCodeResolver();
	}

	@Test
	public void tableBoundToForm() {
		tableBuilder.tableOnly().formName( "my-form" );

		expect( TABLE_WITH_RESULT_NUMBER_AND_FORM );
	}

	@Test
	public void simpleTable() {
		expect(
				"<div class='card em-sortableTable-panel'>" +
						"<div class='card-header'>xx results</div>" +
						"<div class='card-body'>" +
						TABLE_WITH_RESULT_NUMBER +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void defaultPagingMessagesFromEntityConfigurationWillBeUsed() {
		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );

		when( entityConfiguration.getEntityMessageCodeResolver() ).thenReturn( codeResolver );
		when( codeResolver.getMessageWithFallback( eq( RESULTS_FOUND ), any( Object[].class ), eq( "" ) ) )
				.thenReturn( "custom results" );

		tableBuilder.pagingMessages( null );

		expect(
				"<div class='card em-sortableTable-panel'>" +
						"<div class='card-header'>custom results</div>" +
						"<div class='card-body'>" +
						TABLE_WITH_RESULT_NUMBER +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void noResultNumberTable() {
		tableBuilder.showResultNumber( false );

		expect(
				"<div class='card em-sortableTable-panel'>" +
						"<div class='card-header'>xx results</div>" +
						"<div class='card-body'>" +
						TABLE_WITHOUT_RESULT_NUMBER +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void secondPageResults() {
		Sort sort = new Sort( Arrays.asList(
				new Sort.Order( Sort.Direction.ASC, "one" ),
				new Sort.Order( Sort.Direction.DESC, "two" ),
				new Sort.Order( Sort.Direction.DESC, "three" )
		) );

		Pageable pageable = new PageRequest( 1, 20, sort );
		Page page = new PageImpl<>( Arrays.asList( "één", "twee" ), pageable, 57 );

		tableBuilder.tableName( "entityList" ).items( page );

		SimpleIconSet iconSet = new SimpleIconSet();
		iconSet.setDefaultIconResolver( ( name ) -> HtmlViewElements.html.i().set( HtmlViewElement.Functions.css( name ) ) );
		IconSetRegistry.addIconSet( EntityModule.NAME, iconSet );

		expect(
				"<div class='card em-sortableTable-panel'>" +
						"<div class='card-header'>xx results</div>" +
						"<div class='card-body'>" +
						"<div class='table-responsive'>" +
						"<table class='em-sortableTable-table table table-hover' " +
						"data-tbl='entityList' data-tbl-type='paged' data-tbl-entity-type='entity' " +
						"data-tbl-current-page='1' data-tbl-total-pages='3' data-tbl-size='20' " +
						"data-tbl-sort='[{\"prop\":\"one\",\"dir\":\"ASC\"},{\"prop\":\"two\",\"dir\":\"DESC\"},{\"prop\":\"three\",\"dir\":\"DESC\"}]'>" +
						"<thead>" +
						"<tr><th class='result-number'>#</th><th data-tbl-field='propertyOne'>Property name</th></tr>" +
						"</thead>" +
						"<tbody>" +
						"<tr class='odd'><td class='result-number'>21</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
						"<tr class='even'><td class='result-number'>22</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
						"</tbody>" +
						"</table>" +
						"</div>" +
						"</div>" +
						"<div class=\"card-footer\">" +
						"<div class=\"pager-form form-inline axu-flex-row axu-justify-content-center\">" +
						"<a role=\"button\" href=\"#\" data-tbl=\"entityList\" data-tbl-page=\"0\" class=\"btn btn-link\">" +
						"<i class=\"previous-page\"></i>" +
						"</a>" +
						"<label><span></span><input data-bootstrapui-adapter-type=\"basic\" type=\"text\"\n" +
						"\t\tclass=\"form-control\" value=\"2\" data-tbl=\"entityList\" data-tbl-page-selector=\"selector\" /></label>" +
						"<span></span>" +
						"<a data-tbl=\"entityList\" href=\"#\" class=\"total-pages-link\" data-tbl-page=\"2\">3</a>" +
						"<a role=\"button\" href=\"#\" data-tbl=\"entityList\" data-tbl-page=\"2\" class=\"btn btn-link\">" +
						"<i class=\"next-page\"></i>" +
						"</a>" +
						"</div></div>" +
						"</div>"
		);
		IconSetRegistry.removeIconSet( EntityModule.NAME );
	}

	@Test
	public void defaultNoResultsPanel() {
		Page page = new PageImpl<>( Collections.emptyList() );
		tableBuilder.items( page );

		PagingMessages messages = mock( PagingMessages.class );
		when( messages.resultsFound( any( Page.class ), any() ) ).thenReturn( "Geen resultaten gevonden" );

		tableBuilder.pagingMessages( messages );

		expect( "<div class='card axu-border-warning'>" +
				        "<div class='card-body axu-text-warning'>Geen resultaten gevonden</div>" +
				        "</div>" );
	}

	@Test
	public void entityTypeAndDescriptorsSpecified() {
		tableBuilder = new SortableTableBuilder( viewElementBuilderService )
				.noSorting()
				.properties( Collections.singleton( descriptor ) )
				.entityType( "my-entity" )
				.items( Collections.singletonList( "test" ) )
				.tableOnly();

		expect( TABLE_WITH_RESULT_NUMBER.replace( "'entity'", "'my-entity'" ) );
	}

	@Test
	public void entityTypeAndRegistrySpecified() {
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		when( propertyRegistry.select( new EntityPropertySelector( "myprop" ) ) )
				.thenReturn( Collections.singletonList( descriptor ) );

		tableBuilder = new SortableTableBuilder( viewElementBuilderService )
				.noSorting()
				.propertyRegistry( propertyRegistry )
				.properties( "myprop" )
				.entityType( "my-entity" )
				.items( Collections.singletonList( "test" ) )
				.tableOnly();

		expect( TABLE_WITH_RESULT_NUMBER.replace( "'entity'", "'my-entity'" ) );
	}

	@Test
	public void propertiesAsSelectorOnConfiguration() {
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		when( propertyRegistry.select( new EntityPropertySelector( "myprop" ) ) )
				.thenReturn( Collections.singletonList( descriptor ) );

		when( entityConfiguration.getPropertyRegistry() ).thenReturn( propertyRegistry );

		tableBuilder = new SortableTableBuilder( viewElementBuilderService )
				.noSorting()
				.entityConfiguration( entityConfiguration )
				.properties( new EntityPropertySelector( "myprop" ) )
				.items( Collections.singletonList( "test" ) )
				.tableOnly();

		expect( TABLE_WITH_RESULT_NUMBER );
	}

	@Test
	public void customElementTypes() {
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LIST_LABEL ) ).thenReturn( null );
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LIST_VALUE ) ).thenReturn( null );

		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LABEL ) )
				.thenReturn( new TextViewElementBuilder().text( "Property name" ) );
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LIST_CONTROL ) )
				.thenReturn( new TextViewElementBuilder().text( "Property value" ) );

		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		when( propertyRegistry.select( new EntityPropertySelector( "myprop" ) ) )
				.thenReturn( Collections.singletonList( descriptor ) );

		when( entityConfiguration.getPropertyRegistry() ).thenReturn( propertyRegistry );

		tableBuilder = new SortableTableBuilder( viewElementBuilderService )
				.setLabelViewElementMode( ViewElementMode.LABEL )
				.setValueViewElementMode( ViewElementMode.LIST_CONTROL )
				.noSorting()
				.entityConfiguration( entityConfiguration )
				.properties( new EntityPropertySelector( "myprop" ) )
				.items( Collections.singletonList( "test" ) )
				.tableOnly();

		expect( TABLE_WITH_RESULT_NUMBER );
	}

	@Test
	public void customNoResultsElement() {
		Page page = new PageImpl<>( Collections.emptyList() );
		tableBuilder.items( page );

		tableBuilder.noResults( new TextViewElement( "empty" ) );
		expect( "empty" );

		tableBuilder.noResults(
				new NodeViewElementBuilder( "div" )
						.add( new TextViewElement( "empty" ) )
		);
		expect( "<div>empty</div>" );
	}

	@Test
	public void sorting() {
		tableBuilder
				.tableOnly( true )
				.sortableOn( "propertyOne", "propertyTwo" );

		expect( "<div class='table-responsive'>" +
				        "<table class='em-sortableTable-table table table-hover' " +
				        "data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
				        "data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='1'>" +
				        "<thead>" +
				        "<tr><th class='result-number'>#</th>" +
				        "<th data-tbl-field='propertyOne' class='sortable' data-tbl-sort-property='sortOnMe' data-tbl='sortableTable'>" +
				        "Property name</th></tr>" +
				        "</thead>" +
				        "<tbody>" +
				        "<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
				        "</tbody>" +
				        "</table>" +
				        "</div>"
		);

		tableBuilder.defaultSorting();

		expect( "<div class='table-responsive'>" +
				        "<table class='em-sortableTable-table table table-hover' " +
				        "data-tbl='sortableTable' data-tbl-type='paged' data-tbl-entity-type='entity' " +
				        "data-tbl-current-page='0' data-tbl-total-pages='1' data-tbl-size='1'>" +
				        "<thead>" +
				        "<tr><th class='result-number'>#</th>" +
				        "<th data-tbl-field='propertyOne' class='sortable' data-tbl-sort-property='sortOnMe' data-tbl='sortableTable'>" +
				        "Property name</th></tr>" +
				        "</thead>" +
				        "<tbody>" +
				        "<tr class='odd'><td class='result-number'>1</td><td data-tbl-field='propertyOne'>Property value</td></tr>" +
				        "</tbody>" +
				        "</table>" +
				        "</div>"
		);
	}

	@Test
	public void tableStyle() {
		tableBuilder.tableStyles( css.table.small ).tableOnly( true );

		expect(
				TABLE_WITH_RESULT_NUMBER.replace( "table table-hover", "table table-sm" )
		);
	}

	@Test
	public void defaultTableName() {
		ContainerViewElement result = tableBuilder.build( mock( ViewElementBuilderContext.class ) );
		assertEquals( "sortableTable", result.getName() );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-table", TableViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-heading", NodeViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-noresults", TableViewElement.class ) );

		result = tableBuilder.tableOnly().build( mock( ViewElementBuilderContext.class ) );
		assertEquals( "sortableTable", result.getName() );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-table", TableViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-heading", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-noresults", TableViewElement.class ) );

		result = tableBuilder.tableOnly().items( Collections.emptyList() )
		                     .build( mock( ViewElementBuilderContext.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-heading", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-table", TableViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "sortableTable-noresults", ViewElement.class ) );
	}

	@Test
	public void customTableName() {
		ContainerViewElement result = tableBuilder.tableName( "myTable" ).build( mock( ViewElementBuilderContext.class ) );
		assertEquals( "myTable", result.getName() );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-table", TableViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-heading", NodeViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-noresults", TableViewElement.class ) );

		result = tableBuilder.tableOnly().build( mock( ViewElementBuilderContext.class ) );
		assertEquals( "myTable", result.getName() );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-table", TableViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-heading", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-noresults", TableViewElement.class ) );

		result = tableBuilder.tableOnly().items( Collections.emptyList() )
		                     .build( mock( ViewElementBuilderContext.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-heading", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-panel-body", NodeViewElement.class ) );
		assertEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-table", TableViewElement.class ) );
		assertNotEquals( Optional.empty(), ContainerViewElementUtils.find( result, "myTable-noresults", ViewElement.class ) );
	}

	private void expect( String output ) {
		ViewElementBuilderContext ctx = mock( ViewElementBuilderContext.class );

		renderAndExpect( tableBuilder.build( ctx ), output );
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}

		@Bean
		public EntityViewElementBuilderService viewElementBuilderService() {
			return mock( EntityViewElementBuilderService.class );
		}
	}
}
