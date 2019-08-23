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

package com.foreach.across.samples.bootstrapui.application.controllers.form.controls;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElement;
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionsFormElementBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Generates Bootstrap based tabs from a {@link Menu} instance.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/form-controls/select")
class Select extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/form-controls/select", "Select" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String renderSelects() {
		return render(
				panel( "Simple select", simpleSelect().build() ),
				panel( "Simple select with empty option", simpleSelect( true ).build() ),
				panel( "Multi select", multiSelect().build() ),
				panel( "Multi select with empty option", multiSelect( true ).build() ),
				panel( "Simple select - bootstrap-select", simpleBootstrapSelect() ),
				panel( "Simple select with empty option - bootstrap-select", simpleBootstrapSelect( true ) ),
				panel( "Simple select with empty none selected text - bootstrap-select", simpleBootstrapSelect( true )
						.setConfiguration( SelectFormElementConfiguration.liveSearch().setNoneSelectedText( " " ) ) ),
				panel( "Multi select - bootstrap-select", multiBootstrapSelect() ),
				panel( "Multi select with empty option - bootstrap-select", multiBootstrapSelect( true ) )
		);
	}

	private SelectFormElement simpleBootstrapSelect() {
		return simpleBootstrapSelect( false );
	}

	private SelectFormElement simpleBootstrapSelect( boolean addEmptyOption ) {
		return (SelectFormElement) simpleSelect( addEmptyOption ).select( SelectFormElementConfiguration.simple() ).build();
	}

	private SelectFormElement multiBootstrapSelect() {
		return multiBootstrapSelect( false );
	}

	private SelectFormElement multiBootstrapSelect( boolean addEmptyOption ) {
		return (SelectFormElement) multiSelect( addEmptyOption ).select( SelectFormElementConfiguration.liveSearch() ).build();
	}

	private OptionsFormElementBuilder simpleSelect() {
		return simpleSelect( false );
	}

	private OptionsFormElementBuilder simpleSelect( boolean addEmptyOption ) {
		OptionsFormElementBuilder select = BootstrapUiBuilders.options()
		                                                      .select()
		                                                      .controlName( "controlName" )
		                                                      .name( "internalName" )
		                                                      .readonly( false );
		if ( addEmptyOption ) {
			select.add( BootstrapUiBuilders.option().value( "-1" ).text( " " ) );
		}

		select
				.add( BootstrapUiBuilders.option().value( "one" ).text( "Inner text" ) )
				.add( BootstrapUiBuilders.option().value( "two" ).text( "Inner text 2" )
				);
		return select;
	}

	private OptionsFormElementBuilder multiSelect() {
		return multiSelect( false );
	}

	private OptionsFormElementBuilder multiSelect( boolean addEmptyOption ) {
		SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
		group.setLabel( "Group label" );
		group.addChild(
				BootstrapUiBuilders.option()
				                   .value( "two" )
				                   .text( "Inner text 2" )
				                   .build()
		);
		group.addChild(
				BootstrapUiBuilders.option()
				                   .value( "Short two" )
				                   .text( "Some text" )
				                   .build()
		);

		OptionsFormElementBuilder select = BootstrapUiBuilders.options()
		                                                      .select();
		if ( addEmptyOption ) {
			select.add( BootstrapUiBuilders.option().value( "-1" ).text( " " ) );
		}
		return select
				.add( group )
				.multiple( true )
				.controlName( "controlName" )
				.name( "internalName" )
				.readonly( false );
	}
}
