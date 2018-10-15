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

package com.foreach.across.modules.entity.registry.properties;

/**
 * Interface to support resolving {@link EntityPropertyBindingContext} for a parent context on the fly.
 *
 * @author Steven Gentens
 * @since 3.2.0
 */
public interface ChildContextResolver
{
	/**
	 * Resolves the context for a given name.
	 *
	 * @param name of the context to resolve
	 * @return {@link EntityPropertyBindingContext} that has been resolved. Returns {@code null} if no context could be resolved.
	 */
	EntityPropertyBindingContext resolve( String name );
}
