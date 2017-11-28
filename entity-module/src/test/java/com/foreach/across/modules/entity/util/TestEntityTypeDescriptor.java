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

import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
public class TestEntityTypeDescriptor
{
	@Test
	public void defaultValues() {
		EntityTypeDescriptor descriptor = EntityTypeDescriptor.builder().sourceTypeDescriptor( TypeDescriptor.valueOf( String.class ) ).build();
		assertEquals( TypeDescriptor.valueOf( String.class ), descriptor.getSourceTypeDescriptor() );
		assertFalse( descriptor.isCollection() );
		assertFalse( descriptor.isTargetTypeResolved() );
		assertNull( descriptor.getTargetTypeDescriptor() );
		assertNull( descriptor.getSimpleTargetType() );
	}

	@Test
	public void targetType() {
		EntityTypeDescriptor descriptor = EntityTypeDescriptor.builder().sourceTypeDescriptor( TypeDescriptor.valueOf( String.class ) )
		                                                      .targetTypeDescriptor( TypeDescriptor.valueOf( Long.class ) )
		                                                      .collection( true )
		                                                      .build();
		assertEquals( TypeDescriptor.valueOf( String.class ), descriptor.getSourceTypeDescriptor() );
		assertTrue( descriptor.isCollection() );
		assertTrue( descriptor.isTargetTypeResolved() );
		assertEquals( TypeDescriptor.valueOf( Long.class ), descriptor.getTargetTypeDescriptor() );
		assertEquals( Long.class, descriptor.getSimpleTargetType() );
	}
}
