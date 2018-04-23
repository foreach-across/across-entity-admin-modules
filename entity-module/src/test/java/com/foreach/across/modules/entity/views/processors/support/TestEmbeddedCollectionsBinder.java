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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEmbeddedCollectionsBinder
{
	@Mock
	private EntityPropertyRegistry propertyRegistry;

	@InjectMocks
	private EmbeddedCollectionsBinder binder;

	@Test
	public void exceptionIfPropertyDoesNotExist() {
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> binder.get( "myprop" ) )
				.withMessage( "No such property descriptor: 'myprop'" );

		assertThat( binder.containsKey( "myprop" ) ).isFalse();
	}

	@Test
	public void exceptionIfIndexerPropertyDoesNotExist() {
		when( propertyRegistry.getProperty( "myprop" ) ).thenReturn( mock( EntityPropertyDescriptor.class ) );

		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> binder.get( "myprop" ) )
				.withMessage( "Property 'myprop' does not represent a collection" );

		assertThat( binder.containsKey( "myprop" ) ).isFalse();
	}

	@Test
	public void entryGetsCreatedIfPropertyExists() {
		when( propertyRegistry.getProperty( "myprop" ) ).thenReturn( mock( EntityPropertyDescriptor.class ) );
		when( propertyRegistry.getProperty( "myprop[]" ) ).thenReturn( mock( EntityPropertyDescriptor.class ) );

		assertThat( binder.containsKey( "myprop" ) ).isFalse();

		val data = binder.get( "myprop" );
		assertThat( data ).isNotNull();
		assertThat( binder.containsKey( "myprop" ) ).isTrue();
		assertThat( binder.get( "myprop" ) ).isSameAs( data );
	}
}
