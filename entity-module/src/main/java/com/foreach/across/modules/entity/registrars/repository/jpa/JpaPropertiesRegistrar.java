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

package com.foreach.across.modules.entity.registrars.repository.jpa;

import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaPersistentProperty;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

/**
 * Scans a registry for JPA properties and ensures that String properties
 * are sorted with ignore case by default.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.registrars.repository.PersistenceMetadataPropertiesRegistrar
 * @since 2.2.0
 */
@ConditionalOnClass(JpaPersistentProperty.class)
@Component
@OrderInModule(3)
public class JpaPropertiesRegistrar implements DefaultEntityPropertyRegistryProvider.PropertiesRegistrar
{
	@Override
	public void accept( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		registry.getRegisteredDescriptors()
		        .stream()
		        .filter( d -> d.getAttribute( PersistentProperty.class ) instanceof JpaPersistentProperty )
		        .filter( d -> d.hasAttribute( Sort.Order.class ) )
		        .filter( d -> String.class.equals( d.getAttribute( PersistentProperty.class ).getActualType() ) )
		        .map( p -> registry.getProperty( p.getName() ) )
		        .forEach( descriptor -> descriptor.setAttribute( Sort.Order.class, descriptor.getAttribute( Sort.Order.class ).ignoreCase() ) );
	}
}
