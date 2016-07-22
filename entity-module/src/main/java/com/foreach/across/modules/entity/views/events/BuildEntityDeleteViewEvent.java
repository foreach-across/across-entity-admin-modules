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

package com.foreach.across.modules.entity.views.events;

import com.foreach.across.core.events.ParameterizedAcrossEvent;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.core.ResolvableType;

/**
 * Event published by the {@link com.foreach.across.modules.entity.views.EntityDeleteViewFactory} before building
 * the actual view.  Allows basic configuration of the delete form: customizing feedback messages, showing related
 * entities and suppressing the actual ability to delete the entity.  The delete view can be seen as the confirmation
 * page (are you sure you want to?) before the actual delete.
 * <p/>
 * For example: In case of related entities that will not automatically be deleted beforehand,
 * {@link #setDeleteDisabled(boolean)} can be used to disallow the delete action until those related entities have
 * been deleted manually.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class BuildEntityDeleteViewEvent<T> implements ParameterizedAcrossEvent
{
	private final T entity;
	private final EntityViewElementBuilderContext<?> builderContext;

	private boolean deleteDisabled;
	private ContainerViewElement messages, associations;

	public BuildEntityDeleteViewEvent( T entity, EntityViewElementBuilderContext<?> builderContext ) {
		this.entity = entity;
		this.builderContext = builderContext;
	}

	/**
	 * @return container with feedback messages
	 */
	public ContainerViewElement messages() {
		return messages;
	}

	/**
	 * Set the container containing message elements.
	 */
	public void setMessages( ContainerViewElement messages ) {
		this.messages = messages;
	}

	/**
	 * @return list with the association feedback items
	 */
	public ContainerViewElement associations() {
		return associations;
	}

	public void setAssociations( ContainerViewElement associations ) {
		this.associations = associations;
	}

	@Override
	public ResolvableType[] getEventGenericTypes() {
		return new ResolvableType[] { ResolvableType.forInstance( entity ) };
	}

	/**
	 * @return the actual entity for which the delete view is being rendered
	 */
	public T getEntity() {
		return entity;
	}

	/**
	 * @return the current builder context
	 */
	public EntityViewElementBuilderContext<?> getBuilderContext() {
		return builderContext;
	}

	/**
	 * @return {@code true} if the actual delete action is not allowed
	 */
	public boolean isDeleteDisabled() {
		return deleteDisabled;
	}

	/**
	 * Should the delete action be disallowed.  If set to {@code true} the delete view
	 * will not render the actual delete button.
	 *
	 * @param deleteDisabled {@code true} if deleting should not be allowed
	 */
	public void setDeleteDisabled( boolean deleteDisabled ) {
		this.deleteDisabled = deleteDisabled;
	}
}
