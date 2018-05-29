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

import com.foreach.across.modules.entity.registry.properties.GenericEntityPropertyController;
import com.foreach.across.modules.entity.views.support.ContextualValidator;
import lombok.val;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import java.util.Date;
import java.util.function.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
@SuppressWarnings("unchecked")
public class TestGenericEntityPropertyController
{
	private GenericEntityPropertyController<String, Long> controller = new GenericEntityPropertyController<>();

	@Test
	public void getValue() {
		assertThat( controller.fetchValue( "any-string" ) ).isNull();

		val vf = mock( Function.class );
		when( vf.apply( "any-string" ) ).thenReturn( 123L );
		assertThat( controller.valueFetcher( vf ) ).isSameAs( controller );

		assertThat( controller.fetchValue( "any-string" ) ).isEqualTo( 123L );
		assertThat( controller.fetchValue( "other" ) ).isNull();
	}

	@Test
	public void createValue() {
		assertThat( controller.createValue( "any-string" ) ).isNull();

		val s = mock( Supplier.class );
		controller.createValueSupplier( s );
		when( s.get() ).thenReturn( 123L );
		assertThat( controller.createValue( "any-string" ) ).isEqualTo( 123L );

		val f = mock( Function.class );
		controller.createValueFunction( f );
		when( f.apply( "my-string" ) ).thenReturn( 5L );
		assertThat( controller.createValue( "my-string" ) ).isEqualTo( 5L );
	}

	@Test
	public void setValue() {
		assertThat( controller.applyValue( "any-string", 123L ) ).isFalse();

		val consumer = mock( BiConsumer.class );
		assertThat( controller.applyValueConsumer( consumer ) ).isSameAs( controller );
		assertThat( controller.applyValue( "some-string", 555L ) ).isTrue();
		verify( consumer ).accept( "some-string", 555L );

		val vw = mock( BiFunction.class );
		when( vw.apply( "any-string", 123L ) ).thenReturn( true );
		assertThat( controller.applyValueFunction( vw ) ).isSameAs( controller );

		assertThat( controller.applyValue( "any-string", 123L ) ).isTrue();
		assertThat( controller.applyValue( "any-string", 0L ) ).isFalse();
	}

	@Test
	public void save() {
		assertThat( controller.save( "any-string", 123L ) ).isFalse();

		val consumer = mock( BiConsumer.class );
		assertThat( controller.saveConsumer( consumer ) ).isSameAs( controller );
		assertThat( controller.save( "some-string", 555L ) ).isTrue();
		verify( consumer ).accept( "some-string", 555L );

		val vw = mock( BiFunction.class );
		when( vw.apply( "any-string", 123L ) ).thenReturn( true );
		assertThat( controller.saveFunction( vw ) ).isSameAs( controller );

		assertThat( controller.save( "any-string", 123L ) ).isTrue();
		assertThat( controller.save( "any-string", 0L ) ).isFalse();
	}

	@Test
	public void delete() {
		assertThat( controller.delete( "any-string" ) ).isFalse();

		val saveConsumer = mock( BiConsumer.class );
		controller.saveConsumer( saveConsumer );
		assertThat( controller.delete( "some-string" ) ).isTrue();
		verify( saveConsumer ).accept( "some-string", null );

		val consumer = mock( Consumer.class );
		assertThat( controller.deleteConsumer( consumer ) ).isSameAs( controller );
		assertThat( controller.delete( "some-string" ) ).isTrue();
		verify( consumer ).accept( "some-string" );
		verifyNoMoreInteractions( saveConsumer );

		val vw = mock( Function.class );
		when( vw.apply( "any-string" ) ).thenReturn( true );
		assertThat( controller.deleteFunction( vw ) ).isSameAs( controller );

		assertThat( controller.delete( "any-string" ) ).isTrue();
		assertThat( controller.delete( "other-string" ) ).isFalse();
	}

	@Test
	public void exists() {
		assertThat( controller.exists( "any-string" ) ).isTrue();

		val vw = mock( Function.class );
		when( vw.apply( "any-string" ) ).thenReturn( true );
		assertThat( controller.existsFunction( vw ) ).isSameAs( controller );

		assertThat( controller.exists( "any-string" ) ).isTrue();
		assertThat( controller.exists( "other-string" ) ).isFalse();
	}

	@Test
	public void validate() {
		Validator validator = mock( Validator.class );
		SmartValidator smartValidator = mock( SmartValidator.class );
		ContextualValidator contextualValidator = mock( ContextualValidator.class );
		assertThat( controller.addValidator( validator ) ).isSameAs( controller );
		assertThat( controller.addValidators( smartValidator, contextualValidator ) ).isSameAs( controller );

		val errors = mock( Errors.class );
		controller.validate( "context", 123L, errors, Date.class );

		val inOrder = Mockito.inOrder( validator, smartValidator, contextualValidator );
		inOrder.verify( validator ).validate( 123L, errors );
		inOrder.verify( smartValidator ).validate( 123L, errors, Date.class );
		inOrder.verify( contextualValidator ).validate( "context", 123L, errors, Date.class );
	}

	@Test
	public void overridingParentController() {
		GenericEntityPropertyController<String, Long> child = new GenericEntityPropertyController<>( controller );
		val vf = mock( Function.class );
		when( vf.apply( "any-string" ) ).thenReturn( 123L );
		assertThat( controller.valueFetcher( vf ) ).isSameAs( controller );
		assertThat( child.fetchValue( "any-string" ) ).isEqualTo( 123L );
	}
}
