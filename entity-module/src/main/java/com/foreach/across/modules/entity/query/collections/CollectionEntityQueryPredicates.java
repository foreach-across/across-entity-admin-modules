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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
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
				return in( condition.getProperty(), condition.getArguments() );
			case NOT_IN:
				return in( condition.getProperty(), condition.getArguments() ).negate();
			case LIKE:
				return like( condition.getProperty(), condition.getFirstArgument(), descriptor );
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
			else if ( String.class.equals( descriptor.getPropertyType() ) || CharSequence.class.equals( descriptor.getPropertyType() ) ) {
				return StringUtils.contains( (String) propertyValue, (String) value );
			}
			throw new IllegalArgumentException(
					"'contains' operand only supports collections, arrays and strings. Property is an instance of: " + propertyValue.getClass() );
		};
	}

	@SuppressWarnings("ConstantConditions")
	private static <T> Predicate<CollectionEntityQueryItem<T>> in( String property, Object[] values ) {
		return item -> {
			Object propertyValue = item.getPropertyValue( property );
			Object value = values.length == 1 ? values[0] : values;
			if ( Collection.class.isAssignableFrom( value.getClass() ) ) {
				return CollectionUtils.contains( ( (Collection) value ).iterator(), propertyValue );
			}
			else if ( value.getClass().isArray() ) {
				return ArrayUtils.contains( (Object[]) value, propertyValue );
			}
			else if ( values.length == 1 ) {
				return Objects.equals( propertyValue, value );
			}

			throw new IllegalArgumentException( "'in' operand only supports collections and arrays. Given type is: " + values.getClass() );
		};
	}

	//TODO both String and CharSequence?
	@SuppressWarnings("unchecked")
	private static <T> Predicate<CollectionEntityQueryItem<T>> like( String property, Object value, EntityPropertyDescriptor descriptor ) {
		return item -> {
//			if(descriptor.getPropertyType())
			String propertyValue = item.getPropertyValue( property );
			String arg = (String) value;
			String actualArgument = arg.replaceAll( "%", "" );
			if ( arg.startsWith( "%" ) && arg.endsWith( "%" ) ) {
				return contains( property, actualArgument, descriptor ).test( (CollectionEntityQueryItem<Object>) item );
			}
			else if ( arg.startsWith( "%" ) ) {
				return propertyValue.endsWith( actualArgument );
			}
			else if ( arg.endsWith( "%" ) ) {
				return propertyValue.startsWith( actualArgument );
			}
			return equals( property, actualArgument ).test( (CollectionEntityQueryItem<Object>) item );
		};
	}
}
