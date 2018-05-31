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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper target object for binding embedded collection properties of an entity.
 * Dynamically adding or updating a property that represents an embedded collection
 * is often not possible through databinding of the entity itself.
 * <p/>
 * These type of properties will instead be mapped on an {@code EmbeddedCollectionsBinder}.
 * The latter should have support for the common different collection types.
 * Collections updated are bound back to the entity by calling {@link #apply(Object)}
 * <p/>
 * A binder is attached to a {@link EntityPropertyRegistry} and any property requested
 * that is present in the registry will result in a {@link EmbeddedCollectionData} being added.
 * If the property does not exist or does not have a member type descriptor (same property name with []),
 * an exception will be thrown.
 * <p/>
 * This implementation behaves as a {@link java.util.Map} except that a collection gets created
 * as soon as it is requested, ensuring you don't get exceptions using a {@link org.springframework.validation.DataBinder}.
 *
 * @author Arne Vandamme
 * @see EmbeddedCollectionData
 * @since 3.1.0
 * @deprecated see {@link EntityPropertiesBinder}
 */
@Deprecated
@RequiredArgsConstructor
public class EmbeddedCollectionsBinder extends HashMap<String, EmbeddedCollectionData>
{
	public static final String PATH_SEGMENT = "prop";

	@NonNull
	private final EntityPropertyRegistry propertyRegistry;

	@Getter
	@NonNull
	private final String binderPrefix;

	public Map<String, EmbeddedCollectionData> getProp() {
		return this;
	}

	/**
	 * Gets the collection data but creates it if there is a property
	 * with that key but no collection data registered yet.
	 *
	 * @param key to the property (without indexer)
	 * @return collection data
	 */
	@Override
	public EmbeddedCollectionData get( Object key ) {
		EmbeddedCollectionData data = super.get( key );
		String propertyName = (String) key;

		if ( data == null ) {
			val descriptor = propertyRegistry.getProperty( propertyName );
			if ( descriptor == null ) {
				throw new IllegalArgumentException( "No such property descriptor: '" + propertyName + "'" );
			}
			val memberDescriptor = propertyRegistry.getProperty( propertyName + EntityPropertyRegistry.INDEXER );
			if ( memberDescriptor == null ) {
				throw new IllegalArgumentException( "Property '" + propertyName + "' does not represent a collection" );
			}
			data = new EmbeddedCollectionData( descriptor, memberDescriptor );
			put( propertyName, data );
		}

		return data;
	}

	/**
	 * Update the target entity with the collections bound.
	 *
	 * @param target entity
	 */
	public void apply( Object target ) {
		values().forEach( data -> data.apply( target ) );
	}
}
