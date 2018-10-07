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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Represents the type of property binder that should be used for a value.
 * Usually a binding type is determined based on the property type, but in some cases it can be useful
 * to specify it manually. For example if the property type is a custom implementation of a {@link java.util.Map}
 * that should just be treated as a regular single value property.
 * <p/>
 * Note that is different from the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType},
 * the latter determines if an {@link EntityPropertiesBinder} should be used, whereas the former determines how binding
 * should be done if the {@link EntityPropertiesBinder} is used.
 *
 * @author Arne Vandamme
 * @see EntityPropertiesBinder
 * @since 3.2.0
 */
public enum EntityPropertyBindingType
{
	/**
	 * Treat the property as a single value.
	 */
	SINGLE_VALUE,

	/**
	 * Treat the property as a {@link java.util.Collection} of values.
	 */
	COLLECTION,

	/**
	 * Treat the property as a {@link java.util.Map} of key/value entries.
	 */
	MAP;

	/**
	 * Resolve the binding type for a given property descriptor.
	 * If an attribute of type {@code EntityPropertyBindingType.class} is set, that value will be used.
	 * If not the property type ({@link EntityPropertyDescriptor#getPropertyTypeDescriptor()}) will be used
	 * to determine the binding type: by default a {@link java.util.Collection} will use {@link #COLLECTION}
	 * and a {@link java.util.Map} will use {@link #MAP}.
	 *
	 * @param descriptor to resolve the binding type for
	 * @return binding type - never {@code null}
	 */
	public static EntityPropertyBindingType forProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		EntityPropertyBindingType value = descriptor.getAttribute( EntityPropertyBindingType.class );

		if ( value == null ) {
			TypeDescriptor typeDescriptor = descriptor.getPropertyTypeDescriptor();
			if ( typeDescriptor.isMap() ) {
				return MAP;
			}

			if ( typeDescriptor.isCollection() || typeDescriptor.isArray() ) {
				return COLLECTION;
			}

			return SINGLE_VALUE;
		}

		return value;
	}
}
