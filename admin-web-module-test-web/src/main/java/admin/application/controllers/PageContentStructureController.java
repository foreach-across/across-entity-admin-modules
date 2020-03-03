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

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.annotations.AdminWebController;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.styles.AcrossStyleRule;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.MenuSelector;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import static admin.application.config.CustomAdminWebIcons.*;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AdminWebController
@RequiredArgsConstructor
public class PageContentStructureController
{
	@EventListener
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
				.attribute( NavComponentBuilder.customizeViewElement( AcrossStyleRule.utility( css.cssFloat.right ) ) )
				.attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
				.and()
				.item( "/advanced/trash", "Move to trash", "#" )
				.attribute( NavComponentBuilder.ATTR_ICON, iconSet( AdminWebModule.NAME ).icon( DELETE ) )
				.and()
				.group( "/two", "Two" ).order( 3 )
				.attribute( NavComponentBuilder.ATTR_ICON, iconSet( AdminWebModule.NAME ).icon( DOWNLOAD ) )
				.attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
				.attribute( NavComponentBuilder.customizeViewElement( AcrossStyleRule.utility( css.cssFloat.right ) ) )
				.and()
				.item( "/two/one", "Item 1", "#" ).order( 1 ).and()
				.item( "/two/two", "Item 2", "#" ).order( 2 ).and()
				.group( "/two/three", "Sub group 1" ).order( 3 ).and()
				.item( "/two/three/1", "Sub group item 1", "#" ).and()
				.item( "/two/three/2", "Sub group item 2", "#" ).and()
				.group( "/two/four", "Sub group 2" ).order( 4 ).and()
				.item( "/two/four/1", "Sub group 2 item 1", "#" ).and()
				.item( "/two/five", "Selected", "#" )
				.attribute( NavComponentBuilder.ATTR_ICON, iconSet( AdminWebModule.NAME ).icon( DELETE ) )
				.attribute( NavComponentBuilder.ATTR_INSERT_SEPARATOR, NavComponentBuilder.Separator.AROUND )
				.order( 5 )
				.and()
				.item( "/two/six", "Item 5", "#" ).order( 6 )
				.and()
				.build();
		menu.select( MenuSelector.byPath( "/two/three/2" ) );
		menu.sort();

		page.setPageTitle( "Some page title..." );
		page.addToPageTitleSubText( iconSet( AdminWebModule.NAME ).icon( ALERT ) );

		page.addToNav( bootstrap.builders.nav().menu( menu ).tabs().build( builderContext ) );
		page.addToFeedback(
				bootstrap.builders.alert().danger().dismissible()
				                  .text( "Global feedback section with a lot of content that will be rendered as a toastr notification." )
				                  .build( builderContext )
		);
		page.addToFeedback(
				bootstrap.builders.alert().warning().dismissible().with( css.of( "no-toast" ) )
				                  .text( "This is a dismissible alert which will never be rendered as a toast..." )
				                  .build( builderContext )
		);

		page.addToFooter( bootstrap.builders.alert().text( "This is the footer." ).build( builderContext ) );
		page.addChild( TextViewElement.text( "Hello body content..." ) );

		return PageContentStructure.TEMPLATE;
	}
}
