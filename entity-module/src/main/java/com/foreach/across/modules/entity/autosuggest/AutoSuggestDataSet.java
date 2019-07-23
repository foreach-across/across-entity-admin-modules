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

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Represents a simple dataset used for auto-suggest data.
 * A dataset has a function for suggestions based on a query, and one to prefetch suggestion data.
 * <p/>
 * The return value of the functions is a general object which will usually be converted
 * to a specific output format by a controller. Usually the return value is one of the following:
 * <ul>
 * <li>{@code null} or an empty collection to indicate no results</li>
 * <li>a collection holding the objects that should be returned to the caller; usually converted to JSON</li>
 * <li>a {@link org.springframework.http.ResponseEntity} that can already hold the JSON data</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @see AutoSuggestDataController
 * @since 3.0.0
 */
@Builder
@RequiredArgsConstructor
public class AutoSuggestDataSet
{
	public static final String NAME = AutoSuggestDataSet.class.getName() + "_name";

	private final BiFunction<String, String, Object> suggestionsLoader;
	private final Function<String, Object> prefetchLoader;

	/**
	 * Retrieve the suggestions for a particular query.
	 * The second parameter is an optional control name for which the suggestions are requested.
	 *
	 * @param query       usually the input of the user
	 * @param controlName name of the control for which the suggestions are requested
	 * @return suggestions result
	 */
	public Object suggestions( String query, String controlName ) {
		return suggestionsLoader != null ? suggestionsLoader.apply( query, controlName ) : null;
	}

	/**
	 * Retrieve the prefetch data for this set. Prefetch data is the initial data that
	 * should be loaded and possibly shown as suggestions, without the user having entered
	 * a particular query string.
	 *
	 * @param controlName name of the control for which the suggestions are requested
	 * @return suggestions result
	 */
	public Object prefetch( String controlName ) {
		return prefetchLoader != null ? prefetchLoader.apply( controlName ) : null;
	}

	/**
	 * Represent a single auto-suggest result.
	 */
	@RequiredArgsConstructor
	@Data
	public static class Result
	{
		private final Object id;
		private final String label;
	}

	public interface ResultTransformer extends Function<Object, Result> {

	}
}
