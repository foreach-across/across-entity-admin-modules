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
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

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
		         .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.WRENCH ) )
		         .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		         .attribute( PanelsNavComponentBuilder.ATTR_PANEL_STYLE, "panel-warning" )
		         .order( Ordered.LOWEST_PRECEDENCE - 1 );
	}
}
