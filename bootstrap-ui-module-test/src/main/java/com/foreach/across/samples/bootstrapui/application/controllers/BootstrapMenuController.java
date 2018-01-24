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

import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Generates Bootstrap based tabs from a {@link Menu} instance.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/bootstrapNav")
public class BootstrapMenuController
{
	private final BootstrapUiComponentFactory bootstrapUiComponentFactory;

	/**
	 * Register the section in the administration menu.
	 */
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       //.group( "/test", "Functionality demos" ).and()
		       .group( "/test/menu", "Bootstrap menu rendering" ).and()
		       .item( "/test/menu/simple", "Simple navigation", "/bootstrapNav/simple" ).order( 1 ).and()
		       .item( "/test/menu/navbar", "Navbar navigation", "/bootstrapNav/navbar" ).order( 2 ).and()
		       .item( "/test/menu/tabs", "Tabs navigation", "/bootstrapNav/tabs" ).order( 3 ).and()
		       .item( "/test/menu/pills", "Pills navigation", "/bootstrapNav/pills" ).order( 4 ).and()
		       .item( "/test/menu/pills-stacked", "Stacked pills navigation", "/bootstrapNav/pills-stacked" )
		       .order( 5 );
	}

	/**
	 * Entry point that adds the different menus to the model.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/{type}")
	public String listMenus( @PathVariable String type, Model model, ViewElementBuilderContext builderContext ) {

		Map<String, Consumer<PathBasedMenuBuilder>> menusToGenerate = new LinkedHashMap<>();
		menusToGenerate.put( "Simple menu without dropdowns and no selected item", this::simpleMenu );
		menusToGenerate.put( "Simple menu without dropdowns and with a selected item", this::simpleMenuWithSelected );
		menusToGenerate.put( "Single level dropdown menu", this::singleLevelDropDownMenu );
		menusToGenerate.put( "Two level groups dropdown menu", this::twoLevelGroupsWithSeparators );
		menusToGenerate.put( "Selected item used as group name", this::twoLevelGroupsWithSelected );
		menusToGenerate.put( "Icon only menu items with custom HTML attributes", this::iconOnlyItems );

		Map<String, ViewElement> generatedMenus = new LinkedHashMap<>();
		menusToGenerate.forEach(
				( title, consumer ) -> {
					Menu menu = buildMenu( consumer );
					generatedMenus.put( title, menuComponentBuilder( type, menu ).build( builderContext ) );
				}
		);

		model.addAttribute( "generatedMenus", generatedMenus );

		return "th/bootstrapUiTest/menuRendering";
	}

	private void simpleMenu( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "One", "#" ).order( 2 ).and()
		    .item( "/two", "Two", "#" ).order( 1 ).and()
		    .item( "/three", "Three", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.APPLE ) ).order( 3 );
	}

	private void simpleMenuWithSelected( PathBasedMenuBuilder menu ) {
		menu.item( "/one", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.DOWNLOAD ) ).order( 2 )
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
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.DOWNLOAD ) )
		    .and()
		    .group( "/three", "Three" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.ALERT ) )
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
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.TRASH ) )
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
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.HOME ) )
		    .order( 3 )
		    .and()
		    .item( "/two/three/1", "Sub group item 1", "#" ).and()
		    .item( "/two/three/2", "Selected", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.COG ) )
		    .and()
		    .group( "/two/four", "Sub group 2" ).order( 4 ).and()
		    .item( "/two/four/1", "Sub group 2 item 1", "#" ).and()
		    .item( "/two/five", "Item 4", "#" )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.TRASH ) )
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
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.TRASH ) )
		    .and()
		    .group( "/two", "Two" ).order( 3 )
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.DOWNLOAD ) )
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
		    .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.TRASH ) )
		    .attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.AROUND )
		    .order( 5 )
		    .and()
		    .item( "/two/six", "Item 5", "#" ).order( 6 );
	}

	private NavComponentBuilder menuComponentBuilder( String type, Menu menu ) {
		DefaultNavComponentBuilder menuBuilder = bootstrapUiComponentFactory.nav( menu );

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

		return menuBuilder;
	}

	private Menu buildMenu( Consumer<PathBasedMenuBuilder> builderConsumer ) {
		PathBasedMenuBuilder builder = new PathBasedMenuBuilder();
		builderConsumer.accept( builder );
		Menu menu = builder.build();
		menu.sort();
		menu.select( Menu.byTitle( "Selected" ) );
		return menu;
	}
}
