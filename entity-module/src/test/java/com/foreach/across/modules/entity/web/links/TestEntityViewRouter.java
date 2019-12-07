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

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.routing.EntityViewRouter;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
class TestEntityViewRouter
{
	@Test
	void entityRoot() {
		assertRule(
				EntityViewRouter.builder().entityRoot( "/entities/{entityName}" ).build().getEntityRoot(),
				"/entities/{entityName}", "/entities/{entityName}", Collections.emptyMap(), false );

		assertRule(
				EntityViewRouter.builder().entityRoot( "?view={viewName}&do=1" ).build().getEntityRoot(),
				"?view={viewName}&do=1", "", ImmutableMap.of( "view", "{viewName}", "do", "1" ), false
		);

		assertRule(
				EntityViewRouter.builder().entityRoot( "/entities/{entityName}?view={viewName}" ).build().getEntityRoot(),
				"/entities/{entityName}?view={viewName}", "/entities/{entityName}", Collections.singletonMap( "view", "{viewName}" ), false
		);
	}

	@Test
	void viewSegment() {
		assertRule( EntityViewRouter.builder().build().getViewSegment(),
		            "?view={viewName}", "", Collections.singletonMap( "view", "{viewName}" ), true );

		assertRule( EntityViewRouter.builder().viewSegment( "/{viewName}" ).build().getViewSegment(),
		            "/{viewName}", "/{viewName}", Collections.emptyMap(), true );
	}

	@Test
	void ruleForView() {

	}

	@Test
	void defaultRouter() {
		EntityViewRouter router = EntityViewRouter.builder().build();
		assertThat( router.getEntityRoot().getOriginalValue() ).isEqualTo( "/{entityName}" );
		assertThat( router.getViewSegment().getOriginalValue() ).isEqualTo( "?view={viewName}" );
		assertRule( router.getRuleForView( EntityView.LIST_VIEW_NAME ), "", "", Collections.emptyMap(), true );
		assertRule( router.getRuleForView( EntityView.CREATE_VIEW_NAME ), "/create", "/create", Collections.emptyMap(), true );
		//"/associations/{associationName}"
		// > /user/10/groups/new
		//"/associations/{assocationName}/create"
		//router.getAssociationSegment()
		// single association view
		// generic association view
	}

	private void assertRule( EntityViewRouter.Rule rule, String originalValue, String path, Map<Object, Object> queryParams, boolean appendToUri ) {
		assertThat( rule.getOriginalValue() ).isEqualTo( originalValue );
		assertThat( rule.getPath() ).isEqualTo( path );
		assertThat( rule.getQueryParams() ).isEqualTo( queryParams );
		assertThat( rule.isAppendToUri() ).isEqualTo( appendToUri );
	}
}
