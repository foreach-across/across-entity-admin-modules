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

package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;

/**
 * Factory for creating a new {@link MutableEntityConfiguration}.
 * Default settings will be applied to the configuration.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 * @since 2.0.0
 */
public interface EntityConfigurationProvider
{
	/**
	 * Create a new entity configuration for the given entity type.
	 * Assumes this is the single configuration for that class and will register the
	 * property registry as the main registry for that class.
	 *
	 * @param entityType to create the configuration for
	 * @param <T>        type
	 * @return pre-configured entity configuration
	 */
	<T> MutableEntityConfiguration<T> create( Class<T> entityType );

	/**
	 * Create a new entity configuration for the given entity type with the specific name.
	 * <p/>
	 * The registerForClass parameter determines if the property registry of the class should
	 * be the main registry in the {@link EntityPropertyRegistryProvider}.
	 * If that is the case, nested properties on this type will be resolved against this configuration.
	 * If you're expecting to have only one entity configuration using this entity type, then you most
	 * likely want to register this configuration for that type.
	 *
	 * @param <T>              type
	 * @param name             of the configuration
	 * @param entityType       to create the configuration for
	 * @param registerForClass true if the property registry should be the main registry for that type
	 * @return pre-configured entity configuration
	 */
	<T> MutableEntityConfiguration<T> create( String name, Class<T> entityType, boolean registerForClass );

}
