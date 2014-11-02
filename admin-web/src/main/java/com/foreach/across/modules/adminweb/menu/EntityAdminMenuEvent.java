package com.foreach.across.modules.adminweb.menu;

import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import org.springframework.core.ResolvableType;

public class EntityAdminMenuEvent<T> extends BuildMenuEvent<EntityAdminMenu<T>>
{
	public EntityAdminMenuEvent( EntityAdminMenu<T> menu, PathBasedMenuBuilder menuBuilder ) {
		super( menu, menuBuilder, ResolvableType.forClass( menu.getEntityClass() ) );
	}

	public Class<T> getEntityClass() {
		return getMenu().getEntityClass();
	}

	public T getEntity() {
		return getMenu().getEntity();
	}

	public boolean isForUpdate() {
		return getEntity() != null;
	}
}
