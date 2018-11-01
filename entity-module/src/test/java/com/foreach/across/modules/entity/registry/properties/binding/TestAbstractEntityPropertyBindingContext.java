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

package com.foreach.across.modules.entity.registry.properties.binding;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestAbstractEntityPropertyBindingContext
{
	@Mock
	private EntityPropertyController controller;

	@Spy
	private AbstractEntityPropertyBindingContext bindingContext;

	@Test
	public void nullBindingContextIfNoController() {
		assertThat( bindingContext.resolvePropertyBindingContext( "myprop", null ) ).isNull();
	}

	@Test
	public void childBindingContextIsCreated() {
		EntityPropertyBindingContext child = bindingContext.resolvePropertyBindingContext( "myprop", controller );
		assertThat( child ).isNotNull();
		assertThat( child ).isInstanceOf( ChildEntityPropertyBindingContext.class );

		when( controller.fetchValue( bindingContext ) ).thenReturn( 123L );
		assertThat( child.<Long>getEntity() ).isEqualTo( 123L );
	}

	@Test
	public void multipleCallsForSamePropertyReturnTheSameContext() {
		EntityPropertyBindingContext child = bindingContext.resolvePropertyBindingContext( "myprop", controller );
		assertThat( child ).isNotNull();

		assertThat( bindingContext.resolvePropertyBindingContext( "myprop", null ) ).isSameAs( child );
		assertThat( bindingContext.resolvePropertyBindingContext( "myprop", controller ) ).isSameAs( child );
		assertThat( bindingContext.resolvePropertyBindingContext( "otherprop", controller ) ).isNotNull().isNotSameAs( child );
	}
}
