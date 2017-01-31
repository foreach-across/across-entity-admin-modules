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

import com.foreach.across.modules.bootstrapui.elements.AutosuggestFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.builder.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.elements.builder.AutoSuggestFormElementBuilder.CSS_PREFILL_TABLE;

/**
 * Generates Twitter Typeahead autosuggest instances with
 *
 * @author Sander Van Loock
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/bootstrapAutosuggest")
public class AutoSuggestFormController
{
	private final BootstrapUiFactory bootstrapUiFactory;

	@RequestMapping(method = RequestMethod.GET)
	public String autosuggest( ModelMap model ) {

		AutosuggestFormElementConfiguration configuration = new AutosuggestFormElementConfiguration(
				"/bootstrapAutosuggest/suggest" );

		model.addAttribute( "autosuggest1", bootstrapUiFactory.autosuggest()
		                                                      .build() );

		model.addAttribute( "autosuggest2", bootstrapUiFactory.autosuggest()
		                                                      .configuration( configuration )
		                                                      .idProperty( "id" )
		                                                      .properties( "label", "other" )
		                                                      .prefill( Arrays.asList( createPrefill( "abc" ),
		                                                                               createPrefill( "def" ) )
		                                                      )
		                                                      .build() );

		NodeViewElement container = new NodeViewElementBuilder( "ul" )
				.css( CSS_PREFILL_TABLE )
				.build( new DefaultViewElementBuilderContext() );
		NodeViewElement item = new NodeViewElementBuilder( "ul" )
				.add( bootstrapUiFactory.node( "li" )
				                        .css( AutoSuggestFormElementBuilder.CSS_ITEM_TEMPLATE )
				                        .add( bootstrapUiFactory
						                              .div()
						                              .attribute( AutoSuggestFormElementBuilder.ATTRIBUTE_DATA_PROPERTY,
						                                          "label" )
				                        )
				)
				.build( new DefaultViewElementBuilderContext() );
		model.addAttribute( "autosuggest3", bootstrapUiFactory.autosuggest()
		                                                      .configuration( configuration )
		                                                      .itemTemplate( container, item )
		                                                      .build() );

		return "th/bootstrapUiTest/autosuggest";
	}

	private Map<String, Object> createPrefill( String description ) {
		HashMap item = new HashMap();
		item.put( "label", description );
		item.put( "other", "qksmjdfmlqsj" );
		return item;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/suggest", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<Suggestion> suggetions( @RequestParam("query") String query ) {
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
	static class Suggestion
	{
		private int id;
		private String label;
		private String other;
	}
}
