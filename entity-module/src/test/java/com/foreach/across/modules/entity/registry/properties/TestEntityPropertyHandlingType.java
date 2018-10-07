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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertyHandlingType
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Test
	public void defaultHandlingTypeIsExtension() {
		assertThat( forProperty( descriptor ) ).isEqualTo( BINDER );
	}

	@Test
	public void nativePropertyUsesDirectAsDefault() {
		when( descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR ) ).thenReturn( true );
		assertThat( forProperty( descriptor ) ).isEqualTo( DIRECT );
	}

	@Test
	public void ifControlNameSpecifiedDefaultIsManual() {
		when( descriptor.hasAttribute( EntityAttributes.CONTROL_NAME ) ).thenReturn( true );
		assertThat( forProperty( descriptor ) ).isEqualTo( MANUAL );
		verify( descriptor, never() ).hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR );
	}

	@Test
	public void explicitAttributeHasPrecedence() {
		when( descriptor.getAttribute( EntityPropertyHandlingType.class ) ).thenReturn( EntityPropertyHandlingType.DIRECT );
		assertThat( forProperty( descriptor ) ).isEqualTo( DIRECT );
		verify( descriptor, never() ).hasAttribute( EntityAttributes.CONTROL_NAME );
		verify( descriptor, never() ).hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR );
	}
}
