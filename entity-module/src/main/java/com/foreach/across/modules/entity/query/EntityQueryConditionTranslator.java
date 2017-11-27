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

import com.foreach.across.modules.entity.query.support.ExpandingOrEntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.support.IgnoringCaseEntityQueryConditionTranslator;

/**
 * API for converting or optimizing a single {@link EntityQueryCondition}.
 * If registered on a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor} any condition
 * for that property will be pre-processed by the translator.  Condition translation is performed by the {@link EntityQueryTranslator}
 * during pre-processing of a query.
 *
 * @author Arne Vandamme
 * @see EntityQueryTranslator
 * @since 2.1.0
 */
@FunctionalInterface
public interface EntityQueryConditionTranslator
{
	EntityQueryExpression translate( EntityQueryCondition condition );

	/**
	 * Creates a translator that ensures a condition is always applied case insensitive.
	 * Note that if you always want a property to be queried case insensitive, it is probably better
	 * to set a collation in the backing datastore as that offers better performance.
	 *
	 * @return translator that will ensure a condition is always applied case insensitive
	 */
	static EntityQueryConditionTranslator ignoreCase() {
		return IgnoringCaseEntityQueryConditionTranslator.INSTANCE;
	}

	/**
	 * Creates a translator that replaces a single condition by an OR combination of multiple other conditions,
	 * having the same operand and argument values.
	 *
	 * @param propertyNames to combine
	 * @return translator for the property names
	 */
	static EntityQueryConditionTranslator expandingOr( String... propertyNames ) {
		return new ExpandingOrEntityQueryConditionTranslator( propertyNames );
	}
}
