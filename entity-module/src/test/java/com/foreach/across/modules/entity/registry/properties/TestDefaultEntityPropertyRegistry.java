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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.collections.Sets;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestDefaultEntityPropertyRegistry
{
	@Mock
	private EntityPropertyRegistryProvider registryProvider;

	private DefaultEntityPropertyRegistry registry;

	@Before
	public void before() {
		registry = new DefaultEntityPropertyRegistry( registryProvider );

		register( "id", Long.class );
		register( "name", String.class );
		register( "created", Date.class );
	}

	@Test
	public void javaBeansSpecificationAllowsAllCapsPropertyNames() {
		register( "URL", String.class );

		// if second char of property requested is uppercase, assume registered as all-caps property
		EntityPropertyDescriptor descriptor = registry.getProperty( "URL" );
		assertNotNull( descriptor );
		assertSame( descriptor, registry.getProperty( "uRL" ) );

		assertNull( registry.getProperty( "url" ) );
	}

	@Test
	public void initialOrderIsKeptAsFallback() {
		registry.setDefaultOrder( "created", "name", "id" );
		assertEquals(
				Arrays.asList( "created", "name", "id" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);

		registry.setDefaultOrder( "name" );
		assertEquals(
				Arrays.asList( "name", "created", "id" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void defaultFilterReturnsNonHidden() {
		registry.getProperty( "id" ).setHidden( true );

		assertEquals(
				Sets.newSet( "name", "created" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toSet() )
		);
	}

	@Test
	public void customDefaultFilter() {
		registry.setDefaultFilter( entityPropertyDescriptor -> true );
		assertEquals(
				Sets.newSet( "name", "created", "id" ),
				registry.getProperties()
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toSet() )
		);
	}

	@Test
	public void customIncludeFilterKeepsTheDefaultOrder() {
		registry.setDefaultOrder( "created", "name", "id" );

		assertEquals(
				Arrays.asList( "created", "name", "id" ),
				registry.getProperties( entityPropertyDescriptor -> Arrays.asList( "name", "created", "id" ).contains( entityPropertyDescriptor.getName() ) )
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void customExcludeFilterKeepsTheDefaultOrder() {
		registry.setDefaultOrder( "created", "name", "id" );

		assertEquals(
				Arrays.asList( "created", "id" ),
				registry.getProperties( entityPropertyDescriptor -> !"name".equals( entityPropertyDescriptor.getName() ) )
				        .stream().map( EntityPropertyDescriptor::getName ).collect( toList() )
		);
	}

	@Test
	public void nestedProperty() {
		MutableEntityPropertyRegistry nested = mock( MutableEntityPropertyRegistry.class );
		when( registryProvider.get( Date.class ) ).thenReturn( nested );

		MutableEntityPropertyDescriptor timestamp = mock( MutableEntityPropertyDescriptor.class );
		when( timestamp.getDisplayName() ).thenReturn( "Nested display name" );
		when( nested.getProperty( "timestamp" ) ).thenReturn( timestamp );

		SimpleEntityPropertyDescriptor descriptor
				= (SimpleEntityPropertyDescriptor) registry.getProperty( "created.timestamp" );
		assertNotNull( descriptor );
		assertEquals( "Nested display name", descriptor.getDisplayName() );

		assertSame( descriptor, registry.getProperty( "created.timestamp" ) );
	}

	@Test
	public void nestedPropertyImpossibleIfNoRegistryProvider() {
		registry = new DefaultEntityPropertyRegistry();

		register( "name", String.class );
		EntityPropertyDescriptor l = register( "name.length", String.class );
		register( "created", Date.class );

		assertNull( registry.getProperty( "created.timestamp" ) );
		assertSame( l, registry.getProperty( "name.length" ) );
	}

	private SimpleEntityPropertyDescriptor register( String name, Class type ) {
		SimpleEntityPropertyDescriptor descriptor = new SimpleEntityPropertyDescriptor( name );
		descriptor.setPropertyType( type );
		descriptor.setHidden( false );

		registry.register( descriptor );

		return descriptor;
	}
}
