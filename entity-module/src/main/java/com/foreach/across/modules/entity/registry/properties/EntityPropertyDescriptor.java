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
package com.foreach.across.modules.entity.registry.properties;

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import org.springframework.core.convert.TypeDescriptor;

public interface EntityPropertyDescriptor extends ReadableAttributes
{
	/**
	 * @return Property name.
	 */
	String getName();

	String getDisplayName();

	boolean isReadable();

	boolean isWritable();

	boolean isHidden();

	Class<?> getPropertyType();

	/**
	 * @return more detailed information about the property type (supporting generics)
	 */
	TypeDescriptor getPropertyTypeDescriptor();

	/**
	 * @return controller for handling this property
	 */
	<U,V> EntityPropertyController<U,V> getController();

	/**
	 * @return Associated instance that can fetch the property value from an instance.
	 */
	ValueFetcher getValueFetcher();

	/**
	 * Retrieve the property value for the entity parameter.
	 * This is basically the same as calling {@link #getValueFetcher()#getValue}, except null will be returned if no {@link ValueFetcher} is available.
	 *
	 * @param entity from which to get the property value
	 * @return property value or null if unable to get
	 */
	Object getPropertyValue( Object entity );

	/**
	 * @return the property registry that owns this property
	 */
	EntityPropertyRegistry getPropertyRegistry();

	/**
	 * @return does this property represent a nested property ({@link #getParentDescriptor()} will not return {@code null})
	 */
	boolean isNestedProperty();

	/**
	 * @return the parent descriptor in case of a nested property descriptor ({@link #isNestedProperty()} should return {@code true})
	 */
	EntityPropertyDescriptor getParentDescriptor();

	/**
	 * Create a builder for a new {@link SimpleEntityPropertyDescriptor}.
	 *
	 * @param propertyName name of the property (unique within the registry)
	 * @return builder instance
	 */
	static EntityPropertyDescriptorBuilder builder( String propertyName ) {
		return new EntityPropertyDescriptorBuilder( propertyName );
	}
}
