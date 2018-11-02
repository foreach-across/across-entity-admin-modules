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

	// todo: fixme
	// in readonly, no dto should be created!

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
