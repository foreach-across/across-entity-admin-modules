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

import org.springframework.util.Assert;

/**
 * @author Arne Vandamme
 */
public class Size
{
	public static final Size BLOCK = new Size( "block", true );
	public static final Size DEFAULT = new Size( "", true );
	public static final Size LARGE = new Size( "lg", true );
	public static final Size SMALL = new Size( "sm", true );
	public static final Size EXTRA_SMALL = new Size( "xs", true );

	private final boolean isDefault;
	private final String name;

	public Size( String name ) {
		this( name, false );
	}

	private Size( String name, boolean isDefault ) {
		Assert.notNull( name );
		this.name = name;
		this.isDefault = isDefault;
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

	/**
	 * @return The given size as a block level element.
	 */
	public Size asBlock() {
		if ( this.equals( BLOCK ) ) {
			return this;
		}

		final Size parent = this;
		return new Size( "block", true )
		{
			@Override
			public String forPrefix( String prefix ) {
				return parent.forPrefix( prefix ) + " " + super.forPrefix( prefix );
			}
		};
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
}
