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
import com.foreach.across.modules.bootstrapui.elements.SelectFormElementConfiguration;
import com.foreach.across.modules.web.support.MessageCodeSupportingLocalizedTextResolver;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestOptionsAsSelectBuilder extends AbstractBootstrapViewElementTest
{
	protected OptionsFormElementBuilder builder;

	protected DefaultViewElementBuilderContext builderContext;

	@Before
	public void reset() {
		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setLocalizedTextResolver( new MessageCodeSupportingLocalizedTextResolver( null ) );

		builder = new OptionsFormElementBuilder();
	}

	@Test
	public void simple() {
		builder.htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<select name='boxName' id='mybox' class='form-control' data-bootstrapui-adapter-type='select'/>"
		);
	}

	@Test
	public void multiple() {
		builder.multiple();

		expect(
				"<select class='form-control' multiple='multiple' data-bootstrapui-adapter-type='select'/>"
		);
	}

	@Test
	public void asBootstrapSelect() {
		SelectFormElementConfiguration configuration = new SelectFormElementConfiguration();
		configuration.setDeselectAllText( null );
		configuration.setSelectAllText( null );
		configuration.setCountSelectedText( null );
		configuration.setMaxOptionsText( null );
		configuration.setNoneSelectedText( null );

		builder.select( configuration ).multiple().htmlId( "mybox" ).controlName( "boxName" );

		expect(
				"<select name='boxName' id='mybox' multiple='multiple' class='form-control' data-bootstrapui-select='{&quot;dropupAuto&quot;:false}' data-bootstrapui-adapter-type='bootstrap-select'/>"
		);
	}

	@Test
	public void disabledAndReadonly() {
		builder.disabled();

		expect(
				"<select class='form-control' disabled='disabled' data-bootstrapui-adapter-type='select'/>"
		);

		builder.disabled( false ).readonly();

		expect(
				"<select class='form-control' readonly='readonly' data-bootstrapui-adapter-type='select'/>"
		);
	}

	@Test
	public void options() {
		builder
				.add( new OptionFormElementBuilder().text( "Inner text" ).value( "one" ).css( "one", "two" ).attribute( "data-role", "item" ) )
				.add( new OptionFormElementBuilder().label( "Only label" ).value( 123 ) )
				.add( new OptionFormElementBuilder().label( "Short two" ).text( "Some text" ).selected().disabled() );

		expect(
				"<select class='form-control' data-bootstrapui-adapter-type='select'>" +
						"<option class='one two' data-role='item' value='one'>Inner text</option>" +
						"<option value='123'>Only label</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"</select>"
		);
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}
}
