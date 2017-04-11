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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.views.EntityViewProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.function.Consumer;

/**
 * Extension of {@link EntityViewProcessorRegistry} that supports an optional {@link org.springframework.transaction.support.TransactionTemplate}.
 * You can configure a {@link org.springframework.transaction.support.TransactionTemplate} on this registry, when using one of the new
 * dispatch methods ({@link #dispatch(Consumer, boolean)} and {@link #dispatch(Consumer, Class, boolean)}) you can specify if dispatching should
 * happen in a single transaction or not.
 * <p/>
 * By default no transaction will be used, and if no transaction template this registry behaves entirely as the default {@link EntityViewProcessorRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
public class TransactionalEntityViewProcessorRegistry extends EntityViewProcessorRegistry
{
	private TransactionTemplate transactionTemplate;

	/**
	 * Set the transaction template that should be used for transactional calls
	 *
	 * @param transactionTemplate to use - can be null if no transactions are every required
	 * @see #dispatch(Consumer, boolean)
	 * @see #dispatch(Consumer, Class, boolean)
	 */
	public void setTransactionTemplate( TransactionTemplate transactionTemplate ) {
		this.transactionTemplate = transactionTemplate;
	}

	/**
	 * Dispatch a consumer to all processors.  This will apply the consumer to all processors in their
	 * registration order.
	 *
	 * @param consumer       to apply
	 * @param useTransaction should the dispatching be wrapped in a single transaction (only if there is a transaction template set)
	 */
	public void dispatch( Consumer<EntityViewProcessor> consumer, boolean useTransaction ) {
		dispatch( consumer, EntityViewProcessor.class, useTransaction );
	}

	/**
	 * Dispatch a consumer to all processors that are of the specific type.
	 * This will apply to consumer to all processors that match the required type, in registration order.
	 *
	 * @param consumer       to apply
	 * @param processorType  the processors should have
	 * @param useTransaction should the dispatching be wrapped in a single transaction (only if there is a transaction template set)
	 * @param <U>            processor type
	 */
	public <U> void dispatch( Consumer<U> consumer, Class<U> processorType, boolean useTransaction ) {
		if ( useTransaction && transactionTemplate != null ) {
			transactionTemplate.execute( ( status ) -> {
				super.dispatch( consumer, processorType );
				return null;
			} );
		}
		else {
			super.dispatch( consumer, processorType );
		}
	}
}
