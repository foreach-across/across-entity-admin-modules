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
package com.foreach.across.modules.entity.query.querydsl;

import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExpression;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.*;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import java.util.Arrays;

import static com.foreach.across.modules.entity.query.jpa.EntityQueryJpaUtils.toEscapedString;

/**
 * @author Arne Vandamme
 */
@UtilityClass
public class EntityQueryQueryDslUtils
{
	private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

	public <V> Predicate toPredicate( EntityQuery query, EntityConfiguration<?> entityConfiguration ) {
		try {
			return toPredicate( query, entityConfiguration.getEntityType() );
		}
		catch ( Exception e ) {
			/* ignore exception, try creating predicate using entity configuration */
		}

		return toPredicate( query, entityConfiguration.getEntityType(), entityConfiguration.getName() );
	}

	public <V> Predicate toPredicate( EntityQuery query, Class<V> entityType ) {
		return toPredicate( query, DEFAULT_ENTITY_PATH_RESOLVER.createPath( entityType ) );
	}

	public <V> Predicate toPredicate( EntityQuery query, EntityPath<V> rootPath ) {
		return toPredicate( query, rootPath.getType(), rootPath.getMetadata().getName() );
	}

	public <V> Predicate toPredicate( EntityQuery query, Class<V> entityType, String root ) {
		PathBuilder<?> pathBuilder = new PathBuilder<>( entityType, root );
		return buildQueryPredicate( query, pathBuilder );
	}

	private Predicate buildPredicate( EntityQueryExpression expression, PathBuilder<?> pathBuilder ) {
		if ( expression instanceof EntityQueryCondition ) {
			return buildConditionPredicate( (EntityQueryCondition) expression, pathBuilder );
		}
		else {
			return buildQueryPredicate( (EntityQuery) expression, pathBuilder );
		}
	}

	private Predicate buildConditionPredicate( EntityQueryCondition condition, PathBuilder<?> pathBuilder ) {
		if ( condition.getFirstArgument() instanceof EntityQueryConditionQueryDslFunctionHandler ) {
			return ( (EntityQueryConditionQueryDslFunctionHandler) condition.getFirstArgument() ).apply( condition ).toPredicate( pathBuilder );
		}
		switch ( condition.getOperand() ) {
			case IS_NULL:
				return resolveProperty( pathBuilder, condition.getProperty() ).isNull();
			case IS_NOT_NULL:
				return resolveProperty( pathBuilder, condition.getProperty() ).isNotNull();
			case EQ: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.EQ, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case NEQ: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.NE, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case GT: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.GT, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case GE: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.GOE, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case LT: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.LT, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case LE: {
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.LOE, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case CONTAINS: {
				Path<?> property = pathBuilder.getCollection( condition.getProperty(), Object.class );
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.CONTAINS_VALUE, property, constant );
			}
			case NOT_CONTAINS: {
				Path<?> property = pathBuilder.getCollection( condition.getProperty(), Object.class );
				Expression<Object> constant = Expressions.constant( condition.getFirstArgument() );
				return Expressions.predicate( Ops.CONTAINS_VALUE, property, constant ).not();
			}
			case IS_EMPTY: {
				Path<?> property = pathBuilder.getCollection( condition.getProperty(), Object.class );
				return Expressions.predicate( Ops.COL_IS_EMPTY, property );
			}
			case IS_NOT_EMPTY: {
				Path<?> property = pathBuilder.getCollection( condition.getProperty(), Object.class );
				return Expressions.predicate( Ops.COL_IS_EMPTY, property ).not();
			}
			case IN: {
				Expression<Object> constant = Expressions.constant( Arrays.asList( condition.getArguments() ) );
				return Expressions.predicate( Ops.IN, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case NOT_IN: {
				Expression<Object> constant = Expressions.constant( Arrays.asList( condition.getArguments() ) );
				return Expressions.predicate( Ops.NOT_IN, resolveProperty( pathBuilder, condition.getProperty() ), constant );
			}
			case LIKE: {
				Expression<Object> constant = Expressions.constant( toEscapedString( condition.getFirstArgument() ) );
				return Expressions.predicate( Ops.LIKE_ESCAPE, resolveProperty( pathBuilder, condition.getProperty() ), constant, Expressions.constant( ';' ) );
			}
			case LIKE_IC: {
				Expression<Object> constant = Expressions.constant( toEscapedString( condition.getFirstArgument() ) );
				return Expressions.predicate( Ops.LIKE_ESCAPE_IC, resolveProperty( pathBuilder, condition.getProperty() ), constant,
				                              Expressions.constant( ';' ) );
			}
			case NOT_LIKE: {
				Expression<Object> constant = Expressions.constant( toEscapedString( condition.getFirstArgument() ) );
				return Expressions.predicate( Ops.LIKE_ESCAPE, resolveProperty( pathBuilder, condition.getProperty() ), constant, Expressions.constant( ';' ) )
				                  .not();
			}
			case NOT_LIKE_IC: {
				Expression<Object> constant = Expressions.constant( toEscapedString( condition.getFirstArgument() ) );
				return Expressions.predicate( Ops.LIKE_ESCAPE_IC, resolveProperty( pathBuilder, condition.getProperty() ), constant,
				                              Expressions.constant( ';' ) ).not();
			}
		}

		throw new IllegalArgumentException( "Unsupported operand for QueryDsl query: " + condition.getOperand() );
	}

	public static PathBuilder<?> resolveProperty( PathBuilder<?> path, String propertyName ) {
		int ix = propertyName.indexOf( "." );
		if ( ix >= 0 ) {
			String name = StringUtils.left( propertyName, ix );
			String remainder = StringUtils.substring( propertyName, ix + 1 );

			PathBuilder<?> nestedPath;
			if ( StringUtils.endsWith( name, EntityPropertyRegistry.INDEXER ) ) {
				name = StringUtils.removeEnd( name, EntityPropertyRegistry.INDEXER );
				PathBuilder<?> any = path.getCollection( name, Object.class ).any();
				nestedPath = any.get( remainder );
			}
			else {
				nestedPath = path.get( name );
			}

			return resolveProperty( nestedPath, remainder );
		}

		return path.get( propertyName );
	}

	private Predicate buildQueryPredicate( EntityQuery query, PathBuilder<?> pathBuilder ) {
		BooleanBuilder builder = new BooleanBuilder();

		for ( EntityQueryExpression expression : query.getExpressions() ) {
			if ( query.getOperand() == EntityQueryOps.AND ) {
				builder.and( buildPredicate( expression, pathBuilder ) );
			}
			else {
				builder.or( buildPredicate( expression, pathBuilder ) );
			}
		}

		return builder;
	}
}
