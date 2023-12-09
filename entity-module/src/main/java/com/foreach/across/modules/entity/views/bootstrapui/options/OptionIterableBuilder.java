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

package com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

/**
 * Interface for delegating building of {@link OptionFormElementBuilder} items until
 * a {@link ViewElementBuilderContext} is available.  Responsible for creating an {@link Iterable}.
 * <p/>
 * Implementations should set the {@link OptionFormElementBuilder#rawValue(Object)} if they want auto-selection of options to work.
 *
 * @author Arne Vandamme
 * @see OptionGenerator
 * @see FixedOptionIterableBuilder
 * @see EnumOptionIterableBuilder
 * @see EntityQueryOptionIterableBuilder
 */
public interface OptionIterableBuilder
{
	/**
	 * Generates a list of option items.
	 *
	 * @param builderContext context information
	 * @return iterable with the options
	 */
	Iterable<OptionFormElementBuilder> buildOptions( ViewElementBuilderContext builderContext );

	/**
	 * Return sorting information, this will determine if the {@link OptionGenerator}
	 * will sort them by name (if default sorting configuration applies).
	 *
	 * @return true if the options built should be considered as "sorted"
	 */
	default boolean isSorted() {
		return false;
	}
}
