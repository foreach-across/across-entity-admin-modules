/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.samples.bootstrapui.application.controllers.examples;

import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.withDataSet;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
@Controller
@RequestMapping("/control-adapters")
public class ControlAdapterController
{
	/**
	 * Register the section in the administration menu.
	 */
	@EventListener(condition = "#navMenu.menuName=='navMenu'")
	protected void registerMenuItems( BuildMenuEvent navMenu ) {
		navMenu.builder()
		       .item( "/examples/control-adapters", "Control adapters", "/control-adapters" ).order( 3 );
	}

	@GetMapping
	public String render( Model model ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Datetime", datetime().value( LocalDate.of( 2019, 1, 23 ) ).controlName( "ca-datetime" ).build() );
		addOptionFormElements( generatedElements, "checkbox" );
		addOptionFormElements( generatedElements, "radio" );
		generatedElements.put( "Group of checkboxes", optionElement().controlName( "ca-multi-checkbox" ).checkbox().build() );
		generatedElements.put( "Group of radiobuttons", optionElement().controlName( "ca-multi-radio" ).radio().build() );
		generatedElements.put( "Select", optionElement().controlName( "ca-select" ).select().build() );
		generatedElements.put( "Multi select", optionElement().controlName( "ca-multi-select" ).select().multiple().build() );
		generatedElements.put( "Bootstrap select",
		                       optionElement().controlName( "ca-bootstrap-select" )
		                                      .select( SelectFormElementConfiguration.liveSearch() )
		                                      .build() );
		generatedElements.put( "Bootstrap multi select",
		                       optionElement().controlName( "ca-bootstrap-multi-select" )
		                                      .multiple()
		                                      .select( SelectFormElementConfiguration.liveSearch() )
		                                      .build() );
		generatedElements.put( "Autosuggest",
		                       autosuggest().controlName( "ca-autosuggest" )
		                                    .configuration( withDataSet( dataset -> dataset.remoteUrl( "/bootstrapAutosuggest/suggest?query={{query}}" ) ) )
		                                    .build() );
		generatedElements.put( "Textbox", textbox().controlName( "ca-textbox" ).build() );
		generatedElements.put( "Autosizing textbox", textbox().autoSize().controlName( "ca-textbox-autosize" ).build() );
		generatedElements.put( "Textarea", textarea().controlName( "ca-textarea" ).build() );
		generatedElements.put( "Numeric", numeric().currency( Currency.getInstance( "EUR" ) ).controlName( "ca-numeric" ).build() );

		model.addAttribute( "generatedElements", generatedElements );

		return "th/bootstrapUiTest/elementsRendering";
	}

	private void addOptionFormElements( Map<String, ViewElement> generatedElements, String identifier ) {
		generatedElements.put( "Single " + identifier, getOptionBuilder( identifier ).controlName( "ca-" + identifier ).text( "Alive" ).value( "Yes" )
		                                                                             .build() );
		generatedElements.put( "Unwrapped " + identifier, getOptionBuilder( identifier ).unwrapped().controlName( "ca-" + identifier + "-unwrapped" ).text(
				"Alive" ).value( "Yes" )
		                                                                                .build() );
		generatedElements.put( "Unwrapped " + identifier + " without label", getOptionBuilder( identifier ).unwrapped().value( "Yes" )
		                                                                                                   .controlName(
				                                                                                                   "ca-" + identifier + "-unwrapped-no-label" )
		                                                                                                   .build() );
		generatedElements.put( identifier + " outside label", div()
				.add( label().text( "Alive" ).target( "ca-" + identifier + "-out-label" ) )
				.add( getOptionBuilder( identifier ).value( "Yes" ).controlName( "ca-" + identifier + "-out-label" ) )
				.build() );
	}

	private OptionFormElementBuilder getOptionBuilder( String identifier ) {
		return "checkbox".equals( identifier ) ? checkbox() : radio();
	}

	private OptionsFormElementBuilder optionElement() {
		return options()
				.addAll( optionChildElements() );
	}

	private Collection<ViewElementBuilder> optionChildElements() {
		return Arrays.asList(
				option().text( "One" ).value( 1 ),
				option().text( "Two" ).value( 2 ),
				option().text( "3" ).value( "Three" )
		);
	}

}
