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

package com.foreach.across.modules.entity;

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertyTemplateValueResolver;
import com.foreach.across.modules.entity.config.AttributeRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuAttributeRegistrar;
import com.foreach.across.modules.entity.views.menu.EntityAdminMenuEvent;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.NonNull;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Contains common {@link com.foreach.across.modules.entity.config.AttributeRegistrar} returning functions
 * for things like {@link EntityConfiguration}, {@link EntityPropertyDescriptor}, {@link EntityViewFactory} etc.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.EntityViewFactoryAttributes
 * @see com.foreach.across.modules.entity.views.menu.EntityAdminMenuAttributeRegistrar
 * @since 3.0.0
 */
public interface EntityAttributeRegistrars
{
	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath for the item
	 * @return view attribute registrar
	 */
	static AttributeRegistrar<EntityViewFactory> adminMenu( String menuPath ) {
		return EntityAdminMenuAttributeRegistrar.adminMenu( menuPath );
	}

	/**
	 * Register a default view menu item with the registered path.
	 *
	 * @param menuPath       for the item
	 * @param itemCustomizer for additional customization of the menu item
	 * @return view attribute registrar
	 */
	static AttributeRegistrar<EntityViewFactory> adminMenu( String menuPath, Consumer<PathBasedMenuBuilder.PathBasedMenuItemBuilder> itemCustomizer ) {
		return EntityAdminMenuAttributeRegistrar.adminMenu( menuPath, itemCustomizer );
	}

	/**
	 * Register a custom menu event consumer.
	 *
	 * @param menuEventConsumer for handling the menu
	 * @param <S>               entity type
	 * @return view attribute registrar
	 */
	static <S> AttributeRegistrar<EntityViewFactory> adminMenu( Consumer<EntityAdminMenuEvent<S>> menuEventConsumer ) {
		return EntityAdminMenuAttributeRegistrar.adminMenu( menuEventConsumer );
	}

	/**
	 * Register a fixed value as the value that should be used for a template control of a property.
	 * Mainly relevant for indexer property descriptors: list member, map key or map value.
	 *
	 * @param value to use
	 * @param <U>   attribute target
	 * @return attribute registrar
	 */
	static <U extends ReadableAttributes> AttributeRegistrar<U> templateValue( Object value ) {
		return templateValue( ( bindingContext, descriptor ) -> value );
	}

	/**
	 * Register a value supplier that should be used when fetching the value for a template control of a property.
	 * Mainly relevant for indexer property descriptors: list member, map key or map value.
	 *
	 * @param supplier to call
	 * @param <U>      attribute target
	 * @return attribute registrar
	 */
	static <U extends ReadableAttributes> AttributeRegistrar<U> templateValue( @NonNull Supplier<Object> supplier ) {
		return templateValue( ( bindingContext, descriptor ) -> supplier.get() );
	}

	/**
	 * Register a {@link EntityPropertyTemplateValueResolver} that should be used to resolve the value for a template control of a property.
	 * Mainly relevant for indexer property descriptors: list member, map key or map value.
	 *
	 * @param resolver to use
	 * @param <U>      attribute target
	 * @return attribute registrar
	 */
	static <U extends ReadableAttributes> AttributeRegistrar<U> templateValue( @NonNull EntityPropertyTemplateValueResolver resolver ) {
		return ( o, attributes ) -> attributes.setAttribute( EntityPropertyTemplateValueResolver.class, resolver );
	}
}
