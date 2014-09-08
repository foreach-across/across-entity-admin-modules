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

import com.foreach.across.core.revision.Revision;
import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.RevisionBasedEntityPropertiesRepository;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractRevisionBasedEntityPropertiesService<T extends EntityProperties<U>, U, R extends Revision>
		implements RevisionBasedEntityPropertiesService<T, U, R>
{
	private final EntityPropertiesRegistry entityPropertiesRegistry;
	private final RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository;

	protected AbstractRevisionBasedEntityPropertiesService( EntityPropertiesRegistry entityPropertiesRegistry,
	                                                        RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository ) {
		this.entityPropertiesRegistry = entityPropertiesRegistry;
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	@Override
	public T getProperties( U entityId, R revision ) {

		StringPropertiesSource source = entityPropertiesRepository.loadProperties( entityId, revision );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               entityPropertiesRegistry.getConversionService(),
		                               source );
	}

	@Override
	public void saveProperties( T entityProperties, R revision ) {
		entityPropertiesRepository.saveProperties( entityProperties.getId(), revision, entityProperties.getSource() );
	}

	@Override
	public void deleteProperties( U entityId ) {

	}

	protected abstract T createEntityProperties( U entityId,
	                                             PropertyTypeRegistry<String> propertyTypeRegistry,
	                                             ConversionService conversionService,
	                                             StringPropertiesSource source );
}
