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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Implementation of {@link EntityQueryConditionTranslator} that expands a single condition to either an {@code OR} or a {@code AND} query of multiple properties.
 * Mostly used for text properties, where a single property (eg. "name like X") could be expanded to a query on multiple (eg. "firstName like X or lastName like X").
 * <p/>
 * When the original condition has an {@link EntityQueryOps} operand where {@link EntityQueryOps#isNegation()} is {@code true}, the expanded query
 * will have a reverse operand than the one originally defined. Eg:
 * <ul>
 * <li>{@code name like X} would be expanded to {@code firstName like X or lastName like X}</li>
 * <li>{@code name not like X} would be expanded to {@code firstName not like X and lastName not like X}</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
public final class ExpandingEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	private final EntityQueryOps operand;
	private final Collection<String> propertyNames;

	private ExpandingEntityQueryConditionTranslator( EntityQueryOps operand, String... propertyNames ) {
		this.operand = operand;
		this.propertyNames = new ArrayList<>( propertyNames.length );

		Stream.of( propertyNames )
		      .filter( name -> !this.propertyNames.contains( name ) )
		      .forEach( this.propertyNames::add );

	}

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		EntityQuery subQuery = new EntityQuery( condition.getOperand().isNegation() ? operand.reverse() : operand );
		propertyNames.forEach( propertyName -> subQuery.add( new EntityQueryCondition( propertyName, condition.getOperand(), condition.getArguments() ) ) );
		return subQuery;
	}

	/**
	 * Create translator that expands using an {@code AND} operand.
	 *
	 * @param propertyNames properties to expand to
	 * @return condition translator
	 */
	public static ExpandingEntityQueryConditionTranslator and( String... propertyNames ) {
		return new ExpandingEntityQueryConditionTranslator( EntityQueryOps.AND, propertyNames );
	}

	/**
	 * Create translator that expands using an {@code OR} operand.
	 *
	 * @param propertyNames properties to expand to
	 * @return condition translator
	 */
	public static ExpandingEntityQueryConditionTranslator or( String... propertyNames ) {
		return new ExpandingEntityQueryConditionTranslator( EntityQueryOps.OR, propertyNames );
	}
}
