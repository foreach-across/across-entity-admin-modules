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
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.samples.entity.application.repositories.PartnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.children;

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
@RequestMapping("/viewElementFieldset")
public class ViewElementFieldsetController
{
	private PartnerRepository partnerRepository;
	private EntityViewElementBuilderHelper builderHelper;

	@Autowired
	public ViewElementFieldsetController( PartnerRepository partnerRepository,
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
		         .item( "/test/viewElementFieldset", "Fieldsets", "/viewElementFieldset" )
		         .order( 3 );
	}

	/**
	 * Entry point that adds the different tables to the model.
	 * This method does not much more but dispatch to the specific table creation methods.
	 * See the separate methods for more documentation.
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String listSortableTables( Model model, ViewElementBuilderContext builderContext ) {
		Map<String, ViewElement> generatedFieldsets = new LinkedHashMap<>();
		generatedFieldsets.put(
				"Panel-info fieldset",
				ViewElementFieldset.TEMPLATE_PANEL_INFO.apply( fieldset() )
		);
		generatedFieldsets.put(
				"Panel-success fieldset",
				ViewElementFieldset.TEMPLATE_PANEL_SUCCESS.apply( fieldset() )
		);
		generatedFieldsets.put(
				"Panel-warning fieldset",
				ViewElementFieldset.TEMPLATE_PANEL_WARNING.apply( fieldset() )
		);
		generatedFieldsets.put(
				"Panel-danger fieldset",
				ViewElementFieldset.TEMPLATE_PANEL_DANGER.apply( fieldset() )
		);
		generatedFieldsets.put(
				"default fieldset",
				ViewElementFieldset.TEMPLATE_FIELDSET.apply( fieldset() )
		);
		generatedFieldsets.put(
				"Section with heading",
				ViewElementFieldset.TEMPLATE_SECTION_H1.apply( fieldset() )
		);
		generatedFieldsets.put(
				"Body only",
				ViewElementFieldset.TEMPLATE_BODY_ONLY.apply( fieldset() )
		);

		model.addAttribute( "fieldsets", generatedFieldsets );

		return "th/entityModuleTest/viewElementFieldsets";
	}

	private ViewElementFieldset fieldset() {
		ViewElementFieldset fieldset = new ViewElementFieldset();
		fieldset.getTitle().addChild( HtmlViewElements.text( "Fieldset title" ) );
		fieldset.getFooter().addChild( HtmlViewElements.text( "Fieldset footer" ) );
		fieldset.getBody().set( children( formGroup( label( "Field 1" ), textbox() ).build() ) );
		return fieldset;
	}

}
