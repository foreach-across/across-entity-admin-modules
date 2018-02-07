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

import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataEndpoint;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataSet;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.samples.entity.application.business.Group;
import com.foreach.across.samples.entity.application.repositories.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.autosuggest;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.withDataSet;

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
		AutoSuggestDataEndpoint.MappedDataSet dataSet = autoSuggestDataEndpoint.registerDataSet(
				AutoSuggestDataSet
						.builder()
						.suggestionsLoader(
								( query, controlName ) -> groupRepository.findByNameContaining( query, new PageRequest( 0, 15, new Sort( "name" ) ) )
								                                         .getContent()
								                                         .stream()
								                                         .map( group -> {
									                                         Map<String, Object> entry = new HashMap<>();
									                                         entry.put( "id", group.getId() );
									                                         entry.put( "label", group.getName() );
									                                         return entry;
								                                         } )
								                                         .collect( Collectors.toList() ) )
						.build()
		);

		entities.withType( Group.class )
		        .viewElementBuilder(
				        ViewElementMode.CONTROL,
				        autosuggest()
						        .required( true )
						        .controlName( "entity.group" )
						        .configuration( withDataSet( ds -> ds.remoteUrl( dataSet.suggestionsUrl() ) ) )
						        .postProcessor( ( viewElementBuilderContext, autoSuggestFormElement ) -> {
							        Group group = EntityViewElementUtils.currentPropertyValue( viewElementBuilderContext, Group.class );

							        if ( group != null ) {
								        autoSuggestFormElement.setValue( group.getId() );
								        autoSuggestFormElement.setText( group.getName() );
							        }
						        } )
		        );
	}
}
