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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.styles.BootstrapStyleRule;
import lombok.NonNull;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

/**
 * @author Arne Vandamme
 * @deprecated since 3.0.0 - use {@link com.foreach.across.modules.bootstrapui.styles.BootstrapStyles#button} instead
 */
public final class Size
{
	public static final Size DEFAULT = new Size( "", true, null );
	public static final Size LARGE = new Size( "lg", true, css.button.large );
	public static final Size SMALL = new Size( "sm", true, css.button.small );

	private final boolean isDefault;
	private final String name;
	private final BootstrapStyleRule bootstrapStyleRule;

	private Size( @NonNull String name, boolean isDefault, BootstrapStyleRule bootstrapStyleRule ) {
		this.name = name;
		this.isDefault = isDefault;
		this.bootstrapStyleRule = bootstrapStyleRule;
	}

	public String getName() {
		return name;
	}

	public String forPrefix( String prefix ) {
		return isDefault ? prefix + "-" + name : name;
	}

	/**
	 * @return True if should behave as a default size, meaning it will be prefixed depending on the controls
	 * it is used on.
	 */
	public boolean isDefaultSize() {
		return isDefault;
	}

	BootstrapStyleRule toBootstrapStyleRule() {
		return bootstrapStyleRule;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}

		Size size = (Size) o;

		if ( isDefault != size.isDefault ) {
			return false;
		}
		if ( name != null ? !name.equals( size.name ) : size.name != null ) {
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

	static Size fromBootstrapStyleRule( @NonNull BootstrapStyleRule styleRule ) {
		if ( styleRule.equals( LARGE.bootstrapStyleRule ) ) {
			return LARGE;
		}
		if ( styleRule.equals( SMALL.bootstrapStyleRule ) ) {
			return SMALL;
		}
		return null;
	}
}
