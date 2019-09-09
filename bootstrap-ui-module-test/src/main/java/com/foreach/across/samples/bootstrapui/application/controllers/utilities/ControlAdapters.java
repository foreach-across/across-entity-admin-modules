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

package com.foreach.across.samples.bootstrapui.application.controllers.utilities;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiViewElementAttributes;
import com.foreach.across.modules.bootstrapui.elements.NumericFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.*;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuildersBroken.*;
import static com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementConfiguration.withDataSet;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
@Controller
@RequestMapping("/utilities/control-adapters")
class ControlAdapters extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/utilities/control-adapters", "Control adapters" );
	}

	@GetMapping
	public String render( Model model ) {
		Map<String, ViewElement> generatedElements = new LinkedHashMap<>();
		generatedElements.put( "Datetime", datetime().value( LocalDate.of( 2019, 1, 23 ) ).controlName( "ca-datetime" ).build() );
		addOptionFormElements( generatedElements, "checkbox" );
		addOptionFormElements( generatedElements, "radio" );
		generatedElements.put( "Group of checkboxes", optionElement().controlName( "ca-multi-checkbox" ).checkbox().build() );
		generatedElements.put( "Group of radiobuttons", optionElement().controlName( "ca-multi-radio" ).radio().build() );
		generatedElements.put( "Nested control adapters",
		                       div().htmlId( "ca-nested-containers" )
		                            .attribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "container" )
		                            .add(
				                            optionElement().controlName( "ca-nested-multi-checkbox" ).checkbox(),
				                            optionElement().controlName( "ca-nested-multi-radio" ).radio()
		                            ).build()
		);
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
		                                    .configuration(
				                                    withDataSet( dataset -> dataset.remoteUrl( "/form-controls/autosuggest/suggest?query={{query}}" ) ) )
		                                    .build() );
		generatedElements.put( "Textbox", textbox().controlName( "ca-textbox" ).build() );
		generatedElements.put( "Autosizing textbox", textbox().autoSize().controlName( "ca-textbox-autosize" ).build() );
		generatedElements.put( "Textarea", textarea().controlName( "ca-textarea" ).build() );

		NumericFormElementConfiguration numericFormElementConfiguration = new NumericFormElementConfiguration( Locale.US );
		numericFormElementConfiguration.setLocalizeOutputFormat( false );
		numericFormElementConfiguration.setLocalizeDecimalSymbols( false );
		generatedElements.put( "Numeric", numeric().configuration( numericFormElementConfiguration ).controlName( "ca-numeric" ).build() );
		generatedElements.put( "Numeric without formatting", numeric().controlName( "ca-numeric-noformat" ).build() );

		return render( generatedElements.entrySet().stream().map( e -> panel( e.getKey(), e.getValue() ) ).toArray() );
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
