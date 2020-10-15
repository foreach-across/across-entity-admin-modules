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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.HashMap;

import static com.foreach.across.modules.entity.bind.EntityPropertyBindingType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@ExtendWith(MockitoExtension.class)
public class TestEntityPropertyBindingType
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Test
	public void typeDescriptorBased() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( String.class ) );
		assertThat( EntityPropertyBindingType.forProperty( descriptor ) ).isEqualTo( SINGLE_VALUE );

		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( ArrayList.class ) );
		assertThat( EntityPropertyBindingType.forProperty( descriptor ) ).isEqualTo( COLLECTION );

		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( HashMap.class ) );
		assertThat( EntityPropertyBindingType.forProperty( descriptor ) ).isEqualTo( MAP );
	}

	@Test
	public void attributeIsUsedInsteadOfDefault() {
		when( descriptor.getAttribute( EntityPropertyBindingType.class ) ).thenReturn( EntityPropertyBindingType.SINGLE_VALUE );
		assertThat( EntityPropertyBindingType.forProperty( descriptor ) ).isEqualTo( SINGLE_VALUE );

		when( descriptor.getAttribute( EntityPropertyBindingType.class ) ).thenReturn( EntityPropertyBindingType.COLLECTION );
		assertThat( EntityPropertyBindingType.forProperty( descriptor ) ).isEqualTo( COLLECTION );

		verify( descriptor, never() ).getPropertyTypeDescriptor();
	}
}
