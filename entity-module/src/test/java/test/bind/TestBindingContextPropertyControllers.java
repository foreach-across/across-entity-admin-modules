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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import org.junit.Test;

import java.util.Arrays;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext.forReading;
import static com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext.forUpdating;
import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestBindingContextPropertyControllers extends AbstractEntityPropertyBindingContextTest
{
	@Test
	public void fetchPropertyValue() {
		User user = new User( new Address( "street one" ) );

		Address otherAddress = new Address( "street two" );
		assertThat( addressStreet.getController().fetchValue( forReading( otherAddress ) ) ).isEqualTo( "street two" );
		otherAddress.setStreet( "street updated" );
		assertThat( addressStreet.getController().fetchValue( forReading( otherAddress ) ) ).isEqualTo( "street updated" );

		assertThat( userAddress.getController().fetchValue( forReading( user ) ) ).isEqualTo( new Address( "street one" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void defaultSimpleBulkFetching() {
		assertThat( addressStreet.getController().isOptimizedForBulkValueFetching() ).isFalse();
		assertThat( userAddress.getController().isOptimizedForBulkValueFetching() ).isFalse();

		EntityPropertyBindingContext addressOne = forReading( new Address( "street one" ) );
		EntityPropertyBindingContext addressTwo = forReading( new Address( "street two" ) );
		assertThat( addressStreet.getController().fetchValues( Arrays.asList( addressOne, addressTwo ) ) )
				.containsOnly( entry( addressOne, "street one" ), entry( addressTwo, "street two" ) );

		EntityPropertyBindingContext userOne = forReading( new User( new Address( "street one" ) ) );
		EntityPropertyBindingContext userTwo = forReading( new User( new Address( "street two" ) ) );
		assertThat( userAddress.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, new Address( "street one" ) ), entry( userTwo, new Address( "street two" ) ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void defaultNestedBulkFetching() {
		assertThat( userAddressStreet.getController().isOptimizedForBulkValueFetching() ).isFalse();

		EntityPropertyBindingContext userOne = forReading( new User( new Address( "street one" ) ) );
		EntityPropertyBindingContext userTwo = forReading( new User( new Address( "street two" ) ) );
		assertThat( userAddressStreet.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, "street one" ), entry( userTwo, "street two" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void bulkFetchingOnFirstLevelIsUsedForChildren() {
		builder( userAddress.getName() )
				.controller(
						ctl -> ctl.withTarget( User.class, Address.class )
						          .bulkValueFetcher( users -> users.stream().collect(
								          toMap( identity(), u -> new Address( "user-bulk:" + u.getAddress().getStreet() ) ) ) )
				)
				.apply( (MutableEntityPropertyDescriptor) userAddress );

		assertThat( userAddress.getController().isOptimizedForBulkValueFetching() ).isTrue();
		assertThat( addressStreet.getController().isOptimizedForBulkValueFetching() ).isFalse();
		assertThat( userAddressStreet.getController().isOptimizedForBulkValueFetching() ).isTrue();

		EntityPropertyBindingContext userOne = forReading( new User( new Address( "street one" ) ) );
		EntityPropertyBindingContext userTwo = forReading( new User( new Address( "street two" ) ) );
		assertThat( userAddress.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, new Address( "user-bulk:street one" ) ), entry( userTwo, new Address( "user-bulk:street two" ) ) );
		assertThat( userAddressStreet.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, "user-bulk:street one" ), entry( userTwo, "user-bulk:street two" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void bulkFetchingOnChildLevelIsUsedForParent() {
		builder( addressStreet.getName() )
				.controller(
						ctl -> ctl.withTarget( Address.class, String.class )
						          .bulkValueFetcher( addresses -> addresses.stream().collect( toMap( identity(), a -> "bulk:" + a.getStreet() ) ) )
				)
				.apply( (MutableEntityPropertyDescriptor) addressStreet );
		assertThat( addressStreet.getController().isOptimizedForBulkValueFetching() ).isTrue();
		assertThat( userAddress.getController().isOptimizedForBulkValueFetching() ).isFalse();
		assertThat( userAddressStreet.getController().isOptimizedForBulkValueFetching() ).isTrue();

		EntityPropertyBindingContext addressOne = forReading( new Address( "street one" ) );
		EntityPropertyBindingContext addressTwo = forReading( new Address( "street two" ) );
		assertThat( addressStreet.getController().fetchValues( Arrays.asList( addressOne, addressTwo ) ) )
				.containsOnly( entry( addressOne, "bulk:street one" ), entry( addressTwo, "bulk:street two" ) );

		EntityPropertyBindingContext userOne = forReading( new User( new Address( "street one" ) ) );
		EntityPropertyBindingContext userTwo = forReading( new User( new Address( "street two" ) ) );
		assertThat( userAddress.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, new Address( "street one" ) ), entry( userTwo, new Address( "street two" ) ) );
		assertThat( userAddressStreet.getController().fetchValues( Arrays.asList( userOne, userTwo ) ) )
				.containsOnly( entry( userOne, "bulk:street one" ), entry( userTwo, "bulk:street two" ) );
	}

	@Test
	public void fetchNestedPropertyValue() {
		City city = new City( "Brussels" );
		CityAddress address = new CityAddress( city );
		UserWithCityAddress user = new UserWithCityAddress( address );

		assertThat( cityName.getController().fetchValue( forReading( city ) ) ).isEqualTo( "Brussels" );
		assertThat( cityName.getController().fetchValue( EntityPropertyBindingContext.forUpdating( city, city ) ) ).isEqualTo( "Brussels" );
		assertThat( cityName.getController().fetchValue( EntityPropertyBindingContext.forUpdating( null, city ) ) ).isEqualTo( "Brussels" );

		assertThat( cityAddressCity.getController().fetchValue( forReading( address ) ) ).isEqualTo( city );
		assertThat( cityAddressCity.getController().fetchValue( forUpdating( address, address ) ) ).isEqualTo( city );
		assertThat( cityAddressCity.getController().fetchValue( forUpdating( null, address ) ) ).isEqualTo( city );
		assertThat( cityAddressCityName.getController().fetchValue( forReading( address ) ) ).isEqualTo( "Brussels" );
		assertThat( cityAddressCityName.getController().fetchValue( forUpdating( address, address ) ) ).isEqualTo( "Brussels" );
		assertThat( cityAddressCityName.getController().fetchValue( forUpdating( null, address ) ) ).isEqualTo( "Brussels" );

		EntityPropertyBindingContext userContext = forReading( user );
		assertThat( userWithCityAddressCityAddress.getController().fetchValue( userContext ) ).isEqualTo( address );
		assertThat( userWithCityAddressCityAddressCity.getController().fetchValue( userContext ) ).isEqualTo( city );
		assertThat( userWithCityAddressCityAddressCityName.getController().fetchValue( userContext ) ).isEqualTo( "Brussels" );
		userContext = forUpdating( user, user );
		assertThat( userWithCityAddressCityAddress.getController().fetchValue( userContext ) ).isEqualTo( address );
		assertThat( userWithCityAddressCityAddressCity.getController().fetchValue( userContext ) ).isEqualTo( city );
		assertThat( userWithCityAddressCityAddressCityName.getController().fetchValue( userContext ) ).isEqualTo( "Brussels" );
		userContext = forUpdating( null, user );
		assertThat( userWithCityAddressCityAddress.getController().fetchValue( userContext ) ).isEqualTo( address );
		assertThat( userWithCityAddressCityAddressCity.getController().fetchValue( userContext ) ).isEqualTo( city );
		assertThat( userWithCityAddressCityAddressCityName.getController().fetchValue( userContext ) ).isEqualTo( "Brussels" );
	}

	@Test
	public void childContextValueIsCachedIfReadonly() {
		User user = new User( new Address( "street one" ) );
		EntityPropertyBindingContext bindingContext = forReading( user );

		assertThat( userAddressStreet.getController().fetchValue( bindingContext ) ).isEqualTo( "street one" );
		user.setAddress( new Address( "modified" ) );
		assertThat( userAddressStreet.getController().fetchValue( bindingContext ) ).isEqualTo( "street one" );
	}

	@Test
	public void childContextValueIsNotCachedIfReadonly() {
		User user = new User( new Address( "street one" ) );
		EntityPropertyBindingContext bindingContext = EntityPropertyBindingContext.forUpdating( user, user );

		assertThat( userAddressStreet.getController().fetchValue( bindingContext ) ).isEqualTo( "street one" );
		user.setAddress( new Address( "modified" ) );
		assertThat( userAddressStreet.getController().fetchValue( bindingContext ) ).isEqualTo( "modified" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void applyValueOfNestedProperty() {
		User user = new User( new Address( "street one" ) );
		assertThat( userAddressStreet.getController().applyValue( EntityPropertyBindingContext.forUpdating( user, user ),
		                                                          new EntityPropertyValue<>( null, "modified", false ) ) ).isTrue();

		assertThat( user.getAddress().getStreet() ).isEqualTo( "modified" );
	}
}
