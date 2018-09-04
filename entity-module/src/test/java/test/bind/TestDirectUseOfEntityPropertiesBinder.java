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
import com.foreach.across.modules.entity.registry.properties.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestDirectUseOfEntityPropertiesBinder
{
	@Test
	public void simpleBinderWithEntityPropertyRegistry() {
		MutableEntityPropertyRegistry propertyRegistry = DefaultEntityPropertyRegistry.forClass( User.class );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );

		User user = new User( "my name" );
		binder.setBindingContext( EntityPropertyBindingContext.of( user ) );

		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "my name" );
		binder.get( "name" ).setValue( "other name" );
		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "other name" );

		assertThat( user.getName() ).isEqualTo( "my name" );
		binder.bind();
		assertThat( user.getName() ).isEqualTo( "other name" );
	}

	@Test
	public void nestedObjectBinder() {
		AtomicInteger counter = new AtomicInteger( 0 );

		BiConsumer<User, EntityPropertyValue<UserProperties>> consumer = mock( BiConsumer.class );

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

		User user = new User( "john.doe" );

		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );
		binder.setBindingContext( EntityPropertyBindingContext.of( user ) );

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
		child.get( "email" ).setValue( "jane.doe@localhost" );

		binder.bind();

		EntityPropertyValue<UserProperties> value = new EntityPropertyValue<>(
				new UserProperties( 1, user, "john.doe@localhost" ),
				new UserProperties( 1, user, "jane.doe@localhost" ),
				false
		);

		verify( consumer ).accept( user, value );
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
