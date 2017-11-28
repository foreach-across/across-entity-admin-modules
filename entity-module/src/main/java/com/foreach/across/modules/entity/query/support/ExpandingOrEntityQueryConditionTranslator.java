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
 * Implementation of {@link EntityQueryConditionTranslator} that expands a single condition to an OR query of multiple properties.
 * Mostly used for text properties, where a single property (eg. "name like X") could be expanded to a query on multiple (eg. "firstName like X or lastName like X").
 *
 * @author Arne Vandamme
 * @since 2.2.0
 */
public final class ExpandingOrEntityQueryConditionTranslator implements EntityQueryConditionTranslator
{
	private final Collection<String> propertyNames;

	public ExpandingOrEntityQueryConditionTranslator( String... propertyNames ) {
		this.propertyNames = new ArrayList<>( propertyNames.length );

		Stream.of( propertyNames )
		      .filter( name -> !this.propertyNames.contains( name ) )
		      .forEach( this.propertyNames::add );

	}

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		EntityQuery subQuery = new EntityQuery( EntityQueryOps.OR );
		propertyNames.forEach( propertyName -> subQuery.add( new EntityQueryCondition( propertyName, condition.getOperand(), condition.getArguments() ) ) );
		return subQuery;
	}
}
