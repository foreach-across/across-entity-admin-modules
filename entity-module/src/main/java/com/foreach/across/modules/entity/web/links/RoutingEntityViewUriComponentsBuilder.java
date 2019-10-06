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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.web.routing.EntityViewRouter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.Map;

import static com.foreach.across.modules.entity.web.routing.EntityViewRouter.*;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@RequiredArgsConstructor
public class RoutingEntityViewUriComponentsBuilder implements EntityViewUriComponentsBuilder
{
	private final EntityViewRouter router;

	@Override
	public UriComponentsBuilder forEntityConfiguration( UriComponentsBuilder previousComponents,
	                                                    EntityConfiguration entityConfiguration,
	                                                    String viewName ) {
		UriComponentsBuilder uri = previousComponents.cloneBuilder();
		uri.uriVariables( Collections.singletonMap( VAR_ENTITY_NAME, entityConfiguration.getName() ) );
		applyRule( uri, router.getEntityRoot() );

		if ( !StringUtils.isBlank( viewName ) ) {
			uri.uriVariables( Collections.singletonMap( VAR_VIEW_NAME, viewName ) );

			Rule ruleForView = router.getRuleForView( viewName );

			if ( ruleForView != null ) {
				applyRule( uri, ruleForView );
			}
		}

		return uri;
	}

	@Override
	public UriComponentsBuilder forEntity( UriComponentsBuilder previousComponents,
	                                       Map<String, Object> uriVariables,
	                                       EntityConfiguration entityConfiguration,
	                                       String entityId,
	                                       String viewName ) {
		return null;
	}

	@Override
	public UriComponentsBuilder forEntityAssociation( UriComponentsBuilder previousComponents,
	                                                  Map<String, Object> uriVariables,
	                                                  EntityAssociation entityAssociation,
	                                                  String entityId,
	                                                  String associatedEntityId,
	                                                  String viewName ) {
		return null;
	}

	@Override
	public UriComponentsBuilder forAssociatedEntity( UriComponentsBuilder previousComponents,
	                                                 Map<String, Object> uriVariables,
	                                                 EntityAssociation entityAssociation,
	                                                 String entityId,
	                                                 String associatedEntityId,
	                                                 String viewName ) {
		return null;
	}

	private void applyRule( UriComponentsBuilder builder, Rule rule ) {
		if ( StringUtils.isNotEmpty( rule.getPath() ) ) {
			if ( rule.isAppendToUri() ) {
				builder.path( rule.getPath() );
			}
			else {
				builder.replacePath( rule.getPath() );
			}
		}

		rule.getQueryParams().forEach( builder::replaceQueryParam );
	}

	private void resetUriVariables( Map<String, Object> uriVariables ) {
		uriVariables.put( VAR_ENTITY_NAME, URI_ENTITY_NAME );
		uriVariables.put( VAR_ENTITY_ID, URI_ENTITY_ID );
		uriVariables.put( VAR_ASSOCIATION_NAME, URI_ASSOCIATION_NAME );
		uriVariables.put( VAR_ASSOCIATED_ENTITY_ID, URI_ASSOCIATED_ENTITY_ID );
		uriVariables.put( VAR_VIEW_NAME, URI_VIEW_NAME );
	}
}
