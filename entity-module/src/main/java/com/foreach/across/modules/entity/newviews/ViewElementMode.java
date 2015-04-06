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
package com.foreach.across.modules.entity.newviews;

import java.util.Objects;

/**
 * Represents the mode for which a {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * is being requested.  Two default modes exist: {@link #READING} and {@link #WRITING}.
 * A mode is essentially represented by a string, so it is easy to add custom modes.
 *
 * @author Arne Vandamme
 */
public class ViewElementMode
{
	public static final ViewElementMode READING = new ViewElementMode( "reading" );
	public static final ViewElementMode WRITING = new ViewElementMode( "writing" );

	private final String type;

	public ViewElementMode( String type ) {
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
}
