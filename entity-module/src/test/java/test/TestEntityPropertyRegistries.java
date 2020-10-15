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
package test;

import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.registry.properties.registrars.DefaultPropertiesRegistrar;
import com.foreach.across.modules.entity.registry.properties.registrars.TestEntityPropertyRegistryDefaultPropertiesBuilder.Address;
import com.foreach.across.modules.entity.registry.properties.registrars.TestEntityPropertyRegistryDefaultPropertiesBuilder.Customer;
import com.foreach.across.modules.entity.views.support.SpelValueFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { TestEntityPropertyRegistries.Config.class })
public class TestEntityPropertyRegistries
{
	@Autowired
	private DefaultEntityPropertyRegistryProvider entityPropertyRegistryProvider;

	@Test
	public void customPropertyAndValueFetcher() {
		MutableEntityPropertyRegistry parent = entityPropertyRegistryProvider.create( Customer.class );
		MutableEntityPropertyRegistry registry = entityPropertyRegistryProvider.createForParentRegistry( parent );

		SimpleEntityPropertyDescriptor calculated = new SimpleEntityPropertyDescriptor( "address.size()" );
		calculated.setValueFetcher( new SpelValueFetcher( "address.size()" ) );
		registry.register( calculated );

		Customer customer = new Customer();
		customer.setName( "some name" );
		customer.setSomeValue( "some value" );
		customer.setId( 123 );

		Address address = new Address();
		address.setStreet( "my street" );
		address.setNumber( 666 );
		customer.setAddress( address );

		assertEquals( "some name", fetch( registry, customer, "name" ) );
		assertEquals( "some name (123)", fetch( registry, customer, "displayName" ) );
		assertEquals( "my street", fetch( registry, customer, "address.street" ) );
		assertEquals( 9, fetch( registry, customer, "address.size()" ) );
	}

	@Test
	public void wildcardShouldNeverReturnNested() {
		MutableEntityPropertyRegistry registry = entityPropertyRegistryProvider.create( Customer.class );
		MutableEntityPropertyDescriptor descriptor = registry.getProperty( "address.street" );
		registry.register( descriptor );

		List<EntityPropertyDescriptor> descriptors = registry.select( new EntityPropertySelector( "*" ) );
		assertFalse( descriptors.contains( descriptor ) );
	}

	@SuppressWarnings("unchecked")
	private Object fetch( EntityPropertyRegistry registry, Object entity, String propertyName ) {
		return registry.getProperty( propertyName ).getValueFetcher().getValue( entity );
	}

	@Configuration
	public static class Config
	{
		@Bean
		public DefaultEntityPropertyRegistryProvider entityPopertyRegistryProvider() {
			return new DefaultEntityPropertyRegistryProvider( entityPropertyDescriptorFactory() );
		}

		@Bean
		public EntityPropertyDescriptorFactory entityPropertyDescriptorFactory() {
			return new EntityPropertyDescriptorFactoryImpl();
		}

		@Bean
		public DefaultPropertiesRegistrar entityPropertyRegistryDefaultPropertiesBuilder() {
			return new DefaultPropertiesRegistrar( entityPropertyDescriptorFactory() );
		}
	}
}
