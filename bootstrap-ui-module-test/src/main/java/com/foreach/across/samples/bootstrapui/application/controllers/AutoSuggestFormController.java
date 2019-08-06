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

import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiFormElementsWebResources;
import com.foreach.across.modules.bootstrapui.resource.BootstrapUiWebResources;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
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
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;

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

		WebResourceRegistry webResourceRegistry = builderContext.getAttribute( WebResourceRegistry.class );

		webResourceRegistry.apply(
				WebResourceRule.add( WebResource.javascript( "@static:/bootstrapUiTest/switch-autosuggest-datasource.js" ) )
				               .withKey( "testJs" )
				               .after( BootstrapUiWebResources.NAME )
				               .before( BootstrapUiFormElementsWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);

		generatedElements.put( "Simple autosuggest with default settings", defaultAutoSuggest() );
		generatedElements.put( "Autosuggest with switching datasource", autoSuggestWithMultipleDatasets() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private AutoSuggestFormElement defaultAutoSuggest() {
		return autosuggest()
				.configuration(
						AutoSuggestFormElementConfiguration.withDataSet(
								dataSet -> dataSet.remoteUrl( "/bootstrapAutosuggest/suggest?query={{query}}" )
								                  .setAttribute( "templates", Collections.singletonMap( "footer", "End of dataset" ) )
						).withDataSet( "willies",
						               dataSet -> dataSet.remoteUrl( "/bootstrapAutosuggest/suggest-more?query={{query}}" )
						)
				)
				.notFoundTemplate( text( "Ah, ah ah, '{{query}}' is not the magic word..." ) )
				.suggestionTemplate( div().add( text( "{{label}} (alt: {{other}})" ) ) )
				.headerTemplate( div().attribute( "style", "text-decoration: underline" ).add( text( "Suggestions" ) ) )
				.notFoundTemplate( "willies", text( "Hey willy, '{{query}}' doesn't exist!" ) )
				.headerTemplate( "willies", div().attribute( "style", "color: red" ).add( text( "My red header" ) ) )
				.footerTemplate( "willies", div().attribute( "style", "color: red" ).add( text( "My red footer" ) ) )
				.build();
	}

	private NodeViewElement autoSuggestWithMultipleDatasets() {
		return div()
				.add(
						checkbox()
								.htmlId( "datasource-switcher" )
								.label( "Switch datasource" )
				).add(
						autosuggest()
								.htmlId( "js-switch-source-autosuggest" )
								.configuration(
										AutoSuggestFormElementConfiguration.withDataSet(
												dataSet -> dataSet.remoteUrl( "/bootstrapAutosuggest/suggest?query={{query}}" )
										)
								)
								.notFoundTemplate( "willies", text( "Hey willy, '{{query}}' doesn't exist!" ) )
								.notFoundTemplate( text( "Ah, ah ah, '{{query}}' is not the magic word..." ) )
								.suggestionTemplate( div().add( text( "{{label}} (alt: {{other}})" ) ) )
								.headerTemplate( "willies", div().attribute( "style", "color: red" ).add( text( "My red header" ) ) )
								.headerTemplate( div().attribute( "style", "text-decoration: underline" ).add( text( "Suggestions" ) ) )
								.footerTemplate( "willies", div().attribute( "style", "color: red" ).add( text( "My red footer" ) ) )

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

	@RequestMapping(method = RequestMethod.GET, value = "/suggest-more", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<Suggestion> moreSuggestions( @RequestParam("query") String query ) {
		List<Suggestion> suggestions = Arrays.asList(
				Suggestion.builder()
				          .id( 1 )
				          .label( "123" )
				          .other( "abc" )
				          .build(),
				Suggestion.builder()
				          .id( 2 )
				          .label( "456a" )
				          .other( "def" )
				          .build(),
				Suggestion.builder()
				          .id( 3 )
				          .label( "789" )
				          .other( "ghi" )
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
