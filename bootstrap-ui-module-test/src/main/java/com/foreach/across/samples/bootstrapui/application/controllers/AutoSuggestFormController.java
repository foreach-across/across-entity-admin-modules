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

package com.foreach.across.samples.bootstrapui.application.controllers;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder.CSS_PREFILL_TABLE;

/**
 * Generates Twitter Typeahead autosuggest instances.
 *
 * @author Sander Van Loock
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/bootstrapAutosuggest")
public class AutoSuggestFormController
{
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/test/form-elements/autosuggest", "AutoSuggest", "/bootstrapAutosuggest" ).order( 4 );
	}

	@RequestMapping(method = RequestMethod.GET)
	public String showElements( ModelMap model, ViewElementBuilderContext builderContext ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();

		generatedElements.put( "Simple autosuggest with default settings", defaultAutoSuggest() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private AutoSuggestFormElement defaultAutoSuggest() {
		return BootstrapUiBuilders
				.autosuggest()
				.configuration(
						AutoSuggestFormElementConfiguration.withDataSet(
								dataSet -> dataSet.remoteUrl( "/bootstrapAutosuggest/suggest?query={{query}}" )
						)
				).build();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/suggest", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<Suggestion> suggestions( @RequestParam("query") String query ) {
		List<Suggestion> suggestions = Arrays.asList(
				Suggestion.builder()
				          .id( 1 )
				          .label( "AAAlabel" )
				          .other( "123other" )
				          .build(),
				Suggestion.builder()
				          .id( 2 )
				          .label( "BBBlabel" )
				          .other( "456other" )
				          .build(),
				Suggestion.builder()
				          .id( 3 )
				          .label( "ABClabel" )
				          .other( "123other" )
				          .build()
		);
		return suggestions.stream()
		                  .filter( suggestion -> StringUtils.containsIgnoreCase( suggestion.getLabel(), query ) )
		                  .collect( Collectors.toList() );
	}

	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	@Data
	private static class Suggestion
	{
		private int id;
		private String label;
		private String other;
	}
}
