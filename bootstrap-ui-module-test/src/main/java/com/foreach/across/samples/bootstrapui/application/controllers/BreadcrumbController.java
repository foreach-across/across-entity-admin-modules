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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
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

import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

@Controller
@RequestMapping("/breadcrumb")
public class BreadcrumbController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	public void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/breadcrumb", "Breadcrumb", "/breadcrumb" ).order( 24 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String render( Model model, ViewElementBuilderContext builderContext, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.addPackage( BootstrapUiFormElementsWebResources.NAME );

		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Simple breadcrumb nav", simpleBreadCrumbNav() );
		generatedElements.put( "Breadcrumb nav with icons", breadcrumbWithIcons() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private NodeViewElement simpleBreadCrumbNav() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "components", "Components" ).and()
				.item( "components/breadcrumb", "Breadcrumb example" ).and().build();

		menu.select( MenuSelector.byTitle( "Breadcrumb example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.breadcrumb( menu ).build();
	}

	private NodeViewElement breadcrumbWithIcons() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "components", "Components" )
				.attribute( NavComponentBuilder.ATTR_ICON, i( BootstrapStyles.css.fa.solid( "home" ) ) )
				.attribute( NavComponentBuilder.ATTR_ICON_ONLY, true ).and()
				.item( "components/breadcrumb", "Breadcrumb example" )
				.attribute( NavComponentBuilder.ATTR_ICON, i( BootstrapStyles.css.fa.solid( "hourglass" ) ) )
				.and().build();

		menu.select( MenuSelector.byTitle( "Breadcrumb example" ) );
		menu.setTitle( "Bootstrap Ui Module" );

		return BootstrapUiBuilders.breadcrumb( menu ).build();
	}

}
