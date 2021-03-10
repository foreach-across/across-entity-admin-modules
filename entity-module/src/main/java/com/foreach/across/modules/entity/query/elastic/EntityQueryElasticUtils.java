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

import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
				return Criteria.where( resolveProperty( condition.getProperty() ) ).not().exists();
			case IS_NOT_NULL:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).exists();
			case EQ:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).matchesAll( condition.getFirstArgument() );
			case NEQ:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).not().matchesAll( condition.getFirstArgument() );
			case GT:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).greaterThan( condition.getFirstArgument() );
			case GE:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).greaterThanEqual( condition.getFirstArgument() );
			case LT:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).lessThan( condition.getFirstArgument() );
			case LE:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).lessThanEqual( condition.getFirstArgument() );
/*
			case CONTAINS: {
				Expression<Collection> collection
						= (Expression<Collection>) resolveProperty( root, resolveProperty( condition.getProperty() ));
				return cb.isMember( condition.getFirstArgument(), collection );
			}

			case NOT_CONTAINS: {
				Expression<Collection> collection
						= (Expression<Collection>) resolveProperty( root, resolveProperty( condition.getProperty() ));
				return cb.isNotMember( condition.getFirstArgument(), collection );
			}

 */
			case IS_EMPTY:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).exists();
			case IS_NOT_EMPTY:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).not().exists();
			case IN:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).in( condition.getArguments() );
			case NOT_IN:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).notIn( condition.getArguments() );
			case LIKE:
				return resolveLikeCriteria( condition.getProperty(), Objects.toString( condition.getFirstArgument() ) );
			case LIKE_IC:
				return resolveLikeCriteria( condition.getProperty(), StringUtils.lowerCase( Objects.toString( condition.getFirstArgument() ) ) );
			case NOT_LIKE:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).not().contains( Objects.toString( condition.getFirstArgument() ) );
			case NOT_LIKE_IC:
				return Criteria.where( resolveProperty( condition.getProperty() ) ).not().contains(
						StringUtils.lowerCase( Objects.toString( condition.getFirstArgument() ) ) );
		}
		throw new IllegalArgumentException( "Unsupported operand for Elasticsearch query: " + condition.getOperand() );
	}

	private static Criteria resolveLikeCriteria( String property, String argument ) {
//		todo: Support args like `itemCode like 'Y%215%03%2021'` some day? Currently not supported via Criteria api.
//		String sanitizedArgument = QueryParserBase.escape( argument ).replaceAll( "%", "*" );
//		return queryStringQuery( sanitizedArgument ).field(property).analyzeWildcard( true ).escape(false);

		Criteria where = Criteria.where( resolveProperty( property ) );

		List<String> split = Arrays.stream( argument.split( "%" ) )
		                           .filter( StringUtils::isNotEmpty )
		                           .collect( Collectors.toList() );
		if ( split.size() > 3 ) {
			throw new EntityQueryParsingException.IllegalValue( property,
			                                                    argument + ". Multiple inner wildcards are currently not supported." );
		}

		boolean startsWithWildcard = argument.startsWith( "%" );
		boolean endsWithWildcard = argument.endsWith( "%" );

		if ( split.size() == 3 ) {
			if ( startsWithWildcard || endsWithWildcard ) {
				throw new EntityQueryParsingException.IllegalValue( property,
				                                                    argument + ". Multiple inner wildcards are currently not supported." );
			}
			// Example: Y%2021%4785
			return Criteria.and().subCriteria( where.startsWith( split.get( 0 ) ) )
			               .subCriteria( where.contains( split.get( 1 ) ) )
			               .subCriteria( where.endsWith( split.get( 2 ) ) );
		}

		if ( split.size() == 2 ) {
			// Example: %Y2021%4785
			if ( startsWithWildcard ) {
				return Criteria.and().subCriteria( where.contains( split.get( 0 ) ) )
				               .subCriteria( where.endsWith( split.get( 1 ) ) );
			}
			// Example: Y2021%4785%
			else if ( endsWithWildcard ) {
				return Criteria.and().subCriteria( where.startsWith( split.get( 0 ) ) )
				               .subCriteria( where.contains( split.get( 1 ) ) );
			}
			// Example: Y2021%4785
			else {
				return Criteria.and().subCriteria( where.startsWith( split.get( 0 ) ) )
				               .subCriteria( where.endsWith( split.get( 1 ) ) );
			}
		}

		// Example: %Y20214785%
		if ( startsWithWildcard && endsWithWildcard ) {
			return where.contains( argument.substring( 1, argument.length() - 1 ) );
		}
		// Example: %Y20214785
		if ( startsWithWildcard ) {
			return where.endsWith( argument.substring( 1 ) );
		}
		// Example: Y20214785%
		if ( endsWithWildcard ) {
			return where.startsWith( argument.substring( 0, argument.length() - 1 ) );
		}
		// Example: Y20214785 (same as EntityQueryOps.EQ)
		return where.matchesAll( argument );
	}

	private static String resolveProperty( String property ) {
		// Collection searches are just json document searches
		return StringUtils.replace( property, EntityPropertyRegistry.INDEXER + ".", "." );
	}

	private Criteria buildQueryPredicate( EntityQuery query ) {
		Criteria basePredicate = query.getOperand() == EntityQueryOps.AND ? Criteria.and() : Criteria.or();
		for ( EntityQueryExpression expression : query.getExpressions() ) {
			basePredicate.subCriteria( buildPredicate( expression ) );
		}
		return basePredicate;
	}
}
