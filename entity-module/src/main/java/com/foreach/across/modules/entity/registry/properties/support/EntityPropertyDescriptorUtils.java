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

package com.foreach.across.modules.entity.registry.properties.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry.*;

/**
 * Helper functions for working with {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}s.
 * Mainly for internal use in the framework.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@UtilityClass
public class EntityPropertyDescriptorUtils
{
	/**
	 * Check if a property represents an indexer property.
	 *
	 * @param descriptor property descriptor
	 * @return true if indexer
	 */
	public static boolean isIndexerProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), INDEXER );
	}

	/**
	 * Check if a property represents a map key property.
	 *
	 * @param descriptor property descriptor
	 * @return true if map key
	 */
	public static boolean isMapKeyProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), MAP_KEY );
	}

	/**
	 * Check if a property represents a map value property.
	 *
	 * @param descriptor property descriptor
	 * @return true if map value
	 */
	public static boolean isMapValueProperty( @NonNull EntityPropertyDescriptor descriptor ) {
		return StringUtils.endsWith( descriptor.getName(), MAP_VALUE );
	}

	/**
	 * Check if a property represents a member type: either an indexer, map key or map value.
	 *
	 * @param descriptor property descriptor
	 * @return true if member property descriptor
	 */
	public static boolean isMemberProperty( EntityPropertyDescriptor descriptor ) {
		return isIndexerProperty( descriptor ) || isMapKeyProperty( descriptor ) || isMapValueProperty( descriptor );
	}

	/**
	 * Get the first non-nested parent for a nested property descriptor.
	 * The original descriptor will be returned if it is not a nested descriptor at all.
	 *
	 * @param descriptor that is the first non-nested descriptor encountered
	 * @return descriptor
	 */
	public static EntityPropertyDescriptor getRootDescriptor( @NonNull EntityPropertyDescriptor descriptor ) {
		return descriptor.isNestedProperty() ? getRootDescriptor( descriptor.getParentDescriptor() ) : descriptor;
	}

	/**
	 * For a nested {@code descriptor}, find the descriptor that is the direct child of the specified {@code parent}.
	 * This could be the {@code descriptor} itself or any of its parents.
	 * <p/>
	 * If not found or {@code descriptor} is not a nested descriptor, {@code null} will be returned.
	 *
	 * @param descriptor nested descriptor
	 * @param parent     direct parent that the result should have
	 * @return descriptor or {@code null} if not found
	 */
	public static EntityPropertyDescriptor findDirectChild( @NonNull EntityPropertyDescriptor descriptor, @NonNull EntityPropertyDescriptor parent ) {
		if ( descriptor.isNestedProperty() ) {
			EntityPropertyDescriptor directParent = descriptor.getParentDescriptor();
			if ( !StringUtils.equals( directParent.getName(), parent.getName() ) ) {
				return findDirectChild( directParent, parent );
			}
			return descriptor;
		}

		return null;
	}
}
