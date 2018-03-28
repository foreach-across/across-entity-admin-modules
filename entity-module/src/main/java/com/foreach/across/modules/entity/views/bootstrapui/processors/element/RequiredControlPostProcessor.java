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

import com.foreach.across.modules.bootstrapui.elements.FormControlElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

/**
 * Checks if a {@link com.foreach.across.modules.bootstrapui.elements.FormControlElement} should be set as required.
 * If there is an attribute {@link com.foreach.across.modules.entity.EntityAttributes#PROPERTY_REQUIRED} present,
 * that one will be applied. Else the required status will be kept as is.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class RequiredControlPostProcessor<T extends ViewElement> extends AbstractPropertyDescriptorAwarePostProcessor<T, FormControlElement>
{
	@Override
	protected void postProcess( ViewElementBuilderContext builderContext, FormControlElement element, EntityPropertyDescriptor propertyDescriptor ) {
		Boolean required = propertyDescriptor.getAttribute( EntityAttributes.PROPERTY_REQUIRED, Boolean.class );

		if ( required != null ) {
			element.setRequired( Boolean.TRUE.equals( required ) );
		}
	}
}
