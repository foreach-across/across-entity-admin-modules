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
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.entity.registry.properties.support.EntityPropertyDescriptorUtils.getRootDescriptor;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestEntityPropertyDescriptorUtils
{
	@Test
	public void getRootDescriptorReturnsSelfIfNonNested() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		assertThat( getRootDescriptor( one ) ).isSameAs( one );

		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).build();
		assertThat( getRootDescriptor( two ) ).isSameAs( two );
	}

	@Test
	public void getRootDescriptorReturnsParentIfNested() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).parent( one ).build();
		EntityPropertyDescriptor three = EntityPropertyDescriptor.builder( "one.two.three" ).parent( two ).build();

		assertThat( getRootDescriptor( one ) ).isSameAs( one );
		assertThat( getRootDescriptor( two ) ).isSameAs( one );
		assertThat( getRootDescriptor( three ) ).isSameAs( one );
	}

	@Test
	public void findDirectChildReturnsNullIfNotNested() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).build();
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( two, one ) ).isNull();
	}

	@Test
	public void findDirectChildReturnsSelfIfDirectParent() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).parent( one ).build();
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( two, one ) ).isSameAs( two );
	}

	@Test
	public void findDirectChildReturnsNullIfParentNotPresent() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).parent( one ).build();
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( two, two ) ).isNull();
	}

	@Test
	public void findDirectChildInMultipleParents() {
		EntityPropertyDescriptor one = EntityPropertyDescriptor.builder( "one" ).build();
		EntityPropertyDescriptor two = EntityPropertyDescriptor.builder( "one.two" ).parent( one ).build();
		EntityPropertyDescriptor three = EntityPropertyDescriptor.builder( "one.two.three" ).parent( two ).build();
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( two, one ) ).isSameAs( two );
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( three, one ) ).isSameAs( two );
		assertThat( EntityPropertyDescriptorUtils.findDirectChild( three, two ) ).isSameAs( three );
	}
}
