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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;

/**
 * Responsible for checking if the view is actually allowed.  Requires the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
 * or {@link com.foreach.across.modules.entity.registry.EntityAssociation} to be visible, and the principal to have the {@link AllowableAction}.
 * <p/>
 * If no {@link AllowableAction} is configured, only the visibility check for the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
 * or {@link com.foreach.across.modules.entity.registry.EntityAssociation} will be done.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
public final class ActionAllowedAuthorizationViewProcessor extends SimpleEntityViewProcessorAdapter
{
	/**
	 * -- SETTER --
	 * Set the {@link AllowableAction} that the principal should have on the {@link com.foreach.across.modules.entity.registry.EntityConfiguration}
	 * being viewed.  This will take the entity instance into account if there is one.
	 */
	@Setter
	@Getter
	private AllowableAction requiredAllowableAction;

	@Override
	public void authorizeRequest( EntityViewRequest entityViewRequest ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

		if ( entityViewContext.isForAssociation() ) {
			EntityAssociation association = entityViewContext.getEntityAssociation();
			if ( association.isHidden() ) {
				LOG.warn(
						"Refusing view {} for association {} and source entity {} because the EntityAssociation is hidden",
						entityViewRequest.getViewName(),
						association.getName(),
						association.getSourceEntityConfiguration().getName() );
				throw new AccessDeniedException( "Not allowed to access this entity association." );
			}
		}
		else {
			EntityConfiguration configuration = entityViewContext.getEntityConfiguration();
			if ( configuration.isHidden() ) {
				LOG.warn( "Refusing view {} for configuration {} because the the EntityConfiguration is hidden",
				          entityViewRequest.getViewName(),
				          configuration.getName() );
				throw new AccessDeniedException( "Not allowed to access this entity configuration." );
			}
		}

		if ( requiredAllowableAction != null && !entityViewContext.getAllowableActions().contains( requiredAllowableAction ) ) {
			throw new AccessDeniedException( "Action " + requiredAllowableAction.getId() + " for entity is not allowed" );
		}
	}
}
