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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Different operand types that an {@link EntityQueryCondition} supports.
 *
 * @author Arne Vandamme
 */
public enum EntityQueryOps
{
	AND(
			( field, args ) ->
					Stream.of( args )
					      .map( a -> a instanceof EntityQuery ? "(" + a.toString() + ")" : a.toString() )
					      .collect( Collectors.joining( " and " ) ),
			"and"
	),
	OR(
			( field, args ) ->
					Stream.of( args )
					      .map( a -> a instanceof EntityQuery ? "(" + a.toString() + ")" : a.toString() )
					      .collect( Collectors.joining( " or " ) ),
			"or"
	),
	EQ( ( field, args ) -> field + " = " + ( args.length > 0 ? objectAsString( args[0] ) : "" ), "=" ),
	NEQ( ( field, args ) -> field + " != " + ( args.length > 0 ? objectAsString( args[0] ) : "" ), "!=", "<>" ),
	CONTAINS( ( field, args ) -> field + " contains " + objectAsString( args[0] ), "contains" ),
	NOT_CONTAINS(
			( ( field, args ) -> field + " not contains " + objectAsString( args[0] ) ),
			"not contains"
	),
	IN( ( field, args ) -> field + " in " + joinAsGroup( args ), "in" ),
	NOT_IN( ( field, args ) -> field + " not in " + joinAsGroup( args ), "not in" ),
	LIKE( ( field, args ) -> field + " like " + objectAsString( args[0] ), "like" ),
	LIKE_IC( ( field, args ) -> field + " ilike " + objectAsString( args[0] ), "ilike" ),
	NOT_LIKE( ( field, args ) -> field + " not like " + objectAsString( args[0] ), "not like" ),
	NOT_LIKE_IC( ( field, args ) -> field + " not ilike " + objectAsString( args[0] ), "not ilike" ),
	GT( ( field, args ) -> field + " > " + objectAsString( args[0] ), ">" ),
	GE( ( field, args ) -> field + " >= " + objectAsString( args[0] ), ">=" ),
	LT( ( field, args ) -> field + " < " + objectAsString( args[0] ), "<" ),
	LE( ( field, args ) -> field + " <= " + objectAsString( args[0] ), "<=" ),
	IS_NULL( ( field, args ) -> field + " is NULL", "is" ),
	IS_NOT_NULL( ( field, args ) -> field + " is not NULL", "is not" ),
	IS_EMPTY( ( field, args ) -> field + " is EMPTY", "is" ),
	IS_NOT_EMPTY( ( field, args ) -> field + " is not EMPTY", "is not" );

	private interface OpsWriter
	{
		String toString( String propertyName, Object... arguments );
	}

	private static String objectAsString( Object object ) {
		if ( object == null ) {
			return "NULL";
		}

		if ( object instanceof String ) {
			return "'" + escapeChars( (String) object ) + "'";
		}

		return object.toString();
	}

	private static String escapeChars( String value ) {
		return value.replace( "\\", "\\\\" ).replace( "'", "\\'" );
	}

	private static String joinAsGroup( Object... arguments ) {
		if ( arguments.length == 1 && arguments[0] instanceof EQGroup ) {
			return arguments[0].toString();
		}

		return "(" + Stream.of( arguments )
		                   .map( EntityQueryOps::objectAsString )
		                   .collect( Collectors.joining( "," ) ) + ")";
	}

	private String[] tokens;
	private final OpsWriter opsWriter;

	EntityQueryOps( OpsWriter opsWriter, String... tokens ) {
		this.opsWriter = opsWriter;
		this.tokens = tokens;
	}

	public String toString( String propertyName, Object... arguments ) {
		return opsWriter.toString( propertyName, arguments );
	}

	public String getToken() {
		return tokens[0];
	}

	public static EntityQueryOps forToken( String token ) {
		String lookup = StringUtils.lowerCase( token ).trim();

		for ( EntityQueryOps ops : values() ) {
			if ( ArrayUtils.contains( ops.tokens, lookup ) ) {
				return ops;
			}
		}

		return null;
	}

	/**
	 * Retrieve the multi-value equivalent operand for a single-value operand.
	 * Eg. the multi-value of {@code EQ} is {@code IN}.
	 *
	 * @param single value operand
	 * @return multi-value operand or {@code null} if there is none
	 */
	public static EntityQueryOps resolveMultiValueOperand( @NonNull EntityQueryOps single ) {
		switch ( single ) {
			case EQ:
				return IN;
			case NEQ:
				return NOT_IN;
			case CONTAINS:
			case NOT_CONTAINS:
			case IN:
			case NOT_IN:
				return single;
		}

		return null;
	}
}
