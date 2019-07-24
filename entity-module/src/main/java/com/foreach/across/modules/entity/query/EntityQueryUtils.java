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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
public abstract class EntityQueryUtils
{
	private EntityQueryUtils() {
	}

	/**
	 * Appends an optional predicate to an existing query using an AND operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery and( EntityQuery existing, Object predicate ) {
		return appendToQuery( existing, EntityQueryOps.AND, predicate );
	}

	/**
	 * Appends an optional predicate to an existing query using an OR operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery or( EntityQuery existing, Object predicate ) {
		return appendToQuery( existing, EntityQueryOps.OR, predicate );
	}

	/**
	 * Appends an optional predicate to an existing query using the specified operand.
	 * If the predicate is not an {@link EntityQueryExpression} then it will be parsed as an EQL statement.
	 *
	 * @param existing  query to append to
	 * @param operand   operand to use
	 * @param predicate to append
	 * @return new query instance
	 */
	public static EntityQuery appendToQuery( EntityQuery existing, EntityQueryOps operand, Object predicate ) {
		if ( predicate != null ) {
			if ( predicate instanceof String ) {
				return EntityQuery.create( operand, existing, EntityQuery.parse( (String) predicate ) );
			}
			if ( predicate instanceof EntityQueryExpression ) {
				return EntityQuery.create( operand, existing, (EntityQueryExpression) predicate );
			}
			return EntityQuery.create( operand, existing, EntityQuery.parse( predicate.toString() ) );
		}

		return existing;
	}

	/**
	 * Perform simple condition translation on an {@link EntityQuery}, for example to remove conditions.
	 * For more complex translation scenarios, see the {@link EntityQueryTranslator} implementations.
	 * <p/>
	 * If no properties are specified all conditions will be translated. Else only conditions for the specified
	 * properties will be passed to the translator.
	 *
	 * @param query      to translate
	 * @param translator function, may return {@code null} to remove a condition entirely
	 * @param properties optional a set of properties whose conditions should be translated, if empty all properties will be passed
	 * @return new query instance - never {@code null}
	 */
	public static EntityQuery translateConditions( @NonNull EntityQuery query,
	                                               @NonNull Function<EntityQueryCondition, EntityQueryExpression> translator,
	                                               String... properties ) {
		EntityQuery translated = new EntityQuery( query.getOperand() );
		translated.setSort( query.getSort() );

		query.getExpressions()
		     .stream()
		     .map(
				     expression -> {
					     if ( expression instanceof EntityQueryCondition ) {
						     EntityQueryCondition condition = (EntityQueryCondition) expression;

						     if ( properties.length == 0 || ArrayUtils.contains( properties, condition.getProperty() ) ) {
							     return translator.apply( condition );
						     }
						     return condition;
					     }
					     return translateConditions( (EntityQuery) expression, translator, properties );
				     }
		     )
		     .filter( Objects::nonNull )
		     .forEach( translated::add );

		return translated;
	}

	/**
	 * Simplifies a query by removing useless levels (grouping of predicates).
	 *
	 * @param query to simplify
	 * @return simplified
	 */
	public static EntityQuery simplify( @NonNull EntityQuery query ) {
		EntityQuery simplified = new EntityQuery();
		simplified.setSort( query.getSort() );
		AtomicReference<EntityQueryOps> finalOperand = new AtomicReference<>();
		simplify( finalOperand, query ).forEach( simplified::add );
		simplified.setOperand( finalOperand.get() != null ? finalOperand.get() : query.getOperand() );
		return simplified;
	}

	private static Stream<EntityQueryExpression> simplify( AtomicReference<EntityQueryOps> parentOperand, EntityQueryExpression expression ) {
		if ( expression instanceof EntityQueryCondition ) {
			return Stream.of( expression );
		}

		return simplify( parentOperand, (EntityQuery) expression );
	}

	private static Stream<EntityQueryExpression> simplify( AtomicReference<EntityQueryOps> parentOperand, EntityQuery query ) {
		List<EntityQueryExpression> expressions = query.getExpressions();

		if ( expressions.isEmpty() ) {
			return Stream.empty();
		}

		if ( expressions.size() == 1 ) {
			return simplify( parentOperand, expressions.get( 0 ) );
		}

		if ( parentOperand.get() == null ) {
			parentOperand.set( query.getOperand() );
		}

		if ( query.getOperand() == parentOperand.get() ) {
			return expressions.stream()
			                  .flatMap( e -> simplify( parentOperand, e ) );
		}

		return Stream.of( query );
	}

	/**
	 * Create the predicate for fetching entities associated to a specific parent entity, mapped by an {@link EntityAssociation}.
	 *
	 * @param association entities to fetch
	 * @param parent      entity they belong to
	 * @return query predicate
	 */
	public static EntityQueryCondition createAssociationPredicate( @NonNull EntityAssociation association, @NonNull Object parent ) {
		EntityPropertyDescriptor sourceProperty = association.getSourceProperty();
		EntityPropertyDescriptor targetProperty = association.getTargetProperty();

		Object sourceValue = sourceProperty != null ? sourceProperty.getPropertyValue( parent ) : parent;
		TypeDescriptor targetTypeDescriptor = targetProperty.getPropertyTypeDescriptor();
		EntityQueryOps operand = targetTypeDescriptor.isCollection() || targetTypeDescriptor.isArray() ? EntityQueryOps.CONTAINS : EntityQueryOps.EQ;

		return new EntityQueryCondition( targetProperty.getName(), operand, sourceValue );
	}

	/**
	 * Finds an {@link EntityQueryCondition} inside an {@link EntityQuery}
	 *
	 * @param propertyName the name of the property in the expression
	 * @return all {@link EntityQueryCondition} that match the propertyName
	 */
	public static List<EntityQueryCondition> find( @NonNull EntityQuery entityQuery, @NonNull String propertyName ) {
		return find( entityQuery, propertyName, new ArrayList<>() );
	}

	private static List<EntityQueryCondition> find( @NonNull EntityQuery entityQuery,
	                                                @NonNull String propertyName,
	                                                @NonNull List<EntityQueryCondition> matches ) {
		for ( EntityQueryExpression expression : entityQuery.getExpressions() ) {

			if ( expression instanceof EntityQuery ) {
				find( (EntityQuery) expression, propertyName, matches );
			}
			else {
				EntityQueryCondition entityQueryCondition = (EntityQueryCondition) expression;
				if ( StringUtils.equals( propertyName, entityQueryCondition.getProperty() ) ) {
					matches.add( entityQueryCondition );
				}
			}
		}
		return matches;
	}
}
