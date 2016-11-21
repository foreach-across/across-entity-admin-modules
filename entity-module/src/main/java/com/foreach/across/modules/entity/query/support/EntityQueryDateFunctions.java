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

package com.foreach.across.modules.entity.query.support;

import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQueryFunctionHandler;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Calendar;
import java.util.Date;

/**
 * Provides date related functions to be used in entity queries.
 * <ul>
 * <li>now(): returns the current time</li>
 * <li>today(): returns the date of today</li>
 * </ul>
 * Supported property types are {@link Date} and {@link Long}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityQueryDateFunctions implements EntityQueryFunctionHandler
{
	public static final String NOW = "now";
	public static final String TODAY = "today";

	private static final String[] FUNCTION_NAMES = new String[] { NOW, TODAY };

	@Override
	public boolean accepts( String functionName, TypeDescriptor desiredType ) {
		return ArrayUtils.contains( FUNCTION_NAMES, functionName ) && (
				Date.class.equals( desiredType.getObjectType() )
						|| Long.class.equals( desiredType.getObjectType() )
		);
	}

	@Override
	public Object apply( String functionName,
	                     Object[] arguments,
	                     TypeDescriptor desiredType,
	                     EQTypeConverter argumentConverter ) {
		Date calculated = calculateDate( functionName, arguments );

		return convertToDesiredType( calculated, desiredType.getObjectType() );
	}

	private Date calculateDate( String functionName, Object[] arguments ) {
		switch ( functionName ) {
			case "today":
				return DateUtils.truncate( new Date(), Calendar.DATE );
		}

		return new Date();
	}

	private Object convertToDesiredType( Date date, Class<?> desiredType ) {
		if ( Long.class.equals( desiredType ) ) {
			return date.getTime();
		}

		return date;
	}
}
