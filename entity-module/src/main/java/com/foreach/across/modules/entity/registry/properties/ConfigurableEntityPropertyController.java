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
import org.springframework.validation.Validator;

import java.util.function.*;

/**
 * Builder-like interface for customizing an {@link GenericEntityPropertyController}.
 *
 * @author Arne Vandamme
 * @see GenericEntityPropertyController
 * @see EntityPropertyController
 * @since 3.1.0
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
	 * Set a supplier for creating a new property value using {@link EntityPropertyController#createValue(Object)}.
	 * See {@link #createValueFunction(Function)} if you want to create a new value based on the entity context.
	 *
	 * @param supplier to use for creating a new value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> createValueSupplier( Supplier<U> supplier );

	/**
	 * Set the function that should be used to create a new property value using {@link EntityPropertyController#createValue(Object)}.
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
	ConfigurableEntityPropertyController<T, U> applyValueConsumer( BiConsumer<T, U> valueWriter );

	/**
	 * The function that should be called when setting the property value using {@link EntityPropertyController#applyValue(Object, Object, Object)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param valueWriter function for setting the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> applyValueFunction( BiFunction<T, U, Boolean> valueWriter );

	/**
	 * The consumer that should be called when saving a property value using {@link EntityPropertyController#save(Object, Object)}.
	 * The return value of calling {@link EntityPropertyController#save(Object, Object)} will always be {@code true}
	 * if you specify a {@link BiConsumer}. See {@link #saveFunction(BiFunction)} if you
	 * want to control the return value.
	 *
	 * @param saveFunction consumer for saving the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> saveConsumer( BiConsumer<T, U> saveFunction );

	/**
	 * The function that should be called when saving a property value using {@link EntityPropertyController#save(Object, Object)}.
	 * If the {@link BiFunction} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param saveFunction function for saving the value
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> saveFunction( BiFunction<T, U, Boolean> saveFunction );

	/**
	 * The function that should be called when deleting a property using {@link EntityPropertyController#delete(Object)}.
	 * The return value of calling {@link EntityPropertyController#delete(Object)} will always be {@code true}
	 * if you specify a {@link Consumer}. See {@link #deleteFunction(Function)} if you
	 * want to control the return value.
	 *
	 * @param deleteFunction function for deleting the property
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> deleteConsumer( Consumer<T> deleteFunction );

	/**
	 * The function that should be called when deleting a property using {@link EntityPropertyController#delete(Object)}.
	 * If the {@link Function} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param deleteFunction function for deleting the property
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> deleteFunction( Function<T, Boolean> deleteFunction );

	/**
	 * The function that should be called when checking if a property exists using {@link EntityPropertyController#exists(Object)}.
	 * If the {@link Function} returns {@code null}, this will be converted to {@code false}.
	 *
	 * @param existsFunction function for checking if the property exists
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> existsFunction( Function<T, Boolean> existsFunction );

	/**
	 * Add a contextual validator for this property type and entity combination.
	 *
	 * @param contextualValidator validator to add
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> addValidator( ContextualValidator<T, U> contextualValidator );

	/**
	 * Add a generic validator for this property. Note that {@link Validator#supports(Class)}
	 * will not be called before calling the actual validate method.
	 *
	 * @param validator to add
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> addValidator( Validator validator );

	/**
	 * Add a number of validators for this property. Prefer {@link ContextualValidator} instances.
	 *
	 * @param validators to add
	 * @return self
	 */
	ConfigurableEntityPropertyController<T, U> addValidators( Validator... validators );

	@SuppressWarnings("unchecked")
	default <X, V> ConfigurableEntityPropertyController<X, V> as() {
		return (ConfigurableEntityPropertyController<X, V>) this;
	}

	@SuppressWarnings("unchecked")
	default <X, V> ConfigurableEntityPropertyController<X, V> as( Class<X> entityType, Class<V> propertyType ) {
		return (ConfigurableEntityPropertyController<X, V>) this;
	}

	/**
	 * Return a scoped instance that works directly on the original entity.
	 *
	 * @param entityType   type of the original entity
	 * @param propertyType type of the property
	 * @param <X>          type of the original entity
	 * @param <V>          type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	default <X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<X> entityType, Class<V> propertyType ) {
		return null;
	}

	/**
	 * Return a scoped instance that works directly on the target.
	 *
	 * @param targetType   type of the target for binding
	 * @param propertyType type of the property
	 * @param <X>          type of the target for binding
	 * @param <V>          type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	default <X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<X> targetType, Class<V> propertyType ) {
		return null;
	}

	/**
	 * Return a scoped instance that types the binding context.
	 *
	 * @param entityType   type of the original entity
	 * @param targetType   type of the target for binding
	 * @param propertyType type of the property
	 * @param <X>          type of the original entity
	 * @param <W>          type of the target for binding
	 * @param <V>          type of the property
	 * @return configurable controller
	 * @see ScopedConfigurableEntityPropertyController
	 */
	default <X, W, V> ConfigurableEntityPropertyController<EntityPropertyBindingContext<X, W>, V> withBindingContext( Class<X> entityType,
	                                                                                                                  Class<W> targetType,
	                                                                                                                  Class<V> propertyType ) {
		return null;
	}
}
