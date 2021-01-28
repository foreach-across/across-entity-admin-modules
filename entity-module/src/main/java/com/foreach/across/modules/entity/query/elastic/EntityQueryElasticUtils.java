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

package com.foreach.across.modules.entity.query.elastic;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.Objects;

@UtilityClass
public class EntityQueryElasticUtils
{
	public CriteriaQuery toCriteriaQuery( final EntityQuery query ) {
		return new CriteriaQuery( buildPredicate( query ) );
	}

	private Criteria buildPredicate( EntityQueryExpression expression ) {
		if ( expression instanceof EntityQueryCondition ) {
			return buildConditionPredicate( (EntityQueryCondition) expression );
		}
		else {
			return buildQueryPredicate( (EntityQuery) expression );
		}
	}

	@SuppressWarnings("unchecked")
	@SneakyThrows
	private Criteria buildConditionPredicate( EntityQueryCondition condition ) {
		if ( condition.getFirstArgument() instanceof EntityQueryConditionElasticFunctionHandler ) {
			return ( (EntityQueryConditionElasticFunctionHandler) condition.getFirstArgument() ).apply( condition );
		}
		switch ( condition.getOperand() ) {
			case IS_NULL:
				return Criteria.where( condition.getProperty() ).not().exists();
			case IS_NOT_NULL:
				return Criteria.where( condition.getProperty() ).exists();
			case EQ:
				return Criteria.where( condition.getProperty() ).matchesAll( condition.getFirstArgument() );
			case NEQ:
				return Criteria.where( condition.getProperty() ).not().matchesAll( condition.getFirstArgument() );
			case GT:
				return Criteria.where( condition.getProperty() ).greaterThan( condition.getFirstArgument() );
			case GE:
				return Criteria.where( condition.getProperty() ).greaterThanEqual( condition.getFirstArgument() );
			case LT:
				return Criteria.where( condition.getProperty() ).lessThan( condition.getFirstArgument() );
			case LE:
				return Criteria.where( condition.getProperty() ).lessThanEqual( condition.getFirstArgument() );
/*
			case CONTAINS: {
				Expression<Collection> collection
						= (Expression<Collection>) resolveProperty( root, condition.getProperty() );
				return cb.isMember( condition.getFirstArgument(), collection );
			}

			case NOT_CONTAINS: {
				Expression<Collection> collection
						= (Expression<Collection>) resolveProperty( root, condition.getProperty() );
				return cb.isNotMember( condition.getFirstArgument(), collection );
			}

 */
			case IS_EMPTY:
				return Criteria.where( condition.getProperty() ).exists();
			case IS_NOT_EMPTY:
				return Criteria.where( condition.getProperty() ).not().exists();
			case IN:
				return Criteria.where( condition.getProperty() ).in( condition.getArguments() );
			case NOT_IN:
				return Criteria.where( condition.getProperty() ).notIn( condition.getArguments() );
			case LIKE:
				return Criteria.where( condition.getProperty() ).contains( Objects.toString( condition.getFirstArgument() ) );
			case LIKE_IC:
				return Criteria.where( condition.getProperty() ).contains( StringUtils.lowerCase( Objects.toString( condition.getFirstArgument() ) ) );
			case NOT_LIKE:
				return Criteria.where( condition.getProperty() ).not().contains( Objects.toString( condition.getFirstArgument() ) );
			case NOT_LIKE_IC:
				return Criteria.where( condition.getProperty() ).not().contains( StringUtils.lowerCase( Objects.toString( condition.getFirstArgument() ) ) );
		}
		throw new IllegalArgumentException( "Unsupported operand for Elasticsearch query: " + condition.getOperand() );
	}

	private Criteria buildQueryPredicate( EntityQuery query ) {
		Criteria basePredicate = query.getOperand() == EntityQueryOps.AND ? Criteria.and() : Criteria.or();
		for ( EntityQueryExpression expression : query.getExpressions() ) {
			basePredicate.subCriteria( buildPredicate( expression ) );
		}
		return basePredicate;
	}
}
