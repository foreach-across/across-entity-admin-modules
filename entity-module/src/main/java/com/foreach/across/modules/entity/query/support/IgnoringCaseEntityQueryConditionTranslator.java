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

import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Converts all equals and like conditions to case-insensitive like equivalents ({@link com.foreach.across.modules.entity.query.EntityQueryOps#LIKE_IC}),
 * ensuring that the query is always case insensitive.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
public final class IgnoringCaseEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	public static final IgnoringCaseEntityQueryConditionTranslator INSTANCE = new IgnoringCaseEntityQueryConditionTranslator();

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		if ( condition.hasArguments() && condition.getArguments().length == 1 ) {
			EntityQueryOps translatedOperand = translateOperand( condition.getOperand() );
			if ( translatedOperand != null ) {
				Object argument = condition.getFirstArgument();
				if ( argument instanceof String ) {
					return new EntityQueryCondition( condition.getProperty(), translatedOperand,
					                                 escapeCharsIfNecessary( (String) argument, condition.getOperand() ) );
				}
			}
		}
		return condition;
	}

	private String escapeCharsIfNecessary( String argument, EntityQueryOps operand ) {
		if ( operand == EQ || operand == NEQ ) {
			return StringUtils.replace( StringUtils.replace( argument, "\\", "\\\\" ), "%", "\\%" );
		}

		return argument;
	}

	private EntityQueryOps translateOperand( EntityQueryOps operand ) {
		switch ( operand ) {
			case EQ:
			case LIKE:
				return LIKE_IC;
			case NEQ:
			case NOT_LIKE:
				return NOT_LIKE_IC;
			default:
				return null;
		}
	}
}
