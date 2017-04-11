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

import com.foreach.across.modules.bootstrapui.elements.FieldsetFormElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains common Entity attribute keys.
 */
public interface EntityAttributes
{
	/**
	 * If set, contains the control name that should be used for form elements.
	 */
	String CONTROL_NAME = EntityPropertyDescriptor.class + ".controlName";

	/**
	 * If set, this attribute should contain the
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertySelector} to be used for selecting
	 * the members of a {@link FieldsetFormElement}.
	 */
	String FIELDSET_PROPERTY_SELECTOR = FieldsetFormElement.class.getName() + ".EntityPropertySelector";

	/**
	 * If set, contains the EQL statement or {@link com.foreach.across.modules.entity.query.EntityQuery} that should be used
	 * to fetch the selectable options.  Can be used to for example filter out deleted items.
	 * <p/>
	 * Will only be used if there is no {@link com.foreach.across.modules.entity.views.bootstrapui.options.OptionGenerator}
	 * or {@link OptionIterableBuilder} attribute set.
	 */
	String OPTIONS_ENTITY_QUERY = OptionIterableBuilder.class.getName() + ".EntityQuery";

	/**
	 * Retrieve the control name to use for a {@link EntityPropertyDescriptor}.
	 * If an attribute {@link #CONTROL_NAME} is present, it will be used, else the regular name will be used.
	 *
	 * @param descriptor of the property
	 * @return control name to use
	 */
	static String controlName( EntityPropertyDescriptor descriptor ) {
		return StringUtils.defaultString( descriptor.getAttribute( CONTROL_NAME, String.class ), descriptor.getName() );
	}
}
