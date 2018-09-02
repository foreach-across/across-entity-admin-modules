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

import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import org.junit.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestDefaultEntityPropertyController
{
	private DefaultEntityPropertyController controller = new DefaultEntityPropertyController();
	private EntityPropertyBindingContext<String, BigDecimal> context = new EntityPropertyBindingContext<>( "123", BigDecimal.TEN );

	@Test
	public void order() {
		controller.order( 1 );
		assertThat( controller.getOrder() ).isEqualTo( 1 );

		controller.withEntity( String.class, Long.class )
		          .order( 2 );
		assertThat( controller.getOrder() ).isEqualTo( 2 );

		controller.withTarget( BigDecimal.class, Long.class )
		          .order( 3 );
		assertThat( controller.getOrder() ).isEqualTo( 3 );
	}

	@Test
	public void getValue() {
		assertThat( controller.fetchValue( context ) ).isNull();

		controller.valueFetcher( EntityPropertyBindingContext::getEntity );
		assertThat( controller.fetchValue( context ) ).isEqualTo( "123" );

		controller.withEntity( String.class, Long.class )
		          .valueFetcher( Long::parseLong );
		assertThat( controller.fetchValue( context ) ).isEqualTo( 123L );

		controller.withTarget( BigDecimal.class, Long.class )
		          .valueFetcher( BigDecimal::longValue );
		assertThat( controller.fetchValue( context ) ).isEqualTo( 10L );

		controller.withBindingContext( String.class, BigDecimal.class, Long.class )
		          .valueFetcher( ctx -> ctx.getTarget().longValue() + ctx.getEntity().length() );
		assertThat( controller.fetchValue( context ) ).isEqualTo( 13L );
	}

	@Test
	public void createValue() {
		assertThat( controller.createValue( context ) ).isNull();

		// function
		controller.createValueFunction( ctx -> ctx.getEntity() + "-created" );
		assertThat( controller.createValue( context ) ).isEqualTo( "123-created" );

		controller.withEntity( String.class, Long.class )
		          .createValueFunction( e -> Long.parseLong( e + "0" ) );
		assertThat( controller.createValue( context ) ).isEqualTo( 1230L );

		controller.withTarget( BigDecimal.class, Long.class )
		          .createValueFunction( d -> d.longValue() + 10L );
		assertThat( controller.createValue( context ) ).isEqualTo( 20L );

		controller.withBindingContext( String.class, BigDecimal.class, Long.class )
		          .createValueFunction( ctx -> ctx.getTarget().longValue() + ctx.getEntity().length() );
		assertThat( controller.createValue( context ) ).isEqualTo( 13L );

		// supplier
		controller.createValueSupplier( () -> "created" );
		assertThat( controller.createValue( context ) ).isEqualTo( "created" );

		controller.withEntity( String.class, Long.class )
		          .createValueSupplier( () -> 555L );
		assertThat( controller.createValue( context ) ).isEqualTo( 555L );

		controller.withTarget( BigDecimal.class, Long.class )
		          .createValueSupplier( () -> 333L );
		assertThat( controller.createValue( context ) ).isEqualTo( 333L );

		controller.withBindingContext( String.class, BigDecimal.class, Long.class )
		          .createValueSupplier( () -> 222L );
		assertThat( controller.createValue( context ) ).isEqualTo( 222L );
	}

	@Test
	public void applyValue() {

	}
	/*
	@Test
	public void setValue() {
		assertThat( controller.applyValue( "any-string", null, 123L ) ).isFalse();

		val consumer = mock( BiConsumer.class );
		assertThat( controller.applyValueConsumer( consumer ) ).isSameAs( controller );
		assertThat( controller.applyValue( "some-string", null, 555L ) ).isTrue();
		verify( consumer ).accept( "some-string", 555L );

		val vw = mock( BiFunction.class );
		when( vw.apply( "any-string", 123L ) ).thenReturn( true );
		assertThat( controller.applyValueFunction( vw ) ).isSameAs( controller );

		assertThat( controller.applyValue( "any-string", null, 123L ) ).isTrue();
		assertThat( controller.applyValue( "any-string", null, 0L ) ).isFalse();
	}
	 */
}
