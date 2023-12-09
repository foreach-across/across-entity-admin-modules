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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableActions;

/**
 * Holds the properties of a current entity or entity configuration being viewed.
 * There are 4 distinct possibilities:
 * <ul>
 * <li>{@link com.foreach.across.modules.entity.registry.EntityConfiguration} without an actual entity</li>
 * <li>{@link com.foreach.across.modules.entity.registry.EntityConfiguration} with an existing entity</li>
 * <li>{@link com.foreach.across.modules.entity.registry.EntityAssociation} without an actual entity</li>
 * <li>{@link com.foreach.across.modules.entity.registry.EntityAssociation} with an actual entity</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public interface EntityViewContext
{
	/**
	 * Get the current entity being viewed as raw object.
	 *
	 * @return entity or {@code null} if none present
	 */
	Object getEntity();

	/**
	 * Get the current entity being viewed.
	 *
	 * @param <T> type the entity should have
	 * @return entity or {@code null} if none present
	 */
	<T> T getEntity( Class<T> entityType );

	/**
	 * @return the label for the current entity being viewed
	 */
	String getEntityLabel();

	/**
	 * @return the current entity configuration being viewed
	 */
	EntityConfiguration getEntityConfiguration();

	/**
	 * @return the model to use for the current entity
	 */
	EntityModel getEntityModel();

	/**
	 * @return link builder for the entity
	 */
	EntityViewLinkBuilder getLinkBuilder();

	/**
	 * @return message code resolver for the current entity
	 */
	EntityMessageCodeResolver getMessageCodeResolver();

	/**
	 * @return generic {@link EntityMessages} for common views
	 */
	EntityMessages getEntityMessages();

	/**
	 * @return the {@link EntityPropertyRegistry} that should be used for this view
	 */
	EntityPropertyRegistry getPropertyRegistry();

	/**
	 * @return the allowable actions for this entity view, will either return the allowable actions
	 * for the specific entity, or for the general entity configuration if no entity is set
	 */
	AllowableActions getAllowableActions();

	/**
	 * @return {@code true} if an existing entity is present
	 */
	boolean holdsEntity();

	/**
	 * @return true if an association is being viewed, if so both {@link #getParentContext()}
	 * and {@link #getEntityAssociation()} should return values
	 */
	boolean isForAssociation();

	/**
	 * @return association that is being viewed
	 */
	EntityAssociation getEntityAssociation();

	/**
	 * If set the parent context will hold the parent entity this entity is associated to.
	 *
	 * @return parent context, can be {@code null} if {@link #isForAssociation()} returns {@code false}
	 */
	EntityViewContext getParentContext();
}
