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

import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.Setter;
import org.springframework.data.domain.Sort;

/**
 * Responsible for creating a list view page layout for an entity type. Initializes the {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}
 * with a default {@link org.springframework.data.domain.Pageable}.  The {@link #setDefaultSort(Sort)} and {@link #setPageSize(int)} properties determine
 * the values for a newly initialized {@link org.springframework.data.domain.Pageable}.  These can be replaced during data binding.
 * <p/>
 * Will add a basic form on top of the list that can be used for adding filters or action buttons.
 * If {@link #setAddDefaultButtons(boolean)} is {@code true}, the {@link AllowableActions} for the entity type will be inspected and a <strong>create</strong>
 * button will be added if the authenticated principal has the {@link AllowableAction#CREATE} action.
 * <p/>
 * This processor <u>does not fetch the actual items for rendering</u>.
 * <p/>
 * Mostly used together with a {@link SortableTableRenderingViewProcessor} that executes <strong>after</strong> the {@link ListViewProcessor}.
 *
 * @author Arne Vandamme
 * @see SortableTableRenderingViewProcessor
 * @since 2.0.0
 */
public class ListViewProcessor
{
	/**
	 * Should the default create button be added if the principal has the  {@link AllowableAction#CREATE} action?
	 */
	@Setter
	private boolean addDefaultButtons;

	/**
	 * The default sort that should be applied to a newly initialized {@link org.springframework.data.domain.Pageable}.
	 */
	@Setter
	private Sort defaultSort;

	/**
	 * Set the page size to apply to a newly initialized {@link org.springframework.data.domain.Pageable}.
	 */
	@Setter
	private int pageSize;
}
