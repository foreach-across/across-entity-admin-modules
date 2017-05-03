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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.core.support.WritableAttributes;
import com.foreach.across.modules.entity.actions.EntityConfigurationAllowableActionsBuilder;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;

import java.io.Serializable;

/**
 * Manageable version of a {@link com.foreach.across.modules.entity.registry.EntityConfiguration},
 * implementations must allow the configuration to be modified through this interface, without
 * changing the core properties like {@link #getEntityType()} and {@link #getName()}.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
public interface MutableEntityConfiguration<T> extends EntityConfiguration<T>, WritableAttributes, ConfigurableEntityViewRegistry
{
	/**
	 * Set the default display name for this entity type.
	 *
	 * @param displayName value
	 */
	void setDisplayName( String displayName );

	/**
	 * Should this configuration be shown in UI implementations?
	 *
	 * @param hidden true if this configuration should <b>not</b> be shown in UI implementations
	 */
	void setHidden( boolean hidden );

	/**
	 * Set the {@link EntityModel} for interacting with the entity repository.
	 * Required for auto-generated administration UI.
	 *
	 * @param entityModel instance
	 */
	void setEntityModel( EntityModel<T, Serializable> entityModel );

	/**
	 * Set the property registry for this configuration.
	 *
	 * @param propertyRegistry instancce
	 */
	void setPropertyRegistry( MutableEntityPropertyRegistry propertyRegistry );

	/**
	 * Set the message code resolver.
	 *
	 * @param entityMessageCodeResolver instance
	 */
	void setEntityMessageCodeResolver( EntityMessageCodeResolver entityMessageCodeResolver );

	/**
	 * Set the builder for the {@link com.foreach.across.modules.spring.security.actions.AllowableActions}
	 * of this entity configuration.
	 *
	 * @param allowableActionsBuilder instance
	 */
	void setAllowableActionsBuilder( EntityConfigurationAllowableActionsBuilder allowableActionsBuilder );

	/**
	 * Retrieve a named association as a {@link MutableEntityAssociation}.
	 *
	 * @param name of the association
	 * @return mutable instance of the association (or null if non existing)
	 */
	@Override
	MutableEntityAssociation association( String name );

	/**
	 * Get the property registry as a {@link MutableEntityPropertyRegistry}.
	 *
	 * @return mutable instance of the property registry
	 */
	@Override
	MutableEntityPropertyRegistry getPropertyRegistry();

	/**
	 * Register a new association with a specific name.
	 * Should return any already existing association with that name.
	 *
	 * @param name of the association
	 * @return mutable instance of the association
	 */
	MutableEntityAssociation createAssociation( String name );

	/**
	 * @return the allowable actions builder instance
	 */
	EntityConfigurationAllowableActionsBuilder getAllowableActionsBuilder();
}
