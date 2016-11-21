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

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents an unprocessed group of values.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EQGroup implements EQType
{
	private final Object[] values;

	public EQGroup( Collection<Object> values ) {
		Assert.notNull( values );
		this.values = values.toArray();
	}

	public EQGroup( Object... values ) {
		Assert.notNull( values );
		this.values = values;
	}

	public Object[] getValues() {
		return values;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		EQGroup eqGroup = (EQGroup) o;
		return Arrays.equals( values, eqGroup.values );
	}

	@Override
	public int hashCode() {
		return Objects.hash( values );
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder( "(" );
		s.append( StringUtils.join( values, "," ) );
		s.append( ")" );

		return s.toString();
	}
}
