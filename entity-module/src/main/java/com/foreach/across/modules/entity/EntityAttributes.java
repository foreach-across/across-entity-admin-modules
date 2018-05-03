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

package com.foreach.across.modules.entity;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.processors.support.EntityPropertiesBinder;
import com.foreach.across.modules.entity.views.processors.support.EntityPropertyValueHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Contains common {@link com.foreach.across.modules.entity.registry.EntityConfiguration}, {@link com.foreach.across.modules.entity.registry.EntityAssociation}
 * and {@link EntityPropertyDescriptor} attribute keys.
 *
 * @see com.foreach.across.modules.entity.views.EntityViewFactoryAttributes
 */
public interface EntityAttributes
{
	/**
	 * If set, holds the name of the {@link PlatformTransactionManager} bean that the repositories of this entity use.
	 */
	String TRANSACTION_MANAGER_NAME = PlatformTransactionManager.class.getName() + ".name";

	/**
	 * If set, contains the control name that should be used for form elements.
	 */
	String CONTROL_NAME = EntityPropertyDescriptor.class.getName() + ".controlName";

	/**
	 * If set, indicates that the property descriptor represents a native property on a class.
	 * The class is usually the entity this property belongs to.
	 * <p/>
	 * The actual value can differ, for example {@link org.springframework.core.convert.Property} or {@link java.beans.PropertyDescriptor}.
	 */
	String NATIVE_PROPERTY_DESCRIPTOR = EntityPropertyDescriptor.class.getName() + ".nativeProperty";

	/**
	 * If set, determines if a control for this property should be marked as required or not.
	 * The actual value should be either {@code true} or {@code false}.
	 */
	String PROPERTY_REQUIRED = EntityPropertyDescriptor.class.getName() + ".required";

	/**
	 * If set, this attribute should contain the
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertySelector} to be used for selecting
	 * the members of a {@link com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement}.
	 */
	String FIELDSET_PROPERTY_SELECTOR = "com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement.EntityPropertySelector";

	/**
	 * If set, contains the EQL statement or {@link com.foreach.across.modules.entity.query.EntityQuery} that should be used
	 * to fetch the selectable options.  Can be used to for example filter out deleted items.
	 * <p/>
	 * Will only be used if there is no {@link com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator}
	 * or {@link OptionIterableBuilder} attribute set.
	 */
	String OPTIONS_ENTITY_QUERY = OptionIterableBuilder.class.getName() + ".EntityQuery";

	/**
	 * If set, should contain a collection of the allowed values.  Usually used in combination with an enum option,
	 * in which case the value is expected to be an {@link java.util.EnumSet}.
	 */
	String OPTIONS_ALLOWED_VALUES = OptionIterableBuilder.class.getName() + ".AllowedValues";

	/**
	 * If set, should contain an {@link com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder} consumer.
	 * Used to apply changes to an {@link com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder}.
	 */
	String OPTIONS_ENHANCER = OptionGenerator.class.getName() + ".enhancer";

	/**
	 * Retrieve the control name to use for a {@link EntityPropertyDescriptor}.
	 * Which control name gets generated depends on the value of {@link #handlingType(EntityPropertyDescriptor)}.
	 * An extension property will return a path to a {@link EntityPropertyValueHolder} on the {@link EntityPropertiesBinder}.
	 * A direct or manual property will generate a control name based on the property name or attribute.
	 * If an attribute {@link #CONTROL_NAME} is present, it will be used, else the regular name will be used.
	 *
	 * @param descriptor of the property
	 * @return control name to use
	 */
	static String controlName( EntityPropertyDescriptor descriptor ) {
		if ( handlingType( descriptor ) == EntityPropertyHandlingType.EXTENSION && descriptor.getName() != null ) {
			return "properties[" + descriptor.getName() + "].value";
		}
		return StringUtils.defaultString( descriptor.getAttribute( CONTROL_NAME, String.class ), descriptor.getName() );
	}

	/**
	 * Determine the {@link EntityPropertyHandlingType} for a {@link EntityPropertyDescriptor}.
	 * If no attribute of the former type is present, a handling type will be resolved.
	 *
	 * @param descriptor of the property
	 * @return handling type - never {@code null}
	 */
	static EntityPropertyHandlingType handlingType( EntityPropertyDescriptor descriptor ) {
		return EntityPropertyHandlingType.forProperty( descriptor );
	}

	/**
	 * Check if the descriptor has the {@link #PROPERTY_REQUIRED} attribute with a {@code true} value,
	 * marking the control as required.
	 *
	 * @param descriptor of the property
	 * @return true if it is required
	 */
	static boolean isRequired( EntityPropertyDescriptor descriptor ) {
		return Boolean.TRUE.equals( descriptor.getAttribute( PROPERTY_REQUIRED, Boolean.class ) );
	}
}
