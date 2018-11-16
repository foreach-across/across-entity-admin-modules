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

package com.foreach.across.modules.entity.views.menu;

import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;

/**
 * Event published when an {@link com.foreach.across.modules.entity.views.menu.EntityAdminMenu} is being generated.
 * <p/>
 * Extends the deprecated {@link com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent} for backwards compatibility.
 * Provides some direct information about the entity for which the admin menu is being built. The entire view context
 * is exposed using {@link #getViewContext()}.
 *
 * @param <T> entity type
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("findbugs:NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class EntityAdminMenuEvent<T> extends com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent<T>
{
	@SuppressWarnings("WeakerAccess")
	protected EntityAdminMenuEvent( EntityAdminMenu<T> menu, PathBasedMenuBuilder menuBuilder ) {
		super( menu, menuBuilder );
	}

	@Override
	public EntityAdminMenu<T> getMenu() {
		return (EntityAdminMenu<T>) super.getMenu();
	}

	/**
	 * @return name of the registered entity type
	 */
	public String getEntityName() {
		return getMenu().getEntityName();
	}

	/**
	 * @return is an entity present
	 */
	public boolean holdsEntity() {
		return getMenu().getViewContext().holdsEntity();
	}

	/**
	 * @return the view context for the current entity
	 */
	public EntityViewContext getViewContext() {
		return getMenu().getViewContext();
	}

	/**
	 * @return the link builder for the current entity type
	 */
	public EntityViewLinkBuilder getLinkBuilder() {
		return getViewContext().getLinkBuilder();
	}

	/**
	 * @return the allowable actions for the current entity (or configuration)
	 */
	public AllowableActions getAllowableActions() {
		return getMenu().getViewContext().getAllowableActions();
	}
}
