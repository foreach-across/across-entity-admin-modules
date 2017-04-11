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
package com.foreach.across.modules.entity.registrars.repository.associations;

import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.MutableEntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityRegistry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.OneToMany;

/**
 * @author Andy Somers
 */
@Component
class OneToManyEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( OneToManyEntityAssociationBuilder.class );

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( OneToMany.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration entityConfiguration,
	                              PersistentProperty property, String propertyPrefix ) {
		String fqPropertyName = propertyPrefix + property.getName();
		MutableEntityConfiguration other = entityRegistry.getEntityConfiguration( property.getActualType() );

		if ( other != null ) {
			String mappedBy = (String) AnnotationUtils.getValue( property.findAnnotation( OneToMany.class ),
			                                                     "mappedBy" );

			if ( StringUtils.isBlank( mappedBy ) ) {
				LOG.warn( "Unable to process unidirectional @OneToMany relationship." );
			}
			else {
				String associationName = entityConfiguration.getName() + "." + fqPropertyName;

				MutableEntityAssociation association = entityConfiguration.createAssociation( associationName );
				association.setAttribute( PersistentProperty.class, property );
				association.setSourceProperty( entityConfiguration.getPropertyRegistry().getProperty( fqPropertyName ) );
				association.setTargetEntityConfiguration( other );
				association.setTargetProperty( other.getPropertyRegistry().getProperty( mappedBy ) );

				// Hide by default as will be managed through the property
				association.setHidden( true );

				EntityQueryExecutor<?> queryExecutor = other.getAttribute( EntityQueryExecutor.class );
				association.setAttribute(
						AssociatedEntityQueryExecutor.class,
						new AssociatedEntityQueryExecutor<>( association.getTargetProperty(), queryExecutor )
				);
			}
		}
	}
}
