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

package com.foreach.across.modules.entity.views.support;

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.bootstrapui.util.PagingMessages;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.data.domain.Page;
import org.springframework.util.ObjectUtils;

/**
 * Base class for resolving common entity messages.
 *
 * @author Arne Vandamme
 */
public class EntityMessages implements PagingMessages
{
	public static final String ACTION_CREATE = "actions.create";
	public static final String ACTION_UPDATE = "actions.update";
	public static final String ACTION_DELETE = "actions.delete";
	public static final String ACTION_VIEW = "actions.view";

	public static final String PAGE_TITLE_LIST = "pageTitle.list";
	public static final String PAGE_TITLE_CREATE = "pageTitle.create";
	public static final String PAGE_TITLE_UPDATE = "pageTitle.update";
	public static final String PAGE_TITLE_DELETE = "pageTitle.delete";
	public static final String PAGE_TITLE_VIEW = "pageTitle.view";

	public static final String RESULTS_FOUND = "sortableTable.resultsFound";
	public static final String PAGER = "sortableTable.pager";
	public static final String PAGE = "sortableTable.pager.page";
	public static final String OF_PAGES = "sortableTable.pager.ofPages";
	public static final String NEXT_PAGE = "sortableTable.pager.nextPage";
	public static final String PREVIOUS_PAGE = "sortableTable.pager.previousPage";

	private final EntityMessageCodeResolver messageCodeResolver;

	public EntityMessages( EntityMessageCodeResolver messageCodeResolver ) {
		this.messageCodeResolver = messageCodeResolver;
	}

	public String createAction( Object... arguments ) {
		return withNameSingular( ACTION_CREATE, arguments );
	}

	public String updateAction( Object... arguments ) {
		return withNameSingular( ACTION_UPDATE, arguments );
	}

	public String deleteAction( Object... arguments ) {
		return withNameSingular( ACTION_DELETE, arguments );
	}

	public String viewAction( Object... arguments ) {
		return withNameSingular( ACTION_VIEW, arguments );
	}

	public String createPageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_CREATE, arguments );
	}

	public String updatePageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_UPDATE, arguments );
	}

	public String deletePageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_DELETE, arguments );
	}

	public String viewPageTitle( Object... arguments ) {
		return withNameSingular( PAGE_TITLE_VIEW, arguments );
	}

	@Override
	public String pagerText( Page currentPage, Object... args ) {
		return messageWithFallback( PAGER, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String page( Page currentPage, Object... args ) {
		return messageWithFallback( PAGE, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String ofPages( Page currentPage, Object... args ) {
		return messageWithFallback( OF_PAGES, currentPage.getNumber() + 1, currentPage.getTotalPages(), args );
	}

	@Override
	public String nextPage( Page currentPage, Object... args ) {
		return messageWithFallback( NEXT_PAGE, currentPage.getNumber() + 2, args );
	}

	@Override
	public String previousPage( Page currentPage, Object... args ) {
		return messageWithFallback( PREVIOUS_PAGE, currentPage.getNumber(), args );
	}

	@Override
	public String resultsFound( Page currentPage, Object... args ) {
		return messageWithFallback( RESULTS_FOUND,
		                            currentPage.getTotalElements(),
		                            messageCodeResolver.getNameSingularInline(),
		                            messageCodeResolver.getNamePluralInline(),
		                            args );
	}

	/**
	 * Will r
	 *
	 * @param code
	 * @param arguments
	 * @return
	 */
	public String withNameSingular( String code, Object... arguments ) {
		return messageWithFallback( code,
		                            messageCodeResolver.getNameSingular(),
		                            messageCodeResolver.getNameSingularInline(),
		                            arguments );
	}

	public String withNamePlural( String code, Object... arguments ) {
		return messageWithFallback( code,
		                            messageCodeResolver.getNamePlural(),
		                            messageCodeResolver.getNamePluralInline(),
		                            arguments );
	}

	/**
	 * Will return empty if message code could not be resolved.
	 */
	public String message( String code, Object... arguments ) {
		return messageCodeResolver.getMessage( code, arguments( arguments ), "" );
	}

	/**
	 * Will return empty if message code could not be resolved.
	 */
	public String messageWithFallback( String code, Object... arguments ) {
		return messageCodeResolver.getMessageWithFallback( code, arguments( arguments ), "" );
	}

	protected Object[] arguments( Object... candidates ) {
		if ( candidates.length > 0 ) {
			int last = candidates.length - 1;
			if ( ObjectUtils.isArray( candidates[last] ) ) {
				return ArrayUtils.addAll(
						ArrayUtils.subarray( candidates, 0, last ),
						(Object[]) candidates[last]
				);
			}
		}
		return candidates;
	}
}
