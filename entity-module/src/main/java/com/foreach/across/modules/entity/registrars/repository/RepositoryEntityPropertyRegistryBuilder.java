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

import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.stereotype.Component;

import javax.validation.ValidatorFactory;
import javax.validation.metadata.BeanDescriptor;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Creates a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry} for a
 * {@link org.springframework.data.repository.core.support.RepositoryFactoryInformation} bean.</p>
 * <p>Puts every EntityPropertyRegistry in the central registry so properties of associated entities
 * can be determined as well.</p>
 */
@Component
class RepositoryEntityPropertyRegistryBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( RepositoryEntityPropertyRegistryBuilder.class );

	@Autowired
	private ValidatorFactory validatorFactory;
	@Autowired
	private EntityPropertyRegistryProvider entityPropertyRegistryProvider;

	public <T> void buildEntityPropertyRegistry( MutableEntityConfiguration<T> entityConfiguration ) {
		Class<? extends T> entityType = entityConfiguration.getEntityType();
		RepositoryFactoryInformation<T, ?> repositoryFactoryInformation
				= entityConfiguration.getAttribute( RepositoryFactoryInformation.class );

		MutableEntityPropertyRegistry registry =
				(MutableEntityPropertyRegistry) entityPropertyRegistryProvider.get( entityType );

		registry.setDefaultOrder( new EntityPropertyComparators.Ordered() );

		setBeanDescriptor( entityConfiguration );

		// add @Embedded
		PersistentEntity<?, ?> persistentEntity = repositoryFactoryInformation.getPersistentEntity();

		configureDefaultFilter( entityType, registry );
		configureKnownDescriptors( entityType, registry );

		entityConfiguration.setPropertyRegistry( registry );
	}

	private void configureDefaultFilter( Class<?> entityType, MutableEntityPropertyRegistry registry ) {
		if ( registry.getDefaultFilter() == null ) {
			List<String> excludedProps = new LinkedList<>();
			excludedProps.add( "class" );

//			if ( Persistable.class.isAssignableFrom( entityType ) ) {
//				excludedProps.add( "new" );
//			}
//
//			if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
//				excludedProps.add( "newEntityId" );
//			}

			//registry.setDefaultFilter( EntityPropertyFilters.exclude( excludedProps ) );
		}

		registry.getProperty( "class" ).setHidden( true );
	}

	private void configureKnownDescriptors( Class<?> entityType, MutableEntityPropertyRegistry registry ) {

//		if ( Persistable.class.isAssignableFrom( entityType ) ) {
//			registry.getMutableProperty( "id" ).setHidden( true );
//		}
//
//		if ( SettableIdBasedEntity.class.isAssignableFrom( entityType ) ) {
//			MutableEntityPropertyDescriptor mutable = registry.getMutableProperty( "newEntityId" );
//			mutable.setReadable( false );
//			mutable.setHidden( true );
//		}
	}

	private void setBeanDescriptor( MutableEntityConfiguration<?> entityConfiguration ) {
		BeanDescriptor beanDescriptor = validatorFactory.getValidator().getConstraintsForClass(
				entityConfiguration.getEntityType() );

		if ( beanDescriptor != null ) {
			entityConfiguration.setAttribute( BeanDescriptor.class, beanDescriptor );
		}
	}
}
