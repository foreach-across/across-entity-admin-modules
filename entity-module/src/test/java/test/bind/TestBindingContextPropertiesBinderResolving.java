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

import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.SingleEntityPropertyBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestBindingContextPropertiesBinderResolving extends AbstractEntityPropertyBindingContextTest
{
	@Test
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	public void identicalPropertyBindersAreReturnedForDirectOrIndirectNestedPropertyAccess() {
		EntityPropertiesBinder userBinder = new EntityPropertiesBinder( userProperties );
		SingleEntityPropertyBinder addressBinder = (SingleEntityPropertyBinder) userBinder.get( "address" );
		EntityPropertyBinder streetBinder = addressBinder.getProperties().get( "street" );

		assertThat( userBinder.get( "address.street" ) ).isNotNull().isSameAs( streetBinder );
		assertThat( userBinder.get( userAddress ) ).isSameAs( addressBinder );
		assertThat( userBinder.get( userAddressStreet ) ).isSameAs( streetBinder );
		assertThat( addressBinder.getProperties().get( userAddressStreet ) ).isSameAs( streetBinder );
		assertThat( addressBinder.getProperties().get( addressStreet ) ).isSameAs( streetBinder );
	}

	@Test
	public void simpleNativeProperty() {
		Address original = new Address( "some street" );
		Address target = new Address( "some street" );

		assertThat( addressStreet.getPropertyValue( target ) ).isEqualTo( "some street" );

		// create a properties binder for the address entity
		EntityPropertiesBinder addressBinder = new EntityPropertiesBinder( addressProperties );
		addressBinder.setEntity( original );
		addressBinder.setTarget( target );

		// using the binder itself as binding context should return the same value
		EntityPropertyBindingContext addressContext = addressBinder.asBindingContext();
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "some street", "some street", false );

		// just as fetching it directly as property from the binder
		assertThat( addressBinder.get( "street" ).getValue() ).isEqualTo( "some street" );

		// update the property on the binder
		addressBinder.get( "street" ).setValue( "updated street" );

		// fetching the property from the binder should return the update value
		assertThat( addressBinder.get( "street" ).getValue() ).isEqualTo( "updated street" );

		// using the binder as binding context should also return the updated value
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "some street", "updated street", false );

		// target has been updated as the values have been applied
		assertThat( target.getStreet() ).isEqualTo( "updated street" );

		// but the original should not be updated
		assertThat( original.getStreet() ).isEqualTo( "some street" );
	}

	@Test
	public void simpleNonNativeProperty() {
		Address original = new Address( "some street" );
		Address target = new Address( "some street" );

		assertThat( addressCity.getPropertyValue( target ) ).isNull();

		// create a properties binder for the address entity
		EntityPropertiesBinder addressBinder = new EntityPropertiesBinder( addressProperties );
		addressBinder.setEntity( original );
		addressBinder.setTarget( target );

		// using the binder itself as binding context should return the same value
		EntityPropertyBindingContext addressContext = addressBinder.asBindingContext();
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, null, false );

		// just as fetching it directly as property from the binder
		assertThat( addressBinder.get( "city" ).getValue() ).isNull();

		// update the property on the binder
		addressBinder.get( "city" ).setValue( new City( "Antwerp" ) );

		// fetching the property from the binder should return the update value
		assertThat( addressBinder.get( "city" ).getValue() ).isEqualTo( new City( "Antwerp" ) );

		// using the binder as binding context should also return the updated value
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Antwerp" ), false );
	}

	@Test
	public void nestedNativePropertyWithIntermediateBinder() {
		User original = new User( new Address( "some street" ) );
		User target = new User( new Address( "some street" ) );

		assertThat( userAddress.getPropertyValue( target ) ).isEqualTo( new Address( "some street" ) );
		assertThat( addressStreet.getPropertyValue( target.getAddress() ) ).isEqualTo( "some street" );
		assertThat( userAddressStreet.getPropertyValue( target ) ).isEqualTo( "some street" );

		// create a properties binder for the user entity
		EntityPropertiesBinder userBinder = new EntityPropertiesBinder( userProperties );
		userBinder.setEntity( original );
		userBinder.setTarget( target );

		// using the binder itself as binding context should return the address value
		EntityPropertyBindingContext userContext = userBinder.asBindingContext();
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "some street" ), new Address( "some street" ), false );

		// the equivalent binding context should be correct
		EntityPropertyBindingContext addressContext = userContext.resolvePropertyBindingContext( userAddress );
		assertBindingContext( addressContext, new Address( "some street" ), new Address( "some street" ), false );

		// fetching the address and address.street values directly from the binder
		SingleEntityPropertyBinder addressBinder = (SingleEntityPropertyBinder) userBinder.get( "address" );
		assertThat( addressBinder.getValue() ).isEqualTo( new Address( "some street" ) );
		assertThat( addressBinder.getProperties().get( "street" ).getValue() ).isEqualTo( "some street" );

		// a binding context for street can be resolved either from the address context or from the user context using the right descriptor
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "some street", "some street", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "some street", "some street", false );

		// update the street property on the address binder
		addressBinder.getProperties().get( "street" ).setValue( "updated street" );

		// fetching the property from the binder should return the update value
		assertThat( addressBinder.getProperties().get( "street" ).getOriginalValue() ).isEqualTo( "some street" );
		assertThat( addressBinder.getProperties().get( "street" ).getValue() ).isEqualTo( "updated street" );
		assertThat( addressBinder.getValue() ).isEqualTo( new Address( "updated street" ) );

		// using the binder as binding context should also return the updated value
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "some street", "updated street", false );

		// because the Address reference is unmodified, both the entity and target Address are the same reference
		assertBindingContext( addressContext, new Address( "updated street" ), new Address( "updated street" ), false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "updated street" ), new Address( "updated street" ), false );

		// the singular street value should always hold the correct old value
		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "some street", "updated street", false );

		// street binding context should also be resolved
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "some street", "updated street", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "some street", "updated street", false );

		// target has been updated as the values have been applied
		assertThat( target.getAddress() ).isEqualTo( new Address( "updated street" ) );
		assertThat( target.getAddress().getUpdateCount() ).isEqualTo( 1 );

		// but the original should not be updated
		assertThat( original.getAddress() ).isEqualTo( new Address( "some street" ) );
		assertThat( original.getAddress().getUpdateCount() ).isEqualTo( 0 );
	}

	@Test
	public void nestedNonNativePropertyWithIntermediateBinder() {
		User original = new User( new Address( "some street" ) );
		User target = new User( new Address( "some street" ) );

		assertThat( userAddress.getPropertyValue( target ) ).isEqualTo( new Address( "some street" ) );
		assertThat( addressCity.getPropertyValue( target.getAddress() ) ).isNull();
		assertThat( userAddressCity.getPropertyValue( target ) ).isNull();

		// create a properties binder for the user entity
		EntityPropertiesBinder userBinder = new EntityPropertiesBinder( userProperties );
		userBinder.setEntity( original );
		userBinder.setTarget( target );

		// using the binder itself as binding context should return the address value
		EntityPropertyBindingContext userContext = userBinder.asBindingContext();
		EntityPropertyBindingContext addressContext = userContext.resolvePropertyBindingContext( userAddress );

		// fetching the address city values directly from the binder
		SingleEntityPropertyBinder addressBinder = (SingleEntityPropertyBinder) userBinder.get( "address" );
		assertThat( addressBinder.getProperties().get( "city" ).getValue() ).isNull();

		// a binding context for city can be resolved either from the address context or from the user context using the right descriptor
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, null, false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressCity ), null, null, false );

		// update the city property on the address binder
		addressBinder.getProperties().get( "city" ).setValue( new City( "Antwerp" ) );

		// fetching the property from the binder should return the update value
		assertThat( addressBinder.getProperties().get( "city" ).getOriginalValue() ).isNull();
		assertThat( addressBinder.getProperties().get( "city" ).getValue() ).isEqualTo( new City( "Antwerp" ) );

		// address is unmodified as street has not been updated
		assertThat( addressBinder.getValue() ).isEqualTo( new Address( "some street" ) );

		// using the binder as binding context should return the updated value
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Antwerp" ), false );

		// the singular city value should always hold the correct old value
		assertPropertyValue( userContext.resolvePropertyValue( userAddressCity ), null, new City( "Antwerp" ), false );

		// city binding context should also be resolved
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, new City( "Antwerp" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressCity ), null, new City( "Antwerp" ), false );
	}

	@Test
	public void withoutTargetAllValueResolvingUsesOnlyOriginalValue() {
		User original = new User( new Address( "some street" ) );

		// create a properties binder for the user entity
		EntityPropertiesBinder userBinder = new EntityPropertiesBinder( userProperties );
		userBinder.setEntity( original );

		// using the binder itself as binding context should return the address value
		EntityPropertyBindingContext userContext = userBinder.asBindingContext();
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "some street" ), new Address( "some street" ), false );

		// the equivalent binding context should be correct
		EntityPropertyBindingContext addressContext = userContext.resolvePropertyBindingContext( userAddress );
		assertBindingContext( addressContext, new Address( "some street" ), new Address( "some street" ), true );

		// fetching the address and address.street values directly from the binder
		SingleEntityPropertyBinder addressBinder = (SingleEntityPropertyBinder) userBinder.get( "address" );
		assertThat( addressBinder.getValue() ).isEqualTo( new Address( "some street" ) );
		assertThat( addressBinder.getProperties().get( "street" ).getValue() ).isEqualTo( "some street" );

		// a binding context for street can be resolved either from the address context or from the user context using the right descriptor
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "some street", "some street", true );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "some street", "some street", true );

		// update the street property on the address binder
		addressBinder.getProperties().get( "street" ).setValue( "updated street" );

		// fetching the property from the binder should return the update value
		assertThat( addressBinder.getProperties().get( "street" ).getOriginalValue() ).isEqualTo( "some street" );
		assertThat( addressBinder.getProperties().get( "street" ).getValue() ).isEqualTo( "updated street" );

		// but fetching the address should not, as values are not applied in readonly mode
		assertThat( addressBinder.getValue() ).isEqualTo( new Address( "some street" ) );

		// using the binder as binding context should however still return the original value
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "some street", "some street", false );

		// the address reference should not be modified as values should not be applied upwards in readonly mode
		assertBindingContext( addressContext, new Address( "some street" ), new Address( "some street" ), true );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "some street" ), new Address( "some street" ), false );

		// the singular street value should still hold the original value
		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "some street", "some street", false );

		// street binding context should also be resolved with the original value
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "some street", "some street", true );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "some street", "some street", true );

		// and the original should not be updated
		assertThat( original.getAddress() ).isEqualTo( new Address( "some street" ) );
		assertThat( original.getAddress().getUpdateCount() ).isEqualTo( 0 );
	}

	@Test
	public void nestedNativePropertyWithDto() {
		CityAddress original = new CityAddress( new City( "Antwerp" ) );
		CityAddress target = new CityAddress( new City( "Antwerp" ) );

		assertThat( cityAddressCity.getPropertyValue( target ) ).isEqualTo( new City( "Antwerp" ) );
		assertThat( cityName.getPropertyValue( target.getCity() ) ).isEqualTo( "Antwerp" );
		assertThat( cityAddressCityName.getPropertyValue( target ) ).isEqualTo( "Antwerp" );

		// create a properties binder for the user entity
		EntityPropertiesBinder addressBinder = new EntityPropertiesBinder( cityAddressProperties );
		addressBinder.setEntity( original );
		addressBinder.setTarget( target );

		// using the binder itself as binding context should return the address value
		EntityPropertyBindingContext addressContext = addressBinder.asBindingContext();
		assertPropertyValue( addressContext.resolvePropertyValue( cityAddressCity ), new City( "Antwerp" ), new City( "Antwerp" ), false );

		// the equivalent binding context should be correct
		EntityPropertyBindingContext cityContext = addressContext.resolvePropertyBindingContext( cityAddressCity );
		assertBindingContext( cityContext, new City( "Antwerp" ), new City( "Antwerp" ), false );

		// fetching the city and city.name values directly from the binder
		SingleEntityPropertyBinder cityBinder = (SingleEntityPropertyBinder) addressBinder.get( "city" );
		assertThat( cityBinder.getValue() ).isEqualTo( new City( "Antwerp" ) );
		assertThat( cityBinder.getProperties().get( "name" ).getValue() ).isEqualTo( "Antwerp" );

		// a binding context for name can be resolved either from the city context or from the address context using the right descriptor
		assertBindingContext( cityContext.resolvePropertyBindingContext( cityName ), "Antwerp", "Antwerp", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( cityAddressCityName ), "Antwerp", "Antwerp", false );

		// update the street property on the address binder
		cityBinder.getProperties().get( "name" ).setValue( "Brussels" );

		// fetching the property from the binder should return the update value
		assertThat( cityBinder.getProperties().get( "name" ).getOriginalValue() ).isEqualTo( "Antwerp" );
		assertThat( cityBinder.getProperties().get( "name" ).getValue() ).isEqualTo( "Brussels" );
		assertThat( cityBinder.getValue() ).isEqualTo( new City( "Brussels" ) );

		// using the binder as binding context should also return the updated value
		assertPropertyValue( cityContext.resolvePropertyValue( cityName ), "Antwerp", "Brussels", false );

		// because the city property has a custom DTO method, the entity should refer to the original and the target to the updated DTO
		assertBindingContext( cityContext, new City( "Antwerp" ), new City( "Brussels" ), false );
		assertPropertyValue( addressContext.resolvePropertyValue( cityAddressCity ), new City( "Antwerp" ), new City( "Brussels" ), false );

		// the singular name value should always hold the correct old value
		assertPropertyValue( addressContext.resolvePropertyValue( cityAddressCityName ), "Antwerp", "Brussels", false );

		// city name binding context should also be resolved
		assertBindingContext( cityContext.resolvePropertyBindingContext( cityName ), "Antwerp", "Brussels", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( cityAddressCityName ), "Antwerp", "Brussels", false );

		// target has been updated as the values have been applied
		assertThat( target.getCity() ).isEqualTo( new City( "Brussels" ) );

		// but the original should not be updated
		assertThat( original.getCity() ).isEqualTo( new City( "Antwerp" ) );
	}

	@Test
	public void nativePropertyIsAlwaysFetchedFromTheTargetIfNotExplicitlyAccessedViaTheBinder() {
		Address target = new Address( "some street" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( addressProperties );
		binder.setTarget( target );

		EntityPropertyBindingContext addressContext = binder.asBindingContext();
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "some street", "some street", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "some street", "some street", false );

		// because street is a native (DIRECT) property, no binder should have been created for resolving the property value
		assertThat( binder.containsKey( "street" ) ).isFalse();

		// update the backing target
		target.setStreet( "updated street" );

		// changes to the backing target are immediately visible
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "updated street", "updated street", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "updated street", "updated street", false );
		assertThat( binder.containsKey( "street" ) ).isFalse();

		// create a binder for the street property
		assertThat( binder.get( "street" ).getValue() ).isEqualTo( "updated street" );
		assertThat( binder.containsKey( "street" ) ).isTrue();

		// update the street directly on the original target
		target.setStreet( "updated again" );

		// the property value and binding context now use the binder property instead and changes in the underlying target are ignored
		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "updated street", "updated street", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "updated street", "updated street", false );

		// because the binder is not dirty changes will not be applied to the target
		binder.createController().applyValues();
		assertThat( target.getStreet() ).isEqualTo( "updated again" );

		// but if we update and apply, the target will be updated
		binder.get( "street" ).setValue( "updated through binder" );
		binder.createController().applyValues();
		assertThat( target.getStreet() ).isEqualTo( "updated through binder" );

		assertPropertyValue( addressContext.resolvePropertyValue( addressStreet ), "updated street", "updated through binder", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressStreet ), "updated street", "updated through binder", false );
	}

	@Test
	public void nestedNativePropertyIsAlwaysFetchedFromTheTargetIfNotExplicitlyAccessedViaTheBinder() {
		User target = new User( new Address( "some street" ) );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( userProperties );
		binder.setTarget( target );

		EntityPropertyBindingContext userContext = binder.asBindingContext();
		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "some street", "some street", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "some street", "some street", false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "some street" ), new Address( "some street" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "some street" ), new Address( "some street" ), false );

		assertThat( binder ).isEmpty();

		target.getAddress().setStreet( "updated street" );

		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "updated street", "updated street", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "updated street", "updated street", false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "updated street" ), new Address( "updated street" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "updated street" ), new Address( "updated street" ),
		                      false );

		target.setAddress( new Address( "updated again!" ) );

		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "updated again!", "updated again!", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "updated again!", "updated again!", false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "updated again!" ), new Address( "updated again!" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "updated again!" ), new Address( "updated again!" ),
		                      false );

		assertThat( binder.get( "address" ).getValue() ).isEqualTo( new Address( "updated again!" ) );

		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "updated again!", "updated again!", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "updated again!", "updated again!", false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "updated again!" ), new Address( "updated again!" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "updated again!" ), new Address( "updated again!" ),
		                      false );

		EntityPropertyBinder streetBinder = ( (SingleEntityPropertyBinder) binder.get( "address" ) ).getProperties().get( "street" );
		assertThat( streetBinder.getValue() ).isEqualTo( "updated again!" );
		target.getAddress().setStreet( "your street" );

		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "updated again!", "updated again!", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "updated again!", "updated again!", false );

		// address is still the same reference, so is updated
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "your street" ), new Address( "your street" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "your street" ), new Address( "your street" ),
		                      false );

		streetBinder.setValue( "my street" );
		assertPropertyValue( userContext.resolvePropertyValue( userAddressStreet ), "updated again!", "my street", false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddressStreet ), "updated again!", "my street", false );
		assertPropertyValue( userContext.resolvePropertyValue( userAddress ), new Address( "my street" ), new Address( "my street" ), false );
		assertBindingContext( userContext.resolvePropertyBindingContext( userAddress ), new Address( "my street" ), new Address( "my street" ),
		                      false );
		assertThat( target.getAddress().getStreet() ).isEqualTo( "my street" );
	}

	@Test
	public void nonNativePropertyIsAlwaysImmediatelyAddedToTheBinder() {
		Address target = new Address( "some street" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( addressProperties );
		binder.setTarget( target );

		EntityPropertyBindingContext addressContext = binder.asBindingContext();
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, null, false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, null, false );

		// because city is a custom (BINDER) property, a binder will have automatically been added
		assertThat( binder.containsKey( "city" ) ).isTrue();

		SingleEntityPropertyBinder cityBinder = (SingleEntityPropertyBinder) binder.get( "city" );

		// update the city value
		City city = new City( "Antwerp" );
		cityBinder.setValue( city );

		// city is the BINDER property
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Antwerp" ), false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, new City( "Antwerp" ), false );

		// city.name is a DIRECT property and as such resolved from the target
		assertPropertyValue( addressContext.resolvePropertyValue( addressCityName ), "Antwerp", "Antwerp", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCityName ), "Antwerp", "Antwerp", false );

		// no binder for the name property should be registered
		assertThat( cityBinder.getProperties().containsKey( "name" ) ).isFalse();

		// update name directly on the city value
		city.setName( "Brussels" );

		// city.name resolving immediately takes the updated city name
		assertPropertyValue( addressContext.resolvePropertyValue( addressCityName ), "Brussels", "Brussels", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCityName ), "Brussels", "Brussels", false );
		assertThat( cityBinder.getProperties().containsKey( "name" ) ).isFalse();

		// the address binder is also updated as the same reference has been directly modified
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Brussels" ), false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, new City( "Brussels" ), false );

		// create a binder for the city name property
		assertThat( cityBinder.getProperties().get( "name" ).getValue() ).isEqualTo( "Brussels" );
		assertThat( cityBinder.getProperties().containsKey( "name" ) ).isTrue();

		// update the city name directly on the original target
		city.setName( "Ghent" );

		// the property value and binding context now use the binder property instead and changes in the underlying target are ignored
		assertPropertyValue( addressContext.resolvePropertyValue( addressCityName ), "Brussels", "Brussels", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCityName ), "Brussels", "Brussels", false );

		// because the city name binder is not set as dirty (it was simply loaded), it will not be applied and the reference has been updated
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Ghent" ), false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, new City( "Ghent" ), false );

		// setting the city name through the binder
		cityBinder.getProperties().get( "name" ).setValue( "Amsterdam" );

		assertPropertyValue( addressContext.resolvePropertyValue( addressCityName ), "Brussels", "Amsterdam", false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCityName ), "Brussels", "Amsterdam", false );

		// because the city name binder is not set as dirty (it was simply loaded), it will not be applied and the reference has been updated
		assertPropertyValue( addressContext.resolvePropertyValue( addressCity ), null, new City( "Amsterdam" ), false );
		assertBindingContext( addressContext.resolvePropertyBindingContext( addressCity ), null, new City( "Amsterdam" ), false );

		// and the city reference should be updated as well
		assertThat( city.getName() ).isEqualTo( "Amsterdam" );
	}

	private void assertPropertyValue( EntityPropertyValue<?> propertyValue, Object oldValue, Object newValue, boolean deleted ) {
		assertThat( propertyValue ).isNotNull();
		assertThat( propertyValue.getOldValue() ).isEqualTo( oldValue );
		assertThat( propertyValue.getNewValue() ).isEqualTo( newValue );
		assertThat( propertyValue.isDeleted() ).isEqualTo( deleted );
	}

	private void assertBindingContext( EntityPropertyBindingContext bindingContext, Object entity, Object target, boolean readonly ) {
		assertThat( bindingContext ).isNotNull();
		assertThat( bindingContext.<Object>getEntity() ).isEqualTo( entity );
		assertThat( bindingContext.<Object>getTarget() ).isEqualTo( target );
		assertThat( bindingContext.isReadonly() ).isEqualTo( readonly );
	}
}
