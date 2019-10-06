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

package com.foreach.across.modules.entity.web.routing;

import com.foreach.across.modules.web.context.WebAppPathResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import java.util.Collection;

import static com.foreach.across.modules.entity.views.EntityView.CREATE_VIEW_NAME;
import static com.foreach.across.modules.entity.views.EntityView.LIST_VIEW_NAME;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
class TestEntityViewRouterMappings
{
	@Mock
	private WebAppPathResolver pathResolver;

	private EntityViewRouterMappings mappings;

	@BeforeEach
	void configureMappings( @Mock ObjectProvider<WebAppPathResolver> pathResolverProvider ) {
		mappings = new EntityViewRouterMappings( null, null, pathResolverProvider );
	}

	@Test
	void defaultRouterMappings() {
		EntityViewRouter router = EntityViewRouter.builder().build();
		Collection<EntityViewRouterMappings.EntityViewRouterMapping> routerMappings = mappings.createRouterMappings( router );

		assertThat( routerMappings )
				//.hasSize( 3 )
				.contains( routerMapping( "/{entityName:.+}", 2, router.getViewSegment(), null, null ) )
				.contains( routerMapping( "/{entityName:.+}", 1, router.getRuleForView( LIST_VIEW_NAME ), LIST_VIEW_NAME, null ) )
				.contains( routerMapping( "/{entityName:.+}/create", 1, router.getRuleForView( CREATE_VIEW_NAME ), CREATE_VIEW_NAME, null ) );
	}

	private EntityViewRouterMappings.EntityViewRouterMapping routerMapping( String pathToMatch, int numberOfVars, EntityViewRouter.Rule rule,
	                                                                        String viewName, String associationName ) {
		return new EntityViewRouterMappings.EntityViewRouterMapping( rule, pathToMatch, numberOfVars, viewName, associationName );
	}

	@Test
	void webAppPathResolverIsUsedForBuildingTheRequests() {

	}
}