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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Applies a default filter to an {@link EntityQuery}: scopes the query to all entities
 * of a given associated (linked to a parent entity).
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class AssociatedEntityQueryExecutor<T>
{
	private final EntityPropertyDescriptor property;
	private final EntityQueryExecutor<T> parentExecutor;

	public AssociatedEntityQueryExecutor( EntityPropertyDescriptor property,
	                                      EntityQueryExecutor<T> entityQueryExecutor ) {
		this.property = property;
		this.parentExecutor = entityQueryExecutor;
	}

	public List<T> findAll( Object parent, EntityQuery query ) {
		return parentExecutor == null
				? propertyValue( parent )
				: parentExecutor.findAll( EntityQuery.and( query, buildEqualsOrContainsCondition( parent ) ) );
	}

	public Page<T> findAll( Object parent, EntityQuery query, Pageable pageable ) {
		return parentExecutor == null
				? new PageImpl<>( propertyValue( parent ) )
				: parentExecutor.findAll( EntityQuery.and( query, buildEqualsOrContainsCondition( parent ) ), pageable );
	}

	@SuppressWarnings("unchecked")
	private List<T> propertyValue( Object parent ) {
		BeanWrapper beanWrapper = new BeanWrapperImpl( parent );
		Object itemsValue = beanWrapper.getPropertyValue( property.getName() );

		List items = Collections.emptyList();

		if ( itemsValue != null ) {
			if ( itemsValue instanceof Collection ) {
				items = new ArrayList<>( (Collection) itemsValue );
			}
			else {
				throw new IllegalArgumentException(
						"Property " + property.getName() + " was expected to be a collection type but is a " + itemsValue
								.getClass().getName() );
			}
		}

		return items;
	}

	private EntityQueryCondition buildEqualsOrContainsCondition( Object value ) {
		return new EntityQueryCondition(
				property.getName(),
				property.getPropertyTypeDescriptor().isCollection() ? EntityQueryOps.CONTAINS : EntityQueryOps.EQ,
				value
		);
	}

	/**
	 * Create a fixed query executor that does not support any parameters but will always return the value of the property
	 * on the parent entity.
	 *
	 * @param property on the parent that contains the result
	 * @return items
	 */
	public static AssociatedEntityQueryExecutor<?> forBeanProperty( EntityPropertyDescriptor property ) {
		return new AssociatedEntityQueryExecutor<>( property, null );
	}
}
