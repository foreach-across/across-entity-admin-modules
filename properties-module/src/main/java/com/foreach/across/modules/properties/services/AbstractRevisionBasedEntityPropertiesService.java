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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractRevisionBasedEntityPropertiesService<T extends EntityProperties<U>, U, R extends Revision<U>>
		implements RevisionBasedEntityPropertiesService<T, U, R>
{
	private final EntityPropertiesRegistry entityPropertiesRegistry;
	private final RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository;

	protected AbstractRevisionBasedEntityPropertiesService( EntityPropertiesRegistry entityPropertiesRegistry,
	                                                        RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository ) {
		this.entityPropertiesRegistry = entityPropertiesRegistry;
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	@Transactional(readOnly = true)
	@Override
	public T getProperties( R revision ) {
		return getProperties( revision.getRevisionOwner(), revision.getRevisionNumber() );
	}

	@Transactional(readOnly = true)
	@Override
	public T getProperties( U entityId, int revisionNumber ) {
		StringPropertiesSource source = entityPropertiesRepository.loadProperties( entityId,
		                                                                           revisionNumber );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               source );
	}

	@Transactional
	@Override
	public void saveProperties( T entityProperties, R revision ) {
		saveProperties( entityProperties, revision.getRevisionOwner(), revision.getRevisionNumber() );
	}

	@Transactional
	@Override
	public void saveProperties( T entityProperties, U entityId, int revisionNumber ) {
		entityPropertiesRepository.saveProperties( entityProperties.getSource(), entityId, revisionNumber );
	}

	@Transactional
	@Override
	public void deleteProperties( R revision ) {
		deleteProperties( revision.getRevisionOwner(), revision.getRevisionNumber() );
	}

	@Transactional
	@Override
	public void deleteProperties( U entityId, int revisionNumber ) {
		entityPropertiesRepository.deleteEntities( entityId, revisionNumber );
	}

	@Transactional
	@Override
	public void deleteProperties( U entityId ) {
		entityPropertiesRepository.deleteEntities( entityId );
	}

	@Transactional
	@Override
	public void checkin( R revision, int newRevisionNumber ) {
		checkin( revision.getRevisionOwner(), revision.getRevisionNumber(), newRevisionNumber );
	}

	@Transactional
	@Override
	public void checkin( U entityId, int revisionNumber, int newRevisionNumber ) {
		entityPropertiesRepository.checkin( entityId, revisionNumber, newRevisionNumber );
	}

	@Transactional
	@Override
	public T checkout( R revision ) {
		return checkout( revision.getRevisionOwner(), revision.getRevisionNumber() );
	}

	@Transactional
	@Override
	public T checkout( U entityId, int revisionNumber ) {
		StringPropertiesSource source = entityPropertiesRepository.checkoutProperties( entityId, revisionNumber );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               source );
	}

	public T createProperties( R revision ) {
		return createProperties( revision.getRevisionOwner() );
	}

	public T createProperties( U entityId ) {
		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               new StringPropertiesSource( new HashMap<String,String>() )
		                               );
	}

	protected abstract T createEntityProperties( U entityId,
	                                             PropertyTypeRegistry<String> propertyTypeRegistry,
	                                             StringPropertiesSource source );
}
