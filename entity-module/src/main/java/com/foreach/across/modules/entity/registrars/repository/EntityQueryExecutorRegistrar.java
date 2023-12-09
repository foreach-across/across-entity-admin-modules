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

import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import org.springframework.data.repository.Repository;

import java.util.Objects;

public interface EntityQueryExecutorRegistrar
{
	default boolean supports( EntityConfiguration entityConfiguration ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );
		return Objects.nonNull( repository ) && supports( entityConfiguration, repository );
	}

	boolean supports( EntityConfiguration entityConfiguration, Repository repository );

	default void handle( MutableEntityConfiguration entityConfiguration ) {
		Repository repository = entityConfiguration.getAttribute( Repository.class );
		EntityQueryExecutor executor = resolveEntityQueryExecutor( entityConfiguration, repository );
		if ( executor != null ) {
			entityConfiguration.setAttribute( EntityQueryExecutor.class, executor );
		}
	}

	// hard-couple with spring data repository?
	EntityQueryExecutor resolveEntityQueryExecutor( MutableEntityConfiguration entityConfiguration, Repository repository );
}
