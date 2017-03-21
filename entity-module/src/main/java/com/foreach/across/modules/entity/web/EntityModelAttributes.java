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

package com.foreach.across.modules.entity.web;

/**
 * Contains common {@link org.springframework.ui.Model} or {@link com.foreach.across.modules.web.ui.ViewElementBuilderContext} attribute names.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public interface EntityModelAttributes
{
	/**
	 * When set, expected to hold the current entity that is being modified.
	 * This can be a modified entity instance (DTO).  If you want the original entity that is being
	 * edited, you should get it via the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}.
	 */
	String ENTITY = "entity";

	/**
	 * Expected to hold the current {@link com.foreach.across.modules.entity.views.request.EntityViewRequest}.
	 * Provides access to the current {@link com.foreach.across.modules.entity.views.context.EntityViewContext} and
	 * {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}.  These might also be directly available
	 * as {@link #VIEW_CONTEXT} and {@link #VIEW_COMMAND} respectively.
	 */
	String VIEW_REQUEST = "entityViewRequest";

	/**
	 * Expected to hold the current {@link com.foreach.across.modules.entity.views.request.EntityViewCommand}.
	 */
	String VIEW_COMMAND = "entityViewCommand";

	/**
	 * Expected to hold the current {@link com.foreach.across.modules.entity.views.context.EntityViewContext}.
	 */
	String VIEW_CONTEXT = "entityViewContext";
}
