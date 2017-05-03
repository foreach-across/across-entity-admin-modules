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

package com.foreach.across.modules.entity.views.request;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an entity view command object.  In case of a form view will usually hold the DTO of the
 * entity being created or updated.  Additionally extension objects can be registered that
 * will be bound and validated as well.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityViewCommand
{
	@Valid
	private Object entity;

	@Valid
	private final Map<String, Object> extensions = new HashMap<>();

	public EntityViewCommand() {
	}

	public void setEntity( Object entity ) {
		this.entity = entity;
	}

	/**
	 * @return The entity (dto) instance for the command.
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * @return the entity cast as type
	 */
	public <V> V getEntity( Class<V> entityType ) {
		return entityType.cast( getEntity() );
	}

	/**
	 * @return Map of possible command extensions that have been registered.
	 */
	public Map<String, Object> getExtensions() {
		return extensions;
	}

	/**
	 * Add an extension object under the given key.  Will replace any previously registered extension.
	 *
	 * @param name      of the extension object
	 * @param extension object - data binding will also happen on this extension as well as validation if default binding occurs
	 */
	public void addExtension( String name, Object extension ) {
		extensions.put( name, extension );
	}

	/**
	 * Return the extension with the given name and coerce it to the expected type.
	 *
	 * @param extensionName name of the extension
	 * @param <Y>           type of the extension object
	 * @return extension value
	 */
	@SuppressWarnings("unchecked")
	public <Y> Y getExtension( String extensionName ) {
		return (Y) extensions.get( extensionName );
	}

	/**
	 * Return the extension with the given name and coerce it to the expected type.
	 *
	 * @param extensionName name of the extension
	 * @param extensionType type of the extension object
	 * @param <Y>           type of the extension object
	 * @return extension value
	 */
	public <Y> Y getExtension( String extensionName, Class<Y> extensionType ) {
		return extensionType.cast( extensions.get( extensionName ) );
	}

	/**
	 * @return {@code true} if an entity is set on this command
	 */
	public boolean holdsEntity() {
		return entity != null;
	}

	/**
	 * Check if an extension was registered under the given name.  Note that the extension value can be {@code null}.
	 *
	 * @param extensionName name of the extension
	 * @return {@code true} if extension was registered
	 */
	public boolean hasExtension( String extensionName ) {
		return extensions.containsKey( extensionName );
	}

	/**
	 * Remove the extension with that name.
	 *
	 * @param extensionName name of the extension
	 */
	public void removeExtension( String extensionName ) {
		extensions.remove( extensionName );
	}
}
