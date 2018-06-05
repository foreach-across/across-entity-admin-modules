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

package com.foreach.across.modules.entity.query.collections;

import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * Contains common {@link java.util.function.Predicate} implementations for {@link com.foreach.across.modules.entity.query.EntityQueryCondition}
 * to apply for a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@UtilityClass
class CollectionEntityQueryPredicates
{
	static <T> Predicate<CollectionEntityQueryItem<T>> createPredicate( EntityQueryCondition condition, @NonNull EntityPropertyDescriptor descriptor ) {
		switch ( condition.getOperand() ) {
			case EQ:
				return equalsPredicate( condition.getProperty(), condition.getFirstArgument() );
		}

		throw new IllegalArgumentException( "Unsupported operand for collections query: " + condition.getOperand() );
	}

	private static <T> Predicate<CollectionEntityQueryItem<T>> equalsPredicate( String property, Object value ) {
		return item -> Objects.equals( item.getPropertyValue( property ), value );
	}
}
