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

package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.NestedEntityPropertyController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestNestedEntityPropertyController
{
	@Mock
	private EntityPropertyController user;

	@Mock
	private EntityPropertyController address;

	@Mock
	private EntityPropertyController street;

	@Test
	public void fetchValueOnSingleNestedController() {
		NestedEntityPropertyController controller = new NestedEntityPropertyController( "user", user, address );

		EntityPropertyBindingContext<String, String> context = EntityPropertyBindingContext.of( "page" );
		when( user.fetchValue( context ) ).thenReturn( 123L );

		EntityPropertyBindingContext<Long, Long> childContext = EntityPropertyBindingContext.of( 123L ).withParent( context );
		EntityPropertyBindingContext<String, String> expected = EntityPropertyBindingContext.of( "page" );
		expected.setAttribute( NestedEntityPropertyController.class.getName() + ".user", childContext );

		when( address.fetchValue( childContext ) ).thenReturn( "address value" );
		assertThat( controller.fetchValue( context ) ).isEqualTo( "address value" );
		verify( user ).fetchValue( context );

		assertThat( controller.fetchValue( context ) ).isEqualTo( "address value" );
		verify( user, times( 1 ) ).fetchValue( context );
	}
}
