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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.stereotype.Component;

import javax.persistence.ManyToMany;

import static com.foreach.across.modules.entity.config.builders.EntityAssociationBuilder.registerAssociationMessageCodeResolver;

/**
 * @author Andy Somers
 */
@Component
class ManyToManyEntityAssociationBuilder implements EntityAssociationBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger( ManyToManyEntityAssociationBuilder.class );

	@Override
	public boolean supports( PersistentProperty<?> sourceProperty ) {
		return sourceProperty.isAnnotationPresent( ManyToMany.class );
	}

	@Override
	public void buildAssociation( MutableEntityRegistry entityRegistry,
	                              MutableEntityConfiguration entityConfiguration,
	                              PersistentProperty property, String propertyPrefix ) {
		String fqPropertyName = propertyPrefix + property.getName();
		MutableEntityConfiguration other = entityRegistry.getEntityConfiguration( property.getActualType() );

		if ( other != null ) {
			String associationName = entityConfiguration.getName() + "." + fqPropertyName;

			// Create source to target association
			MutableEntityAssociation association = entityConfiguration.association( associationName );

			if ( association == null ) {
				association = entityConfiguration.createAssociation( associationName );
				association.setTargetEntityConfiguration( other );
				association.setSourceProperty( entityConfiguration.getPropertyRegistry().getProperty( fqPropertyName ) );
				association.setAttribute( PersistentProperty.class, property );

				// By default hide this association as it would be managed as a regular property
				association.setHidden( true );
				association.setAttribute( AssociatedEntityQueryExecutor.class, buildAssociatedEntityQueryExecutor( association ) );

				registerAssociationMessageCodeResolver( association );
			}
			else {
				LOG.info( "Skipping automatic registration of association {} on {} as it is already registered.",
				          associationName, entityConfiguration.getName() );
			}

			// Create target to source association (reverse)
			association = other.association( associationName );

			if ( association == null ) {
				if ( canAssociationBeBuilt( other, entityConfiguration ) ) {
					association = other.createAssociation( associationName );

					association.setTargetEntityConfiguration( entityConfiguration );
					association.setTargetProperty( entityConfiguration.getPropertyRegistry().getProperty( fqPropertyName ) );
					association.setAttribute( PersistentProperty.class, property );

					association.setAttribute( AssociatedEntityQueryExecutor.class, buildAssociatedEntityQueryExecutor( association ) );

					registerAssociationMessageCodeResolver( association );
				}
			}
			else {
				LOG.info( "Skipping automatic registration of association {} on {} as it is already registered.",
				          associationName, other.getName() );
			}
		}
	}

	private boolean canAssociationBeBuilt( MutableEntityConfiguration from, MutableEntityConfiguration to ) {
		if ( !to.hasAttribute( EntityQueryExecutor.class ) ) {
			LOG.warn(
					"Unable to build association between {} and {} because {} does not provide an EntityQueryExecutor.",
					from.getName(), to.getName(), to.getName() );
			return false;
		}

		return true;
	}

	private AssociatedEntityQueryExecutor buildAssociatedEntityQueryExecutor( MutableEntityAssociation association ) {
		EntityPropertyDescriptor source = association.getSourceProperty();

		if ( source != null ) {
			return AssociatedEntityQueryExecutor.forBeanProperty( source );
		}
		else {
			// Reverse association
			EntityQueryExecutor<?> queryExecutor = association.getTargetEntityConfiguration()
			                                                  .getAttribute( EntityQueryExecutor.class );

			if ( queryExecutor != null ) {
				return new AssociatedEntityQueryExecutor<>( association.getTargetProperty(), queryExecutor );
			}
		}

		return null;
	}
}
