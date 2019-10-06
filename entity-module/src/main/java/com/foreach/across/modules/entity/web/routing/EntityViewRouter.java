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

import com.foreach.across.modules.entity.views.EntityView;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class EntityViewRouter
{
	public static final String URI_ENTITY_NAME = "{entityName}";
	public static final String URI_ENTITY_ID = "{entityId}";
	public static final String URI_ASSOCIATION_NAME = "{associationName}";
	public static final String URI_ASSOCIATED_ENTITY_ID = "{associatedEntityId}";
	public static final String URI_VIEW_NAME = "{viewName}";

	public static final String VAR_ENTITY_NAME = "entityName";
	public static final String VAR_ENTITY_ID = "entityId";
	public static final String VAR_ASSOCIATION_NAME = "associationName";
	public static final String VAR_ASSOCIATED_ENTITY_ID = "associatedEntityId";
	public static final String VAR_VIEW_NAME = "viewName";

	private Rule entityRoot;
	private Rule viewSegment;
	private Map<String, Rule> viewMappings;

	public Rule getRuleForView( String viewName ) {
		return viewMappings.getOrDefault( viewName, viewSegment );
	}

	public EntityViewRouterBuilder toBuilder() {
		return new EntityViewRouterBuilder( entityRoot.originalValue, viewSegment.originalValue, new LinkedHashMap<>( viewMappings ) );
	}

	public static EntityViewRouterBuilder builder() {
		return new EntityViewRouterBuilder()
				.entityRoot( "/" + EntityViewRouter.URI_ENTITY_NAME )
				.viewSegment( "?view={viewName}" )
				//.entitySegment("/{entityId}")
				//.associationRoot( "/associations/{associationName}", true )
				//.associatedEntitySegment("/{associatedEntityId")
				.routeView( EntityView.LIST_VIEW_NAME, "", true )
				.routeView( EntityView.CREATE_VIEW_NAME, "/create", true )
				//.routeView( EntityView.DELETE_VIEW_NAME, "/{entityId}/delete", true )
				// .routeAssociation( "x", "y", true)
				// .routeAssociationView( "x", "view", "path",true)
				;

	}

	@Getter
	@RequiredArgsConstructor
	@SuppressWarnings("WeakerAccess")
	@EqualsAndHashCode
	@ToString
	public static class Rule
	{
		private final String originalValue;
		private final String path;
		private final Map<String, Object> queryParams;
		private final boolean appendToUri;

		private static Rule of( String value, boolean appendToUri ) {
			if ( value != null ) {
				String[] segments = StringUtils.splitByWholeSeparatorPreserveAllTokens( value, "?" );
				return new Rule( value, segments.length > 0 ? segments[0] : "", buildQueryParams( segments.length > 1 ? segments[1] : null ), appendToUri );
			}
			return null;
		}

		private static Map<String, Object> buildQueryParams( String query ) {
			if ( StringUtils.isNotEmpty( query ) ) {
				Map<String, Object> queryParams = new LinkedHashMap<>();

				Stream.of( StringUtils.split( query, "&" ) )
				      .map( param -> StringUtils.split( param, "=" ) )
				      .forEach( paramParts -> queryParams.put( paramParts[0], paramParts.length > 1 ? paramParts[1] : null ) );

				return queryParams;
			}
			return Collections.emptyMap();
		}
	}

	@Accessors(chain = true, fluent = true)
	@NoArgsConstructor
	@AllArgsConstructor
	@Setter
	@EqualsAndHashCode
	public static class EntityViewRouterBuilder
	{
		/**
		 * Root path for the entity (usually also the overview page).
		 */
		private String entityRoot;

		/**
		 * Default segment that should be added to the uri when a view name is present.
		 * Unlike most other settings, this will not replace all URI components but append.
		 */
		private String viewSegment;

		@Setter(AccessLevel.NONE)
		private Map<String, Rule> viewMappings = new LinkedHashMap<>();

		public EntityViewRouterBuilder routeView( @NonNull String viewName, String uri, boolean appendToUri ) {
			viewMappings.put( viewName, Rule.of( uri, appendToUri ) );
			return this;
		}

		public EntityViewRouter build() {
			EntityViewRouter router = new EntityViewRouter();
			router.entityRoot = Rule.of( entityRoot, false );
			router.viewSegment = Rule.of( viewSegment, true );
			router.viewMappings = Collections.unmodifiableMap( viewMappings );

			return router;
		}
	}
}
