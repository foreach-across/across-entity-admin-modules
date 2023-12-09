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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import lombok.experimental.UtilityClass;

import java.util.function.Consumer;

/**
 * Contains some common factory/registrar methods
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@UtilityClass
public class EntityViewElementPostProcessors
{
	/**
	 * Registers a set of default post processors to apply when rendering a control or write mode form group
	 * for a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
	 *
	 * @return element builder configuration consumer
	 * @see PropertyPlaceholderTextPostProcessor
	 */
	public static <V extends ViewElementBuilderSupport<?, ?>> Consumer<V> forPropertyControl() {
		return builder -> {
			builder.postProcessor( new PropertyPlaceholderTextPostProcessor<>() );
			builder.postProcessor( new RequiredControlPostProcessor<>() );
		};
	}
}
