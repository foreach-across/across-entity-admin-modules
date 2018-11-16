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
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityPropertyValidator
{
	@Mock
	private EntityPropertyBindingContext bindingContext;

	@Mock
	private EntityPropertyValue<Object> propertyValue;

	@Mock
	private Errors errors;

	@Test
	public void andThen() {
		EntityPropertyValidator first = mock( EntityPropertyValidator.class );
		EntityPropertyValidator second = mock( EntityPropertyValidator.class );
		when( first.andThen( any() ) ).thenCallRealMethod();

		EntityPropertyValidator composed = first.andThen( second );
		assertThat( composed ).isNotNull();
		composed.validate( bindingContext, propertyValue, errors, "hint" );

		InOrder inOrder = inOrder( first, second );
		inOrder.verify( first ).validate( bindingContext, propertyValue, errors, "hint" );
		inOrder.verify( second ).validate( bindingContext, propertyValue, errors, "hint" );
	}

	@Test
	public void compose() {
		EntityPropertyValidator first = mock( EntityPropertyValidator.class );
		EntityPropertyValidator second = mock( EntityPropertyValidator.class );
		when( first.compose( any() ) ).thenCallRealMethod();

		EntityPropertyValidator composed = first.compose( second );
		assertThat( composed ).isNotNull();
		composed.validate( bindingContext, propertyValue, errors, "hint" );

		InOrder inOrder = inOrder( first, second );
		inOrder.verify( second ).validate( bindingContext, propertyValue, errors, "hint" );
		inOrder.verify( first ).validate( bindingContext, propertyValue, errors, "hint" );
	}

	@Test
	public void ofValidator() {
		Validator target = mock( Validator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( 123 );
		adapted.validate( bindingContext, propertyValue, errors, "hint" );

		verify( target ).validate( 123, errors );
	}

	@Test
	public void ofValidatorWithNullValidation() {
		Validator target = mock( Validator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target, true );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( null );
		adapted.validate( bindingContext, propertyValue, errors, "hint" );

		verify( target ).validate( null, errors );
	}

	@Test
	public void ofValidatorWithoutNullValidation() {
		Validator target = mock( Validator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( null );
		adapted.validate( bindingContext, propertyValue, errors, "hint" );

		verifyNoMoreInteractions( target );
	}

	@Test
	public void ofSmartValidator() {
		SmartValidator target = mock( SmartValidator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( 123 );
		adapted.validate( bindingContext, propertyValue, errors, "hint", "extra" );

		verify( target ).validate( 123, errors, "hint", "extra" );
	}

	@Test
	public void ofSmartValidatorWithNullValidation() {
		SmartValidator target = mock( SmartValidator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target, true );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( null );
		adapted.validate( bindingContext, propertyValue, errors, "hint", "extra" );

		verify( target ).validate( null, errors, "hint", "extra" );
	}

	@Test
	public void ofSmartValidatorWithoutNullValidation() {
		SmartValidator target = mock( SmartValidator.class );
		EntityPropertyValidator adapted = EntityPropertyValidator.of( target );

		assertThat( adapted ).isNotNull();

		when( propertyValue.getNewValue() ).thenReturn( null );
		adapted.validate( bindingContext, propertyValue, errors, "hint", "extra" );

		verifyNoMoreInteractions( target );
	}
}
