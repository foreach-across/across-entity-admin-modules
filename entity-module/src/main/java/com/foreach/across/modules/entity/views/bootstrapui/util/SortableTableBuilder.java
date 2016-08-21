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
package com.foreach.across.modules.entity.views.bootstrapui.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.support.ListViewEntityMessages;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;

import java.util.*;

import static com.foreach.across.modules.entity.views.ViewElementMode.LIST_LABEL;
import static com.foreach.across.modules.entity.views.ViewElementMode.LIST_VALUE;

/**
 * Helper that aids in building a sortable {@link com.foreach.across.modules.bootstrapui.elements.TableViewElement}
 * for a list of {@link EntityPropertyDescriptor}s.
 *
 * @author Arne Vandamme
 */
@Scope("prototype")
public class SortableTableBuilder implements ViewElementBuilder<ViewElement>
{
	/**
	 * Sets an 'odd' or 'even' class on a table row depending on the iterator index.
	 */
	public static final ViewElementPostProcessor<TableViewElement.Row> CSS_ODD_EVEN_ROW_PROCESSOR
			= ( builderContext, element ) -> {
		if ( builderContext instanceof IteratorViewElementBuilderContext ) {
			boolean even = ( ( (IteratorViewElementBuilderContext) builderContext ).getIndex() + 1 ) % 2 == 0;
			element.addCssClass( even ? "even" : "odd" );
		}
	};

	public static String ELEMENT_TABLE = "table";
	public static String ELEMENT_PANEL = "panel";
	public static String ELEMENT_NORESULTS = "noresults";
	public static String ELEMENT_PAGER = "pager";

	public static String DATA_ATTR_FIELD = "data-tbl-field";
	public static String DATA_ATTR_TABLE_NAME = "data-tbl";
	public static String DATA_ATTR_TABLE_TYPE = "data-tbl-type";
	public static String DATA_ATTR_ENTITY_TYPE = "data-tbl-entity-type";
	public static String DATA_ATTR_CURRENT_PAGE = "data-tbl-current-page";
	public static String DATA_ATTR_PAGE = "data-tbl-page";
	public static String DATA_ATTR_PAGES = "data-tbl-total-pages";
	public static String DATA_ATTR_PAGE_SIZE = "data-tbl-size";
	public static String DATA_ATTR_SORT = "data-tbl-sort";
	public static String DATA_ATTR_SORT_PROPERTY = "data-tbl-sort-property";

	protected final EntityViewElementBuilderService viewElementBuilderService;
	protected final BootstrapUiFactory bootstrapUi;

	private String tableName = "sortableTable";
	private String entityType;
	private EntityConfiguration entityConfiguration;
	private EntityPropertyRegistry entityPropertyRegistry;
	private EntityPropertySelector propertySelector;
	private Collection<String> sortableProperties;
	private Collection<EntityPropertyDescriptor> propertyDescriptors;
	private boolean tableOnly, showResultNumber = true;
	private Page<Object> page;
	private Style[] tableStyles = new Style[] { Style.Table.HOVER };
	private PagingMessages pagingMessages;
	private ViewElementBuilderSupport.ElementOrBuilder noResultsElement;
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> headerRowProcessors = new ArrayList<>();
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> valueRowProcessors = new ArrayList<>();

	private PagingMessages resolvedPagingMessages;
	private Collection<EntityPropertyDescriptor> resolvedPropertyDescriptors;

	@Autowired
	public SortableTableBuilder( EntityViewElementBuilderService viewElementBuilderService,
	                             BootstrapUiFactory bootstrapUi ) {
		this.viewElementBuilderService = viewElementBuilderService;
		this.bootstrapUi = bootstrapUi;
	}

	public String getEntityType() {
		return entityType;
	}

	/**
	 * Set the entity type attribute value that should be set as data attribute on the table.
	 * If not set, the name of the {@link EntityConfiguration} will be used.
	 *
	 * @param entityType value
	 * @return current builder
	 */
	public SortableTableBuilder entityType( String entityType ) {
		this.entityType = entityType;
		return this;
	}

	/**
	 * Set a custom no results view element to be returned
	 *
	 * @param element to be shown in case the page is empty
	 */
	public SortableTableBuilder noResults( ViewElement element ) {
		this.noResultsElement = ViewElementBuilderSupport.ElementOrBuilder.wrap( element );
		return this;
	}

	/**
	 * Set a custom no results view element to be returned if the data page is empty.
	 * If not set a default panel with text will be created.
	 *
	 * @param builder to be shown in case the page is empty
	 */
	public SortableTableBuilder noResults( ViewElementBuilder builder ) {
		this.noResultsElement = ViewElementBuilderSupport.ElementOrBuilder.wrap( builder );
		return this;
	}

	public SortableTableBuilder headerRowProcessor( ViewElementPostProcessor<TableViewElement.Row> processor ) {
		headerRowProcessors.add( processor );
		return this;
	}

	public SortableTableBuilder valueRowProcessor( ViewElementPostProcessor<TableViewElement.Row> processor ) {
		valueRowProcessors.add( processor );
		return this;
	}

	public String getTableName() {
		return tableName;
	}

	/**
	 * @param tableName to be used both internally and as data attribute on the resulting html table
	 */
	public SortableTableBuilder tableName( String tableName ) {
		this.tableName = tableName;
		return this;
	}

	protected EntityConfiguration getEntityConfiguration() {
		return entityConfiguration;
	}

	public SortableTableBuilder entityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
		this.resolvedPagingMessages = null;
		this.resolvedPropertyDescriptors = null;
		return this;
	}

	protected EntityPropertyRegistry getPropertyRegistry() {
		return entityPropertyRegistry;
	}

	/**
	 * Configure the {@link EntityPropertyRegistry} that should be used for fetching the property descriptors.
	 * If none specified, the registry from the {@link EntityConfiguration} will be used.
	 * If {@link EntityPropertyDescriptor}s are added directly, the registry will never be used.
	 *
	 * @param entityPropertyRegistry instance
	 * @return current builder
	 */
	public SortableTableBuilder propertyRegistry( EntityPropertyRegistry entityPropertyRegistry ) {
		this.entityPropertyRegistry = entityPropertyRegistry;
		this.resolvedPropertyDescriptors = null;
		return this;
	}

	/**
	 * Explicitly specify the collection of {@link EntityPropertyDescriptor} instances that should be rendered.
	 * This will take precedence over any selector specified and does not require you to have a valid
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}.
	 *
	 * @param propertyDescriptors to use
	 * @return current builder
	 */
	public SortableTableBuilder properties( Collection<EntityPropertyDescriptor> propertyDescriptors ) {
		this.propertyDescriptors = propertyDescriptors;
		return this;
	}

	/**
	 * Specify the arguments of a {@link EntityPropertySelector} that should be applied to the registry
	 * configured on this builder.  See also {@link #propertyRegistry(EntityPropertyRegistry)}.
	 *
	 * @param properties names of the properties
	 * @return current builder
	 */
	public SortableTableBuilder properties( String... properties ) {
		return properties( new EntityPropertySelector( properties ) );
	}

	/**
	 * Specify the properties to render as a selector that should be applied to the registry
	 * configured on this builder.  See also {@link #propertyRegistry(EntityPropertyRegistry)}.
	 *
	 * @param selector for the properties
	 * @return current builder
	 */
	public SortableTableBuilder properties( EntityPropertySelector selector ) {
		this.propertySelector = selector;
		this.resolvedPropertyDescriptors = null;
		return this;
	}

	public boolean isTableOnly() {
		return tableOnly;
	}

	/**
	 * Render only the table, not the panel around it.
	 */
	public SortableTableBuilder tableOnly() {
		return tableOnly( true );
	}

	/**
	 * @param tableOnly true if only the table should be returned (no panel)
	 */
	public SortableTableBuilder tableOnly( boolean tableOnly ) {
		this.tableOnly = tableOnly;
		return this;
	}

	protected Page getPage() {
		return page;
	}

	/**
	 * Full list of data items to be shown.  Paging will be disabled.
	 *
	 * @param items list of items
	 * @return current builder
	 */
	public SortableTableBuilder items( List<?> items ) {
		return items( new PageImpl<>( items ) );
	}

	/**
	 * @param page of data items to be shown
	 */
	@SuppressWarnings("unchecked")
	public SortableTableBuilder items( Page page ) {
		this.page = page;
		return this;
	}

	protected boolean isShowResultNumber() {
		return showResultNumber;
	}

	public SortableTableBuilder showResultNumber() {
		return showResultNumber( true );
	}

	public SortableTableBuilder hideResultNumber() {
		return showResultNumber( false );
	}

	/**
	 * @param showResultNumber true if result number should be included
	 */
	public SortableTableBuilder showResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
		return this;
	}

	protected Style[] getTableStyles() {
		return tableStyles;
	}

	/**
	 * @param tableStyles that should be applied to the generated table
	 */
	public SortableTableBuilder tableStyles( Style... tableStyles ) {
		this.tableStyles = tableStyles;
		return this;
	}

	protected PagingMessages getPagingMessages() {
		return pagingMessages;
	}

	/**
	 * Set the paging messages to be used in case paging is enabled.
	 * Will only be used if the panel is being rendered.
	 *
	 * @param pagingMessages to be used for the result text
	 */
	public SortableTableBuilder pagingMessages( PagingMessages pagingMessages ) {
		this.pagingMessages = pagingMessages;
		return this;
	}

	/**
	 * Disables sorting.
	 */
	public SortableTableBuilder noSorting() {
		return sortableOn( Collections.emptyList() );
	}

	/**
	 * Enables sorting on all properties that have a {@link org.springframework.data.domain.Sort.Order} attribute.
	 *
	 * @return current builder
	 */
	public SortableTableBuilder defaultSorting() {
		this.sortableProperties = null;
		return this;
	}

	/**
	 * Limit the properties that can be sorted on by specifiying them explicitly.  If the collection
	 * is null then all properties that have a {@link org.springframework.data.domain.Sort.Order} attribute will
	 * be sortable.
	 *
	 * @param sortableProperties collection of property names that can be sorted on
	 */
	public SortableTableBuilder sortableOn( String... sortableProperties ) {
		return sortableOn( Arrays.asList( sortableProperties ) );
	}

	/**
	 * Limit the properties that can be sorted on by specifiying them explicitly.
	 *
	 * @param sortableProperties collection of property names that can be sorted on
	 */
	public SortableTableBuilder sortableOn( Collection<String> sortableProperties ) {
		this.sortableProperties = sortableProperties;
		return this;
	}

	protected Collection<ViewElementPostProcessor<TableViewElement.Row>> getHeaderRowProcessors() {
		return headerRowProcessors;
	}

	public SortableTableBuilder headerRowProcessors( Collection<ViewElementPostProcessor<TableViewElement.Row>> headerRowProcessors ) {
		this.headerRowProcessors.addAll( headerRowProcessors );
		return this;
	}

	protected Collection<ViewElementPostProcessor<TableViewElement.Row>> getValueRowProcessors() {
		return valueRowProcessors;
	}

	public SortableTableBuilder valueRowProcessors( Collection<ViewElementPostProcessor<TableViewElement.Row>> valueRowProcessors ) {
		this.valueRowProcessors.addAll( valueRowProcessors );
		return this;
	}

	/**
	 * @return actual {@link PagingMessages} to use - fetched from {@link EntityConfiguration} or use value set
	 */
	protected PagingMessages getResolvedPagingMessages() {
		PagingMessages actual = getPagingMessages();

		if ( actual == null ) {
			if ( resolvedPagingMessages == null ) {
				resolvedPagingMessages
						= new ListViewEntityMessages( getEntityConfiguration().getEntityMessageCodeResolver() );
			}
			actual = resolvedPagingMessages;
		}

		return actual;
	}

	protected Collection<EntityPropertyDescriptor> getResolvedPropertyDescriptors() {
		Collection<EntityPropertyDescriptor> actual = propertyDescriptors;

		if ( actual == null ) {
			if ( resolvedPropertyDescriptors == null ) {
				EntityPropertyRegistry registry = getPropertyRegistry() != null
						? getPropertyRegistry() : getEntityConfiguration().getPropertyRegistry();
				resolvedPropertyDescriptors = registry.select( propertySelector );
			}
			actual = resolvedPropertyDescriptors;
		}

		return actual;
	}

	protected String getResolvedEntityType() {
		return entityType != null
				? entityType : ( getEntityConfiguration() != null ? getEntityConfiguration().getName() : null );
	}

	/**
	 * Create a {@link ViewElement} containing the configured table.
	 *
	 * @param parentBuilderContext for element creation
	 * @return viewElement
	 */
	@Override
	public ContainerViewElement build( ViewElementBuilderContext parentBuilderContext ) {
		ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext( parentBuilderContext );

		if ( getEntityConfiguration() != null ) {
			// set the message code resolver of the specific entity type being rendered
			builderContext.setAttribute(
					EntityMessageCodeResolver.class,
					getEntityConfiguration().getEntityMessageCodeResolver()
			);
		}

		if ( page == null || !page.hasContent() ) {
			if ( noResultsElement != null ) {
				return bootstrapUi.container().add( noResultsElement.get( builderContext ) ).build( builderContext );
			}

			return bootstrapUi.container().add( createDefaultNoResultsPanel() ).build( builderContext );
		}

		TableViewElementBuilder table = createTable();

		return ( isTableOnly() ? table : createPanelForTable( table ) ).build( builderContext );
	}

	protected TableViewElementBuilder createTable() {
		TableViewElementBuilder table = bootstrapUi.table()
		                                           .name( ELEMENT_TABLE )
		                                           .responsive()
		                                           .style( getTableStyles() )
		                                           .attributes( createTableAttributes() );

		createTableHeader( table );
		createTableBody( table );

		return table;
	}

	private Map<String, Object> createTableAttributes() {
		Page currentPage = getPage();

		Map<String, Object> attributes = new HashMap<>();
		attributes.put( DATA_ATTR_TABLE_NAME, getTableName() );
		attributes.put( DATA_ATTR_TABLE_TYPE, "paged" );
		attributes.put( DATA_ATTR_ENTITY_TYPE, getResolvedEntityType() );
		attributes.put( DATA_ATTR_CURRENT_PAGE, currentPage.getNumber() );
		attributes.put( DATA_ATTR_PAGES, currentPage.getTotalPages() );
		attributes.put( DATA_ATTR_PAGE_SIZE, currentPage.getSize() );
		attributes.put( DATA_ATTR_SORT, convertSortAttribute( currentPage.getSort() ) );

		return attributes;
	}

	protected List<OrderPair> convertSortAttribute( Sort sort ) {
		if ( sort == null ) {
			return null;
		}

		List<OrderPair> orderPairs = new ArrayList<>();

		for ( Sort.Order order : sort ) {
			orderPairs.add( new OrderPair( order.getProperty(), order.getDirection().name() ) );
		}

		return orderPairs;
	}

	protected void createTableHeader( TableViewElementBuilder table ) {
		TableViewElementBuilder.Row headerRow = table.row();

		if ( isShowResultNumber() ) {
			headerRow.add(
					table.heading()
					     .attribute( "class", "result-number" )
					     .text( "#" )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getResolvedPropertyDescriptors() ) {
			TableViewElementBuilder.Cell heading = table.heading()
			                                            .attribute( DATA_ATTR_FIELD, descriptor.getName() )
			                                            .add( createLabel( descriptor ) );

			String sortsOn = determineSortableProperty( descriptor );

			if ( sortsOn != null ) {
				heading.attribute( "class", "sortable" )
				       .attribute( DATA_ATTR_SORT_PROPERTY, sortsOn )
				       .attribute( DATA_ATTR_TABLE_NAME, getTableName() );
			}

			headerRow.add( heading );
		}

		for ( ViewElementPostProcessor<TableViewElement.Row> postProcessor : getHeaderRowProcessors() ) {
			headerRow.postProcessor( postProcessor );
		}

		table.header().add( headerRow );
	}

	protected String determineSortableProperty( EntityPropertyDescriptor descriptor ) {
		if ( sortableProperties == null || sortableProperties.contains( descriptor.getName() ) ) {
			Sort.Order order = descriptor.getAttribute( Sort.Order.class );
			return order != null ? order.getProperty() : null;
		}

		return null;
	}

	protected void createTableBody( TableViewElementBuilder table ) {
		TableViewElementBuilder.Row valueRow = table.row()
		                                            .postProcessor( CSS_ODD_EVEN_ROW_PROCESSOR );

		if ( isShowResultNumber() ) {
			int startIndex = Math.max( 0, page.getNumber() ) * page.getSize();
			valueRow.add(
					table.cell()
					     .attribute( "class", "result-number" )
					     .add(
							     bootstrapUi.text().postProcessor( new ResultNumberProcessor( startIndex ) )
					     )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getResolvedPropertyDescriptors() ) {
			ViewElementBuilder valueBuilder = createValue( descriptor );

			TableViewElementBuilder.Cell cell = table.cell()
			                                         .attribute( DATA_ATTR_FIELD, descriptor.getName() );

			if ( valueBuilder != null ) {
				cell.add( valueBuilder );
			}

			valueRow.add( cell );
		}

		for ( ViewElementPostProcessor<TableViewElement.Row> postProcessor : getValueRowProcessors() ) {
			valueRow.postProcessor( postProcessor );
		}

		table.body()
		     .add(
				     bootstrapUi.generator( Object.class, TableViewElement.Row.class )
				                .itemBuilder( valueRow )
				                .items( page.getContent() )
		     );
	}

	protected ViewElementBuilder createLabel( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( descriptor, LIST_LABEL );
	}

	protected ViewElementBuilder createValue( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( descriptor, LIST_VALUE );
	}

	protected NodeViewElementBuilder createPanelForTable( TableViewElementBuilder tableBody ) {
		String resultsFound = getResolvedPagingMessages().resultsFound( getPage() );

		NodeViewElementBuilder panel = bootstrapUi.node( "div" )
		                                          .name( ELEMENT_PANEL )
		                                          .attribute( "class", "panel panel-default" )
		                                          .add(
				                                          bootstrapUi.node( "div" )
				                                                     .attribute( "class", "panel-heading" )
				                                                     .add( bootstrapUi.text( resultsFound ) )
		                                          )
		                                          .add(
				                                          bootstrapUi.node( "div" )
				                                                     .attribute( "class", "panel-body" )
				                                                     .add( tableBody )
		                                          );

		if ( page.getTotalPages() > 1 ) {
			panel.add(
					bootstrapUi.node( "div" )
					           .attribute( "class", "panel-footer" )
					           .add( createPager() )
			);
		}
		return panel;
	}

	protected ViewElementBuilder createDefaultNoResultsPanel() {
		return bootstrapUi.node( "div" )
		                  .name( ELEMENT_NORESULTS )
		                  .attribute( "class", "panel panel-warning" )
		                  .add(
				                  bootstrapUi.node( "div" )
				                             .attribute( "class", "panel-body" )
				                             .add( bootstrapUi.text( getResolvedPagingMessages()
						                                                     .resultsFound( getPage() ) ) )
		                  );
	}

	protected ViewElementBuilder createPager() {
		Page currentPage = getPage();
		PagingMessages messages = getResolvedPagingMessages();

		NodeViewElementBuilder pager = bootstrapUi.node( "div" )
		                                          .name( ELEMENT_PAGER )
		                                          .attribute( "class", "pager-form form-inline text-center" );

		if ( currentPage.hasPrevious() ) {
			pager.add(
					bootstrapUi.button()
					           .link( "#" )
					           .icon( new GlyphIcon( GlyphIcon.STEP_BACKWARD ) )
					           .title( messages.previousPage( currentPage ) )
					           .attribute( DATA_ATTR_PAGE, currentPage.getNumber() - 1 )
					           .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( bootstrapUi.node( "span" ).attribute( "class", "no-btn" ) );
		}

		pager.add(
				bootstrapUi.label()
				           .add( bootstrapUi.node( "span" ).add( bootstrapUi.text( messages.page( currentPage ) ) ) )
				           .add(
						           bootstrapUi.textbox()
						                      .attribute( "data-tbl-page-selector", "selector" )
						                      .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
						                      .text( String.valueOf( currentPage.getNumber() + 1 ) )
				           )
		)
		     .add( bootstrapUi.node( "span" ).add( bootstrapUi.text( messages.ofPages( currentPage ) ) ) )
		     .add(
				     bootstrapUi.node( "a" )
				                .attribute( "href", "#" )
				                .attribute( "class", "total-pages-link" )
				                .attribute( DATA_ATTR_PAGE, currentPage.getTotalPages() - 1 )
				                .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
				                .add( bootstrapUi.text( String.valueOf( currentPage.getTotalPages() ) ) )
		     );

		if ( currentPage.hasNext() ) {
			pager.add(
					bootstrapUi.button()
					           .link( "#" )
					           .icon( new GlyphIcon( GlyphIcon.STEP_FORWARD ) )
					           .title( messages.nextPage( currentPage ) )
					           .attribute( DATA_ATTR_PAGE, currentPage.getNumber() + 1 )
					           .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( bootstrapUi.node( "span" ).attribute( "class", "no-btn" ) );
		}

		return pager;
	}

	/**
	 * Simple class for JSON serializing sortable properties.
	 */
	public static class OrderPair
	{
		@JsonProperty(value = "prop")
		private String property;

		@JsonProperty(value = "dir")
		private String direction;

		public OrderPair( String property, String direction ) {
			this.property = property;
			this.direction = direction;
		}

		public String getProperty() {
			return property;
		}

		public String getDirection() {
			return direction;
		}
	}

	/**
	 * Sets the position of the item being processed (in an {@link IteratorViewElementBuilderContext})
	 * as the text of a {@link TextViewElement}.
	 */
	public static class ResultNumberProcessor implements ViewElementPostProcessor<TextViewElement>
	{
		private final int startIndex;

		public ResultNumberProcessor( int startIndex ) {
			this.startIndex = startIndex;
		}

		@Override
		public void postProcess( ViewElementBuilderContext builderContext, TextViewElement element ) {
			if ( builderContext instanceof IteratorViewElementBuilderContext ) {
				IteratorViewElementBuilderContext ctx = (IteratorViewElementBuilderContext) builderContext;
				element.setText( String.valueOf( ctx.getIndex() + 1 + startIndex ) );
			}
		}
	}
}
