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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.samples.entity.application.business.Partner;
import com.foreach.across.samples.entity.application.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates some tables using the {@link SortableTableBuilder} and includes the client-side support
 * for paging and sorting.
 *
 * @author Arne Vandamme
 * @see SortableTableSimpleController
 * @since 2.0.0
 */
@AdminWebController
@RequestMapping("/sortableTableWithPaging")
public class SortableTableWithPagingController
{
	private PartnerRepository partnerRepository;
	private EntityViewElementBuilderHelper builderHelper;

	@Autowired
	public SortableTableWithPagingController( PartnerRepository partnerRepository,
	                                          EntityViewElementBuilderHelper builderHelper ) {
		this.partnerRepository = partnerRepository;
		this.builderHelper = builderHelper;
	}

	/**
	 * Registers the CSS/Javascript packed with EntityModule to enable paging and sorting support
	 * on all tables on the page.
	 */
	@ModelAttribute
	public void registerWebResources( WebResourceRegistry registry ) {
		registry.addPackage( EntityModuleWebResources.NAME );
	}

	/**
	 * Entry point that adds the different tables to the model.
	 * Fetches the page of partner entities we wish to render, using a page size of 2.
	 * This method does not much more but dispatch to the specific table creation methods.
	 * See the separate methods for more documentation.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String renderPageTablesWithPageSizeOf2(
			@PageableDefault(size = 2) Pageable pageable,
			Model model,
			ViewElementBuilderContext builderContext
	) {
		Page<Partner> partners = partnerRepository.findAll( pageable );

		model.addAttribute(
				"message",
				"These tables fully support the default sorting and paging coming with the EntityModule."
		);

		Map<String, ViewElement> generatedTables = new LinkedHashMap<>();
		generatedTables.put(
				"Table with checkbox and selected values",
				tableWithCheckbox( partners, builderContext )
		);
		generatedTables.put(
				"Table with specific sortable properties",
				tableWithSpecificSortableProperties( partners, builderContext )
		);

		model.addAttribute( "generatedTables", generatedTables );

		return "th/entityModuleTest/sortableTables";
	}

	/**
	 * [1]
	 * Generates a table with the surrounding panel where both the sort headers and the pager buttons work.
	 * Also adds a checkbox column to the table, with some selected values.
	 */
	private ViewElement tableWithCheckbox( Page<Partner> partners,
	                                       ViewElementBuilderContext builderContext ) {
		// selected partners that should be checked
		Collection<Partner> selectedPartners
				= Arrays.asList( partnerRepository.findById( -1L ).orElse( null ), partnerRepository.findById( -3L ).orElse( null ) );

		return builderHelper.createSortableTableBuilder( Partner.class )
		                    .items( partners )
		                    .properties( "*" )
		                    .hideResultNumber()
		                    .headerRowProcessor( ( builderCtx, row ) -> {
			                    TableViewElement.Cell cell = new TableViewElement.Cell();
			                    cell.setHeading( true );
			                    cell.addChild(
					                    // the checkbox should be unwrapped in order to render correctly
					                    BootstrapUiBuilders.checkbox()
					                                       .unwrapped()
					                                       .htmlId( "select-all-partners" )
					                                       .build( builderCtx )
			                    );
			                    row.addFirstChild( cell );
		                    } )
		                    .valueRowProcessor( ( builderCtx, row ) -> {
			                    Partner partner = EntityViewElementUtils.currentEntity( builderCtx, Partner.class );

			                    TableViewElement.Cell cell = new TableViewElement.Cell();
			                    cell.addChild(
					                    BootstrapUiBuilders.checkbox()
					                                       .unwrapped()
					                                       .controlName( "partners" )
					                                       .selected( selectedPartners.contains( partner ) )
					                                       .value( partner.getId() )
					                                       .build( builderCtx )
			                    );
			                    row.addFirstChild( cell );
		                    } )
		                    .build( builderContext );
	}

	/**
	 * [2]
	 * Generates the table where only one property can be sorted on.
	 * Note that this table follows the paging and sorting settings of the first table,
	 * but only the single column can be sorted on in the table itself.
	 */
	private ViewElement tableWithSpecificSortableProperties( Page<Partner> partners,
	                                                         ViewElementBuilderContext builderContext ) {
		return builderHelper.createSortableTableBuilder( Partner.class )
		                    .items( partners )
		                    .properties( "name", "url" )
		                    .sortableOn( "name" )           // only allow sorting on the name property
		                    .tableOnly()
		                    .build( builderContext );
	}
}
