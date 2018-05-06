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

import com.foreach.across.modules.entity.views.support.ContextualValidator;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Generic implementation of {@link EntityPropertyController} where every controller
 * action is delegated to an optionally present function.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@Accessors(chain = true)
public class GenericEntityPropertyController<T, U> implements EntityPropertyController<T, U>
{
	/**
	 * Processing order for this controller.
	 */
	@Getter
	@Setter
	private int order = AFTER_ENTITY;

	/**
	 * Function to call when getting the property value.
	 */
	@Setter
	@Getter
	private Function<T, U> valueFetcher;

	@Getter
	private BiFunction<T, U, Boolean> applyValueFunction;

	@Getter
	private BiFunction<T, U, Boolean> saveFunction;

	@Getter
	private Function<T, Boolean> deleteFunction;

	@Getter
	private Function<T, Boolean> existsFunction;

	@Getter
	private List<Validator> validators = new ArrayList<>();

	/**
	 * Set the consumer that should be called when applying the property value using {@link #applyValue(Object, Object)}.
	 * The return value of calling {@link #applyValue(Object, Object)} will always be {@code true}
	 * if you specify a {@link BiConsumer}. See {@link #setApplyValueFunction(BiFunction)} if you
	 * want to control the return value.
	 *
	 * @param valueWriter consumer for setting the value
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setApplyValueConsumer( @NonNull BiConsumer<T, U> valueWriter ) {
		this.applyValueFunction = ( entity, value ) -> {
			valueWriter.accept( entity, value );
			return true;
		};
		return this;
	}

	/**
	 * The function that should be called when setting the property value using {@link #applyValue(Object, Object)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param valueWriter function for setting the value
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setApplyValueFunction( BiFunction<T, U, Boolean> valueWriter ) {
		this.applyValueFunction = valueWriter;
		return this;
	}

	/**
	 * The consumer that should be called when saving a property value using {@link #save(Object, Object)}.
	 * The return value of calling {@link #save(Object, Object)} will always be {@code true}
	 * if you specify a {@link BiConsumer}. See {@link #setSaveFunction(BiFunction)} if you
	 * want to control the return value.
	 *
	 * @param saveFunction consumer for saving the value
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setSaveConsumer( @NotNull BiConsumer<T, U> saveFunction ) {
		this.saveFunction = ( entity, value ) -> {
			saveFunction.accept( entity, value );
			return true;
		};
		return this;
	}

	/**
	 * The function that should be called when saving a property value using {@link #save(Object, Object)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param saveFunction function for saving the value
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setSaveFunction( BiFunction<T, U, Boolean> saveFunction ) {
		this.saveFunction = saveFunction;
		return this;
	}

	/**
	 * The function that should be called when deleting a property using {@link #delete(Object)}.
	 * The return value of calling {@link #delete(Object)} will always be {@code true}
	 * if you specify a {@link Consumer}. See {@link #setDeleteFunction(Function)} if you
	 * want to control the return value.
	 *
	 * @param deleteFunction function for deleting the property
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setDeleteConsumer( @NonNull Consumer<T> deleteFunction ) {
		this.deleteFunction = entity -> {
			deleteFunction.accept( entity );
			return true;
		};
		return this;
	}

	/**
	 * The function that should be called when deleting a property using {@link #delete(Object)}.
	 * If the {@link Function} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param deleteFunction function for deleting the property
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setDeleteFunction( Function<T, Boolean> deleteFunction ) {
		this.deleteFunction = deleteFunction;
		return this;
	}

	/**
	 * The function that should be called when checking if a property exists using {@link #exists(Object)}.
	 * If the {@link Function} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param existsFunction function for checking if the property exists
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> setExistsFunction( Function<T, Boolean> existsFunction ) {
		this.existsFunction = existsFunction;
		return this;
	}

	/**
	 * Add a contextual validator for this property type and entity combination.
	 *
	 * @param contextualValidator validator to add
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> addValidator( @NonNull ContextualValidator<T, U> contextualValidator ) {
		return addValidators( contextualValidator );
	}

	/**
	 * Add a generic validator for this property. Note that {@link Validator#supports(Class)}
	 * will not be called before calling the actual validate method.
	 *
	 * @param validator to add
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> addValidator( @NonNull Validator validator ) {
		return addValidators( validator );
	}

	/**
	 * Add a number of validators for this property. Prefer {@link ContextualValidator} instances.
	 *
	 * @param validators to add
	 * @return self
	 */
	public GenericEntityPropertyController<T, U> addValidators( Validator... validators ) {
		this.validators.addAll( Arrays.asList( validators ) );
		return this;
	}

	@Override
	public U fetchValue( T owner ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( owner );
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate( T owner, U propertyValue, Errors errors, Object... validationHints ) {
		validators.forEach(
				validator -> {
					if ( validator instanceof ContextualValidator ) {
						( (ContextualValidator) validator ).validate( owner, propertyValue, errors, validationHints );
					}
					else if ( validator instanceof SmartValidator ) {
						( (SmartValidator) validator ).validate( propertyValue, errors, validationHints );
					}
					else {
						validator.validate( propertyValue, errors );
					}
				}
		);
	}

	@Override
	public boolean applyValue( T owner, U propertyValue ) {
		return applyValueFunction != null && Boolean.TRUE.equals( applyValueFunction.apply( owner, propertyValue ) );
	}

	@Override
	public boolean save( T owner, U propertyValue ) {
		return saveFunction != null && Boolean.TRUE.equals( saveFunction.apply( owner, propertyValue ) );
	}

	@Override
	public boolean delete( T owner ) {
		return deleteFunction != null && Boolean.TRUE.equals( deleteFunction.apply( owner ) );
	}

	@Override
	public boolean exists( T owner ) {
		return existsFunction == null || Boolean.TRUE.equals( existsFunction.apply( owner ) );
	}
}
