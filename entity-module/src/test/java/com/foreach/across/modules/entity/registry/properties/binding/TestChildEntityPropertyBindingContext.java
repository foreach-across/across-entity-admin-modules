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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestChildEntityPropertyBindingContext
{
	@Mock
	private EntityPropertyBindingContext parent;

	@Mock
	private EntityPropertyController controller;

	@InjectMocks
	private ChildEntityPropertyBindingContext bindingContext;

	@Test
	public void readonlyIsInheritedFromTheParent() {
		assertThat( bindingContext.isReadonly() ).isFalse();

		when( parent.isReadonly() ).thenReturn( true );
		assertThat( bindingContext.isReadonly() ).isTrue();
	}

	@Test
	public void targetAndEntityValueAreTheSame() {
		when( controller.fetchValue( parent ) ).thenReturn( "123" );

		assertThat( bindingContext.<String>getEntity() ).isEqualTo( "123" );
		assertThat( bindingContext.<String>getTarget() ).isEqualTo( "123" );
	}

	@Test
	public void inReadonlyTheCachedValueIsUsed() {
		when( parent.isReadonly() ).thenReturn( true );

		when( controller.fetchValue( parent ) ).thenReturn( "123" );
		assertThat( bindingContext.<String>getEntity() ).isEqualTo( "123" );
		assertThat( bindingContext.<String>getTarget() ).isEqualTo( "123" );
		assertThat( bindingContext.<String>getEntity() ).isEqualTo( "123" );
		assertThat( bindingContext.<String>getTarget() ).isEqualTo( "123" );

		verify( controller, times( 1 ) ).fetchValue( any() );
	}

	@Test
	public void nonReadonlyAlwaysFetchesValue() {
		when( controller.fetchValue( parent ) ).thenReturn( "123" );
		assertThat( bindingContext.<String>getEntity() ).isEqualTo( "123" );
		assertThat( bindingContext.<String>getTarget() ).isEqualTo( "123" );

		when( controller.fetchValue( parent ) ).thenReturn( "456" );
		assertThat( bindingContext.<String>getEntity() ).isEqualTo( "456" );
		assertThat( bindingContext.<String>getTarget() ).isEqualTo( "456" );

		verify( controller, times( 4 ) ).fetchValue( any() );
	}
}
