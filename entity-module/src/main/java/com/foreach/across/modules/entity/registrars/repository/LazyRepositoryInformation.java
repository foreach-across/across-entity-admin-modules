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

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.support.Repositories;
import org.springframework.data.repository.support.RepositoryInvoker;
import org.springframework.data.repository.support.RepositoryInvokerFactory;

/***
 * Potentially creates a lazy proxy to wrap around {@link RepositoryInvoker} and {@link Repository} to delay
 * the actual instantiation of these classes. This only happens when {@code across.development.active} is {@code true}.
 *
 * @author Marc Vanbrabant
 * @since 4.1.0
 */
@AllArgsConstructor
@Getter
public class LazyRepositoryInformation
{
	private final boolean acrossDevelopmentModeIsActive;
	private final ClassLoader classLoader;
	private final Repositories repositories;
	private final RepositoryFactoryInformation repositoryFactoryInformation;
	private final Class<?> entityType;
	private final RepositoryInvokerFactory repositoryInvokerFactory;

	public RepositoryInvoker getRepositoryInvoker() {
		if ( !acrossDevelopmentModeIsActive ) {
			return repositoryInvokerFactory.getInvokerFor( entityType );
		}
		TargetSource ts = new TargetSource()
		{
			@Override
			public Class<?> getTargetClass() {
				return repositoryFactoryInformation.getRepositoryInformation().getRepositoryBaseClass();
			}

			@Override
			public boolean isStatic() {
				return false;
			}

			@Override
			public Object getTarget() {
				return repositoryInvokerFactory.getInvokerFor( entityType );
			}

			@Override
			public void releaseTarget( Object target ) {
			}
		};

		ProxyFactory pf = new ProxyFactory();
		pf.setTargetSource( ts );
		Class<?> dependencyType = RepositoryInvoker.class;
		if ( dependencyType.isInterface() ) {
			pf.addInterface( dependencyType );
		}
		return (RepositoryInvoker) pf.getProxy( classLoader );
	}

	public Repository getRepository() {
		if ( !acrossDevelopmentModeIsActive ) {
			return (Repository) repositories.getRepositoryFor( entityType ).get();
		}
		TargetSource ts = new TargetSource()
		{
			@Override
			public Class<?> getTargetClass() {
				return repositoryFactoryInformation.getRepositoryInformation().getRepositoryBaseClass();
			}

			@Override
			public boolean isStatic() {
				return false;
			}

			@Override
			public Object getTarget() {
				return repositories.getRepositoryFor( entityType ).get();
			}

			@Override
			public void releaseTarget( Object target ) {
			}
		};

		ProxyFactory pf = new ProxyFactory();
		pf.setTargetSource( ts );
		Class<?> dependencyType = repositoryFactoryInformation.getRepositoryInformation().getRepositoryInterface();
		if ( dependencyType.isInterface() ) {
			pf.addInterface( dependencyType );
		}
		return (Repository) pf.getProxy( classLoader );
	}

}
