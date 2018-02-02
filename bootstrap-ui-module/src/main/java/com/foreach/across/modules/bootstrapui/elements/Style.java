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
package com.foreach.across.modules.bootstrapui.elements;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains the common Bootstrap styles and provides ability to define new styles as constants.
 *
 * @author Arne Vandamme
 */
public class Style
{
	public static class Button
	{
		public static final Style DEFAULT = Style.DEFAULT;
		public static final Style PRIMARY = Style.PRIMARY;
		public static final Style SUCCESS = Style.SUCCESS;
		public static final Style INFO = Style.INFO;
		public static final Style WARNING = Style.WARNING;
		public static final Style DANGER = Style.DANGER;
		public static final Style LINK = new Style( "link", true );
	}

	public static class Table
	{
		public static final Style STRIPED = new Style( "striped", true );
		public static final Style BORDERED = new Style( "bordered", true );
		public static final Style HOVER = new Style( "hover", true );
		public static final Style CONDENSED = new Style( "condensed", true );
	}

	public static class TableCell
	{
		public static final Style ACTIVE = Style.ACTIVE;
		public static final Style SUCCESS = Style.SUCCESS;
		public static final Style INFO = Style.INFO;
		public static final Style WARNING = Style.WARNING;
		public static final Style DANGER = Style.DANGER;
	}

	public static final Style DEFAULT = new Style( "default", true );
	public static final Style PRIMARY = new Style( "primary", true );
	public static final Style SUCCESS = new Style( "success", true );
	public static final Style INFO = new Style( "info", true );
	public static final Style WARNING = new Style( "warning", true );
	public static final Style DANGER = new Style( "danger", true );
	public static final Style ACTIVE = new Style( "active", true );

	private final boolean isDefault;
	private final String name;

	public Style( String name ) {
		this( name, false );
	}

	private Style( @NonNull String name, boolean isDefault ) {
		this.name = name;
		this.isDefault = isDefault;
	}

	public String getName() {
		return name;
	}

	public String forPrefix( String prefix ) {
		return isDefault && !StringUtils.isBlank( prefix ) ? prefix + "-" + name : name;
	}

	/**
	 * @return True if should behave as a default style, meaning it will be prefixed depending on the controls
	 * it is used on.
	 */
	public boolean isDefaultStyle() {
		return isDefault;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Style style = (Style) o;

		if ( isDefault != style.isDefault ) {
			return false;
		}
		if ( name != null ? !name.equals( style.name ) : style.name != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = ( isDefault ? 1 : 0 );
		result = 31 * result + ( name != null ? name.hashCode() : 0 );
		return result;
	}
}
