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
package com.foreach.across.modules.entity.query.jpa;

import com.foreach.across.modules.entity.query.AbstractEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.foreach.across.modules.entity.query.jpa.EntityQueryJpaUtils.toSpecification;

/**
 * Implementation of {@link EntityQueryExecutor} that runs against a {@link JpaSpecificationExecutor} instance.
 *
 * @author Arne Vandamme
 */
public class EntityQueryJpaExecutor<T> extends AbstractEntityQueryExecutor<T>
{
	private final JpaSpecificationExecutor<T> jpaSpecificationExecutor;


	public EntityQueryJpaExecutor( JpaSpecificationExecutor<T> jpaSpecificationExecutor ) {
		this.jpaSpecificationExecutor = jpaSpecificationExecutor;
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query ) {
		return jpaSpecificationExecutor.findAll( toSpecification( query ) );
	}

	@Override
	protected Iterable<T> executeQuery( EntityQuery query, Sort sort ) {
		return jpaSpecificationExecutor.findAll( toSpecification( query ), sort );
	}

	@Override
	@SneakyThrows
	protected Page<T> executeQuery( EntityQuery query, Pageable pageable ) {
		InvocationHandler invocationHandler = Proxy.getInvocationHandler( jpaSpecificationExecutor );
		Object[] arguments = ( (EntityQueryCondition) query.getExpressions().get( 0 ) ).getArguments();

		Method method = (Method) arguments[0];
		Object[] args = new Object[arguments.length];
		System.arraycopy( arguments, 1, args, 0, arguments.length - 1 );
		args[args.length - 1] = pageable;

		return (Page<T>) invocationHandler.invoke( jpaSpecificationExecutor, method, args );
	}
}
