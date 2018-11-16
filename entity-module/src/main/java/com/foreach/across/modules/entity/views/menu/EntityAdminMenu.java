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
import lombok.Getter;
import lombok.NonNull;

/**
 * Event published when an {@link com.foreach.across.modules.entity.views.menu.EntityAdminMenu} is being generated.
 * <p/>
 * Extends the deprecated {@link com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent} for backwards compatibility.
 *
 * @param <T> entity type
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("findbugs:NM_SAME_SIMPLE_NAME_AS_SUPERCLASS")
public class EntityAdminMenu<T> extends com.foreach.across.modules.adminweb.menu.EntityAdminMenu<T>
{
	/**
	 * Name under which the entity configuration is registered.
	 * Can be the unique discriminator if the {@link #getEntityType()} is not.
	 */
	@Getter
	private final String entityName;

	/**
	 * The {@link EntityViewContext} containing all information of the entity being viewed.
	 */
	@Getter
	private final EntityViewContext viewContext;

	@SuppressWarnings("WeakerAccess")
	protected EntityAdminMenu( Class<T> entityType, String entityName, @NonNull EntityViewContext entityViewContext, T entity ) {
		super( entityType, entity );
		this.entityName = entityName;
		this.viewContext = entityViewContext;
		setName( entityName );
	}

	/**
	 * Create a new instance for a {@link EntityViewContext}.
	 *
	 * @param viewContext for the entity configuration being viewed
	 * @return menu
	 */
	@SuppressWarnings("all")
	public static EntityAdminMenu<?> create( EntityViewContext viewContext ) {
		return new EntityAdminMenu(
				viewContext.getEntityConfiguration().getEntityType(),
				viewContext.getEntityConfiguration().getName(),
				viewContext,
				viewContext.getEntity()
		);
	}
}
