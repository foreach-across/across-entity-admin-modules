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

package com.foreach.across.modules.adminweb.menu.registrars;

import com.foreach.across.core.annotations.ConditionalOnDevelopmentMode;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.adminweb.resource.AdminWebIcons.DEVELOPER_TOOLS;
import static com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder.customizeViewElement;
import static com.foreach.across.modules.bootstrapui.elements.icons.IconSet.iconSet;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.MutableViewElement.Functions.witherFor;

/**
 * If development mode is active, registers the Developer tools section in the administration UI.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ConditionalOnDevelopmentMode
@Component
public final class DeveloperToolsMenuRegistrar
{
	public static final String PATH = "/ax/developer";

	@EventListener
	void registerDeveloperToolsItem( AdminMenuEvent menuEvent ) {
		menuEvent.builder()
		         .group( PATH, "Developer tools" )
		         .attribute(
				         AdminMenu.ATTR_NAV_POSITION,
				         new String[] { AdminWebLayoutTemplate.NAVBAR_RIGHT, AdminWebLayoutTemplate.SIDEBAR }
		         )
		         .attribute( NavComponentBuilder.ATTR_ICON, iconSet( AdminWebModule.NAME ).icon( DEVELOPER_TOOLS )
		                                                                                  .set( css.cssFloat.right, css.margin.top.s1 ) )
		         .attribute( customizeViewElement( css.border.warning, witherFor( NodeViewElement.class, this::warningHeader ) ) )
		         .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		         .order( Ordered.LOWEST_PRECEDENCE - 1 );
	}

	private void warningHeader( NodeViewElement card ) {
		card.findAll( e -> e instanceof HtmlViewElement && ( (HtmlViewElement) e ).hasCssClass( "card-header" ) )
		    .findFirst()
		    .ifPresent( header -> header.set( css.background.warning ) );
	}
}
