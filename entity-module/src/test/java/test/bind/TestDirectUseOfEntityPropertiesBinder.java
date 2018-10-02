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
import com.foreach.across.modules.entity.bind.SingleEntityPropertyBinder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.registry.properties.registrars.DefaultPropertiesRegistrar;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Before;
import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@SuppressWarnings("unchecked")
public class TestDirectUseOfEntityPropertiesBinder
{
	private AtomicInteger counter = new AtomicInteger( 0 );
	private BiConsumer<User, EntityPropertyValue<UserProperties>> consumer = mock( BiConsumer.class );

	private EntityPropertyRegistry userWithPropertiesRegistry;

	@Before
	public void createRegistries() {
		MutableEntityPropertyRegistry propertyRegistry = DefaultEntityPropertyRegistry.forClass( User.class );
		propertyRegistry.register(
				EntityPropertyDescriptor.builder( "properties" )
				                        .propertyType( UserProperties.class )
				                        .controller(
						                        c -> c.withTarget( User.class, UserProperties.class )
						                              .valueFetcher(
								                              user -> new UserProperties( counter.incrementAndGet(), user, user.getName() + "@localhost" )
						                              )
						                              .applyValueConsumer( consumer )
				                        )
				                        .build()
		);

		userWithPropertiesRegistry = propertyRegistry;
	}

	@Test
	public void simpleBinderWithEntityPropertyRegistry() {
		MutableEntityPropertyRegistry propertyRegistry = DefaultEntityPropertyRegistry.forClass( User.class );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );

		User user = new User( "my name" );
		binder.setBindingContext( EntityPropertyBindingContext.forReading( user ) );

		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "my name" );
		binder.get( "name" ).setValue( "other name" );
		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "other name" );

		assertThat( user.getName() ).isEqualTo( "my name" );
		binder.createController().applyValues();
		assertThat( user.getName() ).isEqualTo( "other name" );
	}

	@Test
	public void nestedObjectBinder() {
		User user = new User( "john.doe" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( userWithPropertiesRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.forReading( user ) );

		binder.get( "name" ).setValue( "jane.doe" );
		assertThat( binder.get( "properties.id" ).getValue() ).isEqualTo( 1 );
		assertThat( binder.get( "properties.email" ).getValue() ).isEqualTo( "john.doe@localhost" );
		assertThat( counter.get() ).isEqualTo( 1 );
		assertThat( binder.get( "properties" ).getValue() ).isEqualTo( new UserProperties( 1, user, "john.doe@localhost" ) );

		EntityPropertiesBinder child = ( (SingleEntityPropertyBinder) binder.get( "properties" ) ).getProperties();
		assertThat( child ).isNotNull();
		assertThat( child.get( "id" ).getValue() ).isEqualTo( 1 );
		assertThat( child.get( "owner" ).getValue() ).isEqualTo( user );
		assertThat( child.get( "email" ).getValue() ).isEqualTo( "john.doe@localhost" );
	}

	@Test
	public void accessingThroughNestedPropertiesBinderWillApplyTheParentProperty() {
		User user = new User( "john.doe" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( userWithPropertiesRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.forUpdating( user, user ) );

		SingleEntityPropertyBinder props = (SingleEntityPropertyBinder) binder.get( "properties" );
		props.getProperties().get( "email" ).setValue( "franz@localhost" );

		binder.createController().applyValues();

		verify( consumer ).accept(
				user,
				new EntityPropertyValue<>(
						new UserProperties( 1, user, "franz@localhost" ),
						new UserProperties( 1, user, "franz@localhost" ),
						false
				)
		);
	}

	@Test
	public void accessingNestedPathWillAlsoApplyTheParentProperty() {
		User user = new User( "john.doe" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( userWithPropertiesRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.forUpdating( user, user ) );

		binder.get( "properties.email" ).setValue( "franz@localhost" );

		binder.createController().applyValues();

		verify( consumer ).accept(
				user,
				new EntityPropertyValue<>(
						new UserProperties( 1, user, "franz@localhost" ),
						new UserProperties( 1, user, "franz@localhost" ),
						false
				)
		);
	}

	@Test
	public void accessingTheSamePropertyThroughNestedPathAndPropertiesBinderWillOnlyApplyTheParentPropertyOnce() {
		User user = new User( "john.doe" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( userWithPropertiesRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.forUpdating( user, user ) );

		SingleEntityPropertyBinder props = (SingleEntityPropertyBinder) binder.get( "properties" );
		props.getProperties().get( "id" ).setValue( 2 );
		binder.get( "properties.email" ).setValue( "joseph@localhost" );

		binder.createController().applyValues();

		verify( consumer ).accept(
				user,
				new EntityPropertyValue<>(
						new UserProperties( 2, user, "joseph@localhost" ),
						new UserProperties( 2, user, "joseph@localhost" ),
						false
				)
		);
		verifyNoMoreInteractions( consumer );
	}

	@Test
	public void nestedPropertiesValidation() {
		DefaultEntityPropertyRegistryProvider provider = new DefaultEntityPropertyRegistryProvider( new EntityPropertyDescriptorFactoryImpl() );
		provider.setPropertiesRegistrars( Collections.singleton( new DefaultPropertiesRegistrar( new EntityPropertyDescriptorFactoryImpl() ) ) );

		MutableEntityPropertyRegistry propertyRegistry = provider.get( UserProperties.class );

		new EntityPropertyDescriptorBuilder( "owner" )
				.controller(
						c -> c.withTarget( UserProperties.class, User.class )
						      .contextualValidator( ( userProps, user, errors, hints ) -> {
							      errors.rejectValue( "", "required" );
						      } )
				)
				.apply( propertyRegistry.getProperty( "owner" ) );

		new EntityPropertyDescriptorBuilder( "name" )
				.controller(
						c -> c.withTarget( User.class, String.class )
						      .contextualValidator( ( user, name, errors, hints ) -> {
							      errors.rejectValue( "", "required" );
						      } )
				)
				.apply( provider.get( User.class ).getProperty( "name" ) );

		User user = new User( "john.doe" );
		UserProperties props = new UserProperties( 1, user, "" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.forReading( props ) );

		SingleEntityPropertyBinder ownerProperty = (SingleEntityPropertyBinder) binder.get( "owner" );
		ownerProperty.getProperties().get( "name" ).setValue( null );

		Errors errors = new BeanPropertyBindingResult( binder, "" );
		binder.createController()
		      .applyValuesAndValidate( errors );

		assertThat( errors.getErrorCount() ).isEqualTo( 2 );
		assertThat( errors.getFieldError( "properties[owner].initializedValue" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.getRejectedValue() ).isSameAs( user );
					assertThat( fe.getCode() ).isEqualTo( "required" );
				} );
		assertThat( errors.getFieldError( "properties[owner].properties[name].value" ) )
				.isNotNull()
				.satisfies( fe -> {
					assertThat( fe.getRejectedValue() ).isNull();
					assertThat( fe.getCode() ).isEqualTo( "required" );
				} );
	}

	@Data
	@AllArgsConstructor
	private static class User
	{
		private String name;
	}

	@Data
	@AllArgsConstructor
	private static class UserProperties
	{
		private int id;
		private User owner;
		private String email;
	}
}
