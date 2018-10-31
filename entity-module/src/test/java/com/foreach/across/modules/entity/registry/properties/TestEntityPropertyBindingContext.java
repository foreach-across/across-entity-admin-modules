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

package com.foreach.across.modules.entity.registry.properties;

import org.junit.Test;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestEntityPropertyBindingContext
{
	@Test
	public void simpleContext() {
		EntityPropertyBindingContext context = builder()
				.entity( "string" )
				.build();

		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<String>getTarget() ).isNull();
		assertThat( context.getParent() ).isNull();
		assertThat( context.isReadonly() ).isFalse();
		assertThat( context.getChildContexts() ).isEmpty();
		assertThat( context.getController() ).isNull();
	}

	@Test
	public void readonly() {
		EntityPropertyBindingContext context = EntityPropertyBindingContext.forReading( "string" );

		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<String>getTarget() ).isEqualTo( "string" );
		assertThat( context.getParent() ).isNull();
		assertThat( context.isReadonly() ).isTrue();
		assertThat( context.getChildContexts() ).isEmpty();
		assertThat( context.getController() ).isNull();
	}

	@Test
	public void updating() {
		EntityPropertyBindingContext context = EntityPropertyBindingContext.forUpdating( "string", 123L );

		assertThat( context.<String>getEntity() ).isEqualTo( "string" );
		assertThat( context.<Long>getTarget() ).isEqualTo( 123L );
		assertThat( context.getParent() ).isNull();
		assertThat( context.isReadonly() ).isFalse();
		assertThat( context.getChildContexts() ).isEmpty();
		assertThat( context.getController() ).isNull();

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

		assertThat( EntityPropertyBindingContext.builder().entity( "abc" ).target( null ).readonly( true ).build().toPropertyValue() )
				.isEqualTo( new EntityPropertyValue<>( "abc", null, false ) );
	}

	@Test
	public void attachChildContext() {
		EntityPropertyBindingContext parent = EntityPropertyBindingContext.forReading( "root" );
		assertThat( parent.hasChildContext( "child" ) ).isFalse();

		EntityPropertyBindingContext child = parent.getOrCreateChildContext(
				"child", ( p, builder ) -> builder.entity( 123 ).target( "a" ).controller( mock( EntityPropertyController.class ) ) );
		assertThat( child ).isNotNull();
		assertThat( child.<Integer>getEntity() ).isEqualTo( 123 );
		assertThat( child.<String>getTarget() ).isEqualTo( "a" );
		assertThat( child.getController() ).isNotNull();
		assertThat( child.getParent() ).isSameAs( parent );
		assertThat( child.isReadonly() ).isTrue();

		assertThat( parent.hasChildContext( "child" ) ).isTrue();
		assertThat( parent.getChildContexts().get( "child" ) ).isSameAs( child );
		assertThat( parent.getOrCreateChildContext( "child", ( p, builder ) -> {
		} ) ).isSameAs( child );

		assertThat( parent.removeChildContext( "child" ) ).isSameAs( child );
		assertThat( parent.hasChildContext( "child" ) ).isFalse();

		child = parent.getOrCreateChildContext( "child", ( p, builder ) -> builder.readonly( false ) );
		assertThat( child ).isNotNull();
		assertThat( child.isReadonly() ).isFalse();
		assertThat( child.getParent() ).isSameAs( parent );

		child = EntityPropertyBindingContext.forUpdating( "root", "target" ).getOrCreateChildContext( "child", ( p, builder ) -> {
		} );
		assertThat( child ).isNotNull();
		assertThat( child.isReadonly() ).isFalse();
		assertThat( child.getParent() ).isNotSameAs( parent ).isNotNull();
	}
}
