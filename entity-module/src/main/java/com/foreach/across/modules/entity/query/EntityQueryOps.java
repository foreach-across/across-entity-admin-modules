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

/**
 * Different operand types that an {@link EntityQueryCondition} supports.
 *
 * @author Arne Vandamme
 */
public enum EntityQueryOps
{
	AND( ( propertyName, arguments ) -> {
		Assert.notNull( arguments );
		return "(" + StringUtils.join( arguments, " and " ) + ")";
	}, "and" ),

	OR( ( propertyName, arguments ) -> {
		Assert.notNull( arguments );
		return "(" + StringUtils.join( arguments, " or " ) + ")";
	}, "or" ),

	EQ( ( propertyName, arguments ) -> propertyName + " = " + objectAsString( arguments[0] ), "=" ),

	NEQ( ( propertyName, arguments ) -> propertyName + " != " + objectAsString( arguments[0] ), "!=", "<>" ),

	CONTAINS( ( propertyName, arguments ) -> propertyName + " contains " + objectAsString( arguments[0] ), "contains" );

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

		throw new IllegalArgumentException( "No known entity query operator for token " + token );
	}
}
