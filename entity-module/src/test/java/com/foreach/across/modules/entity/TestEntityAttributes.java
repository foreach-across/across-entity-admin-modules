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

package com.foreach.across.modules.entity;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityAttributes
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Test
	public void noControlName() {
		assertNull( EntityAttributes.controlName( descriptor ) );
	}

	@Test
	public void nameAsControlName() {
		when( descriptor.getName() ).thenReturn( "propertyName" );
		assertEquals( "propertyName", EntityAttributes.controlName( descriptor ) );
	}

	@Test
	public void specificControlName() {
		when( descriptor.getName() ).thenReturn( "propertyName" );
		when( descriptor.getAttribute( EntityAttributes.CONTROL_NAME, String.class ) ).thenReturn( "controlName" );
		assertEquals( "controlName", EntityAttributes.controlName( descriptor ) );
	}
}
