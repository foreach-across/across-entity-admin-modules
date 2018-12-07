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

import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.ATTR_ICON;
import static com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder.ATTR_RENDER_AS_PANEL;

@Controller
@RequestMapping("/panelnav")
public class PanelNavController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/panelnav", "Panel nav", "/panelnav" ).order( 28 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String render( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple panel nav", simplePanelNav() );
		generatedElements.put( "Panel nav with group", panelNavWithGroupsAndIcons() );
		generatedElements.put( "Panel nav with styling", panelNavWithStyling() );
		generatedElements.put( "Panel nav group without panel", panelNavGroupNotAsPanel() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private NodeViewElement simplePanelNav() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "one" ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.item( "/two", "two" ).and()
				.build();

		menu.select( MenuSelector.byTitle( "Panel example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.panels( menu ).build();
	}

	private NodeViewElement panelNavWithGroupsAndIcons() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "one" )
				.attribute( ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) )
				.group( true ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.build();

		menu.select( MenuSelector.byTitle( "Panel example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.panels( menu ).build();
	}


	private NodeViewElement panelNavWithStyling() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "" )
				.group( true )
				.attribute( PanelsNavComponentBuilder.ATTR_PANEL_STYLE, "panel-danger" ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.build();

		menu.select( MenuSelector.byTitle( "Panel example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.panels( menu ).build();
	}
	
	private NodeViewElement panelNavGroupNotAsPanel() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "one" )
				.group( true )
				.attribute( ATTR_RENDER_AS_PANEL, false ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.build();

		menu.select( MenuSelector.byTitle( "Panel example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.panels( menu ).build();
	}

}
