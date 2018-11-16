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

import java.util.List;
import java.util.Map;

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
	MutableEntityPropertyRegistry userWithCityAddressProperties;
	MutableEntityPropertyRegistry cityProperties;
	MutableEntityPropertyRegistry cityAddressProperties;
	MutableEntityPropertyRegistry collectionsProperties;

	EntityPropertyDescriptor userAddress;
	EntityPropertyDescriptor userAddressStreet;
	EntityPropertyDescriptor addressStreet;
	EntityPropertyDescriptor addressCity;
	EntityPropertyDescriptor userAddressCity;
	EntityPropertyDescriptor cityName;
	EntityPropertyDescriptor addressCityName;
	EntityPropertyDescriptor cityAddressCity;
	EntityPropertyDescriptor cityAddressCityName;
	EntityPropertyDescriptor userWithCityAddressCityAddress;
	EntityPropertyDescriptor userWithCityAddressCityAddressCity;
	EntityPropertyDescriptor userWithCityAddressCityAddressCityName;

	@Before
	@SuppressWarnings("unchecked")
	public void createRegistries() {
		EntityPropertyRegistryProvider registryProvider = DefaultEntityPropertyRegistryProvider.INSTANCE;
		addressProperties = registryProvider.get( Address.class );
		userProperties = registryProvider.get( User.class );
		cityProperties = registryProvider.get( City.class );
		cityAddressProperties = registryProvider.get( CityAddress.class );
		collectionsProperties = registryProvider.get( CollectionsHolder.class );
		userWithCityAddressProperties = registryProvider.get( UserWithCityAddress.class );

		addressStreet = addressProperties.getProperty( "street" );
		userAddress = userProperties.getProperty( "address" );
		userAddressStreet = userProperties.getProperty( "address.street" );

		builder( "city" )
				.controller( ctl -> ctl.withTarget( CityAddress.class, City.class ).createDtoFunction( city -> new City( city.name ) ) )
				.apply( cityAddressProperties.getProperty( "city" ) );

		cityAddressCity = cityAddressProperties.getProperty( "city" );

		addressProperties.register( builder( "city" ).propertyType( City.class ).build() );

		addressCity = addressProperties.getProperty( "city" );
		addressCityName = addressProperties.getProperty( "city.name" );
		userAddressCity = userProperties.getProperty( "address.city" );
		cityName = cityProperties.getProperty( "name" );
		cityAddressCityName = cityAddressProperties.getProperty( "city.name" );
		userWithCityAddressCityAddress = userWithCityAddressProperties.getProperty( "address" );
		userWithCityAddressCityAddressCity = userWithCityAddressProperties.getProperty( "address.city" );
		userWithCityAddressCityAddressCityName = userWithCityAddressProperties.getProperty( "address.city.name" );

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

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CityAddress
	{
		private City city;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserWithCityAddress
	{
		private CityAddress address;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CollectionsHolder
	{
		private String name;

		private List<City> cities;

		private Map<String, Address> addressMap;
	}
}
