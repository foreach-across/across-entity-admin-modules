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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValidator;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import com.foreach.across.modules.entity.registry.properties.GenericEntityPropertyController;
import lombok.val;
import org.junit.Test;
import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@SuppressWarnings("unchecked")
public class TestGenericEntityPropertyController
{
	private GenericEntityPropertyController controller = new GenericEntityPropertyController();
	private EntityPropertyBindingContext context = EntityPropertyBindingContext.forUpdating( "123", BigDecimal.TEN );
	private EntityPropertyValue<Long> propertyValue = new EntityPropertyValue<>( 123L, 456L, false );

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
		          .valueFetcher( ctx -> ctx.<BigDecimal>getTarget().longValue() + ctx.<String>getEntity().length() );
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
		          .createValueFunction( ctx -> ctx.<BigDecimal>getTarget().longValue() + ctx.<String>getEntity().length() );
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
		assertThat( controller.applyValue( context, propertyValue ) ).isFalse();

		// function
		val vw = mock( BiFunction.class );
		when( vw.apply( context, propertyValue ) ).thenReturn( true );
		assertThat( controller.applyValueFunction( vw ) ).isSameAs( controller );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();

		controller.withEntity( String.class, Long.class )
		          .applyValueFunction( ( entity, value ) -> entity.equals( "123" ) && value.getNewValue().equals( 456L ) );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();
		assertThat( controller.applyValue( context, new EntityPropertyValue<>( 123L, 123L, false ) ) ).isFalse();

		controller.withTarget( BigDecimal.class, Long.class )
		          .applyValueFunction( ( target, value ) -> target.equals( BigDecimal.TEN ) && value.getOldValue().equals( 123L ) );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();
		assertThat( controller.applyValue( context, new EntityPropertyValue<>( 456L, 456L, false ) ) ).isFalse();

		// consumer
		val consumer = mock( BiConsumer.class );
		controller.applyValueConsumer( consumer );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context, propertyValue );

		controller.withEntity( String.class, Long.class )
		          .applyValueConsumer( consumer );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context.getEntity(), propertyValue );

		controller.withTarget( BigDecimal.class, Long.class )
		          .applyValueConsumer( consumer );
		assertThat( controller.applyValue( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context.getTarget(), propertyValue );
	}

	@Test
	public void save() {
		assertThat( controller.save( context, propertyValue ) ).isFalse();

		// function
		val vw = mock( BiFunction.class );
		when( vw.apply( context, propertyValue ) ).thenReturn( true );
		assertThat( controller.saveFunction( vw ) ).isSameAs( controller );
		assertThat( controller.save( context, propertyValue ) ).isTrue();

		controller.withEntity( String.class, Long.class )
		          .saveFunction( ( entity, value ) -> entity.equals( "123" ) && value.getNewValue().equals( 456L ) );
		assertThat( controller.save( context, propertyValue ) ).isTrue();
		assertThat( controller.save( context, new EntityPropertyValue<>( 123L, 123L, false ) ) ).isFalse();

		controller.withTarget( BigDecimal.class, Long.class )
		          .saveFunction( ( target, value ) -> target.equals( BigDecimal.TEN ) && value.getOldValue().equals( 123L ) );
		assertThat( controller.save( context, propertyValue ) ).isTrue();
		assertThat( controller.save( context, new EntityPropertyValue<>( 456L, 456L, false ) ) ).isFalse();

		// consumer
		val consumer = mock( BiConsumer.class );
		controller.saveConsumer( consumer );
		assertThat( controller.save( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context, propertyValue );

		controller.withEntity( String.class, Long.class )
		          .saveConsumer( consumer );
		assertThat( controller.save( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context.getEntity(), propertyValue );

		controller.withTarget( BigDecimal.class, Long.class )
		          .saveConsumer( consumer );
		assertThat( controller.save( context, propertyValue ) ).isTrue();
		verify( consumer ).accept( context.getTarget(), propertyValue );
	}

	@Test
	public void validate() {
		Errors errors = mock( Errors.class );

		controller.validate( context, new EntityPropertyValue( 123, 321, false ), errors, "hint" );

		EntityPropertyValidator validator = mock( EntityPropertyValidator.class );
		controller.validator( validator );
		controller.validate( context, new EntityPropertyValue( 123, 321, false ), errors, "hint" );
		verify( validator ).validate( context, new EntityPropertyValue( 123, 321, false ), errors, "hint" );

		GenericEntityPropertyController child = new GenericEntityPropertyController( controller );
		child.validate( context, new EntityPropertyValue( 456, 321, false ), errors, "hint" );
		verify( validator ).validate( context, new EntityPropertyValue( 456, 321, false ), errors, "hint" );

		EntityPropertyValidator otherValidator = mock( EntityPropertyValidator.class );
		child.validator( otherValidator );
		child.validate( context, new EntityPropertyValue( 789, 321, false ), errors, "hint" );
		verify( otherValidator ).validate( context, new EntityPropertyValue( 789, 321, false ), errors, "hint" );
		verifyNoMoreInteractions( validator );

		AtomicBoolean called = new AtomicBoolean();
		assertThat( controller.withTarget( BigDecimal.class, Long.class )
		                      .contextualValidator( ( d, l, e, hints ) -> {
			                      assertThat( d ).isEqualTo( context.<BigDecimal>getTarget() );
			                      assertThat( l ).isEqualTo( 321L );
			                      assertThat( e ).isSameAs( errors );
			                      assertThat( hints ).containsExactly( "hint" );
			                      called.set( true );
		                      } ) )
				.isNotNull();

		controller.validate( context, new EntityPropertyValue<>( 123L, 321L, false ), errors, "hint" );
		assertThat( called ).isTrue();
	}
}
