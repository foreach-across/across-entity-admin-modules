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
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;

/**
 * @author Arne Vandamme
 */
public final class BootstrapElementUtils
{
	private BootstrapElementUtils() {
	}

	/**
	 * Will search for the first {@link FormControlElement} instance that is not an {@link InputGroupFormElement}.
	 * It is possible that the instance itself is returned.  This method will recurse through
	 * {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement} types until an instance is returned.
	 *
	 * @param viewElement instance
	 * @return form control instance, or {@code null} if not found
	 */
	public static FormControlElement getFormControl( ViewElement viewElement ) {
		return getFormControl( viewElement, FormControlElement.class );
	}

	/**
	 * Will search for the first {@link FormControlElement} instance that matches the expected type.
	 * It is possible that the instance itself is returned.  This method will recurse through
	 * {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement} types until an instance is returned.
	 *
	 * @param viewElement  instance
	 * @param expectedType the control should have
	 * @return form control instance, or {@code null} if not found
	 */
	public static <V extends FormControlElement> V getFormControl( ViewElement viewElement,
	                                                               Class<V> expectedType ) {
		V control = null;

		if ( viewElement instanceof FormGroupElement ) {
			ViewElement candidate = ( (FormGroupElement) viewElement ).getControl();

			if ( candidate != null ) {
				control = getFormControl( candidate, expectedType );
			}
		}
		else if ( viewElement instanceof FormControlElement.Proxy ) {
			ViewElement candidate = ( (FormControlElement.Proxy) viewElement ).getControl();

			if ( candidate != null ) {
				control = getFormControl( candidate, expectedType );
			}
		}
		else if ( expectedType.isInstance( viewElement ) ) {
			control = expectedType.cast( viewElement );
		}

		if ( control == null && viewElement instanceof ContainerViewElement ) {
			for ( ViewElement child : ( (ContainerViewElement) viewElement ).getChildren() ) {
				control = getFormControl( child, expectedType );

				if ( control != null ) {
					return control;
				}
			}
		}

		return control;
	}
}
