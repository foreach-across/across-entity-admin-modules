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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.core.events.NamedAcrossEvent;
import com.foreach.across.core.events.ParameterizedAcrossEvent;
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;

/**
 * Published by the {@link com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor} during
 * post render after the page structure for a single entity has been rendered.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.processors.SingleEntityPageStructureViewProcessor
 * @since 2.0.0
 */
@RequiredArgsConstructor
public final class EntityPageStructureRenderedEvent<T> implements NamedAcrossEvent, ParameterizedAcrossEvent
{
	/**
	 * The original entity view request.
	 */
	@Getter
	private final EntityViewRequest entityViewRequest;

	/**
	 * The view being generated.
	 */
	@Getter
	private final EntityView entityView;

	/**
	 * The actual {@link EntityViewContext} that was used for generation of the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure}.
	 * Note this might be a different instance than {@link #entityViewRequest#getEntityViewContext} as it might be the parent context.
	 */
	@Getter
	private final EntityViewContext entityViewContext;

	/**
	 * The builder context that was used to generated the view elements.
	 */
	@Getter
	private final ViewElementBuilderContext builderContext;

	@Override
	public String getEventName() {
		return entityViewContext.getEntityConfiguration().getName();
	}

	@Override
	public ResolvableType[] getEventGenericTypes() {
		return new ResolvableType[] { ResolvableType.forClass( entityViewContext.getEntityConfiguration().getEntityType() ) };
	}

	public PageContentStructure getPageContentStructure() {
		return entityViewRequest.getPageContentStructure();
	}

	public boolean holdsEntity() {
		return entityViewContext.holdsEntity();
	}

	@SuppressWarnings("unchecked")
	public T getEntity() {
		return (T) entityViewContext.getEntity( Object.class );
	}
}
