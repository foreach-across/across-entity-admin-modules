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
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @deprecated since 3.0.0 - migrated to EntityModule
 */
@Deprecated
public class EntityAdminMenuBuilder extends RequestMenuBuilder<EntityAdminMenu<Object>, EntityAdminMenuEvent<Object>>
{
	@Autowired
	private AdminWeb adminWeb;

	@Override
	public EntityAdminMenu<Object> build() {
		throw new UnsupportedOperationException( "Unable to auto build a generic menu." );
	}

	@Override
	public EntityAdminMenuEvent<Object> createEvent( EntityAdminMenu<Object> menu ) {
		PathBasedMenuBuilder menuBuilder = new PathBasedMenuBuilder(
				new PrefixContextMenuItemBuilderProcessor( adminWeb )
		);

		return new EntityAdminMenuEvent<>( menu, menuBuilder );
	}
}
