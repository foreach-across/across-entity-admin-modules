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

package com.foreach.across.modules.entity.views.request;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewCommandValidator
{
	@Mock
	private Validator fallbackValidator;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private Errors errors;

	@InjectMocks
	private EntityViewCommandValidator validator;

	@Test
	public void supportsEntityViewCommandOnly() {
		assertTrue( validator.supports( EntityViewCommand.class ) );
		assertFalse( validator.supports( Object.class ) );
	}

	@Test
	public void nullEntity() {
		validator.validate( new EntityViewCommand(), errors );
		verifyNoMoreInteractions( errors );
	}

	@Test
	public void fallbackEntityValidationWithoutExtensions() {
		EntityViewCommand command = new EntityViewCommand();
		command.setEntity( "some entity" );

		validator.validate( command, errors );

		InOrder ordered = inOrder( fallbackValidator, errors );
		ordered.verify( errors ).pushNestedPath( "entity" );
		ordered.verify( fallbackValidator ).validate( "some entity", errors );
		ordered.verify( errors ).popNestedPath();
	}

	@Test
	public void specificEntityValidatorWithoutExtensions() {
		Validator specific = mock( Validator.class );
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		when( entityConfiguration.getAttribute( Validator.class ) ).thenReturn( specific );

		EntityViewCommand command = new EntityViewCommand();
		command.setEntity( "some entity" );

		validator.validate( command, errors );

		InOrder ordered = inOrder( specific, errors );
		ordered.verify( errors ).pushNestedPath( "entity" );
		ordered.verify( specific ).validate( "some entity", errors );
		ordered.verify( errors ).popNestedPath();
	}

	@Test
	public void extensionValidation() {
		EntityViewCommand command = new EntityViewCommand();
		command.addExtension( "someExtension", "123" );
		command.addExtension( "other", "456" );

		validator.validate( command, errors );
		InOrder ordered = inOrder( fallbackValidator, errors );
		ordered.verify( errors ).pushNestedPath( "extensions[someExtension]" );
		ordered.verify( fallbackValidator ).validate( "123", errors );
		ordered.verify( errors ).popNestedPath();
		ordered.verify( errors ).pushNestedPath( "extensions[other]" );
		ordered.verify( fallbackValidator ).validate( "456", errors );
		ordered.verify( errors ).popNestedPath();
	}
}
