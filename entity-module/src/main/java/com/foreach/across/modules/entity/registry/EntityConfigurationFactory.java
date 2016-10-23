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

/**
 * Factory for creating a new {@link MutableEntityConfiguration} with default settings,
 * or requesting a
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder
 * @see com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder
 * @since 2.0.0
 */
public interface EntityConfigurationFactory
{
	/**
	 * Create a new entity configuration for the given entity type.
	 *
	 * @param entityType to create the configuration for
	 * @param <T>        type
	 * @return pre-configured entity configuration
	 */
	<T> MutableEntityConfiguration<T> create( Class<T> entityType );
}
