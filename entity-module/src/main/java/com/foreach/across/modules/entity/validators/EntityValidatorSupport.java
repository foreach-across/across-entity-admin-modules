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

package com.foreach.across.modules.entity.validators;

import com.foreach.across.modules.entity.annotations.EntityValidator;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

/**
 * Base adapter class for a custom {@link Validator} that by default applies the registered
 * entity validator bean (named {@link com.foreach.across.modules.entity.EntityModule#VALIDATOR}) if there is one.
 * <p/>
 * Provides hooks to execute before and after the default validator.
 * Supports smart validation hints and provides pre and post validation methods with and without validation hints.
 * <p/>
 *
 * @author Arne Vandamme
 */
public abstract class EntityValidatorSupport<T> implements SmartValidator
{
	private Validator entityValidator;

	@EntityValidator(required = false)
	public final void setEntityValidator( Validator entityValidator ) {
		this.entityValidator = entityValidator;
	}

	@Override
	public final void validate( Object target, Errors errors ) {
		validate( target, errors, new Object[0] );
	}

	@SuppressWarnings("unchecked")
	@Override
	public final void validate( Object target, Errors errors, Object... validationHints ) {
		preValidation( (T) target, errors, validationHints );

		if ( entityValidator != null ) {
			if ( entityValidator instanceof SmartValidator ) {
				( (SmartValidator) entityValidator ).validate( target, errors, validationHints );
			}
			else {
				entityValidator.validate( target, errors );
			}
		}

		postValidation( (T) target, errors, validationHints );
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return false;
	}

	/**
	 * Executes before the initial entity validation happens.
	 * Override this method if you want to support custom validation hints.
	 * <p/>
	 * By default just dispatches to {@link #preValidation(Object, Errors)}.
	 */
	protected void preValidation( T entity, Errors errors, Object... validationHints ) {
		preValidation( entity, errors );
	}

	/**
	 * Executes before the initial entity validation happens.
	 *
	 * @deprecated in favour of {@link #preValidation(Object, Errors, Object...)}
	 */
	@Deprecated
	protected void preValidation( T entity, Errors errors ) {

	}

	/**
	 * Executes after the initial entity validation has happened.
	 * Override this method if you want to support custom validation hints.
	 * <p/>
	 * By default just dispatches to {@link #preValidation(Object, Errors)}.
	 */
	protected void postValidation( T entity, Errors errors, Object... validationHints ) {
		postValidation( entity, errors );
	}

	/**
	 * Executes after the initial entity validation has happened.
	 *
	 * @deprecated in favour of {@link #postValidation(Object, Errors, Object...)}
	 */
	@Deprecated
	protected void postValidation( T entity, Errors errors ) {

	}
}
