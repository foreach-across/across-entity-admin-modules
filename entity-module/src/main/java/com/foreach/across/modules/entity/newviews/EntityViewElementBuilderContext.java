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
package com.foreach.across.modules.entity.newviews;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.support.AttributeSupport;

/**
 * todo: should delegate to the backing entity view instead
 * @author Arne Vandamme
 */
public class EntityViewElementBuilderContext<T extends EntityView> extends AttributeSupport implements ViewElementBuilderContext
{
	public static final String ENTITY = "entity";

	private final T entityView;

	public EntityViewElementBuilderContext( T view ) {
		this.entityView = view;

		addAttribute( ENTITY, view.getEntity() );
	}

	public T getEntityView() {
		return entityView;
	}
}
