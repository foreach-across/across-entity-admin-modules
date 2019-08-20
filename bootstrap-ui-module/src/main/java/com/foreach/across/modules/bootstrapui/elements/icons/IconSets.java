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

import lombok.NonNull;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds a collection of {@link IconSet} that can be used to generate an {@link IconSet#icon(String)}
 *
 * @author Stijn Vanhoof
 */
public class IconSets
{
	private static Map<String, IconSet> iconSets = new HashMap<>();

	static IconSet iconSet( @NotNull String name ) {
		if ( !iconSets.containsKey( name ) ) {
			throw new IllegalArgumentException( String.format( "IconSet with name %s does not exist", name ) );
		}

		return iconSets.get( name );
	}

	static void add( @NotNull String name, @NotNull IconSet iconSet ) {
		iconSets.put( name, iconSet );
	}

	static void remove( @NonNull String name ) {
		iconSets.remove( name );
	}
}
