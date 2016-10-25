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
package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.core.annotations.RefreshableCollection;
import com.foreach.across.modules.entity.registry.properties.registrars.DefaultPropertiesRegistrar;
import com.foreach.across.modules.entity.registry.properties.registrars.LabelPropertiesRegistrar;
import com.foreach.across.modules.entity.registry.properties.registrars.PersistenceMetadataPropertiesRegistrar;
import com.foreach.across.modules.entity.registry.properties.registrars.ValidationMetadataPropertiesRegistrar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Manages the {@link EntityPropertyRegistry} instances for a set of entity types.
 * Dispatches to {@link PropertiesRegistrar} instances for attribute generation.
 * The collection of {@link PropertiesRegistrar} is a module ordered collection
 * of beans picked up from the different modules.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Service
public class DefaultEntityPropertyRegistryProvider implements EntityPropertyRegistryProvider
{
	private final EntityPropertyDescriptorFactory descriptorFactory;
	private final Map<Class<?>, MutableEntityPropertyRegistry> registries = new HashMap<>();

	private Collection<PropertiesRegistrar> propertiesRegistrars = Collections.emptyList();

	@Autowired
	public DefaultEntityPropertyRegistryProvider( EntityPropertyDescriptorFactory descriptorFactory ) {
		this.descriptorFactory = descriptorFactory;
	}

	@RefreshableCollection(includeModuleInternals = true, incremental = true)
	public void setPropertiesRegistrars( Collection<PropertiesRegistrar> propertiesRegistrars ) {
		this.propertiesRegistrars = propertiesRegistrars;
	}

	@Override
	public MutableEntityPropertyRegistry get( Class<?> entityType ) {
		return registries.computeIfAbsent( entityType, this::create );
	}

	@Override
	public MutableEntityPropertyRegistry create( Class<?> entityType ) {
		DefaultEntityPropertyRegistry newRegistry = new DefaultEntityPropertyRegistry( this );
		propertiesRegistrars.forEach( b -> b.accept( entityType, newRegistry ) );
		return newRegistry;
	}

	@Override
	public MutableEntityPropertyRegistry createForParentRegistry( EntityPropertyRegistry entityPropertyRegistry ) {
		return new MergingEntityPropertyRegistry( entityPropertyRegistry, this, descriptorFactory );
	}

	/**
	 * Central API for auto-registering properties for a class into a
	 * {@link EntityPropertyRegistry}.
	 *
	 * @author Arne Vandamme
	 * @see DefaultEntityPropertyRegistryProvider
	 * @see DefaultPropertiesRegistrar
	 * @see LabelPropertiesRegistrar
	 * @see PersistenceMetadataPropertiesRegistrar
	 * @see ValidationMetadataPropertiesRegistrar
	 * @since 2.0.0
	 */
	@FunctionalInterface
	public interface PropertiesRegistrar extends BiConsumer<Class<?>, MutableEntityPropertyRegistry>
	{
		void accept( Class<?> entityType, MutableEntityPropertyRegistry registry );
	}
}