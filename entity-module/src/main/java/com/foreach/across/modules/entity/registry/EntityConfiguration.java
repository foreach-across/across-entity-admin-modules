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

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.spring.security.actions.AllowableActions;

import java.io.Serializable;
import java.util.Collection;

/**
 * Central configuration API for a single entity type in a {@link EntityRegistry}.
 * Usually an entity is represented by a unique {@link Class}, but this is not a requirement.
 * As a {@link EntityRegistry} identifies entity configurations uniquely by name, it is entirely
 * possible to have more than one configuration for the same class.
 * <p/>
 * Every configuration is expected to have a {@link EntityPropertyRegistry} attached, that can be
 * used to query the available properties of a single entity instance.
 * <p/>
 * Usually a configuration will also have a simple {@link EntityModel} set. This is a basic repository
 * for querying and persisting an entity.  If no model is present, functionality will be limited.
 *
 * @param <T> type of the entity
 * @see MutableEntityConfiguration
 * @see EntityConfigurationImpl
 * @since 1.0.0
 */
public interface EntityConfiguration<T> extends ReadableAttributes, EntityViewRegistry
{
	/**
	 * @return unique name for this configuration
	 */
	String getName();

	/**
	 * @return default display name to use in the UI
	 * @see EntityMessageCodeResolver
	 */
	String getDisplayName();

	/**
	 * @return simple class that instances of this entity are.
	 */
	Class<T> getEntityType();

	/**
	 * @return basic model for performing entity operations
	 */
	EntityModel<T, Serializable> getEntityModel();

	/**
	 * @return property registry for this entity
	 */
	EntityPropertyRegistry getPropertyRegistry();

	/**
	 * @return message code resolver to use for resolving messages in the context of this entity type
	 */
	EntityMessageCodeResolver getEntityMessageCodeResolver();

	/**
	 * @return list of associations this entity has to others
	 */
	Collection<EntityAssociation> getAssociations();

	/**
	 * Get a uniquely named association.
	 *
	 * @param name of the association
	 * @return specific association with that name
	 */
	EntityAssociation association( String name );

	/**
	 * Shortcut to {@link EntityModel#isNew(Object)}.
	 *
	 * @param entity instance
	 * @return true if new (unsaved) entity instance
	 */
	boolean isNew( T entity );

	/**
	 * Shortcut to {@link EntityModel#getIdType()}}.
	 *
	 * @return type of the id parameter of this entity (can be null)
	 */
	Class<?> getIdType();

	/**
	 * Shortcut to {@link EntityModel#getId(Object)}.
	 *
	 * @param entity instance
	 * @return id value of the entity
	 */
	Serializable getId( T entity );

	/**
	 * Shortcut to {@link EntityModel#getLabel(Object)}.
	 *
	 * @param entity instance
	 * @return label valud of the entity
	 */
	String getLabel( T entity );

	/**
	 * @return true if this configuration should not be displayed in UI implementations
	 */
	boolean isHidden();

	/**
	 * @return the set of actions allowed on all entities of this EntityConfiguration
	 */
	AllowableActions getAllowableActions();

	/**
	 * @param entity for which to fetch the allowed actions
	 * @return the set of actions allowes on the specific entity
	 */
	AllowableActions getAllowableActions( T entity );
}
