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

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Collections;
import java.util.Map;

/**
 * Represents a {@link javax.servlet.http.HttpServletRequest} matching a configured entity view router rule.
 * Allows resolving the required variables, provided they are available.
 *
 * @author Arne Vandamme
 * @since 4.0.0
 */
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EntityViewRouterMatch
{
	private final String entityName;
	private final String viewName;
	private final String associationName;

	@Builder.Default
	private final Map<String, String> routeVariables = Collections.emptyMap();

	/**
	 * @return entity name or exception if not present
	 */
	@org.springframework.lang.NonNull
	public String extractEntityName() {
		if ( entityName != null ) {
			return entityName;
		}
		String entityNameFromVars = routeVariables.get( EntityViewRouter.VAR_ENTITY_NAME );

		if ( entityNameFromVars == null ) {
			throw new IllegalStateException( "Unable to resolve entityName from request - your mapping must have a {entityName} variable" );
		}

		return entityNameFromVars;
	}
}
