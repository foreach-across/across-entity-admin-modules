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
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.IdentityHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Helper class for updating the control name of an {@link com.foreach.across.modules.bootstrapui.elements.FormInputElement}.
 * Will update the element control name and (depending on the value of {@link #recurse(boolean)}, will also update all
 * child controls if the element is a container.
 * <p/>
 * A new instance is pre-configured for bean style element control names: dotted separators will be added and any control
 * name starting with an underscore is modified *after* the underscore.
 * <p/>
 * When processing the container members, this prefixer ensures that controls are only modified a single time,
 * and ensures correct handling of {@link com.foreach.across.modules.bootstrapui.elements.FormControlElement.Proxy} controls.
 * <p/>
 * This is an implementation of both {@link Consumer<ViewElement>} and {@link ViewElementPostProcessor} so it can easily
 * be passed around and used in different contexts.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@Accessors(fluent = true, chain = true)
public class ControlNamePrefixAdjuster<T extends ViewElement> implements Consumer<T>, ViewElementPostProcessor<T>
{
	/**
	 * Prefix value that should be replaced. When specified, only controls matching the prefixToAdd will be modified.
	 * If not specified, all control names will be prefixed.
	 */
	@Setter
	private String prefixToReplace;

	/**
	 * The new prefixToAdd that should be set on the control name.
	 */
	@NonNull
	@Setter
	private String prefixToAdd;

	/**
	 * Set to {@code false} if you want to apply the new prefixToAdd as is.
	 * When {@code true} (default), a dot will be inserted between the new prefixToAdd and
	 * the remaining segment if the remaning segment does not start with a dot or
	 * an indexer related character (<em>{</em>,<em>[</em> or <em>(</em>).
	 */
	@Setter
	private boolean insertDotSeparator = true;

	/**
	 * Set to {@code false} if you only want to apply the name modification to the initial element,
	 * and not recurse through the child elements if the element is a container.
	 * <p/>
	 * When false and the initial element is not a {@link FormInputElement}, nothing will happen.
	 */
	@Setter
	private boolean recurse = true;

	/**
	 * Set to {@code false} if you want to treat any control name starting with an underscore as an
	 * exact control name. If not, a starting _ will be ignored for the control name predicate
	 * and prefixing will be done after the _. A control starting with underscore is usually a special
	 * element for Spring data-binding.
	 */
	@Setter
	private boolean ignoreUnderscore = true;

	/**
	 * Additional predicate that the candidates should match before they are modified.
	 * Recursing through members of a candidate will happen regardless of the predicate.
	 */
	@Setter
	private Predicate<FormInputElement> elementPredicate;

	/**
	 * An additional custom predicate that the (unmodified) control name should match
	 * before it is modified.
	 */
	@Setter
	private Predicate<String> controlNamePredicate;

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, ViewElement element ) {
		accept( element );
	}

	@Override
	public void accept( ViewElement element ) {
		if ( prefixToAdd == null || element == null ) {
			return;
		}

		if ( element instanceof FormInputElement ) {
			replaceControlNamePrefix( (FormInputElement) element );
		}

		if ( recurse && element instanceof ContainerViewElement ) {
			val processed = new IdentityHashMap<FormInputElement, Object>();
			( (ContainerViewElement) element ).findAll( FormInputElement.class, i -> !FormControlElement.Proxy.class.isInstance( i ) )
			                                  .forEach( control -> {
				                                  if ( !processed.containsKey( control ) ) {
					                                  replaceControlNamePrefix( control );
					                                  processed.put( control, null );
				                                  }
			                                  } );
		}
	}

	private void replaceControlNamePrefix( FormInputElement control ) {
		if ( elementPredicate != null && !elementPredicate.test( control ) ) {
			return;
		}

		String currentControlName = control.getControlName();

		if ( currentControlName != null && !currentControlName.isEmpty() ) {
			if ( controlNamePredicate != null && !controlNamePredicate.test( removeUnderscore( currentControlName ) ) ) {
				return;
			}

			boolean underscored = shouldSkipUnderscore( currentControlName );

			if ( underscored ) {
				currentControlName = currentControlName.substring( 1 );
			}

			String newControlName = null;

			if ( StringUtils.isEmpty( prefixToReplace ) ) {
				newControlName = addPrefix( currentControlName );
			}
			else if ( currentControlName.startsWith( prefixToReplace ) ) {
				newControlName = addPrefix( StringUtils.removeStart( currentControlName, prefixToReplace ) );
			}

			if ( newControlName != null && !newControlName.equals( currentControlName ) ) {
				if ( underscored ) {
					control.setControlName( '_' + newControlName );
				}
				else {
					control.setControlName( newControlName );
				}
			}
		}
	}

	private String removeUnderscore( String controlName ) {
		return ignoreUnderscore && controlName.charAt( 0 ) == '_' ? controlName.substring( 1 ) : controlName;
	}

	private boolean shouldSkipUnderscore( String controlName ) {
		return ignoreUnderscore && controlName.charAt( 0 ) == '_' && ( StringUtils.isEmpty( prefixToReplace ) || prefixToReplace.charAt( 0 ) != '_' );
	}

	private String addPrefix( String controlName ) {
		if ( StringUtils.isEmpty( prefixToAdd ) ) {
			return controlName;
		}

		if ( insertDotSeparator ) {
			if ( prefixToAdd.charAt( prefixToAdd.length() - 1 ) != '.'
					&& controlName.charAt( 0 ) != '['
					&& controlName.charAt( 0 ) != '('
					&& controlName.charAt( 0 ) != '{'
					&& controlName.charAt( 0 ) != '.' ) {
				return prefixToAdd + "." + controlName;
			}
		}

		return prefixToAdd + controlName;
	}
}
