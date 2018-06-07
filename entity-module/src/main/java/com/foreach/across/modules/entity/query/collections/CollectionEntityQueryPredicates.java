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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Contains common {@link java.util.function.Predicate} implementations for {@link com.foreach.across.modules.entity.query.EntityQueryCondition}
 * to apply for a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@UtilityClass
@Slf4j
class CollectionEntityQueryPredicates
{
	static Predicate<CollectionEntityQueryItem<Object>> createPredicate( EntityQueryCondition condition, @NonNull EntityPropertyDescriptor descriptor ) {
		switch ( condition.getOperand() ) {
			case EQ:
				return equals( condition.getProperty(), condition.getFirstArgument() );
			case NEQ:
				return equals( condition.getProperty(), condition.getFirstArgument() ).negate();
			case CONTAINS:
				return contains( condition.getProperty(), condition.getFirstArgument(), descriptor );
			case NOT_CONTAINS:
				return contains( condition.getProperty(), condition.getFirstArgument(), descriptor ).negate();
			case IN:
				return in( condition.getProperty(), Arrays.asList( condition.getArguments() ) );
			case NOT_IN:
				return in( condition.getProperty(), Arrays.asList( condition.getArguments() ) ).negate();
			case LIKE:
				return like( condition.getProperty(), condition.getFirstArgument(), false );
			case LIKE_IC:
				return like( condition.getProperty(), condition.getFirstArgument(), true );
			case NOT_LIKE:
				return like( condition.getProperty(), condition.getFirstArgument(), false ).negate();
			case NOT_LIKE_IC:
				return like( condition.getProperty(), condition.getFirstArgument(), true ).negate();
			case GT:
				return greaterThan( condition.getProperty(), condition.getFirstArgument(), false );
			case GE:
				return greaterThan( condition.getProperty(), condition.getFirstArgument(), true );
			case LT:
				return lessThan( condition.getProperty(), condition.getFirstArgument(), false );
			case LE:
				return lessThan( condition.getProperty(), condition.getFirstArgument(), true );
			case IS_NULL:
				return isNull( condition.getProperty() );
			case IS_NOT_NULL:
				return isNull( condition.getProperty() ).negate();
			case IS_EMPTY:
				return isEmpty( condition.getProperty(), descriptor );
			case IS_NOT_EMPTY:
				return isEmpty( condition.getProperty(), descriptor ).negate();
		}

		throw new IllegalArgumentException( "Unsupported operand for collections query: " + condition.getOperand() );
	}

	private static <T> Predicate<CollectionEntityQueryItem<T>> equals( String property, Object value ) {
		return item -> Objects.equals( item.getPropertyValue( property ), value );
	}

	private static <T> Predicate<CollectionEntityQueryItem<T>> contains( String property, Object value, EntityPropertyDescriptor descriptor ) {
		return item -> {
			Object propertyValue = item.getPropertyValue( property );

			if ( descriptor.getPropertyTypeDescriptor().isCollection() ) {
				return CollectionUtils.contains( ( (Collection) propertyValue ).iterator(), value );
			}
			else if ( descriptor.getPropertyTypeDescriptor().isArray() ) {
				return ArrayUtils.contains( (Object[]) propertyValue, value );
			}
			throw new IllegalArgumentException(
					"'contains' operand is only supported for collections and arrays. Property is an instance of: " + propertyValue.getClass() );
		};
	}

	@SuppressWarnings("ConstantConditions")
	private static <T> Predicate<CollectionEntityQueryItem<T>> in( String property, List values ) {
		return item -> values.contains( item.getPropertyValue( property ) );
	}

	@SuppressWarnings("unchecked")
	private static <T> Predicate<CollectionEntityQueryItem<T>> like( String property, Object value, boolean caseInsensitive ) {
		return item -> {
			String argument = (String) value;
			String replacedWildCards = getPattern( "(?<!\\\\)%", true ).matcher( argument ).replaceAll( ".*" );
			String regex = getPattern( "[?!\\\\]%", true ).matcher( replacedWildCards ).replaceAll( "%" );
			regex = StringUtils.replace( regex, "\\", "\\\\" );
			return getPattern( regex, caseInsensitive ).matcher( item.getPropertyValue( property ) ).matches();
		};
	}

	@SuppressWarnings("unchecked")
	private static <T> Predicate<CollectionEntityQueryItem<T>> greaterThan( String property, Object value, boolean orEqual ) {
		return item -> {
			int comparison = ( (Comparable) item.getPropertyValue( property ) ).compareTo( value );
			return orEqual ? comparison >= 0 : comparison > 0;
		};
	}

	@SuppressWarnings("unchecked")
	private static <T> Predicate<CollectionEntityQueryItem<T>> lessThan( String property, Object value, boolean orEqual ) {
		return item -> {
			int comparison = ( (Comparable) item.getPropertyValue( property ) ).compareTo( value );
			return orEqual ? comparison <= 0 : comparison < 0;
		};
	}

	private static <T> Predicate<CollectionEntityQueryItem<T>> isNull( String property ) {
		return item -> Objects.isNull( item.getPropertyValue( property ) );
	}

	private static <T> Predicate<CollectionEntityQueryItem<T>> isEmpty( String property, EntityPropertyDescriptor descriptor ) {
		return item -> {
			Object propertyValue = item.getPropertyValue( property );
			if ( descriptor.getPropertyTypeDescriptor().isCollection() ) {
				return CollectionUtils.isEmpty( (Collection<?>) propertyValue );
			}
			else if ( descriptor.getPropertyTypeDescriptor().isMap() ) {
				return CollectionUtils.isEmpty( (Map<?, ?>) propertyValue );
			}
			else if ( descriptor.getPropertyTypeDescriptor().isArray() ) {
				return ArrayUtils.isEmpty( (Object[]) propertyValue );
			}
			throw new IllegalArgumentException( "'is empty' operand is only applicable to collections, arrays and maps." );
		};
	}

	private Pattern getPattern( String regex, boolean caseInsensitive ) {
		if ( caseInsensitive ) {
			return Pattern.compile( regex, Pattern.CASE_INSENSITIVE );
		}
		else {
			return Pattern.compile( regex );
		}
	}

}
