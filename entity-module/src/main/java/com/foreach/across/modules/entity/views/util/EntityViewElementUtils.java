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

import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyBinder;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.bind.SingleEntityPropertyBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains utility methods related to view elements and view building in an entity context.
 *
 * @author Arne Vandamme
 */
@UtilityClass
public class EntityViewElementUtils
{
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
	 * Create a {@link ViewElementPostProcessor} that generates the {@link EntityPropertyControlName} for the given property descriptor
	 * and sets it using {@link FormInputElement#setControlName(String)} on the generated control. This will update all {@link FormInputElement}
	 * elements where the current control name is the same as {@link EntityPropertyDescriptor#getName()}.
	 * <p/>
	 * Any container will be searched for {@code FormInputElement} children that might get updated as well.
	 *
	 * @param descriptor property descriptor
	 * @param <T>        form control element type
	 * @return post processor
	 */
	public static <T extends ViewElement> ViewElementPostProcessor<T> controlNamePostProcessor( @NonNull EntityPropertyDescriptor descriptor ) {
		return ( builderContext, element ) -> {
			String controlName = controlName( descriptor, builderContext ).toString();

			if ( element instanceof FormInputElement ) {
				FormInputElement input = (FormInputElement) element;
				if ( StringUtils.equals( descriptor.getName(), input.getControlName() ) ) {
					input.setControlName( controlName );
				}
			}

			if ( element instanceof ContainerViewElement ) {
				( (ContainerViewElement) element )
						.findAll( FormInputElement.class )
						.filter( input -> StringUtils.equals( descriptor.getName(), input.getControlName() ) )
						.forEach( input -> input.setControlName( controlName ) );
			}
		};
	}

	/**
	 * Generate the right {@link EntityPropertyControlName} for the property represented by the descriptor. Inspect the {@link ViewElementBuilderContext}
	 * and use a parent {@link EntityPropertyControlName} that might be set.
	 * <p/>
	 * The control name returned will be scoped to the {@link EntityPropertyHandlingType} resolved for the descriptor.
	 *
	 * @param descriptor     for the property
	 * @param builderContext that might contain a parent {@link EntityPropertyControlName}
	 * @return control name
	 * @see EntityPropertyControlName
	 */
	public static EntityPropertyControlName controlName( @NonNull EntityPropertyDescriptor descriptor, @NonNull ViewElementBuilderContext builderContext ) {
		EntityPropertyHandlingType handlingType = EntityPropertyHandlingType.forProperty( descriptor );
		EntityPropertyControlName.ForProperty controlName = EntityPropertyControlName.forProperty( descriptor, builderContext );

		return controlName.forHandlingType( handlingType );
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
	public static <U> U currentEntity( @NonNull ViewElementBuilderContext builderContext, @NonNull Class<U> expectedType ) {
		if ( builderContext == null ) {
			return null;
		}

		Object value;

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
	public static Object currentPropertyValue( @NonNull ViewElementBuilderContext builderContext ) {
		return currentPropertyValue( builderContext, Object.class );
	}

	/**
	 * Retrieve the current property value being rendered, if it is of the expected type.
	 * Depending on the type of property, this will fetch the property directly from the entity,
	 * or from the {@link EntityPropertiesBinder} that is present on the {@link ViewElementBuilderContext}.
	 *
	 * @param builderContext current builder context
	 * @param expectedType   the property value should have
	 * @param <U>            property value type indicator
	 * @return property value or null if unable to determine or not of the expected type
	 * @see #currentPropertyValue(ViewElementBuilderContext)
	 */
	public static <U> U currentPropertyValue( @NonNull ViewElementBuilderContext builderContext, @NonNull Class<U> expectedType ) {
		Object propertyValue = null;
		EntityPropertyValue fixedValue = builderContext.getAttribute( EntityPropertyValue.class );

		if ( fixedValue != null ) {
			propertyValue = fixedValue.getNewValue();
		}
		else {
			EntityPropertyBinder propertyBinder = currentPropertyBinder( builderContext );

			if ( propertyBinder != null ) {
				propertyValue = propertyBinder.getValue();
			}
			else {
				EntityPropertyDescriptor descriptor = currentPropertyDescriptor( builderContext );

				if ( descriptor == null ) {
					return null;
				}

				EntityPropertyBindingContext bindingContext = resolveBindingContext( builderContext );
				fixedValue = bindingContext.resolvePropertyValue( currentPropertyDescriptor( builderContext ) );

				if ( fixedValue != null ) {
					propertyValue = fixedValue.getNewValue();
				}
			}
		}

		return expectedType.isInstance( propertyValue ) ? expectedType.cast( propertyValue ) : null;
	}

	private static EntityPropertyBindingContext resolveBindingContext( ViewElementBuilderContext builderContext ) {
		EntityPropertyBindingContext bindingContext = builderContext.getAttribute( EntityPropertyBindingContext.class );

		if ( bindingContext != null ) {
			return bindingContext;
		}

		EntityPropertyBinder parent = builderContext.getAttribute( EntityPropertyBinder.class );

		if ( parent instanceof SingleEntityPropertyBinder ) {
			return ( (SingleEntityPropertyBinder) parent ).getProperties().asBindingContext();
		}

		return builderContext.hasAttribute( EntityPropertiesBinder.class )
				? builderContext.getAttribute( EntityPropertiesBinder.class ).asBindingContext()
				: EntityPropertyBindingContext.forReading( currentEntity( builderContext ) );
	}

	/**
	 * Retrieve a {@link EntityPropertyBinder} for the current property being rendered.
	 * This requires an {@link EntityPropertiesBinder} or a {@link EntityPropertyBinder} for the parent property
	 * to be present.
	 *
	 * @param builderContext current builder context
	 * @return binder for the property
	 */
	public static EntityPropertyBinder currentPropertyBinder( @NonNull ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor descriptor = currentPropertyDescriptor( builderContext );

		if ( descriptor == null ) {
			return null;
		}

		return resolvePropertyBinder( builderContext, descriptor );
	}

	private static EntityPropertyBinder resolvePropertyBinder( ViewElementBuilderContext builderContext, EntityPropertyDescriptor descriptor ) {
		EntityPropertyBinder parent = builderContext.getAttribute( EntityPropertyBinder.class );

		if ( parent != null ) {
			return parent.resolvePropertyBinder( descriptor );
		}

		EntityPropertiesBinder properties = builderContext.getAttribute( EntityPropertiesBinder.class );

		if ( properties != null ) {
			return properties.get( descriptor );
		}

		return null;
	}

	/**
	 * Retrieve the current property descriptor, if there is one. This looks for the {@link EntityPropertyDescriptor} attribute on the context.
	 *
	 * @param builderContext current builder context
	 * @return descriptor or null if not present
	 * @see com.foreach.across.modules.entity.views.helpers.PropertyViewElementBuilderWrapper
	 * @see com.foreach.across.modules.entity.views.EntityViewElementBuilderService
	 */
	public static EntityPropertyDescriptor currentPropertyDescriptor( @NonNull ViewElementBuilderContext builderContext ) {
		return builderContext.getAttribute( EntityPropertyDescriptor.class );
	}

	/**
	 * Set the current entity to the value specified.
	 */
	public static void setCurrentEntity( @NonNull ViewElementBuilderContext builderContext, Object value ) {
		builderContext.setAttribute( EntityViewModel.ENTITY, value );
	}

	/**
	 * Set a fixed {@link EntityPropertyValue} on the builder context. Care should be taken when using this approach
	 * as a property value set this way will take precedence over all other mechanisms. If a parent context contains
	 * a fixed property value, it will be used by all child contexts.
	 * <p/>
	 * Especially useful for test scenarios.
	 *
	 * @param builderContext on which to set the property value
	 * @param value          for the property
	 */
	public static void setCurrentPropertyValue( @NonNull ViewElementBuilderContext builderContext, Object value ) {
		builderContext.setAttribute( EntityPropertyValue.class, EntityPropertyValue.of( value ) );
	}
}
