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

package com.foreach.across.modules.entity.views.context;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ConfigurableEntityViewContext}.
 * Data bean that is also registered as a request-scoped proxy.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope(scopeName = "request", proxyMode = ScopedProxyMode.INTERFACES)
@Data
public class DefaultEntityViewContext implements ConfigurableEntityViewContext
{
	private Object entity;
	private EntityConfiguration entityConfiguration;
	private EntityLinkBuilder linkBuilder;
	private EntityMessageCodeResolver messageCodeResolver;
	private EntityMessages entityMessages;
	private EntityAssociation entityAssociation;
	private EntityViewContext parentContext;
	private EntityModel entityModel;
	private AllowableActions allowableActions;
	private EntityPropertyRegistry propertyRegistry;

	@Setter(AccessLevel.NONE)
	private transient String entityLabel;

	@Setter(AccessLevel.NONE)
	@Getter(AccessLevel.NONE)
	private transient AllowableActions cachedAllowableActions;

	@Override
	public void setEntityConfiguration( EntityConfiguration entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
		entityLabel = null;
		cachedAllowableActions = allowableActions;
	}

	@Override
	public void setEntity( Object entity ) {
		this.entity = entity;
		entityLabel = null;
		cachedAllowableActions = allowableActions;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getEntityLabel() {
		if ( entityLabel == null && entity != null && entityConfiguration != null ) {
			entityLabel = entityConfiguration.getLabel( entity );
		}

		return entityLabel;
	}

	@Override
	public <T> T getEntity( Class<T> entityType ) {
		return entityType.cast( entity );
	}

	@Override
	public boolean holdsEntity() {
		return entity != null;
	}

	@Override
	public boolean isForAssociation() {
		return entityAssociation != null;
	}

	@Override
	public void setAllowableActions( AllowableActions allowableActions ) {
		this.allowableActions = allowableActions;
		cachedAllowableActions = allowableActions;
	}

	@Override
	@SuppressWarnings("unchecked")
	public AllowableActions getAllowableActions() {
		if ( cachedAllowableActions == null && entityConfiguration != null ) {
			cachedAllowableActions = entity != null
					? entityConfiguration.getAllowableActions( entity )
					: entityConfiguration.getAllowableActions();
		}

		return cachedAllowableActions;
	}

	@Override
	public String toString() {
		return "DefaultEntityViewContext{" +
				"entityConfiguration=" + entityConfiguration +
				", parentContext=" + parentContext +
				'}';
	}
}
