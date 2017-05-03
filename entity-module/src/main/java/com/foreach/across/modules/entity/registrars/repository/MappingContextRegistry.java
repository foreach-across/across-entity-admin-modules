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

package com.foreach.across.modules.entity.registrars.repository;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
final class MappingContextRegistry
{
	private final Set<MappingContext> mappingContexts = new LinkedHashSet<>();

	void addMappingContext( MappingContext mappingContext ) {
		mappingContexts.add( mappingContext );
	}

	@SuppressWarnings("unchecked")
	Optional<PersistentEntity> getPersistentEntity( Class<?> entityType ) {
		return mappingContexts.stream()
		                      .filter( ctx -> ctx.hasPersistentEntityFor( entityType ) )
		                      .map( ctx -> ctx.getPersistentEntity( entityType ) )
		                      .findFirst();
	}
}
