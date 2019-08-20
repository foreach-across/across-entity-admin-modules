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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Is used to add,remove or get an {@link MutableIconSet} from the {@link IconSetRegistry} collection.
 * If you are looking for an easy way to render or get an immutable icon, you can use the {@link IconSet#icon(String)}
 *
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
public class IconSetRegistry
{
	private static final Map<String, MutableIconSet> iconSets = new HashMap<>();

	/**
	 * Get's a registered {@link MutableIconSet} from the collection so you can modify the iconSet
	 * If you just want to render the icon take a look at the {@link IconSet}
	 *
	 * @param name of the {@link MutableIconSet} to add
	 * @return the newly added {@link MutableIconSet}
	 */
	public static MutableIconSet getIconSet( @NotNull String name ) {
		MutableIconSet foundMutableIconSet = iconSets.get( name );

		if ( foundMutableIconSet == null ) {
			throw new IllegalArgumentException( String.format( "IconSet with name %s does not exist", name ) );
		}

		return foundMutableIconSet;
	}

	/**
	 * Get all registered {@link MutableIconSet} by name
	 *
	 * @return an {@link Collections.UnmodifiableMap} of the registered {@link MutableIconSet}
	 */
	public static Map<String, MutableIconSet> getAllIconSets() {
		return Collections.unmodifiableMap( iconSets );
	}

	/**
	 * Add an {@link MutableIconSet} to the collection of {@link IconSetRegistry}
	 *
	 * @param name          the {@link MutableIconSet} will be registered under
	 * @param MutableIconSet to register
	 */
	public static void addIconSet( @NotNull String name, @NotNull MutableIconSet MutableIconSet ) {
		iconSets.put( name, MutableIconSet );
	}

	/**
	 * Remove an {@link MutableIconSet} from the {@link IconSetRegistry}
	 *
	 * @param name
	 */
	public static void removeIconSet( @NonNull String name ) {
		iconSets.remove( name );
	}

	/**
	 * Remove all {@link MutableIconSet}  from the {@link IconSetRegistry}
	 */
	public void removeAllIconSets() {
		iconSets.clear();
	}
}
