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

package com.foreach.across.modules.entity.registry.processors;

import com.foreach.across.modules.entity.config.builders.EntityConfigurationView;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.DefaultEntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.stereotype.Component;

/**
 * Checks if the entity type is an enum, and if so, builds a default entity model if there is none yet.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
final class EntityConfigurationViewProcessor implements DefaultEntityConfigurationProvider.PostProcessor
{
	private final EntityRegistry entityRegistry;

	@Override
	public void accept( MutableEntityConfiguration<?> mutableEntityConfiguration ) {
		Class<?> entityType = mutableEntityConfiguration.getEntityType();

		if ( entityType != null ) {
			EntityConfigurationView annotation = AnnotationUtils.findAnnotation( entityType, EntityConfigurationView.class );
			if ( annotation != null ) {
				EntityConfiguration original = entityRegistry.getEntityConfiguration( annotation.originalType() );
				mutableEntityConfiguration.setEntityModel( original.getEntityModel() );
				mutableEntityConfiguration.setAttribute( RepositoryFactoryInformation.class, original.getAttribute( RepositoryFactoryInformation.class ) );
				mutableEntityConfiguration.setAttribute( Repository.class, original.getAttribute( Repository.class ) );
				mutableEntityConfiguration.setAttribute( PersistentEntity.class, original.getAttribute( PersistentEntity.class ) );
				mutableEntityConfiguration.setAttribute( RepositoryInvoker.class, original.getAttribute( RepositoryInvoker.class ) );
				mutableEntityConfiguration.setAttribute( EntityQueryExecutor.class, original.getAttribute( EntityQueryExecutor.class ) );
			}
		}
	}
}
