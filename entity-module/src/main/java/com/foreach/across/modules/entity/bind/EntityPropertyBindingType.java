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
 * @author Arne Vandamme
 * @since 3.2.0
 */
public enum EntityPropertyBindingType
{
	SINGLE,
	COLLECTION,
	MAP;

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

			return SINGLE;
		}

		return value;
	}
}
