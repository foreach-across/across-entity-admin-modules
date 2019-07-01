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

import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.support.properties.EntityIdProxyPropertyRegistrar;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * Contains a number of helpers for registering properties and customizing
 * property registries.
 *
 * @author Arne Vandamme
 * @see EntityIdProxyPropertyRegistrar
 * @since 3.2.0
 */
@Service
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class EntityPropertyRegistrationHelper
{
	private final EntityRegistry entityRegistry;
	private final ConversionService mvcConversionService;

	/**
	 * Register a new property with the given name, which will reference an existing property.
	 * The existing property represents the id of the entity whereas the new property will
	 * represent the entity itself. The new property will be preconfigured with sensible defaults
	 * and inherit settings from the original property where appropriate.
	 *
	 * @param propertyName name of the new property
	 * @return builder
	 * @see EntityIdProxyPropertyRegistrar
	 */
	public EntityIdProxyPropertyRegistrar entityIdProxy( @NonNull String propertyName ) {
		return entityIdProxy().propertyName( propertyName );
	}

	/**
	 * Construct a new {@link EntityIdProxyPropertyRegistrar} which can be used for
	 * registering an entity proxy property: a property of an specific entity type
	 * which reads its value to an already present property which represents the id of the entity.
	 *
	 * @return builder
	 */
	public EntityIdProxyPropertyRegistrar entityIdProxy() {
		return new EntityIdProxyPropertyRegistrar( entityRegistry, mvcConversionService );
	}
}
