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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Generic implementation of {@link EntityPropertyController} where every controller
 * action is delegated to an optionally present function. Implements delete as a {@link #save(Object, Object)}
 * with the value being {@code null}.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
@Accessors(chain = true)
@Deprecated
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
	private Function<EntityPropertyBindingContext, U> valueFetcher;

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
		//this.valueFetcher = valueFetcher;
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> applyValueConsumer( @NonNull BiConsumer<T, EntityPropertyValue<U>> valueWriter ) {
		this.applyValueFunction = ( entity, value ) -> {
			//valueWriter.accept( entity, value );
			return true;
		};
		return this;
	}

	@Override
	public GenericEntityPropertyController<T, U> applyValueFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> valueWriter ) {
		//this.applyValueFunction = valueWriter;
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
	public ConfigurableEntityPropertyController<T, U> saveConsumer( BiConsumer<T, EntityPropertyValue<U>> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> saveFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> saveFunction ) {
		return null;
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
	public U fetchValue( EntityPropertyBindingContext<T, ?> context ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( context );
		}
		return parent != null ? parent.fetchValue( context ) : null;
	}

	@Override
	public U fetchValue( T owner ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( null );
		}
		return parent != null ? parent.fetchValue( owner ) : null;
	}

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

	public boolean applyValue( T owner, U oldPropertyValue, U newPropertyValue ) {
//		if ( applyValueFunction == null && parent != null ) {
//			return parent.applyValue( owner, oldPropertyValue, newPropertyValue );
//		}
		return applyValueFunction != null && Boolean.TRUE.equals( applyValueFunction.apply( owner, newPropertyValue ) );
	}

	public boolean save( T owner, U propertyValue ) {
//		if ( saveFunction == null && parent != null ) {
//			return parent.save( owner, propertyValue );
//		}
		return saveFunction != null && Boolean.TRUE.equals( saveFunction.apply( owner, propertyValue ) );
	}

	@Override
	public U createValue( EntityPropertyBindingContext<T, ?> context ) {
		return null;
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<X> entityType, Class<V> propertyType ) {
		return null;
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<X> targetType, Class<V> propertyType ) {
		return null;
	}

	@Override
	public <X, W, V> ConfigurableEntityPropertyController<EntityPropertyBindingContext<X, W>, V> withBindingContext( Class<X> entityType,
	                                                                                                                 Class<W> targetType,
	                                                                                                                 Class<V> propertyType ) {
		return null;
	}
}
