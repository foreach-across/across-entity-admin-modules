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

package admin.application.controllers;

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AdminWebController
@RequiredArgsConstructor
public class PageContentStructureController
{
	private final BootstrapUiFactory bootstrapUiFactory;
	private final BootstrapUiComponentFactory bootstrapUiComponentFactory;

	@Event
	public void registerMenuItem( AdminMenuEvent adminMenuEvent ) {
		adminMenuEvent.builder()
		              .group( "/demo", "Demo pages" ).and()
		              .item( "/demo/page", "Page content structure", "/page" ).and()
		              .group( "/demo/sub-group", "Sub group" )
		              .attribute( AdminMenu.ATTR_BREADCRUMB, false )
		              .and()
		              .item( "/demo/sub-group/one", "One", "/page?pos=one" )
		              .attribute( AdminMenu.ATTR_NAV_POSITION, "sidebar" )
		              .and()
		              .item( "/demo/sub-group/two", "Two", "/page?pos=two" )
		              .attribute( AdminMenu.ATTR_NAV_POSITION, "navbar" )
		              .and()
		              .group( "/demo/sub-group/three", "Three" ).and()
		              .item( "/demo/sub-group/three/four", "Four", "/page?pos=four" );
	}

	@RequestMapping("/page")
	public String pageContent( @ModelAttribute PageContentStructure page,
	                           ViewElementBuilderContext builderContext ) {
		page.setRenderAsTabs( true );

		Menu menu = new PathBasedMenuBuilder()
				.item( "/one", "One", "#" ).order( 1 ).and()
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
				.item( "/two/six", "Item 5", "#" ).order( 6 )
				.and()
				.build();
		menu.sort();

		page.setPageTitle( "Some page title..." );
		page.addToPageTitleSubText( new GlyphIcon( GlyphIcon.ALERT ) );

		page.addToNav( bootstrapUiComponentFactory.nav( menu ).tabs().build( builderContext ) );
		page.addToFeedback(
				bootstrapUiFactory.alert().danger().dismissible().text( "Global feedback section with a lot of content that will be rendered as a toastr notification." )
				                  .build( builderContext )
		);

		page.addToFooter( bootstrapUiFactory.alert().text( "This is the footer." ).build( builderContext ) );
		page.addChild( TextViewElement.text( "Hello body content..." ) );

		return PageContentStructure.TEMPLATE;
	}
}
