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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.ArrayDeque;
import java.util.Collection;

/**
 * Class for applying a set of {@link EntityConfigurer} instances to an {@link com.foreach.across.modules.entity.registry.EntityRegistry}.
 * Allows specifying the {@link AutowireCapableBeanFactory} that should be used per configurer.
 * <p/>
 * All configurers will be applied in adding order.
 *
 * @author Arne Vandamme
 * @see EntityConfigurer
 * @since 4.0.0
 */
@RequiredArgsConstructor
public class EntityRegistryConfigurer
{
	private final AutowireCapableBeanFactory beanFactory;
	private final Collection<EntityConfigurer> entityConfigurers = new ArrayDeque<>();

	/**
	 * Add a configurer that should be applied.
	 *
	 * @param configurer to apply
	 * @return current configurer
	 */
	public EntityRegistryConfigurer add( @NonNull EntityConfigurer configurer ) {
		entityConfigurers.add( configurer );
		return this;
	}

	/**
	 * Applies the set of configurers to the registry passed in.
	 *
	 * @param entityRegistry to apply the configurers to
	 * @return current configurer
	 */
	@SuppressWarnings("UnusedReturnValue")
	public EntityRegistryConfigurer applyTo( MutableEntityRegistry entityRegistry ) {
		EntitiesConfigurationBuilder builder = new EntitiesConfigurationBuilder( beanFactory );
		entityConfigurers.forEach( cfg -> cfg.configure( builder ) );
		builder.apply( entityRegistry );
		return this;
	}
}
