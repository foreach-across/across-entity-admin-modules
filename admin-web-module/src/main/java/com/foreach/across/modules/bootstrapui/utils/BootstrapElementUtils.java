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
package com.foreach.across.modules.bootstrapui.utils;

import com.foreach.across.modules.bootstrapui.elements.FormControlElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementCollection;

/**
 * @author Arne Vandamme
 */
public final class BootstrapElementUtils
{
	private BootstrapElementUtils() {
	}

	/**
	 * Will get the first {@link FormControlElement} instance.  If the {@link ViewElement} is
	 * a {@link FormControlElement} it will be returned.  If it is a
	 * {@link com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement} the input group control will
	 * be returned instead if it matches.  If the {@link ViewElement} is a
	 * {@link com.foreach.across.modules.web.ui.ViewElementCollection} then a recursive search will happen
	 * until the first element extending {@link FormControlElement} is returned.
	 *
	 * @param viewElement instance
	 * @return form control instance, or {@code null} if not found
	 */
	public static FormControlElement getFormControl( ViewElement viewElement ) {
		return getFormControl( viewElement, FormControlElement.class );
	}

	/**
	 * Will get the first {@link FormControlElement} instance matching the expected type.
	 * If the {@link ViewElement} is of the expected type it will be returned.  If it isIf it is a
	 * {@link com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement} the input group control will
	 * be returned instead if it matches.  If the {@link ViewElement} is a
	 * {@link com.foreach.across.modules.web.ui.ViewElementCollection} then a recursive search will happen
	 * until the first element matching the type is returned.
	 *
	 * @param viewElement  instance
	 * @param expectedType the control should have
	 * @return form control instance, or {@code null} if not found
	 */
	public static <V extends FormControlElement> V getFormControl( ViewElement viewElement,
	                                                               Class<V> expectedType ) {
		ViewElement target = viewElement;

		if ( expectedType.isInstance( viewElement ) ) {
			return (V) viewElement;
		}

		V control = null;

		if ( target instanceof FormGroupElement ) {
			ViewElement candidate = ( (FormGroupElement) target ).getControl();
			if ( candidate != null ) {
				if ( expectedType.isInstance( candidate ) ) {
					control = (V) candidate;
				}
				else {
					target = candidate;
				}
			}
		}

		if ( control == null && target instanceof InputGroupFormElement ) {
			control = ( (InputGroupFormElement) target ).getControl( expectedType );
		}

		if ( control == null && target instanceof ViewElementCollection ) {
			for ( ViewElement child : (ViewElementCollection<ViewElement>) viewElement ) {
				control = getFormControl( child, expectedType );

				if ( control != null ) {
					return control;
				}
			}
		}

		return control;
	}
}
