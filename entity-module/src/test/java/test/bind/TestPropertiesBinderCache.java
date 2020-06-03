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
import com.foreach.across.modules.entity.bind.EntityPropertiesBinderCache;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
public class TestPropertiesBinderCache extends AbstractEntityPropertyBindingContextTest
{
	private EntityPropertiesBinderCache cache;

	@Test
	public void withoutOptimizedSimpleBulkFetching() {
		User userOne = new User( new Address( "street one" ) );
		User userTwo = new User( new Address( "street two" ) );

		cache = EntityPropertiesBinderCache.builder()
		                                   .propertyRegistry( userProperties )
		                                   .entities( Arrays.asList( userOne, userTwo ) )
		                                   .build();

		assertThat( binder( userOne ).get( userAddress ).getValue() )
				.isEqualTo( new Address( "street one" ) );
		assertThat( binder( userTwo ).get( userAddress ).getValue() )
				.isEqualTo( new Address( "street two" ) );
		assertThat( binder( userOne ).get( userAddressStreet ).getValue() )
				.isEqualTo( "street one" );
		assertThat( binder( userTwo ).get( userAddressStreet ).getValue() )
				.isEqualTo( "street two" );
	}

	@Test
	public void withOptimizedSimpleBulkFetching() {
		AtomicInteger callCount = new AtomicInteger();
		builder( userAddress.getName() )
				.controller(
						ctl -> ctl.withTarget( User.class, Address.class )
						          .bulkValueFetcher( users -> {
							          callCount.incrementAndGet();
							          return users.stream().collect(
									          toMap( identity(), u -> new Address( "user-bulk:" + u.getAddress().getStreet() ) )
							          );
						          } )
				)
				.apply( (MutableEntityPropertyDescriptor) userAddress );

		User userOne = new User( new Address( "street one" ) );
		User userTwo = new User( new Address( "street two" ) );

		cache = EntityPropertiesBinderCache.builder()
		                                   .propertyRegistry( userProperties )
		                                   .entities( Arrays.asList( userOne, userTwo ) )
		                                   .build();

		assertThat( binder( userOne ).get( userAddress ).getValue() )
				.isEqualTo( new Address( "user-bulk:street one" ) );
		assertThat( binder( userTwo ).get( userAddress ).getValue() )
				.isEqualTo( new Address( "user-bulk:street two" ) );
		assertThat( callCount.intValue() ).isEqualTo( 1 );

		assertThat( binder( userOne ).get( userAddressStreet ).getValue() )
				.isEqualTo( "user-bulk:street one" );
		assertThat( binder( userTwo ).get( userAddressStreet ).getValue() )
				.isEqualTo( "user-bulk:street two" );

		// reset cache
		cache = EntityPropertiesBinderCache.builder()
		                                   .propertyRegistry( userProperties )
		                                   .entities( Arrays.asList( userOne, userTwo ) )
		                                   .build();
		assertThat( binder( userOne ).get( userAddressStreet ).getValue() )
				.isEqualTo( "user-bulk:street one" );
		assertThat( binder( userTwo ).get( userAddressStreet ).getValue() )
				.isEqualTo( "user-bulk:street two" );
		assertThat( callCount.intValue() ).isEqualTo( 2 );
	}

	private EntityPropertiesBinder binder( Object entity ) {
		return cache.getPropertiesBinder( entity ).orElseThrow( IllegalArgumentException::new );
	}
}
