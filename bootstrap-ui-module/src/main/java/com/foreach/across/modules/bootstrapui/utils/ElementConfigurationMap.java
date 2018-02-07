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

package com.foreach.across.modules.bootstrapui.utils;

import java.util.LinkedHashMap;

/**
 * Base class for a set of key/values that represent the configuration settings for an element.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class ElementConfigurationMap<SELF extends ElementConfigurationMap<SELF>> extends LinkedHashMap<String, Object>
{
	/**
	 * Set an attribute on the configuration. Same as calling {@link #put(Object, Object)} except suitable for fluent API,
	 * because it returns the same instance.
	 *
	 * @param key   attribute key
	 * @param value attribute value
	 * @return current configuration
	 */
	@SuppressWarnings("unchecked")
	public SELF setAttribute( String key, Object value ) {
		put( key, value );
		return (SELF) this;
	}
}
