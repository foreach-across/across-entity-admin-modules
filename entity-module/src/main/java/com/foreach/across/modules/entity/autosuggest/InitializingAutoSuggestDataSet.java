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

import lombok.Data;

import java.util.Collections;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Implementation of {@link AutoSuggestDataSet} that also implements a
 * {@link com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet.ResultTransformer}
 * and allows for lazy initialization upon first use by specifying a {@link #setInitializer(Consumer)}.
 * Mainly used internally when registering auto-suggest dataset attributes.
 *
 * @author Arne Vandamme
 * @see AutoSuggestDataAttributeRegistrar
 * @since 3.4.0
 */
@Data
public class InitializingAutoSuggestDataSet implements AutoSuggestDataSet, AutoSuggestDataSet.ResultTransformer
{
	private String dataSetId;

	private Consumer<InitializingAutoSuggestDataSet> initializer;
	private BiFunction<String, String, Object> suggestionsLoader;
	private Function<String, Object> prefetchLoader;
	private AutoSuggestDataSet.ResultTransformer resultTransformer;

	private boolean initialized = false;

	@Override
	public Object suggestions( String query, String controlName ) {
		initializeIfNecessary();
		return suggestionsLoader != null ? suggestionsLoader.apply( query, controlName ) : Collections.emptyList();
	}

	@Override
	public Object prefetch( String controlName ) {
		initializeIfNecessary();
		return prefetchLoader != null ? prefetchLoader.apply( controlName ) : Collections.emptyList();
	}

	@Override
	public boolean isPrefetchSupported() {
		initializeIfNecessary();
		return prefetchLoader != null;
	}

	@Override
	public Result transformToResult( Object candidate ) {
		initializeIfNecessary();
		return resultTransformer.transformToResult( candidate );
	}

	private void initializeIfNecessary() {
		if ( !initialized ) {
			initializer.accept( this );
			initialized = true;
		}
	}
}
