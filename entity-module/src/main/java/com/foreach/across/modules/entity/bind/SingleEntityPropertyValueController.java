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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.Ordered;
import org.springframework.validation.Errors;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a single value property, always attached to a {@link EntityPropertiesBinder}.
 * The original property value will be loaded as soon as any value-related action is attempted.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class SingleEntityPropertyValueController implements EntityPropertyValueController<Object>
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
	private boolean modified;

	/**
	 * The actual value held for that property.
	 */
	private Object value;

	/**
	 * The original value that was fetched when the property was initialized.
	 * If {@code null} the original value has never been fetched.
	 */
	private Optional<Object> originalValue;

	@Getter
	@Setter
	private int sortIndex;

	@SuppressWarnings("unchecked")
	SingleEntityPropertyValueController( EntityPropertiesBinder binder, EntityPropertyDescriptor descriptor ) {
		this.binder = binder;
		this.descriptor = descriptor;
		controller = descriptor.getController();
	}

	private Object loadOriginalValue() {
		if ( originalValue == null ) {
			value = controller.fetchValue( binder.getBindingContext() );
			originalValue = Optional.ofNullable( value );
		}
		return originalValue.orElse( null );
	}

	@Override
	public boolean isModified() {
		return modified || isDeleted();
	}

	public EntityPropertiesBinder getProperties() {
		if ( properties == null ) {
			loadOriginalValue();

			valueHasBeenSet = true;
			properties = binder.createChildBinder( descriptor, getInitializedValue() );
		}

		return properties;
	}

	@Override
	public Object getValue() {
		loadOriginalValue();

		if ( isDeleted() ) {
			return null;
		}

		if ( properties != null ) {
			properties.values().forEach( EntityPropertyValueController::applyValue );
		}

		return value;
	}

	@Override
	public void setValue( Object value ) {
		loadOriginalValue();

		valueHasBeenSet = true;

		Object newValue = value;

		if ( "".equals( value ) && !String.class.equals( descriptor.getPropertyType() ) ) {
			newValue = null;
		}

		newValue = binder.convertIfNecessary( newValue, descriptor.getPropertyTypeDescriptor(), binderPath() );

		if ( !Objects.equals( this.value, newValue ) ) {
			modified = true;
			this.value = newValue;
			properties = null;
		}
	}

	@Override
	public Object createNewValue() {
		return binder.createValue( controller, descriptor.getPropertyTypeDescriptor() );
	}

	@Override
	public boolean applyValue() {
		if ( controller != null ) {
			return controller.applyValue( binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ) );
		}
		return false;
	}

	@Override
	public boolean save() {
		if ( controller != null ) {
			return controller.save( binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), getValue(), isDeleted() ) );
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

	public boolean isDeleted() {
		return isBound() && !valueHasBeenSet;
	}

	private String binderPath() {
		return "[" + descriptor.getName() + "].value";
	}

	@Override
	public int getControllerOrder() {
		return controller != null ? controller.getOrder() : Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public void resetBindStatus() {
		setBound( false );
		valueHasBeenSet = false;
		modified = false;
	}
}
