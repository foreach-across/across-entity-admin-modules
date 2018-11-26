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
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
@RequestMapping("/options")
public class OptionsController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/options", "Options", "/options" ).order( 20 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String renderIcon( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Options", simpleOptionsBuilder() );
		generatedElements.put( "BootstrapSelect", bootstrapSelectBuilder() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private AbstractNodeViewElement simpleOptionsBuilder() {
		return BootstrapUiBuilders
				.options()
				.controlName( "demo" )
				.add( BootstrapUiBuilders.option().label( "Orange" ).value( "orange" ) )
				.add( BootstrapUiBuilders.option().label( "Apple" ).value( "apple" ) )
				.build();
	}

	private AbstractNodeViewElement bootstrapSelectBuilder() {
		SelectFormElementConfiguration configuration = new SelectFormElementConfiguration();
		configuration.setDeselectAllText( null );
		configuration.setSelectAllText( null );
		configuration.setCountSelectedText( null );
		configuration.setMaxOptionsText( null );
		configuration.setNoneSelectedText( null );

		return BootstrapUiBuilders
				.options()
				.select( configuration )
				.multiple()
				.controlName( "boxName" )
				.add( BootstrapUiBuilders.option().label( "Orange" ).value( "orange" ) )
				.add( BootstrapUiBuilders.option().label( "Apple" ).value( "apple" ) )
				.build();
	}
}
