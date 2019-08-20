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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;

public class TestOptionsAsSwitchBuilder extends AbstractBootstrapViewElementTest
{
	protected OptionsFormElementBuilder builder;

	protected ViewElementBuilderContext builderContext;

	@Before
	public void reset() {
		builderContext = new DefaultViewElementBuilderContext();

		builder = new OptionsFormElementBuilder().toggle();
	}

	@Test
	public void simple() {
		builder.htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<div data-bootstrapui-adapter-type='container' id='mybox' />"
		);
	}

	@Test
	public void options() {
		builder
				.controlName( "mybox" )
				.add(
						new OptionFormElementBuilder().text( "Inner text" ).value( "one" ).css( "one", "two" ).attribute( "data-role", "item" )
				)
				.add(
						new OptionFormElementBuilder().label( "Short two" ).text( "Some text" ).value( 2 ).selected().disabled()
				);

		expect(
				"<div id='options-mybox' data-bootstrapui-adapter-type='container'>" +
						"<div class='one two custom-control custom-switch' data-role='item' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' value='one' id='mybox' name='mybox' />" +
						"<label class='custom-control-label' for='mybox'>Inner text</label></div>" +
						"<div class='custom-control custom-switch' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' value='2' checked='checked' disabled='disabled' name='mybox' id='mybox1' />" +
						"<label class='custom-control-label' for='mybox1'>Short two</label></div>" +
						"</div>"
		);
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
