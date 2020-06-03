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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the mode for which a {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * is being requested.  A mode is essentially represented by a string, so it is easy to add custom modes.
 * <p/>
 * A mode has a single and multiple variant, in the latter the suffix <strong>_MULTIPLE</strong> is present in the mode string.
 * Multiple modes are mainly used on entity level to configure default element types for either a single or collection
 * representation of that entity.
 * <p/>
 * A {@code ViewElementMode} can optionally have child modes configured ({@link #withChildMode(String, ViewElementMode)}.
 * These are named sub-modes which can be used for more complex control rendering.
 *
 * @author Arne Vandamme
 */
@EqualsAndHashCode
public class ViewElementMode
{
	/**
	 * Only the label text of the descriptor.
	 */
	public static final ViewElementMode LABEL = ViewElementMode.of( "LABEL" );

	/**
	 * Only the (readonly) value of the descriptor.
	 */
	public static final ViewElementMode VALUE = ViewElementMode.of( "VALUE" );

	/**
	 * Form control for modifying the descriptor.
	 */
	public static final ViewElementMode CONTROL = ViewElementMode.of( "CONTROL" );

	/**
	 * Only the label text of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_LABEL = ViewElementMode.of( "LIST_LABEL" );

	/**
	 * Only the (readonly) value of the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_VALUE = ViewElementMode.of( "LIST_VALUE" );

	/**
	 * Form control for modifying the descriptor - for use in list view (usually tabular).
	 */
	public static final ViewElementMode LIST_CONTROL = ViewElementMode.of( "LIST_CONTROL" );

	/**
	 * Control for detail (form) view.
	 */
	public static final ViewElementMode FORM_READ = ViewElementMode.of( "FORM_READ" );

	/**
	 * Control for modifying form view.
	 */
	public static final ViewElementMode FORM_WRITE = ViewElementMode.of( "FORM_WRITE" );

	/**
	 * Wrapper for the control for filtering on the property or entity.
	 * Usually a form group with holds the FILTER_CONTROL.
	 */
	public static final ViewElementMode FILTER_FORM = ViewElementMode.of( "FILTER_FORM" );

	/**
	 * Control for filtering on the property or entity.
	 **/
	public static final ViewElementMode FILTER_CONTROL = ViewElementMode.of( "FILTER_CONTROL" );

	private static final String MULTIPLE_SUFFIX = "_MULTIPLE";

	private final String type;

	/**
	 * Get the child modes attached.
	 */
	@Getter
	private final Map<String, ViewElementMode> childModes;

	/**
	 * @deprecated use {@link #of(String)} instead.
	 */
	@Deprecated
	public ViewElementMode( @NonNull String type ) {
		Assert.isTrue( !StringUtils.containsAny( type, "=()," ), "ViewElementMode type cannot contain either '(', ')', ',' or '=' characters" );
		this.type = type;
		this.childModes = Collections.emptyMap();
	}

	private ViewElementMode( String type, Map<String, ViewElementMode> childModes ) {
		//Assert.isTrue( !StringUtils.containsAny( type, "=()" ), "ViewElementMode type cannot contain either '(', ')' or '=' characters" );
		this.type = type;
		this.childModes = Collections.unmodifiableMap( childModes );
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder( type );
		if ( childModes.size() > 0 ) {
			s.append( '(' );
			s.append( childModes.entrySet()
			                    .stream()
			                    .map( e -> e.getKey() + "=" + e.getValue() )
			                    .collect( Collectors.joining( "," ) ) );
			s.append( ')' );
		}
		return s.toString();
	}

	/**
	 * Creates a new element mode without the child mode configured.
	 *
	 * @param childModeName child mode name
	 * @return new mode instance
	 */
	public ViewElementMode withoutChildMode( @NonNull String childModeName ) {
		return withChildMode( childModeName, null );
	}

	/**
	 * Creates a new element mode with the additional child mode configured.
	 * If the {@code viewElementMode} argument is {@code null}, the child mode will be removed.
	 *
	 * @param childModeName   child mode name
	 * @param viewElementMode mode to apply for the child mode
	 * @return new mode instance
	 */
	public ViewElementMode withChildMode( @NonNull String childModeName, ViewElementMode viewElementMode ) {
		Map<String, ViewElementMode> newChildModes = new LinkedHashMap<>( childModes );
		newChildModes.compute( childModeName, ( mode, value ) -> viewElementMode );
		return new ViewElementMode( type, newChildModes );
	}

	/**
	 * Creates a new element mode without any child modes configured.
	 *
	 * @return new mode instance
	 */
	public ViewElementMode withoutChildModes() {
		return new ViewElementMode( type );
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

	/**
	 * Get the child mode.
	 *
	 * @param childModeName name of the child mode
	 * @return mode
	 * @see #getChildMode(String, ViewElementMode)
	 */
	public ViewElementMode getChildMode( @NonNull String childModeName ) {
		return childModes.get( childModeName );
	}

	/**
	 * @return true if a child mode is set
	 */
	public boolean hasChildModes() {
		return !childModes.isEmpty();
	}

	/**
	 * Get the child mode or return the default value if not set.
	 *
	 * @param childModeName   name of the child mode
	 * @param defaultIfNotSet default value
	 * @return mode
	 */
	public ViewElementMode getChildMode( @NonNull String childModeName, ViewElementMode defaultIfNotSet ) {
		return childModes.getOrDefault( childModeName, defaultIfNotSet );
	}

	/**
	 * Check if this view element mode has the same base type as the arguments.
	 * This ignores any child modes set on the view element modes.
	 * This check requires the plurality to match, use {@link #matchesSingleTypeOf(ViewElementMode)} if you
	 * want to ignore the plural indicator.
	 *
	 * @param viewElementMode to compare with
	 * @return true if any element mode matches
	 */
	public boolean matchesTypeOf( ViewElementMode viewElementMode ) {
		return matchesTypeOfAny( viewElementMode );
	}

	/**
	 * Check if this view element mode has the same single type as the argument.
	 * This ignores any child modes set on the view element modes.
	 *
	 * @param viewElementMode to compare with
	 * @return true if any element mode matches
	 */
	public boolean matchesSingleTypeOf( ViewElementMode viewElementMode ) {
		return matchesSingleTypeOfAny( viewElementMode );
	}

	/**
	 * Check if this view element mode has the same base type as any of the arguments.
	 * This ignores any child modes set on the view element modes.
	 * This check requires the plurality to match, use {@link #matchesSingleTypeOfAny(ViewElementMode...)} if you
	 * want to ignore the plural indicator.
	 *
	 * @param viewElementModes to compare with
	 * @return true if any element mode matches
	 */
	public boolean matchesTypeOfAny( ViewElementMode... viewElementModes ) {
		return Stream.of( viewElementModes )
		             .anyMatch( e -> type.equals( e.type ) );
	}

	/**
	 * Check if this view element mode has the same single type as any of the arguments.
	 * This ignores any child modes set on the view element modes.
	 *
	 * @param viewElementModes to compare with
	 * @return true if any element mode matches
	 */
	public boolean matchesSingleTypeOfAny( ViewElementMode... viewElementModes ) {
		ViewElementMode singleMode = forSingle();
		return Stream.of( viewElementModes )
		             .map( ViewElementMode::forSingle )
		             .anyMatch( e -> singleMode.type.equals( e.type ) );
	}

	/**
	 * Parses a string into a valid mode.
	 *
	 * @param viewElementModeString representation
	 * @return view element mode
	 */
	public static ViewElementMode of( @NonNull String viewElementModeString ) {
		int groupStart = viewElementModeString.indexOf( '(' );

		String type = groupStart >= 0 ? viewElementModeString.substring( 0, groupStart ) : viewElementModeString;

		ViewElementMode mode = new ViewElementMode( type );
		if ( groupStart >= 0 ) {
			Assert.isTrue( viewElementModeString.charAt( viewElementModeString.length() - 1 ) == ')', "Illegal ViewElementMode string" );
			String[] childModes = viewElementModeString.substring( groupStart + 1, viewElementModeString.length() - 1 ).split( "," );
			for ( String childMode : childModes ) {
				mode = mode.withChildMode(
						StringUtils.substringBefore( childMode, "=" ), ViewElementMode.of( StringUtils.substringAfter( childMode, "=" ) )
				);
			}
		}
		return mode;
	}

	public static boolean isList( @NonNull ViewElementMode mode ) {
		return mode.matchesSingleTypeOfAny( LIST_LABEL, LIST_VALUE, LIST_CONTROL );
	}

	public static boolean isLabel( @NonNull ViewElementMode mode ) {
		return mode.matchesSingleTypeOfAny( LABEL, LIST_LABEL );
	}

	public static boolean isValue( @NonNull ViewElementMode mode ) {
		return mode.matchesSingleTypeOfAny( VALUE, LIST_VALUE );
	}

	public static boolean isControl( @NonNull ViewElementMode mode ) {
		return mode.matchesSingleTypeOfAny( CONTROL, LIST_CONTROL, FILTER_CONTROL );
	}
}
