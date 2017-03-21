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
package com.foreach.across.modules.entity.registrars.repository;

import com.foreach.across.modules.entity.registrars.repository.associations.EntityAssociationBuilder;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.SimpleAssociationHandler;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.stereotype.Component;

import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import java.util.Collection;

/**
 * Builds the list of associations and their views.
 *
 * @author Arne Vandamme
 */
@Component
class RepositoryEntityAssociationsBuilder
{
	@Autowired
	private Collection<EntityAssociationBuilder> entityAssociationBuilders;

	@Autowired
	private MappingContextRegistry mappingContextRegistry;

	public <T> void buildAssociations( final MutableEntityRegistry entityRegistry,
	                                   final MutableEntityConfiguration entityConfiguration ) {
		final RepositoryFactoryInformation<T, ?> repositoryFactoryInformation
				= entityConfiguration.getAttribute( RepositoryFactoryInformation.class );

		if ( repositoryFactoryInformation != null ) {
			final PersistentEntity persistentEntity = repositoryFactoryInformation.getPersistentEntity();
			handleAssociations( entityRegistry, entityConfiguration, persistentEntity, "" );
		}
	}

	private void handleAssociations(
			MutableEntityRegistry entityRegistry, MutableEntityConfiguration entityConfiguration, PersistentEntity persistentEntity, String prefix ) {
		persistentEntity.doWithAssociations(
				(SimpleAssociationHandler) association -> {
					PersistentProperty property = association.getInverse();

					if ( property.isAnnotationPresent( Embedded.class ) || property.isAnnotationPresent( EmbeddedId.class ) ) {
						// For embedded entities - handle the associations of the embedded entity as well
						if ( property.isEntity() ) {
							mappingContextRegistry
									.getPersistentEntity( property.getActualType() )
									.ifPresent(
											entity -> handleAssociations( entityRegistry, entityConfiguration, entity, prefix + property.getName() + "." )
									);
						}
					}
					else {
						for ( EntityAssociationBuilder builder : entityAssociationBuilders ) {
							if ( builder.supports( property ) ) {
								builder.buildAssociation(
										entityRegistry,
										entityConfiguration,
										property,
										prefix
								);
							}
						}
					}
				}
		);
	}

}
