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

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * Default factory for creating a new {@link MutableEntityConfiguration} that allows a number of
 * {@link PostProcessor} beans to be defined.  These will be run in order and applied to any newly
 * created configuration, before it is returned.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Service
public class DefaultEntityConfigurationProvider implements EntityConfigurationProvider
{
	/**
	 * PostProcessor interface for a single configuration.
	 */
	@FunctionalInterface
	public interface PostProcessor extends Consumer<MutableEntityConfiguration<?>>
	{
	}

	private Collection<PostProcessor> postProcessors = Collections.emptyList();

	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	public void setPostProcessors( Collection<PostProcessor> postProcessors ) {
		this.postProcessors = postProcessors;
	}

	private EntityPropertyRegistryProvider propertyRegistryProvider;

	@Override
	public <T> MutableEntityConfiguration<T> create( Class<T> entityType ) {
		Assert.notNull( entityType );

		MutableEntityConfiguration<T> configuration = new EntityConfigurationImpl<>( entityType );
		configuration.setPropertyRegistry( propertyRegistryProvider.get( entityType ) );
		postProcessors.forEach( p -> p.accept( configuration ) );

		return configuration;
	}

	@Override
	public <T> MutableEntityConfiguration<T> create( String name, Class<T> entityType, boolean registerForClass ) {
		Assert.notNull( name );
		Assert.notNull( entityType );

		MutableEntityConfiguration<T> configuration = new EntityConfigurationImpl<>( name, entityType );
		configuration.setPropertyRegistry(
				registerForClass
						? propertyRegistryProvider.get( entityType )
						: propertyRegistryProvider.create( entityType )
		);
		postProcessors.forEach( p -> p.accept( configuration ) );

		return configuration;
	}
}
