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

package com.foreach.across.samples.bootstrapui.application.controllers;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.thymeleaf.SelectFormElementModelWriter;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generates Bootstrap based tabs from a {@link Menu} instance.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/select")
public class BootstrapSelectController
{
	/**
	 * Register the section in the administration menu.
	 */
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       //.group( "/test", "Functionality demos" ).and()
		       .group( "/test/form-elements", "Form elements" ).and()
		       .item( "/test/form-elements/select", "Select box", "/select" ).order( 1 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String renderSelects( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedSelects = new LinkedHashMap<>();
		generatedSelects.put( "Simple select", simpleSelect().build() );
		generatedSelects.put( "Multi select", multiSelect().build() );
		generatedSelects.put( "Simple select - bootstrap-select", simpleBootstrapSelect() );
		generatedSelects.put( "Multi select - bootstrap-select", multiBootstrapSelect() );

		model.addAttribute( "generatedElements", generatedSelects );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private SelectFormElement simpleBootstrapSelect() {
		return (SelectFormElement) simpleSelect().select( SelectFormElementConfiguration.simple() ).build();
	}

	private SelectFormElement multiBootstrapSelect() {
		return (SelectFormElement) multiSelect().select( SelectFormElementConfiguration.liveSearch() ).build();
	}

	private OptionsFormElementBuilder simpleSelect() {
		return BootstrapUiBuilders.options()
		                          .select()
		                          .controlName( "controlName" )
		                          .name( "internalName" )
		                          .readonly( false )
		                          .add( BootstrapUiBuilders.option().value( "one" ).text( "Inner text" ) )
		                          .add( BootstrapUiBuilders.option().value( "two" ).text( "Inner text 2" )
				);
	}

	private OptionsFormElementBuilder multiSelect() {
		SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
		group.setLabel( "Group label" );
		group.addChild(
				BootstrapUiBuilders.option()
				                   .value( "two" )
				                   .text( "Inner text 2" )
				                   .build()
		);
		group.addChild(
				BootstrapUiBuilders.option()
				                   .value( "Short two" )
				                   .text( "Some text" )
				                   .build()
		);

		return BootstrapUiBuilders.options()
		                          .select()
		                          .add( group )
		                          .multiple( true )
		                          .controlName( "controlName" )
		                          .name( "internalName" )
		                          .readonly( false );
	}
}
