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
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.NonNull;

import java.util.function.Function;

/**
 * The purpose of a {@link MutableIconSet} is to modify the icons of an {@link IconSet} that is registered in the {@link IconSetRegistry}
 * You can add, remove and delete icons from the IconSet.
 * <p>
 * If you just want to get an icon for rendering, you should use the {@link IconSet}
 *
 * @author Stijn Vanhoof
 * @see SimpleIconSet
 * @since 3.0.0
 */
public interface MutableIconSet extends IconSet
{
	/**
	 * Adds an icon to the {@link MutableIconSet}.
	 *
	 * @param name         of the icon to add to the current {@link MutableIconSet}.
	 * @param iconResolver that will be used to resolve a {@link AbstractNodeViewElement} icon
	 */
	void add( @NonNull String name, Function<String, HtmlViewElement> iconResolver );

	/**
	 * Removes an icon from the {@link MutableIconSet}.
	 *
	 * @param name of the icon to be removed
	 */
	void remove( @NonNull String name );

	/**
	 * Remove all icons from the {@link MutableIconSet}.
	 */
	void removeAll();
}
