/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.elements.icons;

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;

import javax.validation.constraints.NotNull;

/**
 * An immutable IconSet that is registered in the {@link IconSetRegistry} and that represents a collection of icons.
 * You can get a registered {@link IconSet} using {@link IconSet#iconSet(String)}
 */
public interface IconSet
{
	/**
	 * Returns an icon with from the current {@link SimpleIconSet}. If no iconResolver function was registered for the requested icon, a fallback to the
	 * defaultIconResolver is used.
	 *
	 * @param name of the icon in the {@link SimpleIconSet}
	 * @return The icon as a {@link AbstractNodeViewElement}
	 */
	public AbstractNodeViewElement icon( String name );

	/**
	 * Shorthand method to get an {@link IconSet} from the {@link IconSetRegistry}
	 *
	 * @param name of the iconSet
	 * @return an {@link IconSet}
	 */
	public static IconSet iconSet( @NotNull String name ) {
		return IconSetRegistry.getIconSet( name );
	}
}
