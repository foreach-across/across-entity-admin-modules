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

import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.experimental.UtilityClass;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static com.foreach.across.modules.entity.views.EntityViewFactoryAttributes.ACCESS_VALIDATOR;
import static com.foreach.across.modules.entity.views.EntityViewFactoryAttributes.ADMIN_MENU;

/**
 * Contains {@link com.foreach.across.modules.entity.config.AttributeRegistrar<com.foreach.across.modules.entity.views.EntityViewFactory>}
 * methods for creating a {@link com.foreach.across.modules.entity.views.EntityViewFactoryAttributes#ADMIN_MENU} attribute.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@UtilityClass
public class EntityAdminMenuAttributeRegistrar
{
	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath for the item
	 * @return view attribute registrar
	 */
	public static AttributeRegistrar<EntityViewFactory> adminMenu( String menuPath ) {
		return adminMenu( menuPath, null );
	}

	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath       for the item
	 * @param itemCustomizer for additional customization of the menu item
	 * @return view attribute registrar
	 */
	public static AttributeRegistrar<EntityViewFactory> adminMenu( String menuPath, Consumer<PathBasedMenuBuilder.PathBasedMenuItemBuilder> itemCustomizer ) {
		return ( entityViewFactory, attributes ) -> {
			attributes.setAttribute( ADMIN_MENU, (Consumer<EntityAdminMenuEvent>) ( menuEvent ) -> {
				if ( menuEvent.isForUpdate() && isConfiguredForContextViewRegistry( entityViewFactory, menuEvent.getViewContext() )
						&& isAllowed( entityViewFactory, menuEvent.getViewContext() ) ) {
					String viewName = entityViewFactory.getAttribute( EntityViewFactoryAttributes.VIEW_NAME, String.class );
					PathBasedMenuBuilder.PathBasedMenuItemBuilder builder = menuEvent.builder().item(
							menuPath,
							"#{adminMenu.views[" + viewName + "]=" + viewName + "}",
							menuEvent.getLinkBuilder().forInstance( menuEvent.getEntity() ).updateView().withViewName( viewName ).toString()
					);
					if ( itemCustomizer != null ) {
						itemCustomizer.accept( builder );
					}
				}
			} );
		};
	}

	/**
	 * Checks if the given {@link EntityViewFactory} is for the same {@link EntityViewRegistry} as the current {@link EntityViewContext}.
	 *
	 * @param viewFactory on which the view is registered
	 * @param viewContext that is present
	 */
	private static boolean isConfiguredForContextViewRegistry( EntityViewFactory viewFactory, EntityViewContext viewContext ) {
		EntityViewRegistry factoryViewRegistry = viewFactory.getAttribute( EntityViewRegistry.class );
		EntityViewRegistry contextViewRegistry = viewContext.isForAssociation() ? viewContext.getEntityAssociation() : viewContext.getEntityConfiguration();
		return factoryViewRegistry.equals( contextViewRegistry );
	}

	private static boolean isAllowed( EntityViewFactory viewFactory, EntityViewContext viewContext ) {
		BiPredicate<EntityViewFactory, EntityViewContext> accessValidator = viewFactory.getAttribute( ACCESS_VALIDATOR, BiPredicate.class );
		return accessValidator == null || accessValidator.test( viewFactory, viewContext );
	}

	/**
	 * Register a custom menu event consumer.
	 *
	 * @param menuEventConsumer for handling the menu
	 * @param <S>               entity type
	 * @return view attribute registrar
	 */
	public static <S> AttributeRegistrar<EntityViewFactory> adminMenu( Consumer<EntityAdminMenuEvent<S>> menuEventConsumer ) {
		return ( ( entityViewFactory, attributes ) -> attributes.setAttribute( ADMIN_MENU, menuEventConsumer ) );
	}
}
