/*
 * Copyright 2019 the original author or authors
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
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;

import java.util.function.Consumer;

/**
 * Contains several static utility methods for working with elements.
 *
 * @author Arne Vandamme
 */
public abstract class BootstrapElementUtils
{
	protected BootstrapElementUtils() {
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
	public static <V extends FormControlElement> V getFormControl( ViewElement viewElement, Class<V> expectedType ) {
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

	/**
	 * Prefix all controls in a container. This is utility method that will scan
	 * for all unique {@link FormInputElement} controls and ensure they are prefixed only once.
	 * When it encounters a {@code FormControlElement.Proxy} it will also exclude the
	 * target {@link ViewElement} from control name updating.
	 * <p/>
	 * This method will insert a . between the prefix and the original control name, unless
	 * the control name starts with a special character.
	 * <p/>
	 * See {@link #prefixControlNames(String)} and {@link ControlNamePrefixAdjuster} if you want to customize.
	 *
	 * @param prefix    to apply to any input element
	 * @param container in which to find all input elements
	 * @see #prefixControlNames(String)
	 * @see ControlNamePrefixAdjuster
	 */
	public static void prefixControlNames( @NonNull String prefix, @NonNull ContainerViewElement container ) {
		prefixControlNames( prefix ).accept( container );
	}

	/**
	 * Updates any control having a control name starting with a given prefix.
	 * Said prefix will be replaced by the new one. Like {@link #prefixControlNames(String, ContainerViewElement)}
	 * this method should handle different form control elements correctly.
	 * <p/>
	 * See {@link #replaceControlNamesPrefix(String, String)} and {@link ControlNamePrefixAdjuster} if you want to customize.
	 *
	 * @param prefixToReplace prefix that should be replaced
	 * @param newPrefix       prefix that should be used instead
	 * @param container       in which to find all input elements
	 * @see #replaceControlNamesPrefix(String, String)
	 * @see ControlNamePrefixAdjuster
	 */
	public static void replaceControlNamesPrefix( @NonNull String prefixToReplace, @NonNull String newPrefix, @NonNull ContainerViewElement container ) {
		replaceControlNamesPrefix( prefixToReplace, newPrefix ).accept( container );
	}

	/**
	 * Create {@link ControlNamePrefixAdjuster} preconfigured to prefix all control names with the given prefix,
	 * ignoring any first underscore character, adding dot separators and recursing through all children
	 * in case the control is a container.
	 * <p/>
	 * The return value is both a {@link Consumer} and {@link com.foreach.across.modules.web.ui.ViewElementPostProcessor}.
	 *
	 * @param prefix to add
	 * @return adjuster instance
	 */
	public static <T extends ViewElement> ControlNamePrefixAdjuster<T> prefixControlNames( @NonNull String prefix ) {
		return new ControlNamePrefixAdjuster<T>().prefixToAdd( prefix );
	}

	/**
	 * Create {@link ControlNamePrefixAdjuster} preconfigured to update all control names that match the initial
	 * prefix, and replace said prefix with the new value. Like {@link #prefixControlNames(String)} the return
	 * value is preconfigured to recurse through container members, ignore underscores and add dot separators
	 * where necessary.
	 * <p/>
	 * The return value is both a {@link Consumer} and {@link com.foreach.across.modules.web.ui.ViewElementPostProcessor}.
	 *
	 * @param prefixToReplace prefix that should be replaced
	 * @param newPrefix       prefix that should be used instead
	 * @return adjuster instance
	 */
	public static <T extends ViewElement> ControlNamePrefixAdjuster<T> replaceControlNamesPrefix( @NonNull String prefixToReplace, @NonNull String newPrefix ) {
		return new ControlNamePrefixAdjuster<T>().prefixToReplace( prefixToReplace ).prefixToAdd( newPrefix );
	}
}
