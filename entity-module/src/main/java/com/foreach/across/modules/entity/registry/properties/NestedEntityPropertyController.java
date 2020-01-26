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

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * {@link EntityPropertyController} implementation for a nested property, the property value
 * of the parent {@link EntityPropertyDescriptor} actually supplies the binding context of
 * the child controller.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class NestedEntityPropertyController implements EntityPropertyController, ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object>
{
	private final EntityPropertyDescriptor parentPropertyDescriptor;
	private final GenericEntityPropertyController child;

	public NestedEntityPropertyController( @NonNull EntityPropertyDescriptor parentPropertyDescriptor, @NonNull EntityPropertyController child ) {
		this.parentPropertyDescriptor = parentPropertyDescriptor;
		this.child = new GenericEntityPropertyController( child );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> order( int order ) {
		return child.order( order );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> valueFetcher( Function<EntityPropertyBindingContext, Object> valueFetcher ) {
		return child.createValueFunction( valueFetcher );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> bulkValueFetcher( Function<Collection<EntityPropertyBindingContext>, Map<EntityPropertyBindingContext, Object>> valueFetcher ) {
		return child.bulkValueFetcher( valueFetcher );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> optimizedForBulkValueFetching( boolean enabled ) {
		return child.optimizedForBulkValueFetching( enabled );
	}

	@Override
	public boolean isOptimizedForBulkValueFetching() {
		return child.isOptimizedForBulkValueFetching() || parentPropertyDescriptor.getController().isOptimizedForBulkValueFetching();
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createValueSupplier( Supplier<Object> supplier ) {
		return child.createValueSupplier( supplier );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createValueFunction( Function<EntityPropertyBindingContext, Object> function ) {
		return child.createValueFunction( function );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createDtoFunction( Function<Object, Object> function ) {
		return child.createDtoFunction( function );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createDtoFunction( BiFunction<EntityPropertyBindingContext, Object, Object> function ) {
		return child.createDtoFunction( function );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> valueWriter ) {
		return child.applyValueConsumer( valueWriter );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> valueWriter ) {
		return child.applyValueFunction( valueWriter );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> saveFunction ) {
		return child.saveConsumer( saveFunction );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> saveFunction ) {
		return child.saveFunction( saveFunction );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> validator( EntityPropertyValidator propertyValidator ) {
		return child.validator( propertyValidator );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> contextualValidator( ContextualValidator<EntityPropertyBindingContext, Object> contextualValidator ) {
		return child.contextualValidator( contextualValidator );
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withEntity( Class<? super X> entityType, Class<? super V> propertyType ) {
		return child.withEntity( entityType, propertyType );
	}

	@Override
	public <X, V> ConfigurableEntityPropertyController<X, V> withTarget( Class<? super X> targetType, Class<? super V> propertyType ) {
		return child.withTarget( targetType, propertyType );
	}

	@Override
	public <V> ConfigurableEntityPropertyController<EntityPropertyBindingContext, V> withBindingContext( Class<? super V> propertyType ) {
		return child.withBindingContext( propertyType );
	}

	@Override
	public Object fetchValue( EntityPropertyBindingContext context ) {
		return child.fetchValue( childContext( context ) );
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<EntityPropertyBindingContext, Object> fetchValues( Collection collection ) {
		Map<EntityPropertyBindingContext, EntityPropertyBindingContext> input
				= mapChildContextToOriginal( (Collection<EntityPropertyBindingContext>) collection );
		Map<EntityPropertyBindingContext, Object> mappedValues = child.fetchValues( input.keySet() );

		IdentityHashMap<EntityPropertyBindingContext, Object> output = new IdentityHashMap<>( mappedValues.size() );
		mappedValues.forEach( ( mappedKey, value ) -> output.put( input.get( mappedKey ), value ) );
		return output;
	}

	@Override
	public Object createValue( EntityPropertyBindingContext context ) {
		return child.createValue( childContext( context ) );
	}

	@Override
	public Object createDto( EntityPropertyBindingContext context, Object value ) {
		return child.createDto( childContext( context ), value );
	}

	@Override
	public void validate( EntityPropertyBindingContext context, EntityPropertyValue propertyValue, Errors errors, Object... validationHints ) {
		child.validate( childContext( context ), propertyValue, errors, validationHints );
	}

	@Override
	public boolean applyValue( EntityPropertyBindingContext context, EntityPropertyValue propertyValue ) {
		return child.applyValue( childContext( context ), propertyValue );
	}

	@Override
	public boolean save( EntityPropertyBindingContext context, EntityPropertyValue propertyValue ) {
		return child.save( childContext( context ), propertyValue );
	}

	@Override
	public int getOrder() {
		return child.getOrder();
	}

	private EntityPropertyBindingContext childContext( EntityPropertyBindingContext context ) {
		return EntityPropertyBindingContextResolver.resolvePropertyBindingContext( context, parentPropertyDescriptor );
	}

	private Map<EntityPropertyBindingContext, EntityPropertyBindingContext> mapChildContextToOriginal( Collection<EntityPropertyBindingContext> original ) {
		return EntityPropertyBindingContextResolver.resolvePropertyBindingContext( original, parentPropertyDescriptor );
	}
}
