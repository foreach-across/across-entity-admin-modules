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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.support.PageableExecutionUtils;

import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Adapter for {@link com.foreach.across.modules.entity.views.EntityViewProcessor} that fetches an {@link Iterable}
 * of entity items and registers the result as {@link #attributeName}.
 * <p/>
 * Expects a {@link org.springframework.data.domain.Pageable} extension to be present on the
 * {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}.  This can be bound from the request by a {@link PageableExtensionViewProcessor}.
 * The {@link org.springframework.data.domain.Pageable} will be used for fetching the items, if it is not present no items will be fetched.
 * Often a {@link SortableTableRenderingViewProcessor} will perform the actual rendering of the items.
 * <p/>
 * Fetching the items happens in the {@link #doControl(EntityViewRequest, EntityView, EntityViewCommand)} phase.
 *
 * @author Arne Vandamme
 * @see PageableExtensionViewProcessor
 * @see SortableTableRenderingViewProcessor
 * @see DelegatingEntityFetchingViewProcessor
 * @see DefaultEntityFetchingViewProcessor
 * @since 2.0.0
 */
public abstract class AbstractEntityFetchingViewProcessor extends SimpleEntityViewProcessorAdapter
{
	public static final String DEFAULT_ATTRIBUTE_NAME = "items";

	/**
	 * Name of the attribute under which the resulting items should be registered.
	 */
	@Setter
	private String attributeName = DEFAULT_ATTRIBUTE_NAME;

	/**
	 * Name of the extension that represents the {@link org.springframework.data.domain.Pageable} that
	 * should be used for fetching the items.
	 */
	@Setter
	private String pageableExtensionName = PageableExtensionViewProcessor.DEFAULT_EXTENSION_NAME;

	/**
	 * Should an existing attribute {@link #attributeName} be replaced or not.  If {@code false},
	 * the items will only be fetched if there is no such attribute yet.
	 */
	@Setter
	private boolean replaceExistingAttribute;

	/**
	 * The {@link AllowableAction} that is required for an item to be viewable.
	 */
	@Setter
	protected AllowableAction showOnlyItemsWithAction;

	@Override
	public final void doControl( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command ) {
		if ( !entityView.containsAttribute( attributeName ) || replaceExistingAttribute ) {
			Pageable pageable = command.getExtension( pageableExtensionName, Pageable.class );

			if ( pageable != null ) {
				entityView.addAttribute( attributeName, fetchItemsAndFilter( entityViewRequest, entityView, pageable ) );
			}
		}
	}

	/**
	 * Fetches all items for a specified {@link Pageable} request.
	 *
	 * @param entityViewRequest current request
	 * @param entityView        current view
	 * @param pageable          that is requested
	 * @return an {@link Iterable} holding the items.
	 */
	protected abstract Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable );

	/**
	 * Fetches all items for a specified {@link Sort}.
	 *
	 * @param entityViewRequest current request
	 * @param entityView        current view
	 * @param sort              that is requested
	 * @return an {@link Iterable} holding the items.
	 */
	protected abstract Iterable fetchItems( EntityViewRequest entityViewRequest, EntityView entityView, Sort sort );

	private Iterable fetchItemsAndFilter( EntityViewRequest entityViewRequest, EntityView entityView, Pageable pageable ) {
		if ( showOnlyItemsWithAction != null ) {
			EntityConfiguration entityConfiguration = entityViewRequest.getEntityViewContext().getEntityConfiguration();
			return filterAccessibleItems( fetchItems( entityViewRequest, entityView, pageable.getSort() ),
			                              entityConfiguration,
			                              pageable );
		}
		return fetchItems( entityViewRequest, entityView, pageable );
	}

	@SuppressWarnings("unchecked")
	private Iterable filterAccessibleItems( Iterable entities, EntityConfiguration configuration, Pageable pageable ) {
		Iterable result = entities;
		if ( showOnlyItemsWithAction != null ) {
			result = (List) StreamSupport.stream( entities.spliterator(), false )
			                             .filter( entity -> configuration.getAllowableActions( entity ).contains( showOnlyItemsWithAction ) )
			                             .collect( Collectors.toList() );
			if ( pageable != null ) {
				result = buildPage( result, pageable );
			}
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private Page buildPage( Iterable allItems, Pageable pageable ) {
		Spliterator spliterator = allItems.spliterator();
		long estimatedSize = spliterator.estimateSize();

		List content = (List) StreamSupport.stream( spliterator, false )
		                                   .skip( pageable.getOffset() )
		                                   .limit( pageable.getPageSize() )
		                                   .collect( Collectors.toList() );

		return PageableExecutionUtils.getPage( content, pageable, () -> estimatedSize );
	}
}
