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
package com.foreach.across.modules.entity.registry;

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;

/**
 * Filtered view on an association between two {@link com.foreach.across.modules.entity.registry.EntityConfiguration}s.
 *
 * @author Arne Vandamme
 */
public interface EntityAssociation extends ReadableAttributes, EntityViewRegistry
{
	/**
	 * When the parent entity is requesting to be deleted, how should this association influence the delete process?
	 */
	enum ParentDeleteMode
	{
		IGNORE,         /* ignore this association */
		WARN,           /* check for associated entities but do not suppress deletion */
		SUPPRESS        /* check for associated entities and suppress deletion if there are any */
	}

	/**
	 * Determines if this entity is managed as a sub-entity of the parent (embedded), or simply a linked entity.
	 */
	enum Type
	{
		EMBEDDED,       /* manage this entity entirely through the parent */
		LINKED          /* manage through the entity itself - refer to it from the parent */
	}

	String getName();

	Class<?> getEntityType();

	/**
	 * @return type of association
	 */
	Type getAssociationType();

	/**
	 * @return True if the association should be hidden from administrative UI implementations.
	 */
	boolean isHidden();

	EntityConfiguration getSourceEntityConfiguration();

	EntityPropertyDescriptor getSourceProperty();

	EntityConfiguration getTargetEntityConfiguration();

	EntityPropertyDescriptor getTargetProperty();

	/**
	 * @return behaviour in case the parent entity will be deleted but associated entities still exist
	 */
	ParentDeleteMode getParentDeleteMode();
}
