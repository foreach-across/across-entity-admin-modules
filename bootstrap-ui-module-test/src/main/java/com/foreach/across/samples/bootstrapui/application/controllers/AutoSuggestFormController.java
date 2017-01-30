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
import com.foreach.across.modules.web.menu.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.List;

/**
 * Generates Bootstrap based tabs from a {@link Menu} instance.
 *
 * @author Arne Vandamme
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
		model.addAttribute( "autosuggest", bootstrapUiFactory.autosuggest()
		                                                     .configuration( configuration )
		                                                     .prefillValues( Arrays.asList( "abc", "def" ) )
		                                                     .build() );

		return "th/bootstrapUiTest/autosuggest";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/suggest", produces = "application/json; charset=UTF-8")
	@ResponseBody
	public List<Suggestion> suggetions() {

		return Arrays.asList(
				new Suggestion( 1, "AAA" ),
				new Suggestion( 2, "ABC" ),
				new Suggestion( 3, "BBB" )
		);
	}

	private class Suggestion
	{
		private int id;
		private String description;

		public Suggestion() {
		}

		public Suggestion( int id, String description ) {
			this.id = id;
			this.description = description;
		}

		public int getId() {
			return id;
		}

		public String getDescription() {
			return description;
		}
	}
}
