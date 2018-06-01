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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.Ordered;
import org.springframework.validation.Errors;

import java.util.Objects;

/**
 * Represents a single value property, always attached to a {@link EntityPropertiesBinder}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class SingleEntityPropertyValue implements EntityPropertyValueController<Object>
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor descriptor;
	private final EntityPropertyController<Object, Object> controller;

	private boolean valueHasBeenSet;

	private EntityPropertiesBinder properties;

	@Getter
	@Setter
	private boolean bound;

	/**
	 * Has {@link #setValue(Object)} been called with a new value.
	 */
	@Getter
	private boolean modified;

	/**
	 * The actual value held for that property.
	 */
	private Object value;

	/**
	 * Sort index value, only relevant when the property value is part of a (sorted) collection.
	 */
	@Getter
	@Setter
	private int sortIndex;

	@SuppressWarnings("unchecked")
	SingleEntityPropertyValue( EntityPropertiesBinder binder, EntityPropertyDescriptor descriptor ) {
		this.binder = binder;
		this.descriptor = descriptor;
		controller = descriptor.getController();

		if ( controller != null ) {
			value = controller.fetchValue( binder.getEntity() );
		}
	}

	public EntityPropertiesBinder getProperties() {
		if ( properties == null ) {
			properties = binder.createChildBinder( descriptor, getInitializedValue() );
		}

		return properties;
	}

	@Override
	public Object getValue() {
		if ( isDeleted() ) {
			return null;
		}

		if ( properties != null ) {
			properties.values().forEach( EntityPropertyValueController::applyValue );
		}

		return value;
	}

	/**
	 * Set the value for this property, can be {@code null}.
	 * The source value will be converted to the expected type defined by the descriptor.
	 *
	 * @param value to set
	 */
	@Override
	public void setValue( Object value ) {
		valueHasBeenSet = true;

		Object newValue = value;

		if ( "".equals( value ) && !String.class.equals( descriptor.getPropertyType() )) {
			newValue = null;
		}

		newValue = binder.convertIfNecessary( newValue, descriptor.getPropertyTypeDescriptor(), binderPath() );
		if ( !Objects.equals( this.value, newValue ) ) {
			modified = true;
		}
		this.value = newValue;
		properties = null;
	}

	@Override
	public Object initializeValue() {
		return binder.createValue( controller, binder.getEntity(), descriptor.getPropertyTypeDescriptor() );
	}

	/**
	 * Apply the property value to the target entity, can only be done if there is a controller.
	 */
	@Override
	public boolean applyValue() {
		// modified? mark as applied.
		if ( controller != null ) {
			return controller.applyValue( binder.getEntity(), getValue() );
		}
		return false;
	}

	@Override
	public boolean save() {
		if ( controller != null ) {
			return controller.save( binder.getEntity(), getValue() );
		}
		return false;
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		int beforeValidate = errors.getErrorCount();
		errors.pushNestedPath( "value" );
		if ( controller != null ) {
			controller.validate( binder.getEntity(), value, errors, validationHints );
		}
		errors.popNestedPath();
		return beforeValidate >= errors.getErrorCount();
	}

	private boolean isDeleted() {
		return isBound() && !valueHasBeenSet;
	}

	private String binderPath() {
		return "[" + descriptor.getName() + "].value";
	}

	@Override
	public int getControllerOrder() {
		return controller != null ? controller.getOrder() : Ordered.LOWEST_PRECEDENCE;
	}

	public void resetBindStatus() {
		setBound( false );
		valueHasBeenSet = false;
	}
}
