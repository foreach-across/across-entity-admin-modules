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

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Translates {@link EntityQueryOps#IS_EMPTY} to {@link EntityQueryOps#IS_NULL} or {@link EntityQueryOps#EQ} empty string.
 * Supports the negative operands as well. Should only be used for {@link String} properties.
 *
 * @author Steven Gentens
 * @since 3.3.0
 */
public class EmptyStringEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	public final static EntityQueryConditionTranslator INSTANCE = new EmptyStringEntityQueryConditionTranslator();

	private EmptyStringEntityQueryConditionTranslator() {
	}

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		if ( shouldTranslate( condition ) ) {
			if ( condition.getOperand().isNegation() ) {
				return EntityQuery.and( new EntityQueryCondition( condition.getProperty(), IS_NOT_NULL ),
				                        new EntityQueryCondition( condition.getProperty(), NEQ, "" ) );
			}
			return EntityQuery.or( new EntityQueryCondition( condition.getProperty(), IS_NULL ),
			                       new EntityQueryCondition( condition.getProperty(), EQ, "" ) );
		}
		return condition;
	}

	private boolean shouldTranslate( EntityQueryCondition condition ) {
		return ( IS_EMPTY.equals( condition.getOperand() ) || IS_NOT_EMPTY.equals( condition.getOperand() ) );
	}
}
