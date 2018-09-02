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
import lombok.RequiredArgsConstructor;
import org.springframework.validation.Validator;

import java.util.function.*;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
@RequiredArgsConstructor
public class DefaultEntityPropertyController implements EntityPropertyController, ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object>
{
	private final EntityPropertyController<EntityPropertyBindingContext, Object> parent;

	/**
	 * Processing order for this controller.
	 */
	@Getter
	private int order = AFTER_ENTITY;

	@Getter
	private Function<EntityPropertyBindingContext, Object> valueFetcher;

	@Getter
	private Function<EntityPropertyBindingContext, Object> createValueFunction;

	public DefaultEntityPropertyController() {
		this.parent = null;
	}

	@Override
	public DefaultEntityPropertyController order( int order ) {
		this.order = order;
		return this;
	}

	@Override
	public DefaultEntityPropertyController valueFetcher( Function<EntityPropertyBindingContext, Object> valueFetcher ) {
		this.valueFetcher = valueFetcher;
		return this;
	}

	@Override
	public DefaultEntityPropertyController createValueSupplier( Supplier<Object> supplier ) {
		return createValueFunction( e -> supplier.get() );
	}

	@Override
	public DefaultEntityPropertyController createValueFunction( Function<EntityPropertyBindingContext, Object> function ) {
		this.createValueFunction = function;
		return this;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueConsumer( BiConsumer<EntityPropertyBindingContext, Object> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueFunction( BiFunction<EntityPropertyBindingContext, Object, Boolean> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveConsumer( BiConsumer<EntityPropertyBindingContext, Object> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveFunction( BiFunction<EntityPropertyBindingContext, Object, Boolean> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> deleteConsumer( Consumer<EntityPropertyBindingContext> deleteFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> deleteFunction( Function<EntityPropertyBindingContext, Boolean> deleteFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> existsFunction( Function<EntityPropertyBindingContext, Boolean> existsFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> addValidator( ContextualValidator<EntityPropertyBindingContext, Object> contextualValidator ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> addValidator( Validator validator ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> addValidators( Validator... validators ) {
		return null;
	}

	@Override
	public Object fetchValue( EntityPropertyBindingContext context ) {
		if ( valueFetcher != null ) {
			return valueFetcher.apply( context );
		}
		return parent != null ? parent.fetchValue( context ) : null;
	}

	@Override
	public Object createValue( EntityPropertyBindingContext context ) {
		if ( createValueFunction != null ) {
			return createValueFunction.apply( context );
		}
		return null;
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
	public <X, W, V> ConfigurableEntityPropertyController<EntityPropertyBindingContext<X, W>, V> withBindingContext( Class<X> entityType,
	                                                                                                                 Class<W> targetType,
	                                                                                                                 Class<V> propertyType ) {
		return new ScopedConfigurableEntityPropertyController<>( this, ctx -> ctx );
	}
}
