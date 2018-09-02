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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Validator;

import java.util.function.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
class ScopedConfigurableEntityPropertyController<T, U> implements ConfigurableEntityPropertyController<T, U>
{
	@NonNull
	private final ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> parent;

	@NonNull
	private final Function<EntityPropertyBindingContext, Object> bindingContextTranslator;

	@Override
	public ConfigurableEntityPropertyController<T, U> order( int order ) {
		parent.order( order );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> valueFetcher( Function<T, U> valueFetcher ) {
		Function<EntityPropertyBindingContext, Object> wrapper = ctx -> valueFetcher.apply( (T) bindingContextTranslator.apply( ctx ) );
		parent.valueFetcher( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> createValueSupplier( Supplier<U> supplier ) {
		parent.createValueSupplier( supplier::get );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> createValueFunction( Function<T, U> function ) {
		Function<EntityPropertyBindingContext, Object> wrapper = ctx -> function.apply( (T) bindingContextTranslator.apply( ctx ) );
		parent.createValueFunction( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> applyValueConsumer( BiConsumer<T, U> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> applyValueFunction( BiFunction<T, U, Boolean> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> saveConsumer( BiConsumer<T, U> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> saveFunction( BiFunction<T, U, Boolean> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> deleteConsumer( Consumer<T> deleteFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> deleteFunction( Function<T, Boolean> deleteFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> existsFunction( Function<T, Boolean> existsFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> addValidator( ContextualValidator<T, U> contextualValidator ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> addValidator( Validator validator ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> addValidators( Validator... validators ) {
		return null;
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<X> entityType, Class<V> propertyType ) {
		return parent.withEntity( entityType, propertyType );
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<X> targetType, Class<V> propertyType ) {
		return parent.withTarget( targetType, propertyType );
	}

	@Override
	public <X, W, V> ConfigurableEntityPropertyController<EntityPropertyBindingContext<X, W>, V> withBindingContext( Class<X> entityType,
	                                                                                                                 Class<W> targetType,
	                                                                                                                 Class<V> propertyType ) {
		return parent.withBindingContext( entityType, targetType, propertyType );
	}
}
