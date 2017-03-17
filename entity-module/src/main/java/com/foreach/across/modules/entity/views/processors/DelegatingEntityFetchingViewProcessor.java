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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.data.domain.Pageable;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Simple implementation of {@link AbstractEntityFetchingViewProcessor} that delegates to either a {@link java.util.function.Function} or
 * a {@link java.util.function.BiFunction} for getting the actual items.  The delegates loses the specific {@link EntityView} that is being rendered,
 * if you really require it you should implement {@link AbstractEntityFetchingViewProcessor} directly.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class DelegatingEntityFetchingViewProcessor extends AbstractEntityFetchingViewProcessor
{
	private BiFunction<EntityViewContext, Pageable, Iterable<?>> delegate;

	public DelegatingEntityFetchingViewProcessor() {
	}

	public DelegatingEntityFetchingViewProcessor( Function<Pageable, Iterable<?>> delegate ) {
		setDelegate( delegate );
	}

	public DelegatingEntityFetchingViewProcessor( BiFunction<EntityViewContext, Pageable, Iterable<?>> delegate ) {
		setDelegate( delegate );
	}

	/**
	 * Set the function that should be called for fetching the items.
	 */
	public void setDelegate( Function<Pageable, Iterable<?>> delegate ) {
		setDelegate( ( ctx, pageable ) -> delegate.apply( pageable ) );
	}

	/**
	 * Set the function that should be called for fetching the items.
	 */
	public void setDelegate( BiFunction<EntityViewContext, Pageable, Iterable<?>> delegate ) {
		this.delegate = delegate;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Iterable<Object> fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable ) {
		if ( delegate == null ) {
			throw new IllegalStateException( "No delegate function has been configured" );
		}

		return (Iterable<Object>) delegate.apply( entityViewRequest.getEntityViewContext(), pageable );
	}
}
