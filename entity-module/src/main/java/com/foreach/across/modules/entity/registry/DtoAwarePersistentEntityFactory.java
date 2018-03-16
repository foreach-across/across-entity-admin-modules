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

package com.foreach.across.modules.entity.registry;

import com.foreach.across.modules.hibernate.business.EntityWithDto;
import org.springframework.data.mapping.PersistentEntity;

/**
 * Separate {@link EntityFactory} that supports {@link EntityWithDto} implementations like
 * {@link com.foreach.across.modules.hibernate.business.SettableIdBasedEntity}.
 *
 * @author Arne Vandamme
 * @see PersistentEntityFactory
 * @since 3.0.0
 */
public class DtoAwarePersistentEntityFactory<T> extends PersistentEntityFactory<T>
{
	public DtoAwarePersistentEntityFactory( PersistentEntity<T, ?> persistentEntity ) {
		super( persistentEntity );
	}

	@SuppressWarnings("unchecked")
	@Override
	public T createDto( T entity ) {
		if ( entity instanceof EntityWithDto ) {
			return ( (EntityWithDto<T>) entity ).toDto();
		}

		return super.createDto( entity );
	}
}
