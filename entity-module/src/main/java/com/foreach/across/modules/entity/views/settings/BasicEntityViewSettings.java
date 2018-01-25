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

package com.foreach.across.modules.entity.views.settings;

import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuAttributeRegistrar;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.BiPredicate;
import java.util.function.Consumer;

/**
 * Basic consumer that allows addition of view processors, setting the admin menu attribute
 * and adding a custom access validation predicate.
 * <p>
 * It supports configuration of the following processors:
 * <ul>
 * <li>{@link com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor}</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("unchecked")
@Accessors(fluent = true)
public class BasicEntityViewSettings implements Consumer<EntityViewFactoryBuilder>
{
	private AttributeRegistrar<EntityViewFactory> adminMenu;

	/**
	 * -- SETTER --
	 * Should this view render the admin menu.
	 */
	@Setter
	private Boolean renderAdminMenu;

	/**
	 * -- SETTER --
	 * Set the message code that should be used as title for the view.
	 */
	@Setter
	private String titleMessageCode;

	/**
	 * -- SETTER --
	 * Attach a custom access validator that should be used for this view.
	 * <p/>
	 * Note that if you want to keep the default access validation as well, you should combine with
	 * {@link EntityViewFactoryAttributes#defaultAccessValidator()}.
	 */
	@NonNull
	@Setter
	private BiPredicate<EntityViewFactory, EntityViewContext> accessValidator;

	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath for the item
	 * @return current consumer
	 */
	public BasicEntityViewSettings adminMenu( @NonNull String menuPath ) {
		adminMenu = EntityAdminMenuAttributeRegistrar.adminMenu( menuPath );
		return this;
	}

	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath       for the item
	 * @param itemCustomizer for additional customization of the menu item
	 * @return current consumer
	 */
	public BasicEntityViewSettings adminMenu( @NonNull String menuPath, Consumer<PathBasedMenuBuilder.PathBasedMenuItemBuilder> itemCustomizer ) {
		adminMenu = EntityAdminMenuAttributeRegistrar.adminMenu( menuPath, itemCustomizer );
		return this;
	}

	/**
	 * Register a custom menu event consumer.
	 *
	 * @param menuEventConsumer for handling the menu
	 * @param <S>               entity type
	 * @return current consumer
	 */
	public <S> BasicEntityViewSettings adminMenu( @NonNull Consumer<EntityAdminMenuEvent<S>> menuEventConsumer ) {
		adminMenu = EntityAdminMenuAttributeRegistrar.adminMenu( menuEventConsumer );
		return this;
	}

	/**
	 * Apply the configuration to the view factory builder.
	 *
	 * @param builder for the view factory
	 */
	@Override
	public void accept( EntityViewFactoryBuilder builder ) {
		if ( adminMenu != null ) {
			builder.attribute( adminMenu );
		}

		if ( accessValidator != null ) {
			builder.attribute( EntityViewFactoryAttributes.ACCESS_VALIDATOR, accessValidator );
		}

		if ( renderAdminMenu != null || titleMessageCode != null ) {
			builder.postProcess( SingleEntityPageStructureViewProcessor.class, page -> {
				if ( renderAdminMenu != null ) {
					page.setAddEntityMenu( renderAdminMenu );
				}
				if ( titleMessageCode != null ) {
					page.setTitleMessageCode( titleMessageCode );
				}
			} );
		}
	}
}
