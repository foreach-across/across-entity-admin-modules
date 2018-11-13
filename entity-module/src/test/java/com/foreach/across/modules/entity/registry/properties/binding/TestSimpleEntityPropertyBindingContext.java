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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestSimpleEntityPropertyBindingContext
{
	@Test
	public void defaultSimpleContextIsReadonly() {
		SimpleEntityPropertyBindingContext context = SimpleEntityPropertyBindingContext.builder().entity( "string" ).build();

		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<String>getTarget() ).isNull();
		assertThat( context.isReadonly() ).isTrue();

		context = context.toBuilder().target( 123L ).readonly( false ).build();
		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<Long>getTarget() ).isEqualTo( 123L );
		assertThat( context.isReadonly() ).isFalse();
	}

	@Test
	public void readonly() {
		EntityPropertyBindingContext context = EntityPropertyBindingContext.forReading( "string" );

		assertThat( context ).isInstanceOf( SimpleEntityPropertyBindingContext.class );
		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<String>getTarget() ).isEqualTo( "string" );
		assertThat( context.isReadonly() ).isTrue();
	}

	@Test
	public void updating() {
		EntityPropertyBindingContext context = EntityPropertyBindingContext.forUpdating( "string", 123L );

		assertThat( context ).isInstanceOf( SimpleEntityPropertyBindingContext.class );

		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<Long>getTarget() ).isEqualTo( 123L );
		assertThat( context.isReadonly() ).isFalse();

		context = EntityPropertyBindingContext.forUpdating( "string", null );
		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<Object>getTarget() ).isNull();
		assertThat( context.isReadonly() ).isFalse();
	}

	@Test
	public void toPropertyValue() {
		assertThat( EntityPropertyBindingContext.forReading( 123L ).toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( 123L, 123L, false ) );

		assertThat( EntityPropertyBindingContext.forUpdating( "abc", 123L ).toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( "abc", 123L, false ) );

		assertThat( EntityPropertyBindingContext.forUpdating( null, "abc" ).toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( null, "abc", false ) );

		assertThat( EntityPropertyBindingContext.forUpdating( "abc", null ).toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( "abc", null, true ) );

		assertThat( SimpleEntityPropertyBindingContext.builder().entity( "abc" ).target( null ).readonly( true ).build().toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( "abc", null, false ) );
	}
}
