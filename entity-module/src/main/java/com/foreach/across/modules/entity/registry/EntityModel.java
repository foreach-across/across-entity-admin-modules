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

import org.springframework.data.repository.core.EntityInformation;

import java.io.Serializable;
import java.util.Locale;

/**
 * Interface for accessing metadata and common entity functions that are required by the basic UI.
 * A model is required for an entity to be manageable through the UI.
 *
 * @param <T> entity type
 * @param <U> id type of the entity
 * @author Arne Vandamme
 * @see EntityConfiguration
 */
public interface EntityModel<T, U extends Serializable> extends EntityInformation<T, U>
{
	/**
	 * Generates a label for the entity and the default locale attached to the thread.
	 *
	 * @param entity to generate the label for
	 * @return label
	 */
	String getLabel( T entity );

	/**
	 * Generates a label for the entity and the locale specified.
	 *
	 * @param entity to generate the label for
	 * @param locale to use for formatting
	 * @return label
	 */
	String getLabel( T entity, Locale locale );

	/**
	 * Create a new instance of the entity type this model represents.
	 * Optionally a number of arguments can be passed, however for basic entity views the model
	 * should support a new instance without any arguments.
	 *
	 * @param args optional list of argumens (eg. constructor parameters)
	 * @return newly created entity
	 */
	T createNew( Object... args );

	/**
	 * Convert an existing entity into a dto.  Used by default views to ensure operations are
	 * not done on managed entities.
	 *
	 * @param entity to convert into a dto
	 * @return dto of the entity
	 */
	T createDto( T entity );

	/**
	 * Find a single entity based on its unique id.
	 *
	 * @param id of the entity
	 * @return entity or null if not found
	 */
	T findOne( U id );

	/**
	 * Saves an entity.  Depending on the entity information this will perform an update or a create.
	 * The return value depends on the implementation but in default cases the entity passed in will be
	 * detached (dto) and the return value will be the managed entity.
	 *
	 * @param entity to save
	 * @return saved entity
	 */
	T save( T entity );

	/**
	 * Deletes the given entity.
	 *
	 * @param entity to delete
	 */
	void delete( T entity );
}
