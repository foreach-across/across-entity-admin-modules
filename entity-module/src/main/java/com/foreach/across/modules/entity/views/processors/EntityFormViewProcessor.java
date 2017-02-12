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

import com.foreach.across.modules.bootstrapui.elements.Grid;
import lombok.Setter;

/**
 * Creates the form based layout for an entity.  Will create a column form based structure for the body and will replace the default container
 * by the left form column.  The page title will be set for the selected entity, or the "create new" title will be used if no entity set.
 * The form itself will have no default action set, and will always submit to the current page.
 * <p/>
 * If {@link #setAddDefaultButtons(boolean)} is {@code true}, a default save and cancel button will be added at the bottom of the form.
 * The cancel button will return to the entity overview page, unless a specific <strong>from</strong> request attribute is present.
 * <p/>
 * If {@link #setAddGlobalBindingErrors(boolean)} is {@code true} (default), any global errors on the {@link org.springframework.validation.BindingResult}
 * will be added above the two form columns.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityFormViewProcessor
{
	/**
	 * Should default save and cancel buttons be added at the bottom of the form, below the column structure?  Defaults to {@code false}.
	 */
	@Setter
	private boolean addDefaultButtons;

	/**
	 * Should global errors present on the {@link org.springframework.validation.BindingResult} be added on top of the form?  Defaults to {@code true}.
	 */
	@Setter
	private boolean addGlobalBindingErrors = true;

	/**
	 * Grid for the form (represents the columns the form should have).  Defaults to 2 columns of the same width.
	 * Use for example <em>Grid.create( Grid.Width.FULL )</em> to get a single full with column.
	 * Every column container will be a {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement} with the name
	 * <strong>entityForm-column-COLUMN_NUMBER</strong>.
	 */
	@Setter
	private Grid grid = Grid.create(
			Grid.position( Grid.Device.MD.width( Grid.Width.HALF ) ),
			Grid.position( Grid.Device.MD.width( Grid.Width.HALF ) )
	);
}
