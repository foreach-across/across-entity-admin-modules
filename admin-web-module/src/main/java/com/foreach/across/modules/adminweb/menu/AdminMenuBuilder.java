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
package com.foreach.across.modules.adminweb.menu;

import com.foreach.across.modules.adminweb.AdminWeb;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.menu.PrefixContextMenuItemBuilderProcessor;
import com.foreach.across.modules.web.menu.RequestMenuBuilder;
import com.foreach.across.modules.web.menu.RequestMenuSelector;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

public class AdminMenuBuilder extends RequestMenuBuilder<AdminMenu, AdminMenuEvent>
{
	private AdminWeb adminWeb;

	@Autowired
	public void setAdminWeb( AdminWeb adminWeb ) {
		this.adminWeb = adminWeb;
	}

	@Override
	public AdminMenu build() {
		return new AdminMenu();
	}

	@Override
	public AdminMenuEvent createEvent( AdminMenu menu ) {
		PathBasedMenuBuilder menuBuilder = new PathBasedMenuBuilder( new PrefixContextMenuItemBuilderProcessor( adminWeb ) );

		menuBuilder.root( "/" )
		           .attribute( RequestMenuSelector.ATTRIBUTE_MATCHERS, Collections.singletonList( "" ) )
		           .title( "Administration" );

		return new AdminMenuEvent( menu, menuBuilder );
	}
}
