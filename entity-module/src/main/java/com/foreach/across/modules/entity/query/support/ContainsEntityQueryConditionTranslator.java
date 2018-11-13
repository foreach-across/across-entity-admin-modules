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
 * Adds support for {@link com.foreach.across.modules.entity.query.EQGroup} arguments as well as {@code null}
 * value arguments for {@link com.foreach.across.modules.entity.query.EntityQueryOps#CONTAINS}.
 * A group will be expanded in {@code OR} combinations, and any {@code null} argument will result in the
 * {@link com.foreach.across.modules.entity.query.EntityQueryOps#IS_EMPTY} predicate being added.
 * <p/>
 * Supports the negative {@link EntityQueryOps#NOT_CONTAINS} as well.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public final class ContainsEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	public static final EntityQueryConditionTranslator INSTANCE = new ContainsEntityQueryConditionTranslator();

	private ContainsEntityQueryConditionTranslator() {
	}

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		if ( shouldTranslate( condition ) ) {
			EntityQuery query = new EntityQuery();
			query.setOperand( EntityQueryOps.CONTAINS == condition.getOperand() ? EntityQueryOps.OR : EntityQueryOps.AND );

			Arrays.stream( condition.getArguments() )
			      .flatMap( arg -> arg instanceof EQGroup ? Arrays.stream( ( (EQGroup) arg ).getValues() ) : Stream.of( arg ) )
			      .map( arg -> separateCondition( condition.getProperty(), condition.getOperand(), arg ) )
			      .forEach( query::add );

			return query.getExpressions().size() == 1 ? query.getExpressions().get( 0 ) : query;
		}

		return condition;
	}

	private EntityQueryCondition separateCondition( String property, EntityQueryOps operand, Object argument ) {
		if ( argument == null || EQValue.NULL.equals( argument ) ) {
			return new EntityQueryCondition( property, operand == EntityQueryOps.CONTAINS ? EntityQueryOps.IS_EMPTY : EntityQueryOps.IS_NOT_EMPTY );
		}

		return new EntityQueryCondition( property, operand, argument );
	}

	private boolean shouldTranslate( EntityQueryCondition condition ) {
		if ( EntityQueryOps.CONTAINS != condition.getOperand() && EntityQueryOps.NOT_CONTAINS != condition.getOperand() ) {
			return false;
		}

		Object[] arguments = condition.getArguments();
		return arguments.length > 1
				|| ( arguments.length == 1 && ( arguments[0] instanceof EQGroup || EQValue.NULL.equals( arguments[0] ) || arguments[0] == null ) );
	}
}
