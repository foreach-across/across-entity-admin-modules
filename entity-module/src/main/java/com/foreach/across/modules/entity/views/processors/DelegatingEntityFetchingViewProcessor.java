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

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;
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
public class DelegatingEntityFetchingViewProcessor extends AbstractEntityFetchingViewProcessor
{
	/**
	 * Default order that this processor will have if it has been added via the {@link EntityListViewFactoryBuilder}.
	 */
	public static final int DEFAULT_ORDER = DefaultEntityFetchingViewProcessor.DEFAULT_ORDER - 100;

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
	 * Set the function that should be called for fetching the items.  Allows a BiFunction that has access to the actual view context.
	 */
	public void setDelegate( BiFunction<EntityViewContext, Pageable, Iterable<?>> delegate ) {
		this.delegate = delegate;
	}

	/**
	 * Fetches items for a single page with {@link Integer#MAX_VALUE} values based on the given sort.
	 *
	 * @see #fetchItems(EntityViewRequest, EntityView, Pageable).
	 */
	@Override
	protected Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Sort sort ) {
		return fetchItems( entityViewRequest, entityView, new PageRequest( 0, Integer.MAX_VALUE, sort ) );
	}

	/**
	 * Fetches items for a given page request.
	 *
	 * @return an {@link Iterable} containing the resulting items.
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable ) {
		if ( delegate == null ) {
			throw new IllegalStateException( "No delegate function has been configured" );
		}

		return delegate.apply( entityViewRequest.getEntityViewContext(), pageable );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		DelegatingEntityFetchingViewProcessor that = (DelegatingEntityFetchingViewProcessor) o;
		return Objects.equals( delegate, that.delegate );
	}

	@Override
	public int hashCode() {
		return Objects.hash( delegate );
	}
}
