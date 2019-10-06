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

import com.foreach.across.core.annotations.PostRefresh;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Central service for routing to entity views.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
@Service
@RequiredArgsConstructor
public class EntityViewRouterMappings
{
	private final ObjectProvider<EntityRegistry> entityRegistry;
	private final ObjectProvider<EntityViewRouter> defaultEntityViewRouter;
	private final ObjectProvider<WebAppPathResolver> webAppPathResolver;

	/**
	 * Reload all router mappings. This will scan the entire entity registry for
	 * routers and convert them into mapping rules.
	 */
	@PostRefresh
	public void reloadMappings() {
		// fetch all routers
		// create router mappings
		// register entity name for each mapping
		// PATH
		// QUERYSTRING
		// Rule
		// entityName
		// number of vars

	}

	Collection<EntityViewRouterMapping> createRouterMappings( EntityViewRouter router ) {
		List<EntityViewRouterMapping> mappingsForRouter = new ArrayList<>();

		EntityViewRouter.Rule entityRoot = router.getEntityRoot();

		// create root mapping
		EntityViewRouter.Rule viewSegment = router.getViewSegment();
		mappingsForRouter.add( constructMapping( entityRoot.getPath(), viewSegment, null, null ) );

		router.getViewMappings().forEach( ( viewName, rule ) -> mappingsForRouter.add( constructMapping( entityRoot.getPath(), rule, viewName, null ) ) );

		return mappingsForRouter;
	}

	private EntityViewRouterMapping constructMapping( String root, EntityViewRouter.Rule rule, String viewName, String associationName ) {
		String path = buildPath( root, rule );

		// todo: better parsing, same var should only count once
		int variables = StringUtils.countMatches( path, "}" );

		for ( Map.Entry<String, Object> entry : rule.getQueryParams().entrySet() ) {
			if ( StringUtils.endsWith( entry.getKey(), "}" ) ) {
				variables++;
			}
			if ( StringUtils.endsWith( "" + entry.getValue(), "}" ) ) {
				variables++;
			}
		}

		return new EntityViewRouterMapping( rule, toRequestMatchingPath( path ), variables, viewName, associationName );
	}

	private String buildPath( String root, EntityViewRouter.Rule rule ) {
		if ( rule.isAppendToUri() ) {
			return root + rule.getPath();
		}
		return rule.getPath();
	}

	private String toRequestMatchingPath( String path ) {
		return StringUtils.replaceEach(
				path,
				new String[] { EntityViewRouter.URI_ENTITY_NAME },
				new String[] { "{entityName:.+}" }
		);
	}

	/**
	 * Checks if the servlet request matches against any of the registered router mappings.
	 *
	 * @param request to check
	 * @return match - if available
	 */
	public Optional<EntityViewRouterMatch> findMatch( @NonNull HttpServletRequest request ) {
		return Optional.empty();
	}

	@EqualsAndHashCode
	@RequiredArgsConstructor
	@ToString
	static class EntityViewRouterMapping
	{
		private final EntityViewRouter.Rule rule;
		private final String pathToMatch;
		private final int numberOfVariables;
		private final String viewName;
		private final String associationName;
	}
}
