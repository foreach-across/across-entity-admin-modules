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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet;
import com.foreach.across.modules.entity.autosuggest.SimpleAutoSuggestDataSet;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.business.User;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Test configuration for auto suggest controls.
 */
@Configuration
@RequiredArgsConstructor
public class AutoSuggestConfiguration implements EntityConfigurer
{
	private final AutoSuggestDataEndpoint autoSuggestDataEndpoint;
	private final GroupRepository groupRepository;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		AutoSuggestDataSet.ResultTransformer groupToSuggestion = candidate -> {
			Group group = (Group) candidate;
			return new SimpleAutoSuggestDataSet.Result( group.getId(), group.getName() );
		};

		autoSuggestDataEndpoint.registerDataSet(
				"possible-groups",
				SimpleAutoSuggestDataSet
						.builder()
						.suggestionsLoader(
								( query, controlName ) -> groupRepository.findByNameContaining( query, new PageRequest( 0, 15, new Sort( "name" ) ) )
								                                         .getContent()
								                                         .stream()
								                                         .map( groupToSuggestion::transformToResult )
								                                         .collect( Collectors.toList() ) )
						.build()
		);

		entities.withType( Group.class )
		        .attribute( AutoSuggestDataAttributeRegistrar.DATASET_ID, "possible-groups" )
		        .attribute( AutoSuggestDataSet.ResultTransformer.class, groupToSuggestion )
		        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST );

		configureCitiesAutoSuggest( entities );
	}

	private void configureCitiesAutoSuggest( EntitiesConfigurationBuilder entities ) {
		List<String> cities = Arrays.asList( "Antwerp", "Brussels", "Ghent", "Kortrijk", "Hasselt" );

		AutoSuggestDataSet.ResultTransformer cityToSuggestion
				= city -> new SimpleAutoSuggestDataSet.Result( city, city.toString() );

		autoSuggestDataEndpoint.registerDataSet(
				"cities",
				SimpleAutoSuggestDataSet
						.builder()
						.suggestionsLoader(
								( query, controlName ) ->
										cities.stream()
										      .filter( candidate -> StringUtils.containsIgnoreCase( candidate, query ) )
										      .map( cityToSuggestion::transformToResult )
										      .collect( Collectors.toList() )
						)
						.build()
		);

		entities.withType( User.class )
		        .properties(
				        props -> props.property( "address[].city" )
				                      .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST )
				                      .attribute( AutoSuggestDataAttributeRegistrar.DATASET_ID, "cities" )
				                      .attribute( AutoSuggestDataSet.ResultTransformer.class, cityToSuggestion )
		        );
	}
}
