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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * An {@link SimpleIconSet} that is made available on an {@link IconSetRegistry}
 *
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
public class SimpleIconSet implements MutableIconSet
{
	/**
	 * The defaultIconResolver is used as a fallback default resolver when requesting an icon that has no
	 * specific {@link SimpleIconSet#registeredIconResolvers} associated with it.
	 * Each {@link SimpleIconSet} requires a {@link SimpleIconSet#defaultIconResolver} to resolve a sensible default {@link AbstractNodeViewElement} for the icon
	 * that has been specified.
	 */
	private Function<String, AbstractNodeViewElement> defaultIconResolver;
	private final Map<String, Function<String, AbstractNodeViewElement>> registeredIconResolvers = new HashMap<>();

	@Override
	public AbstractNodeViewElement icon( String name ) {
		Function<String, AbstractNodeViewElement> registeredIconResolver = registeredIconResolvers.get( name );
		if ( registeredIconResolver != null ) {
			return registeredIconResolver.apply( name );
		}

		if ( defaultIconResolver == null ) {
			throw new IllegalArgumentException( String.format( "No icon with the name %s could be found", name ) );
		}

		return defaultIconResolver.apply( name );
	}

	@Override
	public void add( @NotNull String name, Function<String, AbstractNodeViewElement> iconResolver ) {
		registeredIconResolvers.put( name, iconResolver );
	}

	@Override
	public void remove( @NotNull String name ) {
		registeredIconResolvers.remove( name );
	}

	@Override
	public void removeAll() {
		registeredIconResolvers.clear();
	}

	public void setDefaultIconResolver( Function<String, AbstractNodeViewElement> defaultIconResolver ) {
		this.defaultIconResolver = defaultIconResolver;
	}
}
