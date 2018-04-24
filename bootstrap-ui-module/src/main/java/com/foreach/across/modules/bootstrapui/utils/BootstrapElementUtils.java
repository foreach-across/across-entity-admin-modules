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
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.IdentityHashMap;

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
	 * When it encounters a {@link FormControlElement.Proxy} it will also exclude the
	 * target {@link ViewElement} from control name updating.
	 * <p/>
	 * This method will insert a . between the prefix and the original control name, unless
	 * the control name starts with a special character. Use {@link #prefixControlNames(String, boolean, ContainerViewElement)}
	 * if want to ensure the prefix is always added as is-.
	 *
	 * @param prefix    to apply to any input element
	 * @param container in which to find all input elements
	 * @see #prefixControlNames(String, boolean, ContainerViewElement)
	 */
	public static void prefixControlNames( @NonNull String prefix, @NonNull ContainerViewElement container ) {
		prefixControlNames( prefix, true, container );
	}

	/**
	 * Prefix all controls in a container. This is utility method that will scan
	 * for all unique {@link FormInputElement} controls and ensure they are prefixed only once.
	 * When it encounters a {@link FormControlElement.Proxy} it will also exclude the
	 * target {@link ViewElement} from control name updating.
	 *
	 * @param prefix    to apply to any input element
	 * @param container in which to find all input elements
	 */
	public static void prefixControlNames( @NonNull String prefix, boolean addDotSeparator, @NonNull ContainerViewElement container ) {
		replaceControlNamePrefixes( "", prefix, container );
	}

	/**
	 * Prefix all controls in a container if they have an existing prefix. This is utility method that will scan
	 * for all unique {@link FormInputElement} controls and ensure they are prefixed only once.
	 * Any {@link FormControlElement.Proxy} will be excluded from updating its control name.
	 *
	 * @param prefix    to apply to any input element
	 * @param newPrefix to use
	 * @param container in which to find all input elements
	 */
	public static void replaceControlNamePrefixes( @NonNull String prefix, @NonNull String newPrefix, @NonNull ContainerViewElement container ) {
		val processed = new IdentityHashMap<FormInputElement, Object>();
		container.findAll( FormInputElement.class, i -> !FormControlElement.Proxy.class.isInstance( i ) )
		         .forEach( control -> {
			         if ( !processed.containsKey( control ) ) {
				         replaceControlNamePrefix( prefix, newPrefix, control );
				         processed.put( control, null );
			         }
		         } );
	}

	/**
	 * Replace the first part of the control name of a single {@link FormInputElement}.
	 * By specifying an empty string as current prefix, you can simply prefix
	 * <p/>
	 * This method supports custom control names, a starting <strong>_</strong> will be ignored
	 * and part after the underscore will be replaced.
	 *
	 * @param prefix          the control must start with
	 * @param newPrefix       to use
	 * @param control         to update its control name
	 */
	public static void replaceControlNamePrefix( @NonNull String prefix,
	                                             @NonNull String newPrefix,
	                                             @NonNull FormInputElement control ) {
		String currentControlName = control.getControlName();

		if ( currentControlName != null && !currentControlName.isEmpty() ) {
			boolean underscored = currentControlName.charAt( 0 ) == '_' && ( prefix.isEmpty() || prefix.charAt( 0 ) != '_' );

			if ( underscored ) {
				currentControlName = currentControlName.substring( 1 );
			}

			String newControlName;

			if ( prefix.isEmpty() ) {
				newControlName = newPrefix + currentControlName;
			}
			else {
				newControlName = StringUtils.replace( currentControlName, prefix, newPrefix, 1 );
			}

			if ( !newControlName.equals( currentControlName ) ) {
				if ( underscored ) {
					control.setControlName( '_' + newControlName );
				}
				else {
					control.setControlName( newControlName );
				}
			}
		}
	}
}
