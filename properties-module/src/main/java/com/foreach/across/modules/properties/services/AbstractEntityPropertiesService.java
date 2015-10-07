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
package com.foreach.across.modules.properties.services;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.common.spring.properties.PropertyTypeRegistry;

import java.util.Collection;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertiesService<T extends EntityProperties<U>, U> implements EntityPropertiesService<T, U>
{
	private final EntityPropertiesRegistry entityPropertiesRegistry;
	private final EntityPropertiesRepository<U> entityPropertiesRepository;

	protected AbstractEntityPropertiesService( EntityPropertiesRegistry entityPropertiesRegistry,
	                                           EntityPropertiesRepository<U> entityPropertiesRepository ) {
		this.entityPropertiesRegistry = entityPropertiesRegistry;
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	public T getProperties( U entityId ) {
		StringPropertiesSource source = entityPropertiesRepository.loadProperties( entityId );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               source.detach() );
	}

	public Collection<U> getEntityIdsForPropertyValue( String propertyName, Object propertyValue ) {
		return entityPropertiesRepository.getEntityIdsForPropertyValue( propertyName, propertyValue );
	}

	public void saveProperties( T entityProperties ) {
		entityPropertiesRepository.saveProperties( entityProperties.getId(),
		                                           entityProperties.getSource() );
	}

	public void deleteProperties( U entityId ) {
		entityPropertiesRepository.deleteProperties( entityId );
	}

	protected abstract T createEntityProperties( U entityId,
	                                             PropertyTypeRegistry<String> propertyTypeRegistry,
	                                             StringPropertiesSource source );
}
