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

import org.springframework.util.Assert;

import java.util.Objects;

/**
 * Represents an unprocessed value in an {@link EntityQuery}.
 * The value is represented as a {@link String} but should be converted to an actual type when creating
 * an executable query.  In a string format, this value was not a literal string.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EQValue implements EQType
{
	private final String value;

	public EQValue( String value ) {
		Assert.notNull( value );
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
		EQValue eqString = (EQValue) o;
		return Objects.equals( value, eqString.value );
	}

	@Override
	public int hashCode() {
		return Objects.hash( value );
	}

	@Override
	public String toString() {
		return value;
	}
}
