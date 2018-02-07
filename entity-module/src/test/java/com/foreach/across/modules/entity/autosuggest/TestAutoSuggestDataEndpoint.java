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

import lombok.val;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestAutoSuggestDataEndpoint
{
	private AutoSuggestDataEndpoint endpoint = new AutoSuggestDataEndpoint( "@adminWeb:/my/endpoint" );

	@Test
	public void mappedDataSetWithAssignedId() {
		AutoSuggestDataSet dataSet = AutoSuggestDataSet.builder().build();
		val mapped = endpoint.registerDataSet( dataSet );
		assertThat( mapped ).isNotNull();
		assertThat( mapped.getId() ).isNotNull();
		assertThat( mapped.getDataSet() ).isSameAs( dataSet );

		assertThat( endpoint.getDataSet( mapped.getId() ) ).isSameAs( mapped );

		assertThat( endpoint.removeDataSet( mapped.getId() ) ).isSameAs( dataSet );
		assertThat( endpoint.getDataSet( mapped.getId() ) ).isNull();
	}

	@Test
	public void urlGeneration() {
		AutoSuggestDataSet dataSet = AutoSuggestDataSet.builder().build();
		val mapped = endpoint.registerDataSet( "my-data", dataSet );
		assertThat( mapped.getId() ).isEqualTo( "my-data" );

		assertThat( mapped.suggestionsUrl() ).isEqualTo( "@adminWeb:/my/endpoint/query?query={{query}}&controlName={{controlName}}&dataset=my-data" );
		assertThat( mapped.prefetchUrl() ).isEqualTo( "@adminWeb:/my/endpoint/prefetch?query={{query}}&controlName={{controlName}}&dataset=my-data" );
	}
}
