/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.samples.bootstrapui.application.controllers.components;

import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.ATTR_ICON;
import static com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder.ATTR_RENDER_AS_PANEL;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

@Controller
@RequestMapping("/components/navs/panel")
class PanelNav extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/navs/panel", "Panel layout" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String render() {
		return render(
				panel( "Items without group", simplePanelNav() ),
				panel( "Single group", panelNavWithGroupsAndIcons() ),
				panel( "Custom styling (TODO)", panelNavWithStyling() ),
				panel("Multiple groups", panelWithMultipleGroups())
				//panel( "Panel nav group without panel", panelNavGroupNotAsPanel() )
		);
	}

	private NodeViewElement panelWithMultipleGroups() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "Item 1" ).order( 1 ).and()
				.item("/two", "Item 2").order( 2 ).and()
				.group( "/group1", "Group 1" ).order( 3 ).and()
				.item( "/group1/one", "Group 1 item 1" ).and()
				.item( "/group1/two", "Group 1 item 2" ).and()
				.group( "/group1/subgroup1", "Sub-group 1" ).and()
				.item( "/group1/subgroup1/one", "Sub-group 1 item 1" ).and()
				.item( "/group1/subgroup1/two", "Sub-group 1 item 2" ).and()
				.item( "/group1/subgroup1/three", "Sub-group 1 item 3" ).and()
				.item( "/group1/subgroup1/subgroup1", "Sub-group 1 sub-group 1" ).and()
				.item( "/group1/subgroup1/subgroup1/one", "Sub-group 1-1 item 1" ).and()
				.item("/three", "Item 3").order( 6 ).and()
				.item("/four", "Item 4").order( 7 ).and()
				.build();

		menu.sort();
		menu.select( MenuSelector.byPath( "/group1/subgroup1/three" ) );

		return BootstrapUiBuildersBroken.panels( menu ).build();
	}

	private NodeViewElement simplePanelNav() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "one" ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.item( "/two", "two" ).and()
				.item( "/three", "three" ).and()
				.build();

		menu.select( MenuSelector.byPath( "/two" ) );

		return BootstrapUiBuildersBroken.panels( menu ).build();
	}

	private NodeViewElement panelNavWithGroupsAndIcons() {
		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "one" )
				.attribute( ATTR_ICON, html.i( BootstrapStyles.css.fa.brands( "apple" ) ) )
				.group( true ).and()
				.item( "/one/sub", "sub one" ).and()
				.item( "/one/sub2", "sub one 2" ).and()
				.build();

		menu.select( MenuSelector.byPath( "/one/sub2" ) );

		return BootstrapUiBuildersBroken.panels( menu ).build();
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

		return BootstrapUiBuildersBroken.panels( menu ).build();
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

		return BootstrapUiBuildersBroken.panels( menu ).build();
	}

}
