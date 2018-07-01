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
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.*;

/**
 * Generic implementation of {@link EntityPropertyController} where every controller
 * action is delegated to an optionally present function. Implements delete as a {@link #save(Object, Object)}
 * with the value being {@code null}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@RequiredArgsConstructor
@Accessors(chain = true)
public class GenericEntityPropertyController<T, U> implements EntityPropertyController<T, U>, ConfigurableEntityPropertyController<T, U>
{
	private final EntityPropertyController<T, U> parent;

	public GenericEntityPropertyController() {
		this.parent = null;
	}

	/**
	 * Processing order for this controller.
	 */
	@Getter
	private int order = AFTER_ENTITY;

	/**
	 * Function to call when getting the property value.
	 */
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
	private Function<T, U> createValueFunction;

	@Getter
	private List<Validator> validators = new ArrayList<>();

	@Override
	public GenericEntityPropertyController<T, U> order( int order ) {
		this.order = order;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> valueFetcher( Function<T, U> valueFetcher ) {
		this.valueFetcher = valueFetcher;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> applyValueConsumer( @NonNull BiConsumer<T, U> valueWriter ) {
		this.applyValueFunction = ( entity, value ) -> {
			valueWriter.accept( entity, value );
			return true;
		};
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> applyValueFunction( BiFunction<T, U, Boolean> valueWriter ) {
		this.applyValueFunction = valueWriter;
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> createValueSupplier( Supplier<U> supplier ) {
		return createValueFunction( e -> supplier.get() );
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> createValueFunction( Function<T, U> function ) {
		this.createValueFunction = function;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> saveConsumer( @NotNull BiConsumer<T, U> saveFunction ) {
		this.saveFunction = ( entity, value ) -> {
			saveFunction.accept( entity, value );
			return true;
		};
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> saveFunction( BiFunction<T, U, Boolean> saveFunction ) {
		this.saveFunction = saveFunction;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> deleteConsumer( @NonNull Consumer<T> deleteFunction ) {
		this.deleteFunction = entity -> {
			deleteFunction.accept( entity );
			return true;
		};
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> deleteFunction( Function<T, Boolean> deleteFunction ) {
		this.deleteFunction = deleteFunction;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> existsFunction( Function<T, Boolean> existsFunction ) {
		this.existsFunction = existsFunction;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> addValidator( @NonNull ContextualValidator<T, U> contextualValidator ) {
		return addValidators( contextualValidator );
	}

	@Override
	public GenericEntityPropertyController<T, U> addValidator( @NonNull Validator validator ) {
		return addValidators( validator );
	}

	@Override
	public GenericEntityPropertyController<T, U> addValidators( Validator... validators ) {
		this.validators.addAll( Arrays.asList( validators ) );
		return this;
	}

	@Override
	public U fetchValue( T owner ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( owner );
		}
		return parent != null ? parent.fetchValue( owner ) : null;
	}

	@Override
	public U createValue( T owner ) {
		if ( createValueFunction != null ) {
			return createValueFunction.apply( owner );
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void validate( T owner, U propertyValue, Errors errors, Object... validationHints ) {
		if ( validators.isEmpty() && parent != null ) {
			parent.validate( owner, propertyValue, errors, validationHints );
		}
		else {
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
	}

	@Override
	public boolean applyValue( T owner, U oldPropertyValue, U newPropertyValue ) {
		if ( applyValueFunction == null && parent != null ) {
			return parent.applyValue( owner, oldPropertyValue, newPropertyValue );
		}
		return applyValueFunction != null && Boolean.TRUE.equals( applyValueFunction.apply( owner, newPropertyValue ) );
	}

	@Override
	public boolean save( T owner, U propertyValue ) {
		if ( saveFunction == null && parent != null ) {
			return parent.save( owner, propertyValue );
		}
		return saveFunction != null && Boolean.TRUE.equals( saveFunction.apply( owner, propertyValue ) );
	}
}
