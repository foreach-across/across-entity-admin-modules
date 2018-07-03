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
package com.foreach.across.modules.entity.views.util;

import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertyValues;
import com.foreach.across.modules.entity.bind.MultiEntityPropertyValue;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.val;

/**
 * Contains utility methods related to view elements and view building in an entity context.
 *
 * @author Arne Vandamme
 */
public class EntityViewElementUtils
{
	protected EntityViewElementUtils() {
	}

	/**
	 * <p>Retrieve the current entity being processed in the builder context.  In case of a
	 * {@link IteratorViewElementBuilderContext} the entity of the iterator will be returned,
	 * in all other cases the attribute {@link EntityViewModel#ENTITY}.</p>
	 * <p>Will return null if no entity can be found.</p>
	 *
	 * @param builderContext curret builder context
	 * @return entity or null if none found
	 */
	public static Object currentEntity( ViewElementBuilderContext builderContext ) {
		return currentEntity( builderContext, Object.class );
	}

	/**
	 * <p>Retrieve the current entity being processed in the builder context.  In case of a
	 * {@link IteratorViewElementBuilderContext} the entity of the iterator will be returned,
	 * in all other cases the attribute {@link EntityViewModel#ENTITY}.</p>
	 * <p>Will return null if no entity can be found or if the entity is not of the expected type.</p>
	 *
	 * @param builderContext current builder context
	 * @return entity or null if none found or not of the expected type
	 */
	public static <U> U currentEntity( ViewElementBuilderContext builderContext, Class<U> expectedType ) {
		if ( builderContext == null ) {
			return null;
		}

		Object value = null;

		if ( builderContext instanceof IteratorViewElementBuilderContext ) {
			value = ( (IteratorViewElementBuilderContext) builderContext ).getItem();
		}
		else {
			value = builderContext.getAttribute( EntityViewModel.ENTITY );
		}

		if ( value instanceof MultiEntityPropertyValue.Item ) {
			value = ( (MultiEntityPropertyValue.Item) value ).getValue();
		}

		return expectedType.isInstance( value ) ? expectedType.cast( value ) : null;
	}

	/**
	 * Retrieve the current property value being rendered.  This assumes that an {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}
	 * attribute is set on the context, and that {@link #currentEntity(ViewElementBuilderContext)} returns a non-null value.
	 * If both conditions are true, the {@link EntityPropertyDescriptor#getValueFetcher()} will be used to retrieve the actual property value of the entity.
	 * <p/>
	 * Null will be returned if either entity or property descriptor are missing.  However, null will also be returned if no value fetcher is configured
	 * or if the actual property value is null.
	 *
	 * @param builderContext current builder context
	 * @return property value or null if unable to determine
	 * @see com.foreach.across.modules.entity.views.helpers.PropertyViewElementBuilderWrapper
	 * @see com.foreach.across.modules.entity.views.EntityViewElementBuilderService
	 */
	public static Object currentPropertyValue( ViewElementBuilderContext builderContext ) {
		return currentPropertyValue( builderContext, Object.class );
	}

	/**
	 * Retrieve the current property value being rendered, if it is of the expected type.
	 * Depending on the type of property, this will fetch the property directly from the entity,
	 * or from the {@link EntityPropertyValues} that
	 * is present on the {@link ViewElementBuilderContext}.
	 *
	 * @param builderContext current builder context
	 * @param expectedType   the property value should have
	 * @param <U>            property value type indicator
	 * @return property value or null if unable to determine or not of the expected type
	 * @see #currentPropertyValue(ViewElementBuilderContext)
	 */
	public static <U> U currentPropertyValue( ViewElementBuilderContext builderContext, Class<U> expectedType ) {
		if ( builderContext == null ) {
			return null;
		}

		EntityPropertyDescriptor descriptor = currentPropertyDescriptor( builderContext );

		if ( descriptor == null ) {
			return null;
		}

		EntityPropertiesBinder properties = builderContext.getAttribute( EntityPropertiesBinder.class );

		Object propertyValue = resolveValue( properties, descriptor, currentEntity( builderContext ) );
		return expectedType.isInstance( propertyValue ) ? expectedType.cast( propertyValue ) : null;
	}

	// todo: more and more to clean up
	public static EntityPropertyBinder<Object> currentPropertyValueHolder( ViewElementBuilderContext builderContext ) {
		if ( builderContext == null ) {
			return null;
		}

		EntityPropertyDescriptor descriptor = currentPropertyDescriptor( builderContext );

		if ( descriptor == null ) {
			return null;
		}

		EntityPropertiesBinder properties = builderContext.getAttribute( EntityPropertiesBinder.class );

		return resolveValueHolder( properties, descriptor );
	}

	private static EntityPropertyBinder<Object> resolveValueHolder( EntityPropertiesBinder properties, EntityPropertyDescriptor descriptor ) {
		if ( properties != null ) {
			return properties.get( descriptor.getName() );
		}

		return null;
	}

	private static Object resolveValue( EntityPropertiesBinder properties, EntityPropertyDescriptor descriptor, Object root ) {
		if ( properties != null && EntityAttributes.handlingType( descriptor ) == EntityPropertyHandlingType.EXTENSION ) {
			val valueHolder = properties.get( descriptor.getName() );
			return valueHolder.getValue();
		}
		else {
			if ( descriptor.isNestedProperty() && !descriptor.getParentDescriptor().getName().endsWith( EntityPropertyRegistry.INDEXER ) ) {
				EntityPropertyDescriptor target = descriptor.getAttribute( EntityAttributes.TARGET_DESCRIPTOR, EntityPropertyDescriptor.class );

				if ( target != null ) {
					Object owner = resolveValue( properties, descriptor.getParentDescriptor(), root );
					return target.getPropertyValue( owner );
				}
			}

			return descriptor.getPropertyValue( root );
		}
	}

	/**
	 * Retrieve the current property descriptor, if there is one. This looks for the {@link EntityPropertyDescriptor} attribute on the context.
	 *
	 * @param builderContext current builder context
	 * @return descriptor or null if not present
	 * @see com.foreach.across.modules.entity.views.helpers.PropertyViewElementBuilderWrapper
	 * @see com.foreach.across.modules.entity.views.EntityViewElementBuilderService
	 */
	public static EntityPropertyDescriptor currentPropertyDescriptor( ViewElementBuilderContext builderContext ) {
		return builderContext.getAttribute( EntityPropertyDescriptor.class );
	}

	/**
	 * Set the current entity to the value specified.
	 */
	public static void setCurrentEntity( ViewElementBuilderContext builderContext, Object value ) {
		builderContext.setAttribute( EntityViewModel.ENTITY, value );
	}
}
