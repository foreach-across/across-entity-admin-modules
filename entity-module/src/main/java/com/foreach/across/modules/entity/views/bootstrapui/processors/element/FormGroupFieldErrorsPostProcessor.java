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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

/**
 * Adds additional field errors to show based on the property descriptor.
 * If the property is a native property, this will attempt to register the field errors
 * that might be attached to the native property in case of non-direct property handling.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
public class FormGroupFieldErrorsPostProcessor<T extends ViewElement> extends AbstractPropertyDescriptorAwarePostProcessor<T, FormGroupElement>
{
	@Override
	protected void postProcess( ViewElementBuilderContext builderContext, FormGroupElement element, EntityPropertyDescriptor descriptor ) {
		if ( element.getFieldErrorsToShow().length == 0 ) {
			EntityPropertyHandlingType handlingType = EntityPropertyHandlingType.forProperty( descriptor );

			if ( handlingType != EntityPropertyHandlingType.DIRECT && descriptor.hasAttribute( EntityAttributes.NATIVE_PROPERTY_DESCRIPTOR ) ) {
				EntityPropertyControlName.ForProperty controlName = EntityPropertyControlName.forProperty( descriptor, builderContext );
				element.setFieldErrorsToShow( controlName.forHandlingType( EntityPropertyHandlingType.DIRECT ).toString() );
			}
		}
	}
}
