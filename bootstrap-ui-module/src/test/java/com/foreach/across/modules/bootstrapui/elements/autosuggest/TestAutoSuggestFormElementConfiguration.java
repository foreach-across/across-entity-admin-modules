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

package com.foreach.across.modules.bootstrapui.elements.autosuggest;

import com.foreach.across.modules.bootstrapui.utils.ElementConfigurationMap;
import lombok.val;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestAutoSuggestFormElementConfiguration
{
	@Test
	public void blankConfiguration() {
		assertThat( new AutoSuggestFormElementConfiguration() )
				.containsEntry( "hint", true )
				.containsEntry( "highlight", true )
				.containsEntry( "minLength", 1 )
				.containsEntry( ATTR_DATASETS, Collections.emptyList() );
	}

	@Test
	public void globalOptions() {
		assertThat( new AutoSuggestFormElementConfiguration().showHint( false ).highlight( false ).minLength( 0 ) )
				.containsEntry( "hint", false )
				.containsEntry( "highlight", false )
				.containsEntry( "minLength", 0 );
	}

	@Test
	public void newDataSet() {
		assertThat( AutoSuggestFormElementConfiguration.createDataSet() )
				.containsKey( "name" );
	}

	@Test
	public void singleMinimalDataSet() {
		assertThat( AutoSuggestFormElementConfiguration.withDataSet( dataset -> dataset.remoteUrl( "url" ) ) )
				.containsEntry( ATTR_DATASETS, Collections.singletonList( createDataSet().name( DEFAULT_DATASET ).remoteUrl( "url" ) ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void namedDataSetWithDifferentOptions() {
		val dataset = AutoSuggestFormElementConfiguration.createDataSet()
		                                                 .name( "my-data" )
		                                                 .setAttribute( "display", "id" )
		                                                 .bloodhound( bloodhound -> bloodhound.setAttribute( "initialize", false ) )
		                                                 .remote( options -> options.setAttribute( "url", "remote-url" ) )
		                                                 .prefetch( options -> options.setAttribute( "url", "prefetch-url" ) );

		assertThat( dataset )
				.containsEntry( "name", "my-data" )
				.containsEntry( "display", "id" )
				.containsKey( "bloodhound" );

		val bloodhound = (ElementConfigurationMap) dataset.get( "bloodhound" );
		assertThat( bloodhound )
				.containsEntry( "initialize", false )
				.containsKey( "remote" )
				.containsKey( "prefetch" );

		assertThat( (ElementConfigurationMap) bloodhound.get( "remote" ) )
				.containsEntry( "url", "remote-url" );
		assertThat( (ElementConfigurationMap) bloodhound.get( "prefetch" ) )
				.containsEntry( "url", "prefetch-url" );
	}
}
