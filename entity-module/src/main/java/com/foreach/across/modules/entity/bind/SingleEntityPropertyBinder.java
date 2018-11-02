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

import com.foreach.across.modules.entity.bind.EntityPropertiesBinder.ChildPropertyPropertiesBinder;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import com.foreach.across.modules.entity.registry.properties.support.EntityPropertyDescriptorUtils;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
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
	private final EntityPropertyController controller;

	private boolean valueHasBeenSet;
	private boolean initializedValuePathWasUsed;

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
		super( binder, descriptor.getController() );
		this.binder = binder;
		this.descriptor = descriptor;
		controller = descriptor.getController();
	}

	@Override
	public Object getInitializedValue() {
		initializedValuePathWasUsed = true;
		return super.getInitializedValue();
	}

	@Override
	public boolean isModified() {
		return modified || isDeleted();
	}

	public EntityPropertiesBinder getProperties() {
		if ( properties == null ) {
			loadOriginalValue();

			valueHasBeenSet = true;

			ChildPropertyPropertiesBinder childBinder = binder.createChildPropertyPropertiesBinder();
			childBinder.setParentProperty( descriptor );
			childBinder.setOwningPropertyBinder( this );
			childBinder.setUseLocalBindingContext( EntityPropertyDescriptorUtils.isMemberProperty( descriptor ) );
			childBinder.setEntity( getOriginalValue() );
			if ( !binder.isReadonly() ) {
				childBinder.setTarget( getInitializedValue() );
			}

			properties = childBinder;
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

		if ( properties != null && isDirty() && !binder.isReadonly() ) {
			properties.createController().applyValues();
		}

		return value;
	}

	@Override
	void setValueInternal( Object value ) {
		loadOriginalValue();

		valueHasBeenSet = true;

		Object newValue = value;

		if ( "".equals( value ) && !String.class.equals( descriptor.getPropertyType() ) ) {
			newValue = null;
		}

		newValue = binder.convertIfNecessary( newValue, descriptor.getPropertyTypeDescriptor(), getBinderPath( "value" ) );

		if ( !Objects.equals( this.value, newValue ) ) {
			modified = true;
		}

		if ( this.value != newValue ) {
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
			try {
				errors.pushNestedPath( initializedValuePathWasUsed ? "initializedValue" : "value" );
				controller.validate(
						binder.getValueBindingContext(), new EntityPropertyValue<>( loadOriginalValue(), value, isDeleted() ), errors, validationHints
				);
			}
			finally {
				errors.popNestedPath();
			}
		};

		if ( properties != null ) {
			return properties.createController()
			                 .addEntityValidationCallback( validation )
			                 .applyValuesAndValidate( errors, validationHints );
		}

		int beforeValidate = errors.getErrorCount();
		validation.run();
		return beforeValidate >= errors.getErrorCount();
	}

	/**
	 * Resolve the {@link EntityPropertyBinder} for the property descriptor.
	 * If the descriptor represents a nested property, intermediate binders might get initialized
	 * in order to correctly resolve the target property.
	 * <p/></p>
	 * This will initialize the child properties binder as if calling {@link #getProperties()}.
	 *
	 * @param descriptor to resolve
	 * @return binder for the property
	 */
	@Override
	public EntityPropertyBinder resolvePropertyBinder( @NonNull EntityPropertyDescriptor descriptor ) {
		if ( StringUtils.equals( descriptor.getName(), this.descriptor.getName() ) ) {
			return this;
		}

		EntityPropertyDescriptor directChildProperty = EntityPropertyDescriptorUtils.findDirectChild( descriptor, this.descriptor );

		if ( directChildProperty != null ) {
			EntityPropertyBinder binder = getProperties().get( directChildProperty.getTargetPropertyName() );
			if ( directChildProperty != descriptor ) {
				return binder.resolvePropertyBinder( descriptor );
			}
			else {
				return binder;
			}
		}

		return null;
	}
}
