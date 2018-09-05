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
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
public class GenericEntityPropertyController implements EntityPropertyController, ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object>
{
	private final EntityPropertyController<EntityPropertyBindingContext, Object> original;

	/**
	 * Processing order for this controller.
	 */
	@Getter
	private int order = AFTER_ENTITY;

	@Getter
	private Function<EntityPropertyBindingContext, Object> valueFetcher;

	@Getter
	private Function<EntityPropertyBindingContext, Object> createValueFunction;

	@Getter
	private BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> applyValueFunction;

	@Getter
	private BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> saveFunction;

	@Getter
	private List<Validator> validators = new ArrayList<>();

	public GenericEntityPropertyController() {
		this.original = null;
	}

	@Override
	public GenericEntityPropertyController order( int order ) {
		this.order = order;
		return this;
	}

	@Override
	public GenericEntityPropertyController valueFetcher( Function<EntityPropertyBindingContext, Object> valueFetcher ) {
		this.valueFetcher = valueFetcher;
		return this;
	}

	@Override
	public GenericEntityPropertyController createValueSupplier( Supplier<Object> supplier ) {
		return createValueFunction( e -> supplier.get() );
	}

	@Override
	public GenericEntityPropertyController createValueFunction( Function<EntityPropertyBindingContext, Object> function ) {
		this.createValueFunction = function;
		return this;
	}

	@Override
	public GenericEntityPropertyController applyValueConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> valueWriter ) {
		if ( valueWriter == null ) {
			this.applyValueFunction = null;
		}
		else {
			this.applyValueFunction = ( context, value ) -> {
				valueWriter.accept( context, value );
				return true;
			};
		}
		return this;
	}

	@Override
	public GenericEntityPropertyController applyValueFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> valueWriter ) {
		this.applyValueFunction = valueWriter;
		return this;
	}

	@Override
	public GenericEntityPropertyController saveConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> saveFunction ) {
		if ( saveFunction == null ) {
			this.saveFunction = null;
		}
		else {
			this.saveFunction = ( context, value ) -> {
				saveFunction.accept( context, value );
				return true;
			};
		}
		return this;
	}

	@Override
	public GenericEntityPropertyController saveFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> saveFunction ) {
		this.saveFunction = saveFunction;
		return this;
	}

	@Override
	public GenericEntityPropertyController addValidator( ContextualValidator<EntityPropertyBindingContext, Object> contextualValidator ) {
		return addValidators( contextualValidator );
	}

	@Override
	public GenericEntityPropertyController addValidator( Validator validator ) {
		return addValidators( validator );
	}

	@Override
	public GenericEntityPropertyController addValidators( Validator... validators ) {
		this.validators.addAll( Arrays.asList( validators ) );
		return this;
	}

	@Override
	public Object fetchValue( EntityPropertyBindingContext context ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( context );
		}
		return original != null ? original.fetchValue( context ) : null;
	}

	@Override
	public Object createValue( EntityPropertyBindingContext context ) {
		if ( createValueFunction != null ) {
			return createValueFunction.apply( context );
		}
		return original != null ? original.createValue( context ) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean applyValue( @NonNull EntityPropertyBindingContext context, @NonNull EntityPropertyValue propertyValue ) {
		if ( applyValueFunction != null ) {
			return applyValueFunction.apply( context, propertyValue );
		}

		return original != null ? original.applyValue( context, propertyValue ) : false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean save( EntityPropertyBindingContext context, EntityPropertyValue propertyValue ) {
		if ( saveFunction != null ) {
			return saveFunction.apply( context, propertyValue );
		}

		return original != null ? original.save( context, propertyValue ) : false;
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<X> entityType, Class<V> propertyType ) {
		return new ScopedConfigurableEntityPropertyController<>( this, EntityPropertyBindingContext::getEntity );
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<X> entityType, Class<V> propertyType ) {
		return new ScopedConfigurableEntityPropertyController<>( this, EntityPropertyBindingContext::getTarget );
	}

	@Override
	public <X, W, V> ConfigurableEntityPropertyController<EntityPropertyBindingContext<X, W>, V> withBindingContext(
			Class<X> entityType, Class<W> targetType, Class<V> propertyType ) {
		return new ScopedConfigurableEntityPropertyController<>( this, ctx -> ctx );
	}
}
