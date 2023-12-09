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
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a function value.  A function will be evaluated at runtime to determine the actual arguments returned.
 * A function is identified by a name and optional set of arguments.
 *
 * @author Arne Vandamme
 * @see EntityQueryFunctionHandler
 * @since 2.0.0
 */
public final class EQFunction implements EQType
{
	private final String name;
	private final EQType[] arguments;

	public EQFunction( String name ) {
		this( name, new EQType[0] );
	}

	public EQFunction( String name, Collection<EQType> arguments ) {
		this( name, arguments.toArray( new EQType[arguments.size()] ) );
	}

	public EQFunction( @NonNull String name, @NonNull EQType[] arguments ) {
		this.name = name;
		this.arguments = arguments.clone();
	}

	/**
	 * @return name of the function
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return arguments for this function
	 */
	public EQType[] getArguments() {
		return arguments;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		EQFunction that = (EQFunction) o;
		return Objects.equals( name, that.name ) &&
				Arrays.equals( arguments, that.arguments );
	}

	@Override
	public int hashCode() {
		return Objects.hash( name, arguments );
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder( name ).append( "(" );
		s.append( StringUtils.join( arguments, "," ) );
		s.append( ")" );

		return s.toString();
	}
}
