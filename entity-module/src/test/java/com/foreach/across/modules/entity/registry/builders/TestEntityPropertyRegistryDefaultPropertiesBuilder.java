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

package com.foreach.across.modules.entity.registry.builders;

import com.foreach.across.modules.entity.registry.properties.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertyRegistryDefaultPropertiesBuilder
{
	@Mock
	private EntityPropertyRegistryProvider registryProvider;

	private DefaultEntityPropertyRegistry propertyRegistry;

	@Before
	public void before() {
		propertyRegistry = new DefaultEntityPropertyRegistry( registryProvider );
		new EntityPropertyRegistryDefaultPropertiesBuilder( new EntityPropertyDescriptorFactoryImpl() )
				.buildRegistry( Customer.class, propertyRegistry );
	}

	@Test
	public void allPropertiesAreRegistered() {
		Collection<String> propertyNames = propertyRegistry.getRegisteredDescriptors()
		                                                   .stream()
		                                                   .map( EntityPropertyDescriptor::getName )
		                                                   .collect( Collectors.toList() );
		assertEquals( 6, propertyNames.size() );
		assertTrue( propertyNames.contains( "id" ) );
		assertTrue( propertyNames.contains( "name" ) );
		assertTrue( propertyNames.contains( "displayName" ) );
		assertTrue( propertyNames.contains( "someValue" ) );
		assertTrue( propertyNames.contains( "class" ) );
		assertTrue( propertyNames.contains( "address" ) );
	}

	@Test
	public void readonlyPropertiesAreHiddenByDefault() {
		Collection<String> propertyNames = propertyRegistry.getProperties()
		                                                   .stream()
		                                                   .map( EntityPropertyDescriptor::getName )
		                                                   .collect( Collectors.toList() );

		assertEquals( 3, propertyNames.size() );
		assertTrue( propertyNames.contains( "id" ) );
		assertTrue( propertyNames.contains( "name" ) );
		assertTrue( propertyNames.contains( "address" ) );
	}

	@Test
	public void defaultOrderIsAccordingToDeclarationIfNotSpecified() {
		List<String> propertyNames = propertyRegistry.getProperties( EntityPropertyFilters.NOOP )
		                                             .stream()
		                                             .map( EntityPropertyDescriptor::getName )
		                                             .collect( Collectors.toList() );

		assertEquals( 6, propertyNames.size() );
		assertEquals( "name", propertyNames.get( 0 ) );
		assertEquals( "address", propertyNames.get( 1 ) );
		assertEquals( "displayName", propertyNames.get( 2 ) );
		assertEquals( "someValue", propertyNames.get( 3 ) );
		assertEquals( "id", propertyNames.get( 4 ) );
		assertEquals( "class", propertyNames.get( 5 ) );
	}

	@Test
	public void valueFetchersAreCreated() {
		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );

		customer.setAddress( address );

		assertEquals( "some name", fetch( customer, "name" ) );
		assertEquals( "some name (123)", fetch( customer, "displayName" ) );

		DefaultEntityPropertyRegistry addressRegistry = new DefaultEntityPropertyRegistry( registryProvider );
		new EntityPropertyRegistryDefaultPropertiesBuilder( new EntityPropertyDescriptorFactoryImpl() )
				.buildRegistry( Address.class, addressRegistry );

		when( registryProvider.getOrCreate( Address.class ) ).thenReturn( addressRegistry );

		assertEquals( "my street", fetch( customer, "address.street" ) );
		assertNull( propertyRegistry.getProperty( "address.size()" ) );
	}

	@SuppressWarnings("unchecked")
	private Object fetch( Object entity, String propertyName ) {
		return propertyRegistry.getProperty( propertyName ).getValueFetcher().getValue( entity );
	}

	public static class Address
	{
		private String street;
		private int number;

		public String getStreet() {
			return street;
		}

		public void setStreet( String street ) {
			this.street = street;
		}

		public int getNumber() {
			return number;
		}

		public void setNumber( int number ) {
			this.number = number;
		}

		public int size() {
			return street.length();
		}
	}

	public abstract static class BaseCustomer
	{
		private long id;

		public long getId() {
			return id;
		}

		public void setId( long id ) {
			this.id = id;
		}
	}

	public static class Customer extends BaseCustomer
	{
		private String name;
		private Address address;
		private Object value;

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}

		public Address getAddress() {
			return address;
		}

		public void setAddress( Address address ) {
			this.address = address;
		}

		public String getDisplayName() {
			return String.format( "%s (%s)", getName(), getId() );
		}

		public void setSomeValue( String someValue ) {
			value = someValue;
		}
	}

}
