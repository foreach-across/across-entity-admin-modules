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
import org.springframework.validation.Validator;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class NestedEntityPropertyController implements EntityPropertyController, ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object>
{
	private final String contextName;
	private final EntityPropertyController parent;
	private final EntityPropertyController child;

	public NestedEntityPropertyController( @NonNull String contextName, @NonNull EntityPropertyController parent, @NonNull EntityPropertyController child ) {
		this.contextName = contextName;
		this.parent = parent;
		this.child = new DefaultEntityPropertyController( child );
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> order( int order ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> valueFetcher( Function<EntityPropertyBindingContext, Object> valueFetcher ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createValueSupplier( Supplier<Object> supplier ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> createValueFunction( Function<EntityPropertyBindingContext, Object> function ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> applyValueFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> valueWriter ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveConsumer( BiConsumer<EntityPropertyBindingContext, EntityPropertyValue<Object>> saveFunction ) {
		return null;
	}

	@Override
	public ConfigurableEntityPropertyController<EntityPropertyBindingContext, Object> saveFunction( BiFunction<EntityPropertyBindingContext, EntityPropertyValue<Object>, Boolean> saveFunction ) {
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

	@Override
	public Object fetchValue( EntityPropertyBindingContext context ) {
		EntityPropertyBindingContext<Object, Object> childContext = context.getAttribute( NestedEntityPropertyController.class.getName() + "." + contextName,
		                                                                                  EntityPropertyBindingContext.class );

		if ( childContext == null ) {
			Object parentValue = parent.fetchValue( context );

			childContext = EntityPropertyBindingContext.of( parentValue ).withParent( context );
			context.setAttribute( NestedEntityPropertyController.class.getName() + "." + contextName, childContext );
		}

		return child.fetchValue( childContext );
	}

	@Override
	public Object createValue( EntityPropertyBindingContext context ) {
		return null;
	}

	@Override
	public boolean applyValue( EntityPropertyBindingContext context, EntityPropertyValue propertyValue ) {
		return false;
	}

	@Override
	public boolean save( EntityPropertyBindingContext context, EntityPropertyValue propertyValue ) {
		return false;
	}

	@Override
	public int getOrder() {
		return 0;
	}
}
