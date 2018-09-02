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
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestDirectUserOfEntityPropertiesBinder
{
	private final ConversionService conversionService = new DefaultConversionService();

	@Test
	public void simpleBinderForType() {
		//EntityPropertiesBinder.forPropertyRegistry( new)
	}

	@Test
	public void simpleBinderForInstance() {
		User user = new User( "my name" );

		DefaultEntityPropertyRegistry propertyRegistry = new DefaultEntityPropertyRegistry();
		propertyRegistry.register(
				EntityPropertyDescriptor.builder( "name" )
				                        .propertyType( String.class )
				                        /*.controller(
						                        c -> c.withEntity( User.class, String.class )
						                              .valueFetcher( User::getName )
						                              .applyValueConsumer( User::setName )
				                        )*/
				                        .build()
		);

		/*registeredProperties().forEach( b -> propertyRegistry.register( b.build() ) );*/

		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );
		binder.setEntity( user );

		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "my name" );
		binder.get( "name" ).setValue( "other name" );
		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "other name" );

		assertThat( user.getName() ).isEqualTo( "my name" );
		binder.bind();
		assertThat( user.getName() ).isEqualTo( "other name" );
	}

	@Test
	public void simpleBinderWithEntityPropertyRegistry() {

	}

	@Test
	public void nestedObjectBinder() {

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
		private User owner;
		private String email;
	}
}
