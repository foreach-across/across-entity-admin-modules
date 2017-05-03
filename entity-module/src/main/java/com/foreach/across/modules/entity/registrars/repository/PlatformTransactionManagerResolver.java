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

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Attempts to resolve the {@link org.springframework.transaction.PlatformTransactionManager} that a specific
 * {@link org.springframework.data.repository.Repository} is using.
 * Does so by introspecting {@link TransactionalRepositoryFactoryBeanSupport} and depends heavily on the Spring Data version being used
 * as it expects a certain internal class structure.  If the version is not compatible or security configuration applies, automatic
 * detection should fail and appropriate log messages should be written - but no exceptions should be thrown.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
@Component
class PlatformTransactionManagerResolver
{
	private final Field transactionManagerNameField;
	private final Field transactionsEnabledField;

	PlatformTransactionManagerResolver() {
		Field transactionManagerNameFieldHolder;
		Field transactionsEnabledFieldHolder;

		try {
			transactionManagerNameFieldHolder = ReflectionUtils.findField( TransactionalRepositoryFactoryBeanSupport.class, "transactionManagerName" );
			if ( transactionManagerNameFieldHolder != null ) {
				transactionManagerNameFieldHolder.setAccessible( true );
			}
			else {
				LOG.warn(
						"Field {} could not be reflected on TransactionalRepositoryFactoryBeanSupport. " +
								"It will be impossible to determine the PlatformTransactionManager bean name. " +
								"This can be caused due to security constraints or an incompatible version of the Spring Data libraries.",
						"transactionManagerName"
				);
			}
			transactionsEnabledFieldHolder = ReflectionUtils.findField( TransactionalRepositoryFactoryBeanSupport.class, "enableDefaultTransactions" );
			if ( transactionsEnabledFieldHolder != null ) {
				transactionsEnabledFieldHolder.setAccessible( true );
			}
			else {
				LOG.warn(
						"Field {} could not be reflected on TransactionalRepositoryFactoryBeanSupport. " +
								"It will be impossible to determine the PlatformTransactionManager bean name. " +
								"This can be caused due to security constraints or an incompatible version of the Spring Data libraries.",
						"enableDefaultTransactions"
				);
			}
		}
		catch ( SecurityException ace ) {
			LOG.error(
					"Unable to resolve PlatformTransactionManager bean names for Spring Data repositories. " +
							"An exception has occurred. " +
							"This can be caused due to security constraints or an incompatible version of the Spring Data libraries.",
					ace
			);

			transactionManagerNameFieldHolder = null;
			transactionsEnabledFieldHolder = null;
		}

		transactionManagerNameField = transactionManagerNameFieldHolder;
		transactionsEnabledField = transactionsEnabledFieldHolder;
	}

	String resolveTransactionManagerBeanName( RepositoryFactoryInformation repositoryFactoryInformation ) {
		if ( repositoryFactoryInformation instanceof TransactionalRepositoryFactoryBeanSupport ) {
			if ( transactionsEnabledField != null && transactionManagerNameField != null ) {
				if ( Boolean.TRUE.equals( ReflectionUtils.getField( transactionsEnabledField, repositoryFactoryInformation ) ) ) {
					Object transactionManagerName = ReflectionUtils.getField( transactionManagerNameField, repositoryFactoryInformation );
					return transactionManagerName instanceof String ? (String) transactionManagerName : null;
				}
				else {
					LOG.trace( "Not registering PlatformTransactionManager bean name as default transaction are not enabled on {}",
					           repositoryFactoryInformation );
				}
			}
		}

		return null;
	}
}
