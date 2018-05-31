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

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.*;

import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents a single embedded collection property.
 *
 * @author Arne Vandamme
 * @see EmbeddedCollectionsBinder
 * @since 3.1.0
 */
@Deprecated
@RequiredArgsConstructor
public class EmbeddedCollectionData extends HashMap<String, EmbeddedCollectionData.Member>
{
	@NonNull
	private final EntityPropertyDescriptor descriptor;

	@NonNull
	private final EntityPropertyDescriptor memberDescriptor;

	public Map<String, Member> getItem() {
		return this;
	}

	@Override
	public Member get( Object key ) {
		Member existing = super.get( key );

		if ( existing == null ) {
			existing = new Member( createMember() );
			put( (String) key, existing );
		}

		return existing;
	}

	/**
	 * Apply the collection property to the target entity.
	 *
	 * @param target entity
	 */
	@SneakyThrows
	public void apply( Object target ) {
		// todo: convert to the target type
		val property = descriptor.getAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR, PropertyDescriptor.class );

		List<Member> members = new ArrayList<>( values() );
		members.sort( Comparator.comparingInt( Member::getSortIndex ) );

		if ( property.getWriteMethod() != null ) {
			property.getWriteMethod().invoke( target, members.stream().map( Member::getData ).collect( Collectors.toSet() ) );
		}
	}

	// todo: or use a supplier
	@SneakyThrows
	private Object createMember() {
		return memberDescriptor.getPropertyType().newInstance();
	}

	@Getter
	@RequiredArgsConstructor
	public static class Member
	{
		private final Object data;

		@Setter
		private int sortIndex;
	}
}

