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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.data.domain.Sort;

import java.util.Comparator;

/**
 * Contains common {@link java.util.Comparator} instances for entity query sorting of collections.
 * All default {@link Comparator} methods should return values in ascending order.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
@UtilityClass
class CollectionEntityQueryComparators
{
	@SuppressWarnings("unchecked")
	static <T> Comparator<CollectionEntityQueryItem<T>> createComparator( Sort.Order order, @NonNull EntityPropertyDescriptor descriptor ) {
		Comparator comparator = createComparatorForType( order, descriptor );

		if ( comparator == null ) {
			throw new IllegalArgumentException( "Unable to sort on property: " + order.getProperty() );
		}

		if ( order.getNullHandling() == Sort.NullHandling.NULLS_FIRST ) {
			comparator = Comparator.nullsFirst( comparator );
		}
		else if ( order.getNullHandling() == Sort.NullHandling.NULLS_LAST ) {
			comparator = Comparator.nullsLast( comparator );
		}

		if ( order.isDescending() ) {
			comparator = comparator.reversed();
		}

		return wrapper( order.getProperty(), comparator );
	}

	private static <T> Comparator<CollectionEntityQueryItem<T>> wrapper( String property, final Comparator<Object> comparator ) {
		return ( one, two ) -> {
			val valueOne = one.getPropertyValue( property );
			val valueTwo = two.getPropertyValue( property );
			return comparator.compare( valueOne, valueTwo );
		};
	}

	private static Comparator<?> createComparatorForType( Sort.Order order, EntityPropertyDescriptor descriptor ) {
		Class<?> simpleType = descriptor.getPropertyTypeDescriptor().getObjectType();

		if ( String.class.equals( simpleType ) ) {
			return stringComparator( order.isIgnoreCase() );
		}
		else if ( Comparable.class.isAssignableFrom( descriptor.getPropertyTypeDescriptor().getObjectType() ) ) {
			return defaultComparator();
		}

		return null;
	}

	private static Comparator<String> stringComparator( boolean ignoreCase ) {
		return ignoreCase ? String::compareToIgnoreCase : String::compareTo;
	}

	@SuppressWarnings("unchecked")
	private static Comparator defaultComparator() {
		return ( o1, o2 ) -> ( (Comparable) o1 ).compareTo( o2 );
	}
}
