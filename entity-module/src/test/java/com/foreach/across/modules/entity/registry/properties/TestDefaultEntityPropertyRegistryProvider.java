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

import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider.PropertiesRegistrar;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDefaultEntityPropertyRegistryProvider
{
	@Mock
	private EntityPropertyDescriptorFactory descriptorFactory;

	@Spy
	private DefaultEntityPropertyRegistryProvider provider
			= new DefaultEntityPropertyRegistryProvider( descriptorFactory );

	@Test
	public void createDispatchesToPropertiesRegistrars() {
		PropertiesRegistrar one = mock( PropertiesRegistrar.class );
		PropertiesRegistrar two = mock( PropertiesRegistrar.class );

		provider.setPropertiesRegistrars( Arrays.asList( one, two ) );

		MutableEntityPropertyRegistry registry = provider.create( String.class );
		assertNotNull( registry );

		InOrder ordered = inOrder( one, two );
		ordered.verify( one ).accept( String.class, registry );
		ordered.verify( two ).accept( String.class, registry );
	}

	@Test
	public void createAlwaysReturnsNewInstance() {
		MutableEntityPropertyRegistry first = provider.create( String.class );
		assertNotNull( first );
		assertNotSame( first, provider.create( String.class ) );
	}

	@Test
	public void getReturnsSameInstanceButCallsCreateOnlyOnce() {
		MutableEntityPropertyRegistry registry = mock( MutableEntityPropertyRegistry.class );
		when( provider.create( String.class ) ).thenReturn( registry );

		MutableEntityPropertyRegistry first = provider.get( String.class );
		assertSame( registry, first );
		assertSame( registry, provider.get( String.class ) );

		verify( provider, times( 1 ) ).create( String.class );
	}

	@Test
	public void createForParentReturnsMergingRegistry() {
		MutableEntityPropertyRegistry parent = mock( MutableEntityPropertyRegistry.class );
		MutableEntityPropertyRegistry registry = provider.createForParentRegistry( parent );

		assertNotNull( registry );
		assertTrue( registry instanceof MergingEntityPropertyRegistry );
		assertNotSame( registry, provider.createForParentRegistry( parent ) );

		registry.getDefaultFilter();
		verify( parent ).getDefaultFilter();
	}
}
