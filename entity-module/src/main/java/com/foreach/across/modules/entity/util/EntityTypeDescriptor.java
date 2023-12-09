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

package com.foreach.across.modules.entity.util;

import com.foreach.across.modules.entity.registry.EntityRegistry;
import lombok.*;
import org.springframework.core.convert.TypeDescriptor;

/**
 * Represents the result of resolving a {@link TypeDescriptor} (for example from a property) to its best matching entity type.
 * Mainly for internal use.
 *
 * @author Arne Vandamme
 * @see EntityUtils#resolveEntityTypeDescriptor(TypeDescriptor, EntityRegistry)
 * @since 2.2.0
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EntityTypeDescriptor
{
	/**
	 * Does the {@link #sourceTypeDescriptor} represent a collection of {@link #targetTypeDescriptor}.
	 * A collection in this case is either a {@link java.util.Collection} or an array.
	 */
	private boolean collection;

	/**
	 * Resolved target type descriptor.  In case of a collection source type, this would usually be the member.
	 */
	private TypeDescriptor targetTypeDescriptor;

	/**
	 * The original source type descriptor that has been resolved.
	 */
	@NonNull
	private TypeDescriptor sourceTypeDescriptor;

	/**
	 * Return the simple type of the target type descriptor.
	 *
	 * @return {@link Class} of the {@link #targetTypeDescriptor} or {@code null} if not resolved
	 */
	public Class<?> getSimpleTargetType() {
		return isTargetTypeResolved() ? targetTypeDescriptor.getType() : null;
	}

	/**
	 * True if a singular {@link #targetTypeDescriptor} has been resolved successfully.  Usually the only case
	 * where this is not the case is when the source type was a {@link java.util.Map}.
	 */
	public boolean isTargetTypeResolved() {
		return targetTypeDescriptor != null;
	}
}
