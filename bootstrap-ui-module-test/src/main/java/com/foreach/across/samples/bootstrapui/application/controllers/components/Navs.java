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

import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.children;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.nav;

/**
 * Generates Bootstrap based tabs from a {@link Menu} instance.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/components/navs")
class Navs extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu
				//.group( "/test", "Functionality demos" ).and()
				.group( "/components/navs", "Navs" ).and()
				.item( "/components/navs/simple", "Default" ).and()
				.item( "/components/navs/tabs", "Tabs" ).and()
				.item( "/components/navs/pills", "Pills" ).and()
				.item( "/components/navs/pills-stacked", "Vertical layout" ).and()
				.item( "/components/navbar", "Navbar", "/components/navs/navbar" )
		;
	}

	/**
	 * Entry point that adds the different menus to the model.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{type}")
	String listMenus( @PathVariable String type, Model model, ViewElementBuilderContext builderContext ) {
		Map<String, Consumer<PathBasedMenuBuilder>> menusToGenerate = new LinkedHashMap<>();
		menusToGenerate.put( "Simple menu without dropdowns and no selected item", this::simpleMenu );
		menusToGenerate.put( "Simple menu without dropdowns and with a selected item", this::simpleMenuWithSelected );
		menusToGenerate.put( "Single level dropdown menu", this::singleLevelDropDownMenu );
		menusToGenerate.put( "Two level groups dropdown menu", this::twoLevelGroupsWithSeparators );
		menusToGenerate.put( "Selected item used as group name", this::twoLevelGroupsWithSelected );
		menusToGenerate.put( "Icon only menu items with custom HTML attributes", this::iconOnlyItems );

		return render(
				menusToGenerate.entrySet()
				               .stream()
				               .map( e -> {
					               Menu menu = buildMenu( e.getValue() );
					               return panel( e.getKey(), menuComponentBuilder( type, menu ) );
				               } )
				               .toArray()
		);
	}

	private void simpleMenu( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "One", "#" ).order( 2 ).and()
		    .item( "/two", "Two", "#" ).order( 1 ).and()
		    .item( "/three", "Three", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "trash" ) ).order( 3 );
	}

	private void simpleMenuWithSelected( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "download" ) ).order( 2 )
		    .and()
		    .item( "/two", "Two", "#" ).order( 1 ).and()
		    .item( "/three", "Three", "#" ).order( 3 ).and();
	}

	private void singleLevelDropDownMenu( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "One", "#" ).order( 1 ).and()
		    .group( "/two", "Two" ).order( 2 ).and()
		    .item( "/two/one", "Sub item 1", "#" ).and()
		    .item( "/two/two", "Sub item 2", "#" ).and()
		    .item( "/two/three", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "download" ) )
		    .and()
		    .group( "/three", "Three" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "exclamation" ) )
		    .order( 3 ).and()
		    .item( "/three/one", "Sub item 1", "#" ).order( 1 ).and()
		    .item( "/three/two", "Sub item 2", "#" ).order( 2 );
	}

	private void twoLevelGroupsWithSeparators( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "One", "#" ).order( 1 ).and()
		    .group( "/two", "Two" ).order( 2 ).and()
		    .item( "/two/one", "Item 1", "#" ).order( 1 ).and()
		    .item( "/two/two", "Item 2", "#" ).order( 2 ).and()
		    .group( "/two/three", "Sub group 1" ).order( 3 ).and()
		    .item( "/two/three/1", "Sub group item 1", "#" ).and()
		    .item( "/two/three/2", "Sub group item 2", "#" ).and()
		    .group( "/two/four", "Sub group 2" ).order( 4 ).and()
		    .item( "/two/four/1", "Sub group 2 item 1", "#" ).and()
		    .item( "/two/five", "Item 4", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "trash" ) )
		    .attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.AROUND )
		    .order( 5 )
		    .and()
		    .item( "/two/six", "Item 5", "#" ).order( 6 );
	}

	private void twoLevelGroupsWithSelected( PathBasedMenuBuilder menu ) {
		menu.root( "replaceGroup" ).and()
		    .item( "/one", "One", "#" ).order( 1 ).and()
		    .group( "/two", "Two" ).order( 2 ).and()
		    .item( "/two/one", "Item 1", "#" ).order( 1 ).and()
		    .item( "/two/two", "Item 2", "#" ).order( 2 ).and()
		    .group( "/two/three", "Sub group 1" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "home" ) )
		    .order( 3 )
		    .and()
		    .item( "/two/three/1", "Sub group item 1", "#" ).and()
		    .item( "/two/three/2", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "cog" ) )
		    .and()
		    .group( "/two/four", "Sub group 2" ).order( 4 ).and()
		    .item( "/two/four/1", "Sub group 2 item 1", "#" ).and()
		    .item( "/two/five", "Item 4", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "trash" ) )
		    .attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.AROUND )
		    .order( 5 )
		    .and()
		    .item( "/two/six", "Item 5", "#" ).order( 6 );
	}

	private void iconOnlyItems( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "One", "#" ).order( 1 ).and()
		    .group( "/advanced", "Advanced settings" )
		    .order( 2 )
		    .attribute( "html:class", "pull-right" )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .and()
		    .item( "/advanced/trash", "Move to trash", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "trash" ) )
		    .and()
		    .group( "/two", "Two" ).order( 3 )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "download" ) )
		    .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		    .attribute( "html:class", "pull-right" )
		    .and()
		    .item( "/two/one", "Item 1", "#" ).order( 1 ).and()
		    .item( "/two/two", "Item 2", "#" ).order( 2 ).and()
		    .group( "/two/three", "Sub group 1" ).order( 3 ).and()
		    .item( "/two/three/1", "Sub group item 1", "#" ).and()
		    .item( "/two/three/2", "Sub group item 2", "#" ).and()
		    .group( "/two/four", "Sub group 2" ).order( 4 ).and()
		    .item( "/two/four/1", "Sub group 2 item 1", "#" ).and()
		    .item( "/two/five", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, icon( "trash" ) )
		    .attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.AROUND )
		    .order( 5 )
		    .and()
		    .item( "/two/six", "Item 5", "#" ).order( 6 );
	}

	private NodeViewElement icon( String name ) {
		return i( css.fa.solid( name ) );
	}

	private ViewElementBuilder<NodeViewElement> menuComponentBuilder( String type, Menu menu ) {
		DefaultNavComponentBuilder menuBuilder = BootstrapUiBuilders.nav( menu );

		switch ( type ) {
			case "navbar":
				menuBuilder.navbar();
				break;
			case "tabs":
				menuBuilder.tabs();
				break;
			case "pills":
				menuBuilder.pills();
				break;
			case "pills-stacked":
				menuBuilder.stacked();
				break;
			default:
				menuBuilder.simple();
				break;
		}

		if ( "replaceGroup".equals( menu.getRoot().getPath() ) ) {
			menuBuilder.replaceGroupBySelectedItem();
		}

		if ( "navbar".equals( type ) ) {
			return menuBuilder.map( items -> nav( css.navbar, css.navbar.expand.onLargeAndUp(), css.navbar.dark, css.background.dark, children( items ) ) );
		}

		return menuBuilder;
	}

	private Menu buildMenu( Consumer<PathBasedMenuBuilder> builderConsumer ) {
		PathBasedMenuBuilder builder = new PathBasedMenuBuilder();
		builderConsumer.accept( builder );
		Menu menu = builder.build();
		menu.sort();
		menu.select( MenuSelector.byTitle( "Selected" ) );
		return menu;
	}
}
