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

import com.foreach.across.core.annotations.Event;
import com.foreach.across.modules.adminweb.menu.AdminMenu;
import com.foreach.across.modules.adminweb.menu.AdminMenuEvent;
import com.foreach.across.modules.adminweb.ui.AdminWebLayoutTemplate;
import com.foreach.across.modules.bootstrapui.components.builder.NavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.spring.security.infrastructure.services.CurrentSecurityPrincipalProxy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Registers the user context menu item to render in the right navbar on the default template.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Component
@RequiredArgsConstructor
public final class DefaultAdminMenuRegistrar
{
	public static final String PATH = "/user-context";

	private final CurrentSecurityPrincipalProxy securityPrincipalProxy;

	@Event
	void registerDefaultItems( AdminMenuEvent menuEvent ) {
		menuEvent.builder()
		         .root( "/" )
		         .title( "Administration" )
		         .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.HOME ) )
		         .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		         .and()
		         .group( PATH, StringUtils.defaultString( securityPrincipalProxy.getPrincipalName(), "User" ) )
		         .attribute( AdminMenu.ATTR_NAV_POSITION, AdminWebLayoutTemplate.NAVBAR_RIGHT )
		         .attribute( NavComponentBuilder.ATTR_ICON, new GlyphIcon( GlyphIcon.USER ) )
		         .attribute( NavComponentBuilder.ATTR_ICON_ONLY, true )
		         .attribute( PanelsNavComponentBuilder.ATTR_RENDER_AS_PANEL, false )
		         .order( Ordered.LOWEST_PRECEDENCE )
		         .and()
		         .item( PATH + "/logout", "Logout", "@adminWeb:/logout" );
	}
}
