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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.EntityModule;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator implementation for a {@link EmbeddedCollectionsBinder}, will ensure
 * that all members present in the binder will get validated correctly.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@Component
@Exposed
public class EmbeddedCollectionsBinderValidator implements SmartValidator
{
	private final Validator fallbackValidator;

	@Autowired
	public EmbeddedCollectionsBinderValidator( @NonNull @Qualifier(EntityModule.VALIDATOR) Validator fallbackValidator ) {
		this.fallbackValidator = fallbackValidator;
	}

	@Override
	public boolean supports( Class<?> clazz ) {
		return EmbeddedCollectionsBinder.class.isAssignableFrom( clazz );
	}

	@Override
	public void validate( Object target, Errors errors ) {
		validate( target, errors, new Object[0] );
	}

	@Override
	public void validate( Object target, Errors errors, Object... validationHints ) {
		EmbeddedCollectionsBinder collectionsBinder = (EmbeddedCollectionsBinder) target;

		collectionsBinder
				.forEach( ( key, data ) -> {
					errors.pushNestedPath( "prop[" + key + "]" );
					data.forEach( ( itemId, value ) -> {
						errors.pushNestedPath( "item[" + itemId + "].data" );
						ValidationUtils.invokeValidator( fallbackValidator, value.getData(), errors, validationHints );
						errors.popNestedPath();
					} );
					errors.popNestedPath();
				} );
	}
}
