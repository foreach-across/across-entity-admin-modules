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

package com.foreach.across.modules.entity.config.icons;

import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;

public class EntityModuleListViewIcons
{
	public static final String LINK_DELETE = "listView-link-delete";
	public static final String LINK_DETAIL = "listView-link-detail";
	public static final String LINK_EDIT = "listView-link-edit";

	public static final String ENTITY_QUERY_SEARCH = "entity-query-search";

	public static final String PREVIOUS_PAGE = "previous-page";
	public static final String NEXT_PAGE = "next-page";

	public AbstractNodeViewElement search() {
		return IconSet.iconSet( EntityModule.NAME ).icon( ENTITY_QUERY_SEARCH );
	}

	public AbstractNodeViewElement linkToDeleteView() {
		return IconSet.iconSet( EntityModule.NAME ).icon( LINK_DELETE );
	}

	public AbstractNodeViewElement linkToDetailView() {
		return IconSet.iconSet( EntityModule.NAME ).icon( LINK_DETAIL );
	}

	public AbstractNodeViewElement linkToEditView() {
		return IconSet.iconSet( EntityModule.NAME ).icon( LINK_EDIT );
	}

	public AbstractNodeViewElement nextPage() {
		return IconSet.iconSet( EntityModule.NAME ).icon( ENTITY_QUERY_SEARCH );
	}

	public AbstractNodeViewElement previousPage() {
		return IconSet.iconSet( EntityModule.NAME ).icon( ENTITY_QUERY_SEARCH );
	}
}
