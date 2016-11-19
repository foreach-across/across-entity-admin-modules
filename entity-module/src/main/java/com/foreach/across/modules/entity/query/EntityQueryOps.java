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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Different operand types that an {@link EntityQueryCondition} supports.
 *
 * @author Arne Vandamme
 */
public enum EntityQueryOps
{
	AND( ( field, args ) -> {
		Assert.notNull( args );
		return "(" + StringUtils.join( args, " and " ) + ")";
	}, "and" ),

	OR( ( field, args ) -> {
		Assert.notNull( args );
		return "(" + StringUtils.join( args, " or " ) + ")";
	}, "or" ),

	EQ( ( field, args ) -> field + " = " + ( args.length > 0 ? objectAsString( args[0] ) : "" ), "=" ),

	NEQ( ( field, args ) -> field + " != " + ( args.length > 0 ? objectAsString( args[0] ) : "" ), "!=", "<>" ),

	CONTAINS( ( field, args ) -> field + " contains " + objectAsString( args[0] ), "contains" ),

	NOT_CONTAINS( ( ( field, args ) -> field + " not contains " + objectAsString( args[0] ) ),
	              "not contains" ),

	IN( ( field, args ) -> field + " in (" + joinAsStrings( args ) + ")", "in" ),

	NOT_IN( ( field, args ) -> field + " not in (" + joinAsStrings( args ) + ")", "not in" ),

	LIKE( (field, args ) -> field + " like " + objectAsString( args[0] ), "like" ),

	NOT_LIKE( (field, args ) -> field + " not like " + objectAsString( args[0] ), "not like" );

	private interface OpsWriter
	{
		String toString( String propertyName, Object... arguments );
	}

	private static String objectAsString( Object object ) {
		if ( object == null ) {
			return "NULL";
		}

		if ( object instanceof String ) {
			return "'" + object + "'";
		}

		return object.toString();
	}

	private static String joinAsStrings( Object... arguments ) {
		return Stream.of( arguments )
		             .map( EntityQueryOps::objectAsString )
		             .collect( Collectors.joining( "," ) );
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

	public static EntityQueryOps forToken( String token ) {
		String lookup = StringUtils.lowerCase( token ).trim();

		for ( EntityQueryOps ops : values() ) {
			if ( ArrayUtils.contains( ops.tokens, lookup ) ) {
				return ops;
			}
		}

		return null;
	}
}
