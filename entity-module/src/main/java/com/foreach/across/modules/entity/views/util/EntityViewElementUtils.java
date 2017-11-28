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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

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

		EntityPropertyDescriptor descriptor = builderContext.getAttribute( EntityPropertyDescriptor.class );

		if ( descriptor == null ) {
			return null;
		}

		Object propertyValue = descriptor.getPropertyValue( currentEntity( builderContext ) );

		return expectedType.isInstance( propertyValue ) ? expectedType.cast( propertyValue ) : null;
	}

	/**
	 * Set the current entity to the value specified.
	 */
	public static void setCurrentEntity( ViewElementBuilderContext builderContext, Object value ) {
		builderContext.setAttribute( EntityViewModel.ENTITY, value );
	}
}
