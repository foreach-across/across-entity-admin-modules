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

import org.springframework.validation.Errors;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builder-like interface for customizing a {@link GenericEntityPropertyController}.
 *
 * @author Arne Vandamme
 * @see EntityPropertyController
 * @since 3.2.0
 */
public interface ConfigurableEntityPropertyController<T, U>
{
	/**
	 * Set the relative order for executing these controller methods.
	 *
	 * @param order to use
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> order( int order );

	/**
	 * Set the value fetching function that should be used for retrieving this property value from the entity.
	 *
	 * @param valueFetcher to use
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> valueFetcher( Function<T, U> valueFetcher );

	/**
	 * Set a supplier for creating a new property value using {@link EntityPropertyController#createValue(EntityPropertyBindingContext)}.
	 * See {@link #createValueFunction(Function)} if you want to create a new value based on the entity context.
	 *
	 * @param supplier to use for creating a new value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> createValueSupplier( Supplier<U> supplier );

	/**
	 * Set the function that should be used to create a new property value using {@link EntityPropertyController#createValue(EntityPropertyBindingContext)}.
	 *
	 * @param function that returns a new value instance
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> createValueFunction( Function<T, U> function );

	/**
	 * Set the consumer that should be called when applying the property value using {@link EntityPropertyController#applyValue(Object, Object, Object)}.
	 * The return value of calling {@link EntityPropertyController#applyValue(Object, Object, Object)} will always be {@code true}
	 * if you specify a {@link BiConsumer}. See {@link #applyValueFunction(BiFunction)} if you
	 * want to control the return value.
	 *
	 * @param valueWriter consumer for setting the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> applyValueConsumer( BiConsumer<T, EntityPropertyValue<U>> valueWriter );

	/**
	 * The function that should be called when setting the property value using {@link EntityPropertyController#applyValue(Object, Object, Object)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param valueWriter function for setting the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> applyValueFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> valueWriter );

	/**
	 * The consumer that should be called when saving a property value using {@link EntityPropertyController#save(EntityPropertyBindingContext, EntityPropertyValue)}.
	 * The return value of calling {@link EntityPropertyController#save(EntityPropertyBindingContext, EntityPropertyValue)} will always be {@code true}
	 * if you specify a {@link BiConsumer}. See {@link #saveFunction(BiFunction)} if you
	 * want to control the return value.
	 *
	 * @param saveFunction consumer for saving the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> saveConsumer( BiConsumer<T, EntityPropertyValue<U>> saveFunction );

	/**
	 * The function that should be called when saving a property value using {@link EntityPropertyController#save(EntityPropertyBindingContext, EntityPropertyValue)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param saveFunction function for saving the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> saveFunction( BiFunction<T, EntityPropertyValue<U>, Boolean> saveFunction );

	/**
	 * Set the property validator.
	 *
	 * @param propertyValidator to use
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> validator( EntityPropertyValidator propertyValidator );

	/**
	 * Set the contextual validator for this context and target.
	 * A contextual validator can receive a different binding context but will always validate on the new property value.
	 * If you want to access the full {@link EntityPropertyValue}, you should use {@link #validator(EntityPropertyValidator)} instead.
	 *
	 * @param contextualValidator validator to add
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> contextualValidator( ContextualValidator<T, U> contextualValidator );

	/**
	 * Return a scoped instance that works directly on the original entity.
	 * Can be used for type-specific configuration of the original entity and the property.
	 *
	 * @param entityType   type of the original entity
	 * @param propertyType type of the property
	 * @param <X>          type of the original entity
	 * @param <V>          type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	<X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<X> entityType, Class<V> propertyType );

	/**
	 * Return a scoped instance that works directly on the target.
	 * Can be used for type-specific configuration of the target and the property.
	 *
	 * @param targetType   type of the target for binding
	 * @param propertyType type of the property
	 * @param <X>          type of the target for binding
	 * @param <V>          type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	<X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<X> targetType, Class<V> propertyType );

	/**
	 * Return a scoped instance that types the property.
	 *
	 * @param <V>          type of the property
	 * @param propertyType type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	<V> ConfigurableEntityPropertyController<EntityPropertyBindingContext, V> withBindingContext( Class<V> propertyType );

	@FunctionalInterface
	interface ContextualValidator<T, U>
	{
		/**
		 * Validate the supplied {@code target} in the given context.
		 */
		void validate( T context, U target, Errors errors, Object... validationHints );
	}
}
