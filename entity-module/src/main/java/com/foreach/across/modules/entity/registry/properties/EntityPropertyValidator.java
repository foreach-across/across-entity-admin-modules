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

import lombok.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

/**
 * Specific validator implementation for validating properties in an entity property binding context.
 * This class provides more contextual details about the property value and the context of its owner.
 *
 * @author Arne Vandamme
 * @see EntityPropertyController
 * @see com.foreach.across.modules.entity.bind.EntityPropertiesBinder
 * @since 3.2.0
 */
@FunctionalInterface
public interface EntityPropertyValidator
{
	/**
	 * Validate an {@link EntityPropertyValue} in a specific {@link EntityPropertyBindingContext}.
	 *
	 * @param bindingContext  of the owner of the property
	 * @param propertyValue   property value holder
	 * @param errors          object containing all current errors
	 * @param validationHints to apply specific validation only
	 */
	void validate( EntityPropertyBindingContext bindingContext, EntityPropertyValue propertyValue, Errors errors, Object... validationHints );

	/**
	 * Create a new validator that first apply the current validator and then the one passed as argument.
	 *
	 * @param after validator to execute after this one
	 * @return new chained validator
	 */
	default EntityPropertyValidator andThen( @NonNull EntityPropertyValidator after ) {
		return ( bindingContext, propertyValue, errors, validationHints ) -> {
			validate( bindingContext, propertyValue, errors, validationHints );
			after.validate( bindingContext, propertyValue, errors, validationHints );
		};
	}

	/**
	 * Create a new validator that first applies the validator passed as argument and then the current one.
	 *
	 * @param before validator to execute before this one
	 * @return new composed validator
	 */
	default EntityPropertyValidator compose( @NonNull EntityPropertyValidator before ) {
		return ( bindingContext, propertyValue, errors, validationHints ) -> {
			before.validate( bindingContext, propertyValue, errors, validationHints );
			validate( bindingContext, propertyValue, errors, validationHints );
		};
	}

	/**
	 * Adapt a regular Spring {@link Validator} into an {@link EntityPropertyValidator}.
	 * Only the value of the property will be passed to the target validator, any validation hints will be ignored.
	 * Validation methods will be directly forwarded to the target, no {@link Validator#supports(Class)} will be called.
	 *
	 * @param validator that should be adapted
	 * @return property validator instance
	 */
	static EntityPropertyValidator of( @NonNull Validator validator ) {
		return ( bindingContext, propertyValue, errors, validationHints ) -> validator.validate( propertyValue.getNewValue(), errors );
	}

	/**
	 * Adapt a Spring {@link SmartValidator} into an {@link EntityPropertyValidator}.
	 * Only the value of the property will be passed on to the target validator.
	 *
	 * @param validator that should be adapted
	 * @return property validator instance
	 */
	static EntityPropertyValidator of( @NonNull SmartValidator validator ) {
		return ( bindingContext, propertyValue, errors, validationHints ) -> validator.validate( propertyValue.getNewValue(), errors, validationHints );
	}
}
