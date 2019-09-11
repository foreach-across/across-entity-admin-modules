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

package com.foreach.across.modules.bootstrapui.components;

import com.foreach.across.modules.bootstrapui.components.builder.BreadcrumbNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken;
import com.foreach.across.modules.web.menu.Menu;

/**
 * Factory for more complex component builders.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 * @deprecated use the static {@link BootstrapUiBuildersBroken} instead
 */
@Deprecated
public interface BootstrapUiComponentFactoryBroken
{
	/**
	 * Returns a builder for rendering a {@link Menu} to a Bootstrap nav type unordered list.
	 *
	 * @param menu to render
	 * @return builder
	 */
	DefaultNavComponentBuilder nav( Menu menu );

	/**
	 * Returns a builder for rendering a {@link Menu} to a (sidebar) list of panels.
	 *
	 * @param menu to render
	 * @return builder
	 */
	PanelsNavComponentBuilder panels( Menu menu );

	/**
	 * Returns a builder for rendering the selected path to a Bootstrap breadcrumb list.
	 *
	 * @param menu to render
	 * @return builder
	 */
	BreadcrumbNavComponentBuilder breadcrumb( Menu menu );
}
