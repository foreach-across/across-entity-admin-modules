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

import com.foreach.across.modules.entity.query.*;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Adds support for {@code null} value arguments for {@link EntityQueryOps#IN}.
 * The presence of a {@code null} value will result in a {@link EntityQueryOps#IS_NULL} predicate being added as an {@code OR}.
 * <p/>
 * Supports the negative {@link EntityQueryOps#NOT_IN} as well, which wil result in an {@code AND} clause.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public final class InEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	public static final EntityQueryConditionTranslator INSTANCE = new InEntityQueryConditionTranslator();

	private InEntityQueryConditionTranslator() {
	}

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		if ( shouldTranslate( condition ) ) {
			EntityQuery query = new EntityQuery();
			query.setOperand( EntityQueryOps.IN == condition.getOperand() ? EntityQueryOps.OR : EntityQueryOps.AND );

			Object[] nonNullArguments = removeNullArgument( condition.getArguments() );

			if ( nonNullArguments.length > 0 ) {
				query.add( new EntityQueryCondition( condition.getProperty(), condition.getOperand(), nonNullArguments ) );
			}

			query.add(
					new EntityQueryCondition(
							condition.getProperty(), EntityQueryOps.IN == condition.getOperand() ? EntityQueryOps.IS_NULL : EntityQueryOps.IS_NOT_NULL
					)
			);

			return query.getExpressions().size() == 1 ? query.getExpressions().get( 0 ) : query;
		}

		return condition;
	}

	private Object[] removeNullArgument( Object[] arguments ) {
		if ( arguments.length == 1 && arguments[0] instanceof EQGroup ) {
			EQGroup filtered = removeNullValue( (EQGroup) arguments[0] );
			return filtered.getValues().length > 0 ? new Object[] { filtered } : new Object[0];
		}

		return Stream.of( arguments ).filter( arg -> !EQValue.NULL.equals( arg ) && arg != null ).toArray();
	}

	private EQGroup removeNullValue( EQGroup group ) {
		return new EQGroup( Stream.of( group.getValues() ).filter( arg -> !EQValue.NULL.equals( arg ) ).toArray( EQType[]::new ) );
	}

	private boolean shouldTranslate( EntityQueryCondition condition ) {
		if ( EntityQueryOps.IN != condition.getOperand() && EntityQueryOps.NOT_IN != condition.getOperand() ) {
			return false;
		}

		return Arrays.stream( condition.getArguments() )
		             .flatMap( arg -> arg instanceof EQGroup ? Arrays.stream( ( (EQGroup) arg ).getValues() ) : Stream.of( arg ) )
		             .anyMatch( arg -> arg == null || EQValue.NULL.equals( arg ) );
	}
}
