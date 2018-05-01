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

package com.foreach.across.modules.entity.views.support;

import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;

/**
 * Custom {@link org.springframework.validation.SmartValidator}-like interface
 * for validating an object in a given context situation. Also implements the
 * regular {@link SmartValidator} interface, but be aware that calling it as a
 * regular validator will result in a {@code null} context to be passed by default.
 *
 * @param <T> context type
 * @param <U> type of the object to validate
 * @author Arne Vandamme
 * @since 3.1.0
 */
@FunctionalInterface
public interface ContextualValidator<T, U> extends SmartValidator
{
	default boolean supports( Class<?> targetType, Class<?> contextType ) {
		return supports( targetType );
	}

	default boolean supports( Class<?> targetType ) {
		return true;
	}

	@Override
	default void validate( Object target, Errors errors ) {
		validate( target, errors, new Object[0] );
	}

	@SuppressWarnings("unchecked")
	@Override
	default void validate( Object target, Errors errors, Object... validationHints ) {
		validate( null, (U) target, errors, validationHints );
	}

	/**
	 * Validate the supplied {@code target} object, which must be of a type of {@link Class}
	 * for which the {@link #supports(Class)} method typically returns {@code true}.
	 * <p/>
	 * The supplied {@link Errors errors} instance can be used to report any
	 * resulting validation errors. The {@code context} provides additional information of the
	 * context in which the target value is relevant.
	 * <p/>
	 * This method supports validation hints, such as validation groups against a JSR-303 provider
	 * (in which case, the provided hint objects need to be annotation arguments of type {@code Class}).
	 *
	 * @param target          the object that is to be validated (can be {@code null})
	 * @param errors          contextual state about the validation process (never {@code null})
	 * @param validationHints one or more hint objects to be passed to the validation engine
	 * @see ValidationUtils
	 */
	void validate( T context, U target, Errors errors, Object... validationHints );
}
