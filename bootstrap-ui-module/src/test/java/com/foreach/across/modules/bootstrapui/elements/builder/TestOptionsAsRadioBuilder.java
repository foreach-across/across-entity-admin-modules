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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.AbstractBootstrapViewElementTest;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestOptionsAsRadioBuilder extends AbstractBootstrapViewElementTest
{
	protected OptionsFormElementBuilder builder;

	protected ViewElementBuilderContext builderContext;

	@Before
	public void reset() {
		builderContext = new DefaultViewElementBuilderContext();

		builder = new OptionsFormElementBuilder().radio();
	}

	@Test
	public void simple() {
		builder.htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<div id='mybox' />"
		);
	}

//	@Test
//	public void multiple() {
//		builder.multiple();
//
//		expect(
//				"<select class='form-control' multiple='multiple' />"
//		);
//	}
//
//	@Test
//	public void disabledAndReadonly() {
//		builder.disabled();
//
//		expect(
//				"<select class='form-control' disabled='disabled' />"
//		);
//
//		builder.disabled( false ).readonly();
//
//		expect(
//				"<select class='form-control' readonly='readonly' />"
//		);
//	}

	@Test
	public void options() {
		builder
				.controlName( "mybox" )
				.add( new OptionFormElementBuilder().text( "Inner text" ).value( "one" ).css( "one", "two" ).attribute( "data-role", "item" ) )
				.add( new OptionFormElementBuilder().label( "Short two" ).text( "Some text" ).value( 2 ).selected()
				                                    .disabled() );

		expect(
				"<div id='options-mybox'>" +
						"<div class='one two radio' data-role='item'><label for='mybox'>" +
						"<input type='radio' value='one' id='mybox' name='mybox' /> Inner text" +
						"</label></div>" +
						"<div class='radio disabled'><label for='mybox1'>" +
						"<input type='radio' value='2' checked='checked' disabled='disabled' name='mybox' id='mybox1' /> Short two" +
						"</label></div>" +
						"</div>"
		);
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
