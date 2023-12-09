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

package com.foreach.across.modules.entity.registrars.repository.entityqueryexecutor;

import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.jpa.EntityQueryJpaExecutor;
import com.foreach.across.modules.entity.registrars.repository.EntityQueryExecutorRegistrar;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

@Order(1_000_000)
@Component
@ConditionalOnClass(JpaSpecificationExecutor.class)
public class EntityQueryJpaExecutorRegistrar implements EntityQueryExecutorRegistrar
{
	@Override
	public boolean supports( EntityConfiguration entityConfiguration, Repository repository ) {
		return repository instanceof JpaSpecificationExecutor;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EntityQueryExecutor resolveEntityQueryExecutor( MutableEntityConfiguration entityConfiguration, Repository repository ) {
		return new EntityQueryJpaExecutor( (JpaSpecificationExecutor) repository );
	}
}
