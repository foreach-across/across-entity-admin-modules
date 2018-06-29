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

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
public class TestEntityPropertySelectorExecutor
{
	private MutableEntityPropertyRegistry propertyRegistry, productRegistry;
	private EntityPropertySelectorExecutor executor;
	private EntityPropertySelector selector;

	private List<EntityPropertyDescriptor> result;

	private EntityPropertyDescriptor id;
	private EntityPropertyDescriptor displayName;
	private EntityPropertyDescriptor name;
	private EntityPropertyDescriptor productId;
	private EntityPropertyDescriptor productTitle;
	private EntityPropertyDescriptor productDate;
	private EntityPropertyDescriptor productCatalog;

	@Before
	public void setUp() throws Exception {
		EntityPropertyRegistryProvider registryProvider = mock( EntityPropertyRegistryProvider.class );
		propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		productRegistry = mock( MutableEntityPropertyRegistry.class );

		executor = new EntityPropertySelectorExecutor( propertyRegistry, registryProvider );

		result = null;

		id = property( "id" );
		displayName = property( "displayName" );
		name = property( "name" );
		EntityPropertyDescriptor product = property( "product" );
		Mockito.<Class<?>>when( product.getPropertyType() ).thenReturn( Long.class );
		when( registryProvider.get( Long.class ) ).thenReturn( productRegistry );

		productId = property( "product.id" );
		productTitle = property( "product.title" );
		productDate = property( "product.date" );
		productCatalog = property( "productCatalog" );
	}

	private EntityPropertyDescriptor property( String name ) {
		MutableEntityPropertyDescriptor property = mock( MutableEntityPropertyDescriptor.class );
		when( property.getName() ).thenReturn( name );

		when( propertyRegistry.getProperty( name ) ).thenReturn( property );

		return property;
	}

	@Test
	public void propertyNotFound() {
		selector = EntityPropertySelector.of( "not-existing" );

		Assertions.assertThatExceptionOfType( IllegalArgumentException.class )
		          .isThrownBy( this::select );
	}

	@Test
	public void emptyResults() {
		selector = EntityPropertySelector.of();
		select();
		assertTrue( result.isEmpty() );
	}

	@Test
	public void simpleProperties() {
		selector = EntityPropertySelector.of( "id", "displayName" );
		select();

		assertResult( "id", "displayName" );
	}

	@Test
	public void duplicateSimpleProperties() {
		selector = EntityPropertySelector.of( "id", "displayName", "id" );
		select();

		assertResult( "id", "displayName" );
	}

	@Test
	public void simpleExclude() {
		selector = EntityPropertySelector.of( "id", "~displayName" );
		select();

		assertResult( "id" );
	}

	@Test
	public void allWithDefaultFilter() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, name ) );

		selector = EntityPropertySelector.of( "*" );
		select();

		assertResult( "displayName", "name" );
	}

	@Test
	public void allWithAdditionalAndExclude() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, name ) );

		selector = EntityPropertySelector.of( "*", "id", "name", "~displayName" );
		select();

		assertResult( "name", "id" );
	}

	@Test
	public void allRegistered() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		selector = EntityPropertySelector.of( "**" );
		select();

		assertResult( "id", "displayName", "name" );
	}

	@Test
	public void allRegisteredWithAdditionalAndExclude() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		selector = EntityPropertySelector.of( "product.title", "**", "~id" );
		select();

		assertResult( "product.title", "displayName", "name" );
	}

	@Test
	public void allFromNested() {
		EntityPropertySelector subSelector = EntityPropertySelector.all();
		List<EntityPropertyDescriptor> descriptors
				= Arrays.asList( nestedProperty( "id" ), nestedProperty( "title" ), nestedProperty( "date" ) );

		when( productRegistry.select( subSelector ) ).thenReturn( descriptors );

		selector = EntityPropertySelector.of( "id", "product.*", "~product.date" );
		select();

		assertResult( "id", "product.id", "product.title" );
	}

	@Test
	public void allPropertiesStartingWith() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, productId, productTitle, productDate, productCatalog ) );

		selector = EntityPropertySelector.of( "product*" );
		select();

		assertResult( "product.id", "product.title", "product.date", "productCatalog" );
	}

	@Test
	public void allRegisteredPropertiesStartingWith() {
		when( propertyRegistry.getProperties() ).thenReturn( Arrays.asList( displayName, productId ) );
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( productDate, productCatalog, id) );

		selector = EntityPropertySelector.of( "product**", "product*" );
		select();

		assertResult( "product.date", "productCatalog", "product.id" );
	}

	private EntityPropertyDescriptor nestedProperty( String name ) {
		EntityPropertyDescriptor property = mock( EntityPropertyDescriptor.class );
		when( property.getName() ).thenReturn( name );

		return property;
	}

	@Test
	public void registeredFromNested() {
		EntityPropertySelector subSelector = EntityPropertySelector.of( "**" );
		List<EntityPropertyDescriptor> descriptors
				= Arrays.asList( nestedProperty( "id" ), nestedProperty( "title" ), nestedProperty( "date" ) );

		when( productRegistry.select( subSelector ) ).thenReturn( descriptors );

		selector = EntityPropertySelector.of( "id", "product.**", "~product.date" );
		select();

		assertResult( "id", "product.id", "product.title" );
	}

	@Test
	public void allRegisteredWithAdditionalFilter() {
		when( propertyRegistry.getRegisteredDescriptors() ).thenReturn( Arrays.asList( id, displayName, name ) );

		selector = EntityPropertySelector.builder()
		                                 .predicate( entityPropertyDescriptor -> !"displayName".equals( entityPropertyDescriptor.getName() ) )
		                                 .properties( "product.title", "**", "~id" )
		                                 .build();

		select();

		assertResult( "product.title", "name" );
	}

	private void select() {
		result = executor.select( selector );
	}

	private void assertResult( String... expected ) {
		assertNotNull( expected );
		assertEquals( expected.length, result.size() );

		for ( int i = 0; i < expected.length; i++ ) {
			assertEquals( expected[i], result.get( i ).getName() );
		}
	}
}
