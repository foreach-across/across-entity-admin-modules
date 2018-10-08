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
import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

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
	public ConfigurableEntityPropertyController<T, U> applyValueConsumer( BiConsumer<T, EntityPropertyValue<U>> valueWriter ) {
		BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> wrapper =
				( ctx, value ) -> valueWriter.accept( (T) bindingContextTranslator.apply( ctx ), (EntityPropertyValue<U>) value );
		parent.applyValueConsumer( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> applyValueFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> valueWriter ) {
		BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> wrapper =
				( ctx, value ) -> valueWriter.apply( (T) bindingContextTranslator.apply( ctx ), (EntityPropertyValue<U>) value );
		parent.applyValueFunction( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> saveConsumer( BiConsumer<T, EntityPropertyValue<U>> saveFunction ) {
		BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> wrapper =
				( ctx, value ) -> saveFunction.accept( (T) bindingContextTranslator.apply( ctx ), (EntityPropertyValue<U>) value );
		parent.saveConsumer( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> saveFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> saveFunction ) {
		BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> wrapper =
				( ctx, value ) -> saveFunction.apply( (T) bindingContextTranslator.apply( ctx ), (EntityPropertyValue<U>) value );
		parent.saveFunction( wrapper );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> validator( EntityPropertyValidator propertyValidator ) {
		parent.validator( propertyValidator );
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<T, U> contextualValidator( ContextualValidator<T, U> contextualValidator ) {
		if ( contextualValidator != null ) {
			parent.contextualValidator(
					( ctx, target, errors, validationHints ) -> contextualValidator
							.validate( (T) bindingContextTranslator.apply( ctx ), (U) target, errors, validationHints )
			);
		}
		else {
			parent.validator( null );
		}
		return this;
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
	public <V> ConfigurableEntityPropertyController<EntityPropertyBindingContext, V> withBindingContext( Class<V> propertyType ) {
		return parent.withBindingContext( propertyType );
	}
}
