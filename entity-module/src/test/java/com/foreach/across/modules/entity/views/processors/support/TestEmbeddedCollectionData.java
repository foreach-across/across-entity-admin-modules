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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NoArgsConstructor;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEmbeddedCollectionData
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private EntityPropertyDescriptor memberDescriptor;

	private EmbeddedCollectionData data;

	@Before
	public void before() {
		data = new EmbeddedCollectionData( descriptor, memberDescriptor );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void createInstanceUponAccess() {
		when( memberDescriptor.getPropertyType() ).thenReturn( (Class) MyClass.class );

		val member = data.get( "xyz" );
		assertThat( member ).isNotNull();
		assertThat( member.getSortIndex() ).isEqualTo( 0 );
		assertThat( member.getData() ).isInstanceOf( MyClass.class );
		assertThat( data.get( "xyz" ) ).isSameAs( member );
		assertThat( data.get( "uvw" ) ).isNotNull().isNotSameAs( member );

		assertThat( data.keySet() )
				.hasSize( 2 )
				.contains( "xyz", "uvw" );
	}

	@NoArgsConstructor
	public static class MyClass
	{
	}
}
