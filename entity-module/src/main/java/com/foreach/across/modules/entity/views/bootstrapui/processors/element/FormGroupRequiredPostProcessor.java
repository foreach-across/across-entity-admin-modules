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
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

/**
 * Post-processor that will automatically set a {@link FormGroupElement} as required if the (optional) control inside
 * it is required. This will only ever set the required status to {@code true} but will not set it back to
 * {@code false} in case the control is not required.
 * <p/>
 * This processor will simply do nothing when it is being used on an element that is not a {@link FormGroupElement}.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class FormGroupRequiredPostProcessor<T extends ViewElement> implements ViewElementPostProcessor<T>
{
	@Override
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		if ( element instanceof FormGroupElement ) {
			FormGroupElement formGroup = (FormGroupElement) element;
			FormControlElement control = formGroup.getControl( FormControlElement.class );

			if ( control != null && control.isRequired() ) {
				formGroup.setRequired( true );
			}
		}
	}
}
