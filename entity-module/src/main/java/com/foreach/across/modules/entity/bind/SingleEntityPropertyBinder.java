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
import org.springframework.validation.Errors;

import java.util.Objects;

/**
 * Represents a single value property, always attached to a {@link EntityPropertiesBinder}.
 * The original property value will be loaded as soon as any value-related action is attempted.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public final class SingleEntityPropertyBinder extends AbstractEntityPropertyBinder
{
	private final EntityPropertiesBinder binder;
	private final EntityPropertyDescriptor descriptor;
	private final EntityPropertyController<Object, Object> controller;

	private boolean valueHasBeenSet;

	private EntityPropertiesBinder properties;

	/**
	 * Has {@link #setValue(Object)} been called with a new value.
	 */
	private boolean modified;

	/**
	 * The actual value held for that property.
	 */
	private Object value;

	@SuppressWarnings("unchecked")
	SingleEntityPropertyBinder( EntityPropertiesBinder binder, EntityPropertyDescriptor descriptor ) {
		super( binder, descriptor, descriptor.getController() );
		this.binder = binder;
		this.descriptor = descriptor;
		controller = descriptor.getController();
	}

	@Override
	public boolean isModified() {
		return modified || isDeleted();
	}

	public EntityPropertiesBinder getProperties() {
		if ( properties == null ) {
			loadOriginalValue();

			valueHasBeenSet = true;
			properties = binder.createChildBinder( descriptor, controller, getInitializedValue() );
		}

		return properties;
	}

	@Override
	protected Object fetchOriginalValue() {
		value = super.fetchOriginalValue();
		return value;
	}

	@Override
	protected void setOriginalValue( Object value ) {
		super.setOriginalValue( value );
		this.value = value;
	}

	@Override
	public Object getValue() {
		loadOriginalValue();

		if ( isDeleted() ) {
			return null;
		}

		if ( properties != null ) {
			properties.values().forEach( EntityPropertyBinder::applyValue );
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

	public boolean isDeleted() {
		return super.isDeleted() || ( isBound() && !valueHasBeenSet );
	}

	@Override
	public boolean validate( Errors errors, Object... validationHints ) {
		Runnable validation = () -> {
			// determine if initializedValue or value should be used
			errors.pushNestedPath( "value" );
			controller.validate(
					binder.getBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), value, isDeleted() ), errors, validationHints
			);
			errors.popNestedPath();
		};

		if ( properties != null ) {
			return properties.createController()
			                 .addEntityValidationCallback( validation )
			                 .validateAndBind( errors, validationHints );
		}

		int beforeValidate = errors.getErrorCount();
		validation.run();
		return beforeValidate >= errors.getErrorCount();
	}

	private String binderPath() {
		return "[" + descriptor.getName() + "].value";
	}

	@Override
	public void resetBindStatus() {
		setBound( false );
		valueHasBeenSet = false;
		modified = false;
	}
}
