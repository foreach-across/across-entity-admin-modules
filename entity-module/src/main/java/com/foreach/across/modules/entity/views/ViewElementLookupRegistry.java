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
package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;

import java.util.Collection;

/**
 * Registry that maps {@link com.foreach.across.modules.web.ui.ViewElementBuilder} instances or
 * element type names fo {@link ViewElementMode}.  Has a concept of cacheable communicating to the
 * {@link EntityViewElementBuilderService} if a generated {@link com.foreach.across.modules.web.ui.ViewElementBuilder}
 * can be cached.
 * <p/>
 * NOTE: This class is mainly for internal use by EntityModule, and subject to breaking changes in future releases.
 *
 * @author Arne Vandamme
 */
public interface ViewElementLookupRegistry extends Cloneable
{
	/**
	 * Set the fixed {@link ViewElementBuilder} for lookups.  If set, this builder will always be returned.
	 *
	 * @param mode    to register the builder for
	 * @param builder instance to store
	 */
	void setViewElementBuilder( ViewElementMode mode, ViewElementBuilder builder );

	/**
	 * Add a {@link ViewElementPostProcessor} that should be applied to the element builder.
	 *
	 * @param mode          to register the postprocessor for
	 * @param postProcessor to add
	 */
	void addViewElementPostProcessor( ViewElementMode mode, ViewElementPostProcessor<?> postProcessor );

	/**
	 * If {@link #isCacheable(ViewElementMode)} returns {@code false} this call will have no effect
	 * and return value should also be {@code false}.
	 *
	 * @param mode    to register the builder for
	 * @param builder instance to store
	 * @return true if the builder was actually stored
	 */
	boolean cacheViewElementBuilder( ViewElementMode mode, ViewElementBuilder builder );

	/**
	 * Retrieve the (optionally) cached builder.
	 * Will return null if either not cached or not cacheable.
	 *
	 * @param mode to get the builder for
	 * @return the registered builder - null if none available
	 */
	ViewElementBuilder getCachedViewElementBuilder( ViewElementMode mode );

	/**
	 * Set the view element type for subsequent lookup.
	 *
	 * @param mode            to register the type for
	 * @param viewElementType to store
	 */
	void setViewElementType( ViewElementMode mode, String viewElementType );

	/**
	 * Sets the cacheable status for a given mode.
	 *
	 * @param mode      to set the status for
	 * @param cacheable true if subsequent calls can be cached
	 */
	void setCacheable( ViewElementMode mode, boolean cacheable );

	/**
	 * Removes the cached builder for the given mode.
	 *
	 * @param mode to remove the cached builder for
	 */
	void reset( ViewElementMode mode );

	/**
	 * Removes all cached builders.
	 */
	void clearCache();

	/**
	 * @param mode to create the builder for
	 * @return the registered builder - null if none available
	 */
	ViewElementBuilder getViewElementBuilder( ViewElementMode mode );

	/**
	 * @param mode to get the postprocessors for
	 * @return collection of postprocessors
	 */
	Collection<ViewElementPostProcessor<?>> getViewElementPostProcessors( ViewElementMode mode );

	/**
	 * @param mode to create the type for
	 * @return the registered view element type - null if none available
	 */
	String getViewElementType( ViewElementMode mode );

	/**
	 * If the result of a certain mode is cacheable, this means that an external
	 * {@link EntityViewElementBuilderService} can store back the results of a type lookup
	 * or builder creation.  Usually a {@link ViewElementBuilder} can be cached.
	 *
	 * @param mode to check for
	 * @return true if a builder can be cached
	 */
	boolean isCacheable( ViewElementMode mode );

	/**
	 * Checks whether any lookup values have been applied to the registry.
	 */
	boolean isEmpty();

	/**
	 * @return cloned instance
	 */
	ViewElementLookupRegistry clone();
}
