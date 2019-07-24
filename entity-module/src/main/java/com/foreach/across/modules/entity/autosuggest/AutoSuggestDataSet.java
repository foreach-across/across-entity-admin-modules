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

package com.foreach.across.modules.entity.autosuggest;

/**
 * @author Arne Vandamme
 * @since 3.4.0
 */
public interface AutoSuggestDataSet
{
	/**
	 * Retrieve the suggestions for a particular query.
	 * The second parameter is an optional control name for which the suggestions are requested.
	 *
	 * @param query       usually the input of the user
	 * @param controlName name of the control for which the suggestions are requested
	 * @return suggestions result
	 */
	Object suggestions( String query, String controlName );

	/**
	 * Retrieve the prefetch data for this set. Prefetch data is the initial data that
	 * should be loaded and possibly shown as suggestions, without the user having entered
	 * a particular query string.
	 *
	 * @param controlName name of the control for which the suggestions are requested
	 * @return suggestions result
	 */
	Object prefetch( String controlName );

	/**
	 * @return true if data from this set can be pre-fetched
	 */
	boolean isPrefetchSupported();

	/**
	 * @return builder for a simple auto-suggest dataset
	 */
	static SimpleAutoSuggestDataSet.SimpleAutoSuggestDataSetBuilder builder() {
		return SimpleAutoSuggestDataSet.builder();
	}

	/**
	 * Represents a single auto-suggest result.
	 */
	interface Result
	{
		Object getId();

		String getLabel();
	}

	/**
	 * Optional interface to be implemented by dataset implementations to indicate
	 * its possible to generate a single auto-suggest result for an object.
	 */
	@FunctionalInterface
	interface ResultTransformer
	{
		Result transformToResult( Object candidate );
	}
}
