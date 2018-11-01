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

package test.bind;

import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import lombok.*;
import org.junit.Before;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
abstract class AbstractEntityPropertyBindingContextTest
{
	MutableEntityPropertyRegistry addressProperties;
	MutableEntityPropertyRegistry userProperties;

	EntityPropertyDescriptor userAddress;
	EntityPropertyDescriptor userAddressStreet;
	EntityPropertyDescriptor addressStreet;
	EntityPropertyDescriptor addressCity;
	EntityPropertyDescriptor userAddressCity;

	@Before
	@SuppressWarnings("unchecked")
	public void createRegistries() {
		EntityPropertyRegistryProvider registryProvider = DefaultEntityPropertyRegistryProvider.INSTANCE;
		addressProperties = registryProvider.get( Address.class );
		userProperties = registryProvider.get( User.class );

		addressStreet = addressProperties.getProperty( "street" );
		userAddress = userProperties.getProperty( "address" );
		userAddressStreet = userProperties.getProperty( "address.street" );

		addressProperties.register( builder( "city" ).propertyType( City.class ).build() );

		addressCity = addressProperties.getProperty( "city" );
		userAddressCity = userProperties.getProperty( "address.city" );

		assertThat( addressCity ).isNotNull();
		assertThat( userAddressCity ).isNotNull();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class User
	{
		private Address address;
	}

	@Data
	@NoArgsConstructor
	@EqualsAndHashCode(exclude = "updateCount")
	public static class Address
	{
		@Setter(value = AccessLevel.NONE)
		private int updateCount;

		private String street;

		public Address( String street ) {
			this.street = street;
		}

		public void setStreet( String street ) {
			this.street = street;
			updateCount++;
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class City
	{
		private String name;
	}
}
