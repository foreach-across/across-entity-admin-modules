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
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Arne Vandamme
 * @since 1.2.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/textbox")
public class BoostrapTextboxController
{
	/**
	 * Register the section in the administration menu.
	 */
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/textbox", "Textbox", "/textbox" ).order( 2 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String renderTextboxes( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Textbox without auto-size", simpleTextbox() );
		generatedElements.put( "Textarea with auto-size", textarea() );
		generatedElements.put( "Textbox with auto-size", autoSizingTextbox() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private TextboxFormElement simpleTextbox() {
		return BootstrapUiBuilders.textbox()
		                          .placeholder( "Placeholder text..." )
		                          .build();
	}

	private TextboxFormElement textarea() {
		return BootstrapUiBuilders.textarea()
		                          .placeholder( "Placeholder text..." )
		                          .autoSize()
		                          .build();
	}

	private TextboxFormElement autoSizingTextbox() {
		return BootstrapUiBuilders.textbox()
		                          .placeholder( "Placeholder text..." )
		                          .autoSize()
		                          .build();
	}
}
