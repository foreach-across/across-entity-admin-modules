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

package com.foreach.across.samples.entity.application.controllers;

import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.conditionals.ConditionalOnBootstrapUI;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.samples.entity.application.business.Partner;
import com.foreach.across.samples.entity.application.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.BootstrapUiModuleIcons.ICON_SET_FONT_AWESOME_SOLID;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;

/**
 * Generates some tables using the {@link SortableTableBuilder}.  Does not include the default javascript
 * for activating client-side support of paging and sorting.  The different tables demonstrate some of the
 * features of the {@link SortableTableBuilder}.
 * <p/>
 * These demo pages expect a Partner entity with a *name* and *url* property.  At least 3 partner entities
 * should exist (with id -1, -2, -3) for this controller to work.
 *
 * @author Arne Vandamme
 * @see SortableTableWithPagingController
 * @since 2.0.0
 */
@AdminWebController
@ConditionalOnBootstrapUI
@RequestMapping("/sortableTable")
public class SortableTableSimpleController
{
	private PartnerRepository partnerRepository;
	private EntityViewElementBuilderHelper builderHelper;

	@Autowired
	public SortableTableSimpleController( PartnerRepository partnerRepository,
	                                      EntityViewElementBuilderHelper builderHelper ) {
		this.partnerRepository = partnerRepository;
		this.builderHelper = builderHelper;
	}

	/**
	 * Register the section in the administration menu.
	 */
	@EventListener
	@SuppressWarnings("unused")
	protected void registerMenuItems( AdminMenuEvent adminMenu ) {
		adminMenu.builder()
		         .group( "/test", "Functionality demos" ).and()
		         .group( "/test/st", "Sortable tables" ).and()
		         .item( "/test/st/noPaging", "Paging/sorting disabled", "/sortableTable" ).order( 1 )
		         .and()
		         .item( "/test/st/paging", "Paging/sorting enabled", "/sortableTableWithPaging" )
		         .order( 2 );
	}

	/**
	 * Entry point that adds the different tables to the model.
	 * This method does not much more but dispatch to the specific table creation methods.
	 * See the separate methods for more documentation.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String listSortableTables( Model model, ViewElementBuilderContext builderContext ) {
		List<Partner> partners = partnerRepository.findAll();

		model.addAttribute(
				"message",
				"These tables do not support paging or sorting because the default javascript is not included."
		);

		Map<String, ViewElement> generatedTables = new LinkedHashMap<>();
		generatedTables.put(
				"Minimal table configuration - listing all properties",
				minimalTableOfAllProperties( partners, builderContext )
		);
		generatedTables.put(
				"Table without any results",
				noResultsTable( builderContext )
		);
		generatedTables.put(
				"Table with specific properties and custom styling",
				styledTableWithSelectedProperties( partners, builderContext )
		);
		generatedTables.put(
				"Table with footer and styling added after being generated",
				postProcessedTable( partners, builderContext )
		);
		generatedTables.put(
				"Table with additional cell",
				tableWithCustomCell( partners, builderContext )
		);

		model.addAttribute( "generatedTables", generatedTables );

		return "th/entityModuleTest/sortableTables";
	}

	/**
	 * [1]
	 * Generates a table with only the minimum number of properties of SortableTableBuilder set.
	 * All builders are pre-configured using the EntityConfiguration of our Partner entity.
	 * This is done in the EntityViewElementBuilderHelper class.
	 */
	private ViewElement minimalTableOfAllProperties( List<Partner> partners,
	                                                 ViewElementBuilderContext builderContext ) {
		return builderHelper.createSortableTableBuilder( Partner.class )
		                    .items( partners )              // rows in the table
		                    .properties( "*" )              // columns - render all default properties of Partner
		                    .build( builderContext );       // build the actual TableViewElement (and surrounding containers)
	}

	/**
	 * [2]
	 * Generates a table that does not have any results, because we do not add any items.
	 * The default noResults panel will be generated.
	 */
	private ViewElement noResultsTable( ViewElementBuilderContext builderContext ) {
		return builderHelper.createSortableTableBuilder( Partner.class )
		                    .properties( "*" )
		                    .build( builderContext );
	}

	/**
	 * [3]
	 * Customizes the look and feel of the table using the methods available on the builder.
	 * These cover most of the common use cases like rendering only the table, disabling sorting etc.
	 */
	private ViewElement styledTableWithSelectedProperties( List<Partner> partners,
	                                                       ViewElementBuilderContext builderContext ) {
		return builderHelper.createSortableTableBuilder( Partner.class )
		                    .items( partners )
		                    .properties( "id", "url", "name" )      // specify the properties in order
		                    .tableStyles( css.table.bordered, css.table.small )     // add some bootstrap table styles
		                    .tableOnly()                            // only render the table - not the surrounding panel with paging/results information
		                    .noSorting()                            // disable sorting on all properties
		                    .hideResultNumber()                     // hide the result number column
		                    .build( builderContext );
	}

	/**
	 * [4]
	 * Generate the default table but make some changes to the ViewElement after it has been built.
	 * Not necessarily the easiest or even best way to achieve what you want, but it can be done.
	 * This code demonstrates making view elements directly, skipping the BootstrapUiFactory.
	 * <p>
	 * IMPORTANT: SortableTableBuilder uses a ViewElementGenerator for the building of the rows.  That means
	 * the rows itself are actually only built when being rendered.  That means it is not possible to actually
	 * modify the generated rows individually.
	 */
	private ViewElement postProcessedTable( List<Partner> partners, ViewElementBuilderContext builderContext ) {
		ContainerViewElement container = builderHelper.createSortableTableBuilder( Partner.class )
		                                              .items( partners )
		                                              .properties( "*" )
		                                              .build( builderContext );

		// after the table has been generated we receive a container of elements (including the panel),
		// find the actual table element and make some modifications
		find( container, SortableTableBuilder.ELEMENT_TABLE, TableViewElement.class )
				.ifPresent( table -> {
					            // set custom style attribute
					            table.setAttribute( "style", "border: solid 3px red;" );

					            // manually create ViewElement instances
					            TableViewElement.Footer footer = new TableViewElement.Footer();
					            TableViewElement.Row footerRow = new TableViewElement.Row();
					            TableViewElement.Cell footerCell = new TableViewElement.Cell();
					            footerCell.setColumnSpan( 3 );
					            footerCell.addChild( new TextViewElement( "Manually added footer row." ) );
					            footerRow.addChild( footerCell );
					            footer.addChild( footerRow );

					            table.setFooter( footer );
				            }
				);

		return container;
	}

	/**
	 * [5]
	 * Customize the table by adding a column with a link that opens the partner url in a new window.
	 * Illustrates how you can access the entity for which the row is being generated.
	 */
	private ViewElement tableWithCustomCell( List<Partner> partners,
	                                         ViewElementBuilderContext builderContext ) {
		return builderHelper
				.createSortableTableBuilder( Partner.class )
				.items( partners )
				.properties( "*" )
				.headerRowProcessor( ( ctx, element ) -> {
					// add cell to the header
					element.addChild( bootstrap.builders.table().heading().build( ctx ) );
				} )
				.valueRowProcessor( ( ctx, element ) -> {
					Partner partner = EntityViewElementUtils.currentEntity( ctx, Partner.class );

					// add cell linking to the url of the partner
					element.addChild(
							bootstrap.builders.table().cell().add(
									bootstrap.builders.button()
									                  .link( partner.getUrl() )
									                  .icon( iconSet( ICON_SET_FONT_AWESOME_SOLID ).icon( "new-window" ) )
									                  .iconOnly()
									                  .attribute( "target", "_blank" )
									                  .text( "Visit partner website" )
							).build( ctx )
					);
				} )
				.build( builderContext );
	}
}
