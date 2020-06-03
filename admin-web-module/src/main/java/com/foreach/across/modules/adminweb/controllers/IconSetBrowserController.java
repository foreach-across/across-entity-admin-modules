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

package com.foreach.across.modules.adminweb.controllers;

import com.foreach.across.core.annotations.ConditionalOnDevelopmentMode;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.TreeMap;

@AdminWebController
@RequestMapping("/ax/developer/iconSets")
@ConditionalOnDevelopmentMode
@RequiredArgsConstructor
public class IconSetBrowserController
{
	private final PageContentStructure page;

	@EventListener
	public void registerAdminMenu( AdminMenuEvent menuEvent ) {
		menuEvent.builder().item( "/ax/developer/iconSets", "Icon sets" );
	}

	@GetMapping(path = "/{iconSetName}")
	public String listIconSets( AdminMenu adminMenu, Model model, @PathVariable String iconSetName ) {
		adminMenu.breadcrumbLeaf( iconSetName );
		model.addAttribute( "icons", new TreeMap<>( IconSetRegistry.getIconSet( iconSetName ).getAllRegisteredIcons() ) );
		model.addAttribute( "iconSetName", iconSetName );

		page.setPageTitle( "IconSet: " + iconSetName );
		page.addChild( new TemplateViewElement( "th/adminweb/dev/bootstrapUiModule/icon-set-browser :: listIcons(${iconSetName},${icons})" ) );

		return PageContentStructure.TEMPLATE;
	}

	@GetMapping
	public String listIconSets( Model model ) {
		model.addAttribute( "iconSets", new TreeMap<String, IconSet>( IconSetRegistry.getAllIconSets() ) );

		page.setPageTitle( "Icon sets" );
		page.addChild( new TemplateViewElement( "th/adminweb/dev/bootstrapUiModule/icon-set-browser :: listIconSets(${iconSets})" ) );

		return PageContentStructure.TEMPLATE;
	}
}
