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
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An {@link IconSet} that is made available on an {@link IconSets}
 *
 * @author Stijn Vanhoof
 */
@RequiredArgsConstructor
public class IconSet
{
	/**
	 * The defaultIconResolver is used as a fallback default resolver when requesting an icon that has no
	 * specific {@link IconSet#registeredIconResolvers} associated with it.
	 * Each {@link IconSet} requires a {@link IconSet#defaultIconResolver} to resolve a sensible default {@link AbstractNodeViewElement} for the icon
	 * that has been specified.
	 */
	private final Function<String, AbstractNodeViewElement> defaultIconResolver;
	private Map<String, Function<String, AbstractNodeViewElement>> registeredIconResolvers = new HashMap<>();

	/**
	 * Returns an icon with from the current {@link IconSet}
	 *
	 * @param name of the icon in the {@link IconSet}
	 * @return The icon as a {@link AbstractNodeViewElement}
	 */
	public AbstractNodeViewElement icon( String name ) {
		return registeredIconResolvers.getOrDefault( name, defaultIconResolver ).apply( name );
	}

	/**
	 * Adds an icon to the {@link IconSet}
	 *
	 * @param name         of the icon to add to the current {@link IconSet}
	 * @param iconResolver that will be used to resolve a {@link AbstractNodeViewElement} icon
	 */
	public void add( @NotNull String name, Function<String, AbstractNodeViewElement> iconResolver ) {
		registeredIconResolvers.put( name, iconResolver );
	}

	/**
	 * Removes an icon from the {@link IconSet}
	 * @param name of the icon to be removed
	 */
	public void remove(@NotNull String name) {
		registeredIconResolvers.remove( name );
	}
}
