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

package com.foreach.across.modules.entity.query;

import lombok.NonNull;
import org.thymeleaf.util.StringUtils;

import java.util.Objects;

/**
 * Represents a string literal value in an {@link EntityQuery}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EQString implements EQType
{
	private final String value;

	public EQString( @NonNull String value ) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		EQString eqString = (EQString) o;
		return Objects.equals( value, eqString.value );
	}

	@Override
	public int hashCode() {
		return Objects.hash( value );
	}

	@Override
	public String toString() {
		return "'" + StringUtils.replace( value, "'", "\\'" ) + "'";
	}
}
