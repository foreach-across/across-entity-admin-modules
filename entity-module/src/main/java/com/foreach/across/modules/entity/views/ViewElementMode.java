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
package com.foreach.across.modules.entity.views;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Represents the mode for which a {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * is being requested.  A mode is essentially represented by a string, so it is easy to add custom modes.
 * <p/>
 * A mode has a single and multiple variant, in the latter the suffix <strong>_MULTIPLE</strong> is present in the mode string.
 * Multiple modes are mainly used on entity level to configure default element types for either a single or collection
 * representation of that entity.
 *
 * @author Arne Vandamme
 */
public class ViewElementMode
{
	/**
	 * Only the label text of the descriptor.
	 */
	public static final ViewElementMode LABEL = new ViewElementMode( "LABEL" );

	/**
	 * Only the (readonly) value of the descriptor.
	 */
	public static final ViewElementMode VALUE = new ViewElementMode( "VALUE" );

	/**
	 * Form control for modifying the descriptor.
	 */
	public static final ViewElementMode CONTROL = new ViewElementMode( "CONTROL" );

	/**
	 * Only the label text of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_LABEL = new ViewElementMode( "LIST_LABEL" );

	/**
	 * Only the (readonly) value of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_VALUE = new ViewElementMode( "LIST_VALUE" );

	/**
	 * Form control for modifying the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_CONTROL = new ViewElementMode( "LIST_CONTROL" );

	/**
	 * Control for detail (form) view.
	 */
	public static final ViewElementMode FORM_READ = new ViewElementMode( "FORM_READ" );

	/**
	 * Control for modifying form view.
	 */
	public static final ViewElementMode FORM_WRITE = new ViewElementMode( "FORM_WRITE" );

	/**
	 * Control for filtering on the property or entity.
	 */
	public static final ViewElementMode FILTER_CONTROL = new ViewElementMode( "FILTER_CONTROL" );

	private static final String MULTIPLE_SUFFIX = "_MULTIPLE";

	private final String type;

	public ViewElementMode( String type ) {
		Assert.notNull( type, "type is required" );
		this.type = type;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ViewElementMode that = (ViewElementMode) o;
		return Objects.equals( type, that.type );
	}

	@Override
	public int hashCode() {
		return Objects.hash( type );
	}

	/**
	 * Converts the current mode to the multiple variant.
	 * If the current mode is already a multiple (ends with <strong>_MULTIPLE</strong>),
	 * the current reference will be returned.
	 *
	 * @return new instance or same if already a multiple
	 */
	public ViewElementMode forMultiple() {
		return isForMultiple() ? this : new ViewElementMode( type + MULTIPLE_SUFFIX );
	}

	/**
	 * Converts the current mode to the single variant.
	 * If the current mode is not a multiple variant, the current reference will be returned.
	 *
	 * @return new instance or same if not a multiple
	 */
	public ViewElementMode forSingle() {
		return isForMultiple() ? new ViewElementMode( StringUtils.removeEnd( type, MULTIPLE_SUFFIX ) ) : this;
	}

	/**
	 * @return true if this mode corresponds to multiple
	 */
	public boolean isForMultiple() {
		return type.endsWith( MULTIPLE_SUFFIX );
	}

	public static boolean isList( ViewElementMode mode ) {
		ViewElementMode single = mode.forSingle();
		return LIST_LABEL.equals( single ) || LIST_VALUE.equals( single ) || LIST_CONTROL.equals( single );
	}

	public static boolean isLabel( ViewElementMode mode ) {
		ViewElementMode single = mode.forSingle();
		return LABEL.equals( single ) || LIST_LABEL.equals( single );
	}

	public static boolean isValue( ViewElementMode mode ) {
		ViewElementMode single = mode.forSingle();
		return VALUE.equals( single ) || LIST_VALUE.equals( single );
	}

	public static boolean isControl( ViewElementMode mode ) {
		ViewElementMode single = mode.forSingle();
		return CONTROL.equals( single ) || LIST_CONTROL.equals( single ) || FILTER_CONTROL.equals( single );
	}
}
