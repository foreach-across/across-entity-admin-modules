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

import com.blazebit.persistence.view.EntityView;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationView;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.DefaultEntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.util.EntityUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;

import javax.validation.metadata.BeanDescriptor;

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
	@SneakyThrows
	public void accept( MutableEntityConfiguration<?> mutableEntityConfiguration ) {
		Class<?> entityType = mutableEntityConfiguration.getEntityType();

		if ( entityType != null ) {
			Class originalType = null;
			if ( EntityConfigurationView.class.isAssignableFrom( entityType ) ) {
				EntityConfigurationView byteBuddyClass = (EntityConfigurationView) entityType.newInstance();
				originalType = byteBuddyClass.getOriginalType();

				EntityConfiguration original = entityRegistry.getEntityConfiguration( EntityUtils.generateEntityName( originalType ) );
				mutableEntityConfiguration.setEntityModel( original.getEntityModel() );
				mutableEntityConfiguration.setAttribute( RepositoryFactoryInformation.class, original.getAttribute( RepositoryFactoryInformation.class ) );
				mutableEntityConfiguration.setAttribute( Repository.class, original.getAttribute( Repository.class ) );
				mutableEntityConfiguration.setAttribute( PersistentEntity.class, original.getAttribute( PersistentEntity.class ) );
				mutableEntityConfiguration.setAttribute( RepositoryInvoker.class, original.getAttribute( RepositoryInvoker.class ) );
				mutableEntityConfiguration.setAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME,
				                                         original.getAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME ) );
				mutableEntityConfiguration.setAttribute( Validator.class, original.getAttribute( Validator.class ) );
				mutableEntityConfiguration.setAttribute( BeanDescriptor.class, original.getAttribute( BeanDescriptor.class ) );

				mutableEntityConfiguration.setAttribute( EntityQueryExecutor.class, original.getAttribute( EntityQueryExecutor.class ) );
			}

			EntityView annotation = AnnotationUtils.findAnnotation( entityType, EntityView.class );
			if ( annotation != null ) {

				Class<?> value = annotation.value();

				EntityConfiguration original = entityRegistry.getEntityConfiguration( EntityUtils.generateEntityName( value ) );
				mutableEntityConfiguration.setEntityModel( original.getEntityModel() );
				mutableEntityConfiguration.setAttribute( RepositoryFactoryInformation.class, original.getAttribute( RepositoryFactoryInformation.class ) );
				mutableEntityConfiguration.setAttribute( Repository.class, original.getAttribute( Repository.class ) );
				mutableEntityConfiguration.setAttribute( PersistentEntity.class, original.getAttribute( PersistentEntity.class ) );
				mutableEntityConfiguration.setAttribute( RepositoryInvoker.class, original.getAttribute( RepositoryInvoker.class ) );
				mutableEntityConfiguration.setAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME,
				                                         original.getAttribute( EntityAttributes.TRANSACTION_MANAGER_NAME ) );
				mutableEntityConfiguration.setAttribute( Validator.class, original.getAttribute( Validator.class ) );
				mutableEntityConfiguration.setAttribute( BeanDescriptor.class, original.getAttribute( BeanDescriptor.class ) );

				mutableEntityConfiguration.setAttribute( EntityQueryExecutor.class, original.getAttribute( EntityQueryExecutor.class ) );

			}

			if ( originalType != null ) {

			}

		}
	}
}
