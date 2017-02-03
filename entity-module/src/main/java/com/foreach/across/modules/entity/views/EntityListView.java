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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.ModelMap;

/**
 * Model for a basic list view.
 *
 * @see EntityListViewFactory
 */
@Deprecated
public class EntityListView extends EntityView
{
	public static final String SUMMARY_VIEW_NAME = "listSummaryView";

	public static final String VIEW_NAME = "listView";
	// todo remove
	public static final String VIEW_TEMPLATE = PageContentStructure.TEMPLATE;

	public static final String ATTRIBUTE_PAGEABLE = "pageable";
	public static final String ATTRIBUTE_PAGE = "page";

	public EntityListView( ModelMap model ) {
		super( model );
	}

	public Pageable getPageable() {
		return getAttribute( ATTRIBUTE_PAGEABLE, Pageable.class );
	}

	public void setPageable( Pageable pageable ) {
		addAttribute( ATTRIBUTE_PAGEABLE, pageable );
	}

	public Page getPage() {
		return getAttribute( ATTRIBUTE_PAGE, Page.class );
	}

	public void setPage( Page page ) {
		addAttribute( ATTRIBUTE_PAGE, page );
	}
}
