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
	 * Get the property descriptor path. For simple properties this usually
	 * corresponds with the property name. For nested properties this is the full
	 * property path, optionally with member descriptors.
	 * <p/>
	 * Use {@link #getTargetPropertyName()} to retrieve the exact property name
	 * this descriptor represents.
	 *
	 * @return property descriptor name
	 */
	String getName();

	/**
	 * Get the target property name that this property descriptor represents.
	 * In case of a simple property this is the same as {@link #getName()}, but
	 * for nested properties this will remove the path representing the parent property.
	 * <p/>
	 * For example, suppose the descriptor is {@code user.name}, which represents
	 * a nested property descriptor with parent descriptor {@code user}. In this case
	 * the target property name would be {@code user}.
	 *
	 * @return target property name
	 */
	String getTargetPropertyName();

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
	<U, V> EntityPropertyController getController();

	/**
	 * @return fetcher function that can return the property value directly from a parent (owning) instance
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
