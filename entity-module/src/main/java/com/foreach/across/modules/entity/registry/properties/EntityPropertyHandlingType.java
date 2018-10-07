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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import lombok.NonNull;

/**
 * Setting that determines how a property should be handled.
 * Can be set fixed as an attribute on {@link EntityPropertyDescriptor},
 * but might be determined automatically based on the presence of other attributes.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public enum EntityPropertyHandlingType
{
	/**
	 * Property that must be a native property of the target entity.
	 */
	DIRECT,

	/**
	 * Property that is a custom property, possibly with no direct relation to the entity itself.
	 * Property values will be managed through {@link EntityPropertiesBinder} and validate/save
	 * will be called from the {@link EntityPropertyController}.
	 */
	BINDER,

	/**
	 * Custom property for which the user is responsible for the handling.
	 * Usually means the equivalent {@link EntityPropertyDescriptor} also has a value for {@link EntityAttributes#CONTROL_NAME}.
	 */
	MANUAL;

	/**
	 * Determines the handling type for a given property.
	 *
	 * @param descriptor of the property
	 * @return handling type
	 */
	public static EntityPropertyHandlingType forProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		EntityPropertyHandlingType value = descriptor.getAttribute( EntityPropertyHandlingType.class );

		if ( value == null ) {
			if ( descriptor.hasAttribute( EntityAttributes.CONTROL_NAME ) ) {
				return MANUAL;
			}
			if ( descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR ) ) {
				return DIRECT;
			}
			return BINDER;
		}

		return value;
	}
}
