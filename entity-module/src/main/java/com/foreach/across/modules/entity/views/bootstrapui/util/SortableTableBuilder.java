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
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.entity.config.icons.EntityModuleIcons.entityModuleIcons;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Helper that aids in building a sortable {@link com.foreach.across.modules.bootstrapui.elements.TableViewElement}
 * for a list of {@link EntityPropertyDescriptor}s.
 *
 * @author Arne Vandamme
 */
@ConditionalOnBootstrapUI
@Component
@Scope("prototype")
public class SortableTableBuilder implements ViewElementBuilder<ContainerViewElement>
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

	public static final String ELEMENT_TABLE = "table";
	public static final String ELEMENT_PANEL = "panel";
	public static final String ELEMENT_PANEL_HEADING = "panel-heading";
	public static final String ELEMENT_PANEL_BODY = "panel-body";
	public static final String ELEMENT_PANEL_FOOTER = "panel-footer";
	public static final String ELEMENT_NORESULTS = "noresults";
	public static final String ELEMENT_PAGER = "pager";

	public static final String DATA_ATTR_FIELD = "data-tbl-field";
	public static final String DATA_ATTR_TABLE_NAME = "data-tbl";
	public static final String DATA_ATTR_TABLE_TYPE = "data-tbl-type";
	public static final String DATA_ATTR_ENTITY_TYPE = "data-tbl-entity-type";
	public static final String DATA_ATTR_CURRENT_PAGE = "data-tbl-current-page";
	public static final String DATA_ATTR_PAGE = "data-tbl-page";
	public static final String DATA_ATTR_PAGES = "data-tbl-total-pages";
	public static final String DATA_ATTR_PAGE_SIZE = "data-tbl-size";
	public static final String DATA_ATTR_SORT = "data-tbl-sort";
	public static final String DATA_ATTR_SORT_PROPERTY = "data-tbl-sort-property";
	public static final String DATA_ATTR_FORM = "data-tbl-form";
	private static final String DATA_ATTR_AJAX_LOAD = "data-tbl-ajax-load";

	protected final EntityViewElementBuilderService viewElementBuilderService;

	private String tableName = "sortableTable";
	private String entityType;
	private EntityConfiguration entityConfiguration;
	private EntityPropertyRegistry entityPropertyRegistry;
	private EntityPropertySelector propertySelector;
	private Collection<String> sortableProperties;
	private Collection<EntityPropertyDescriptor> propertyDescriptors;
	private boolean tableOnly, showResultNumber = true;
	private Page<Object> page = new PageImpl<>( Collections.emptyList() );
	private BootstrapStyleRule[] tableStyles = new BootstrapStyleRule[] { css.table.hover };
	private PagingMessages pagingMessages;
	private ViewElementBuilderSupport.ElementOrBuilder noResultsElement;
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> headerRowProcessors = new ArrayList<>();
	private Collection<ViewElementPostProcessor<TableViewElement.Row>> valueRowProcessors = new ArrayList<>();
	private String formName;

	private ViewElementMode valueViewElementMode = ViewElementMode.LIST_VALUE;
	private ViewElementMode labelViewElementMode = ViewElementMode.LIST_LABEL;

	private PagingMessages resolvedPagingMessages;
	private Collection<EntityPropertyDescriptor> resolvedPropertyDescriptors;

	@Autowired
	public SortableTableBuilder( EntityViewElementBuilderService viewElementBuilderService ) {
		this.viewElementBuilderService = viewElementBuilderService;
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

	/**
	 * Set the rendering mode for the values of an entity.  Defaults to {@link ViewElementMode#LIST_VALUE}.
	 *
	 * @param valueViewElementMode element mode
	 * @return current builder
	 */
	public SortableTableBuilder setValueViewElementMode( ViewElementMode valueViewElementMode ) {
		this.valueViewElementMode = valueViewElementMode;
		return this;
	}

	/**
	 * Set the rendering mode for the label of an entity (header row).  Defaults to {@link ViewElementMode#LIST_LABEL}.
	 *
	 * @param labelViewElementMode element mode
	 * @return current builder
	 */
	public SortableTableBuilder setLabelViewElementMode( ViewElementMode labelViewElementMode ) {
		this.labelViewElementMode = labelViewElementMode;
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
	 * Set the table name that will be added as data-attribute on the table element, and will be the internal name of the main container ViewElement.
	 *
	 * @param tableName to be used both internally and as data attribute on the resulting html table
	 */
	public SortableTableBuilder tableName( String tableName ) {
		this.tableName = tableName;
		return this;
	}

	/**
	 * Optionally set the form name that this sortable table is bound to.  When changing page the form
	 * will be updated and submitted instead of the url of the page itself.
	 *
	 * @param formName name of the form element
	 */
	public SortableTableBuilder formName( String formName ) {
		this.formName = formName;
		return this;
	}

	protected String getFormName() {
		return formName;
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

	protected BootstrapStyleRule[] getTableStyles() {
		return tableStyles;
	}

	/**
	 * @param tableStyles that should be applied to the generated table
	 */
	public SortableTableBuilder tableStyles( BootstrapStyleRule... tableStyles ) {
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
						= new EntityMessages( getEntityConfiguration().getEntityMessageCodeResolver() );
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
		EntityViewElementUtils.setCurrentEntity( builderContext, null );

		if ( getEntityConfiguration() != null ) {
			// todo: setting the message code this way is not optimal - find better approach
			EntityViewRequest viewRequest = builderContext.getAttribute( EntityViewRequest.class );
			boolean replaceMessageCodeResolver
					= viewRequest == null
					|| !builderContext.hasAttribute( EntityMessageCodeResolver.class )
					|| !Objects.equals( getEntityConfiguration(), viewRequest.getEntityViewContext().getEntityConfiguration() );

			if ( replaceMessageCodeResolver ) {
				// set the message code resolver of the specific entity type being rendered
				builderContext.setAttribute(
						EntityMessageCodeResolver.class,
						getEntityConfiguration().getEntityMessageCodeResolver()
				);
			}
		}

		if ( page == null ) {
			throw new IllegalArgumentException( "Items or Page must be set on SortableTableBuilder" );
		}

		if ( !page.hasContent() ) {
			if ( noResultsElement != null ) {
				return html.builders.container().name( getTableName() ).add( noResultsElement.get( builderContext ) ).build( builderContext );
			}

			return html.builders.container().name( getTableName() ).add( createDefaultNoResultsPanel() ).build( builderContext );
		}

		TableViewElementBuilder table = createTable();

		return ( isTableOnly() ? table.name( getTableName() ) : createPanelForTable( table ).name( getTableName() ) )
				.build( builderContext );
	}

	protected TableViewElementBuilder createTable() {
		// TODO by rending a BootstrapUi builder two 'table' classes will be present on the table element, see TableViewElementModelBuilder
		TableViewElementBuilder table = bootstrap.builders.table()
		                                                  .css( "em-sortableTable-table" )
		                                                   .name( elementName( ELEMENT_TABLE ) )
		                                                  .responsive()
		                                                  .with( getTableStyles() )
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
		//attributes.put( DATA_ATTR_AJAX_LOAD, false );

		if ( getFormName() != null ) {
			attributes.put( DATA_ATTR_FORM, getFormName() );
		}

		return attributes;
	}

	protected List<OrderPair> convertSortAttribute( Sort sort ) {
		if ( sort == null || sort.isUnsorted() ) {
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
					     .css( "result-number" )
					     .text( "#" )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getResolvedPropertyDescriptors() ) {
			TableViewElementBuilder.Cell heading = table.heading()
			                                            .name( descriptor.getName() )
			                                            .attribute( DATA_ATTR_FIELD, descriptor.getName() )
			                                            .add( createLabel( descriptor ) );

			String sortsOn = determineSortableProperty( descriptor );

			if ( sortsOn != null ) {
				heading.css( "sortable" )
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
					     .with( css.align.middle )
					     .css( "result-number" )
					     .add(
							     html.builders.text( "" ).postProcessor( new ResultNumberProcessor( startIndex ) )
					     )
			);
		}

		for ( EntityPropertyDescriptor descriptor : getResolvedPropertyDescriptors() ) {
			ViewElementBuilder valueBuilder = createValue( descriptor );

			TableViewElementBuilder.Cell cell = table.cell()
			                                         .with( css.align.middle )
			                                         .name( descriptor.getName() )
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
				     bootstrap.builders.generator( Object.class, TableViewElement.Row.class )
				                       .itemBuilder( valueRow )
				                       .items( page.getContent() )
		     );
	}

	protected ViewElementBuilder createLabel( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( descriptor, labelViewElementMode );
	}

	protected ViewElementBuilder createValue( EntityPropertyDescriptor descriptor ) {
		return viewElementBuilderService.getElementBuilder( descriptor, valueViewElementMode );
	}

	protected NodeViewElementBuilder createPanelForTable( TableViewElementBuilder tableBody ) {
		String resultsFound = getResolvedPagingMessages().resultsFound( getPage() );

		NodeViewElementBuilder panel = html.builders.div()
		                                            .name( elementName( ELEMENT_PANEL ) )
		                                            .with( css.card, HtmlViewElement.Functions.css( "em-sortableTable-panel" ) )
		                                            .add(
				                                            html.builders.div()
				                                                         .name( elementName( ELEMENT_PANEL_HEADING ) )
				                                                         .with( css.card.header )
				                                                         .add( html.builders.unescapedText( resultsFound ) )
		                                            )
		                                            .add(
				                                            html.builders.div()
				                                                         .name( elementName( ELEMENT_PANEL_BODY ) )
				                                                         .with( css.card.body )
				                                                         .add( tableBody )
		                                            );

		if ( page.getTotalPages() > 1 ) {
			panel.add(
					html.builders.div()
					             .name( elementName( ELEMENT_PANEL_FOOTER ) )
					             .with( css.card.footer )
					             .add( createPager() )
			);
		}
		return panel;
	}

	protected ViewElementBuilder createDefaultNoResultsPanel() {
		return html.builders.div()
		                    .name( elementName( ELEMENT_NORESULTS ) )
		                    //.attribute( DATA_ATTR_AJAX_LOAD, false )
		                    .with( css.card, css.border.warning )
		                    .add(
				                    html.builders.div()
				                                 .with( css.card.body, css.text.warning )
				                                 .add( html.builders.unescapedText( getResolvedPagingMessages().resultsFound( getPage() ) ) )
		                    );
	}

	protected ViewElementBuilder createPager() {
		Page currentPage = getPage();
		PagingMessages messages = getResolvedPagingMessages();

		NodeViewElementBuilder pager = html.builders.div()
		                                            .name( elementName( ELEMENT_PAGER ) )
		                                            .css( "pager-form", "form-inline" )
		                                            .with( css.flex.row, css.justifyContent.center );

		if ( currentPage.hasPrevious() ) {
			pager.add(
					bootstrap.builders.button()
					                  .link( "#" )
					                  .icon( entityModuleIcons.listView.previousPage() )
					                  .title( messages.previousPage( currentPage ) )
					                  .attribute( DATA_ATTR_PAGE, currentPage.getNumber() - 1 )
					                  .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( html.span().addCssClass( "no-btn" ) );
		}

		pager.add(
				html.builders.label()
				             .add( html.builders.span().with( css.margin.right.s2 )
				                                .add( html.builders.unescapedText( messages.page( currentPage ) ) ) )
				             .add(
						             bootstrap.builders.textbox()
						                               .attribute( "data-tbl-page-selector", "selector" )
						                               .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
						                               .text( String.valueOf( currentPage.getNumber() + 1 ) )
						                               .with( css.margin.right.s2, css.padding.s2 )
				             )
		)
		     .add( html.builders.span().with( css.margin.right.s2 ).add( html.builders.unescapedText( messages.ofPages( currentPage ) ) ) )
		     .add(
				     bootstrap.builders.link()
				                       .url( "#" )
				                       .css( "total-pages-link" )
				                       .attribute( DATA_ATTR_PAGE, currentPage.getTotalPages() - 1 )
				                       .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
				                       .add( html.builders.unescapedText( String.valueOf( currentPage.getTotalPages() ) ) )
		     );

		if ( currentPage.hasNext() ) {
			pager.add(
					bootstrap.builders.button()
					                  .link( "#" )
					                  .icon( entityModuleIcons.listView.nextPage() )
					                  .title( messages.nextPage( currentPage ) )
					                  .attribute( DATA_ATTR_PAGE, currentPage.getNumber() + 1 )
					                  .attribute( DATA_ATTR_TABLE_NAME, getTableName() )
			);
		}
		else {
			pager.add( html.builders.span().css( "no-btn" ) );
		}

		return pager;
	}

	/**
	 * @param name base name
	 * @return tableName prefixed element name
	 */
	private String elementName( String name ) {
		return getTableName() != null ? getTableName() + "-" + name : name;
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
