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

package com.foreach.across.modules.entity.support;

import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains consumers for {@link com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder}.
 *
 * @author Arne Vandamme
 * @since 3.3.0
 */
@UtilityClass
public class EntityConfigurationCustomizers
{
	/**
	 * Register a {@link com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor} with a fixed collection.
	 *
	 * @param collection containing all entities
	 * @return consumer
	 */
	public static <U> Consumer<EntityConfigurationBuilder<U>> registerEntityQueryExecutor( @NonNull Collection<? extends U> collection ) {
		return registerEntityQueryExecutor( () -> collection );
	}

	/**
	 * Register a {@link com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor} with a {@link Supplier}.
	 *
	 * @param supplier returning the collection of all entities
	 * @return consumer
	 */
	@SuppressWarnings("unchecked")
	public static <U> Consumer<EntityConfigurationBuilder<U>> registerEntityQueryExecutor( @NonNull Supplier<Collection<? extends U>> supplier ) {
		return registerEntityQueryExecutor( configuration -> new CollectionEntityQueryExecutor( supplier, configuration.getPropertyRegistry() ) );
	}

	/**
	 * Register a specific {@link com.foreach.across.modules.entity.query.EntityQueryExecutor} instance.
	 *
	 * @param entityQueryExecutor containing all entities
	 * @return consumer
	 */
	public static <U> Consumer<EntityConfigurationBuilder<U>> registerEntityQueryExecutor( @NonNull EntityQueryExecutor<? extends U> entityQueryExecutor ) {
		return configuration -> configuration.attribute( ( cfg, attr ) -> attr.setAttribute( EntityQueryExecutor.class, entityQueryExecutor ) );
	}

	/**
	 * Register a {@code Function} that returns an {@link EntityQueryExecutor} for the given {@link EntityConfiguration}.
	 *
	 * @param factoryFunction that returns the executor
	 * @return consumer
	 */
	public static <U> Consumer<EntityConfigurationBuilder<U>> registerEntityQueryExecutor( @NonNull Function<EntityConfiguration<U>, EntityQueryExecutor<? extends U>> factoryFunction ) {
		return configuration -> configuration.attribute( ( cfg, attr ) -> attr.setAttribute( EntityQueryExecutor.class, factoryFunction.apply( cfg ) ) );
	}
}
