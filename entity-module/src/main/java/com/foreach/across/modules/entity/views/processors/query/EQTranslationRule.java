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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.entity.query.EntityQueryOps;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Provides rules as to whether a {@link EntityQueryOps} can be translated into a different {@link EntityQueryOps}.
 *
 * @author Steven Gentens
 * @since 3.1.0
 */
public enum EQTranslationRule
{
	EQ_TO_IN( EQ, IN, values -> true ),
	EQ_TO_LIKE( EQ, LIKE, values -> {
		Object value = values.get( 0 );
		return EQString.class.isAssignableFrom( value.getClass() )
				&& !( (EQString) value ).getValue().contains( "%" );
	} ),
	EQ_TO_GE( EQ, GE, values -> true ),
	EQ_TO_LE( EQ, LE, values -> true ),
	NEQ_TO_NOT_IN( NEQ, NOT_IN, values -> true ),
	NEQ_TO_NOT_LIKE( NEQ, NOT_LIKE, values -> {
		Object value = values.get( 0 );
		return EQString.class.isAssignableFrom( value.getClass() )
				&& !( (EQString) value ).getValue().contains( "%" );
	} ),
	LIKE_TO_IN( LIKE, IN, values -> !( (EQString) values.get( 0 ) ).getValue().contains( "%" ) ),
	LIKE_TO_EQ( LIKE, EQ,
	            values -> {
		            Object value = values.get( 0 );
		            return values.size() == 1 && EQString.class.isAssignableFrom( value.getClass() )
				            && !( (EQString) value ).getValue().contains( "%" );
	            } ),
	LIKE_TO_CONTAINS( LIKE, CONTAINS, values -> {
		String value = ( (EQString) values.get( 0 ) ).getValue();
		return value.startsWith( "%" ) && value.endsWith( "%" );
	} ),
	NOT_LIKE_TO_NEQ( NOT_LIKE, NEQ, values -> !( (EQString) values.get( 0 ) ).getValue().contains( "%" ) ),
	NOT_LIKE_TO_NOT_IN( NOT_LIKE, NOT_IN, values -> !( (EQString) values.get( 0 ) ).getValue().contains( "%" ) ),
	NOT_LIKE_TO_NOT_CONTAINS( NOT_LIKE, NOT_CONTAINS, values -> {
		String value = ( (EQString) values.get( 0 ) ).getValue();
		return value.startsWith( "%" ) && value.endsWith( "%" );
	} ),
	IS_EMPTY_TO_IN( IS_EMPTY, IN, values -> true ),
	IS_NOT_EMPTY_TO_NOT_IN( IS_NOT_EMPTY, NOT_IN, values -> true ),
	IS_NULL_TO_IN( IS_NULL, IN, values -> true ),
	IS_NULL_TO_EQ( IS_NULL, EQ, values -> true ),
	IS_NOT_NULL_TO_NEQ( IS_NOT_NULL, NEQ, values -> true ),
	IS_NOT_NULL_TO_NOT_IN( IS_NOT_NULL, NOT_IN, values -> true );

	private EntityQueryOps from;
	private EntityQueryOps to;
	private Function<List<Object>, Boolean> canConvert;

	EQTranslationRule( EntityQueryOps from, EntityQueryOps to, Function<List<Object>, Boolean> canConvert ) {
		this.from = from;
		this.to = to;
		this.canConvert = canConvert;
	}

	public static EQTranslationRule getTranslationRuleFor( EntityQueryOps from, EntityQueryOps to ) {
		return Arrays.stream( values() )
		             .filter(
				             rule -> rule.from.equals( from ) && rule.to.equals( to ) )
		             .findFirst()
		             .orElse( null );
	}

	public boolean canConvert( List<Object> values ) {
		return canConvert.apply( values );
	}

}
