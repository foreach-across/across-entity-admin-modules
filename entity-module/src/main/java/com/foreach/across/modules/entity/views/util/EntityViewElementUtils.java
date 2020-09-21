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

import com.foreach.across.core.support.InheritedAttributeValue;
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
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
	 * @param builderContext current builder context
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
		Object propertyValue = resolveCurrentPropertyValue( builderContext );
		return expectedType.isInstance( propertyValue ) ? expectedType.cast( propertyValue ) : null;
	}

	private static Object resolveCurrentPropertyValue( ViewElementBuilderContext builderContext ) {
		EntityPropertyDescriptor descriptor = currentPropertyDescriptor( builderContext );

		InheritedAttributeValue<EntityPropertyValue> attrPropertyValue = builderContext.findAttribute( EntityPropertyValue.class );
		InheritedAttributeValue<EntityPropertyBinder> attrPropertyBinder = builderContext.findAttribute( EntityPropertyBinder.class );

		if ( descriptor == null ) {
			// if only property value specified, use that
			if ( !attrPropertyValue.isEmpty() && attrPropertyBinder.isEmpty() ) {
				return attrPropertyValue.getValue().getNewValue();
			}
			// use the specific property binder if only that is available
			if ( attrPropertyValue.isEmpty() && !attrPropertyBinder.isEmpty() ) {
				return attrPropertyBinder.getValue().getValue();
			}

			if ( attrPropertyValue.isEmpty() && attrPropertyBinder.isEmpty() ) {
				return null;
			}

			return attrPropertyValue.getAncestorLevel() <= attrPropertyBinder.getAncestorLevel()
					? attrPropertyValue.getValue().getNewValue() : attrPropertyBinder.getValue().getValue();
		}

		if ( attrPropertyValue.isLocalAttribute() ) {
			return attrPropertyValue.getValue().getNewValue();
		}

		if ( attrPropertyBinder.isLocalAttribute() ) {
			return Optional.ofNullable( attrPropertyBinder.getValue().resolvePropertyBinder( descriptor ) )
			               .map( EntityPropertyBinder::getValue )
			               .orElse( null );
		}

		int propertyValuePriority = attrPropertyValue.isEmpty() ? Integer.MAX_VALUE : attrPropertyValue.getAncestorLevel();
		int propertyBinderPriority = attrPropertyBinder.isEmpty() ? Integer.MAX_VALUE : attrPropertyBinder.getAncestorLevel();

		PriorityValue<EntityPropertyBindingContext> bindingContext = currentBindingContextValue( builderContext, false );
		int bindingContextPriority = bindingContext.priority;

		if ( descriptor.getController().isOptimizedForBulkValueFetching() && builderContext instanceof IteratorViewElementBuilderContext ) {
			Optional<Object> possibleValue = resolveValueForOptimizedBulkFetching( builderContext, descriptor, bindingContext );
			if ( possibleValue.isPresent() ) {
				return possibleValue.get();
			}
		}

		if ( propertyValuePriority <= propertyBinderPriority && propertyValuePriority <= bindingContextPriority ) {
			return attrPropertyValue.getValue().getNewValue();
		}

		if ( propertyBinderPriority <= bindingContextPriority ) {
			return Optional.ofNullable( attrPropertyBinder.getValue().resolvePropertyBinder( descriptor ) )
			               .map( EntityPropertyBinder::getValue )
			               .orElse( null );
		}

		if ( bindingContext.value != null ) {
			return Optional.ofNullable( bindingContext.value.resolvePropertyValue( descriptor ) ).map( EntityPropertyValue::getNewValue ).orElse( null );
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	private static Optional<Object> resolveValueForOptimizedBulkFetching( ViewElementBuilderContext builderContext,
	                                                                      EntityPropertyDescriptor descriptor,
	                                                                      PriorityValue<EntityPropertyBindingContext> bindingContext ) {
		String bulkContextHolderName = "entityPropertyBindingContext:bulk:" + descriptor.getName();
		if ( !builderContext.hasAttribute( bulkContextHolderName ) ) {
			ViewElementBuilderContext parentContext = ( (IteratorViewElementBuilderContext) builderContext ).getParentContext();
			Object items = builderContext.getAttribute( "items" );
			List<EntityPropertyBindingContext> contexts = Collections.emptyList();
			if ( items instanceof Iterable ) {
				contexts = StreamSupport.stream( ( (Iterable<Object>) items ).spliterator(), false )
				                        .map( EntityPropertyBindingContext::forReading )
				                        .collect( Collectors.toList() );
			}
			parentContext.setAttribute( bulkContextHolderName, descriptor.getController().fetchValues( contexts ) );
		}

		Map<EntityPropertyBindingContext, Object> attribute = builderContext.getAttribute( bulkContextHolderName, Map.class );
		return attribute.entrySet().stream()
		                .filter( e -> Objects.equals( e.getKey(), bindingContext.value ) )
		                .map( Map.Entry::getValue )
		                .findFirst();
	}

	static EntityPropertyBindingContext currentBindingContext( @NonNull ViewElementBuilderContext builderContext ) {
		return currentBindingContextValue( builderContext, true ).value;
	}

	private static PriorityValue<EntityPropertyBindingContext> currentBindingContextValue( ViewElementBuilderContext builderContext, boolean alwaysResolve ) {
		InheritedAttributeValue<EntityPropertyBindingContext> attrBindingContext = builderContext.findAttribute( EntityPropertyBindingContext.class );

		if ( attrBindingContext.isLocalAttribute() ) {
			return new PriorityValue<>( attrBindingContext.getValue(), attrBindingContext.getAncestorLevel() );
		}

		InheritedAttributeValue baseLevel = attrBindingContext;
		InheritedAttributeValue<EntityPropertyBinder> attrPropertyBinder = builderContext.findAttribute( EntityPropertyBinder.class );

		if ( attrPropertyBinder.filter( SingleEntityPropertyBinder.class::isInstance ).isPresent() ) {

			if ( !alwaysResolve ) {
				// don't return the binding context, property binder should be used directly
				return new PriorityValue<>( null, Integer.MAX_VALUE );
			}

			if ( attrPropertyBinder.isLocalAttribute() ) {
				return new PriorityValue<>(
						attrPropertyBinder.map( SingleEntityPropertyBinder.class::cast )
						                  .map( SingleEntityPropertyBinder::getProperties )
						                  .map( EntityPropertiesBinder::asBindingContext )
						                  .orElse( null ),
						attrPropertyBinder.getAncestorLevel()
				);
			}
			else if ( attrPropertyBinder.getAncestorLevel() > baseLevel.getAncestorLevel() ) {
				attrPropertyBinder = null;
			}
			else if ( attrPropertyBinder.getAncestorLevel() < baseLevel.getAncestorLevel() ) {
				attrBindingContext = null;
				baseLevel = attrPropertyBinder;
			}
		}
		else {
			attrPropertyBinder = null;
		}

		InheritedAttributeValue<EntityPropertiesBinder> attrPropertiesBinder = builderContext.findAttribute( EntityPropertiesBinder.class );

		if ( !attrPropertiesBinder.isEmpty() ) {
			if ( attrPropertiesBinder.isLocalAttribute() ) {
				return new PriorityValue<>( attrPropertiesBinder.getValue().asBindingContext(), attrPropertiesBinder.getAncestorLevel() );
			}
			else if ( attrPropertiesBinder.getAncestorLevel() > baseLevel.getAncestorLevel() ) {
				attrPropertiesBinder = null;
			}
			else if ( attrPropertiesBinder.getAncestorLevel() < baseLevel.getAncestorLevel() ) {
				attrBindingContext = null;
				attrPropertyBinder = null;
				baseLevel = attrPropertiesBinder;
			}
		}
		else {
			attrPropertiesBinder = null;
		}

		EntityValue entityValue = resolveEntityValue( builderContext );

		if ( !entityValue.isEmpty() ) {
			if ( entityValue.isLocalAttribute() ) {
				return new PriorityValue<>( EntityPropertyBindingContext.forReading( entityValue.getValue() ), entityValue.ancestorLevel );
			}
			else if ( entityValue.getAncestorLevel() > baseLevel.getAncestorLevel() ) {
				entityValue = null;
			}
			else if ( entityValue.getAncestorLevel() < baseLevel.getAncestorLevel() ) {
				attrBindingContext = null;
				attrPropertyBinder = null;
				attrPropertiesBinder = null;
			}
		}
		else {
			entityValue = null;
		}

		if ( attrBindingContext != null ) {
			return new PriorityValue<>( attrBindingContext.getValue(), attrBindingContext.getAncestorLevel() );
		}

		if ( attrPropertyBinder != null ) {
			return new PriorityValue<>(
					attrPropertyBinder.map( SingleEntityPropertyBinder.class::cast )
					                  .map( SingleEntityPropertyBinder::getProperties )
					                  .map( EntityPropertiesBinder::asBindingContext )
					                  .orElse( null ),
					attrPropertyBinder.getAncestorLevel()
			);
		}

		if ( attrPropertiesBinder != null ) {
			return new PriorityValue<>( attrPropertiesBinder.getValue().asBindingContext(), attrPropertiesBinder.getAncestorLevel() );
		}

		if ( entityValue != null ) {
			return new PriorityValue<>( EntityPropertyBindingContext.forReading( entityValue.getValue() ), entityValue.getAncestorLevel() );
		}

		return new PriorityValue<>( null, Integer.MAX_VALUE );
	}

	@RequiredArgsConstructor
	private static class PriorityValue<T>
	{
		private final T value;
		private final int priority;
	}

	private static EntityValue resolveEntityValue( ViewElementBuilderContext builderContext ) {
		if ( builderContext instanceof IteratorViewElementBuilderContext ) {
			return new EntityValue( ( (IteratorViewElementBuilderContext) builderContext ).getItem(), 0 );
		}

		InheritedAttributeValue<Object> attribute = builderContext.findAttribute( EntityViewModel.ENTITY );
		return new EntityValue( attribute.getValue(), attribute.getAncestorLevel() );
	}

	@RequiredArgsConstructor
	private static class EntityValue
	{
		@Getter
		private final Object value;

		@Getter
		private final int ancestorLevel;

		boolean isLocalAttribute() {
			return ancestorLevel == 0;
		}

		boolean isEmpty() {
			return value == null;
		}
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
		InheritedAttributeValue<EntityPropertyBinder> attrPropertyBinder = builderContext.findAttribute( EntityPropertyBinder.class );
		InheritedAttributeValue<EntityPropertiesBinder> attrPropertiesBinder = builderContext.findAttribute( EntityPropertiesBinder.class );

		if ( !attrPropertyBinder.isEmpty() && attrPropertyBinder.getAncestorLevel() <= attrPropertiesBinder.getAncestorLevel() ) {
			return attrPropertyBinder.getValue().resolvePropertyBinder( descriptor );
		}

		return attrPropertiesBinder.map( binder -> binder.get( descriptor ) ).orElse( null );
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
