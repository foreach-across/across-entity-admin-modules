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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestSelectFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		SelectFormElement box = new SelectFormElement();
		box.setHtmlId( null );
		box.setControlName( "boxName" );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' name='boxName' class='form-control' />"
		);
	}

	@Test
	public void simpleAsBootstrapSelect() {
		SelectFormElement box = new SelectFormElement();
		box.setHtmlId( null );
		box.setConfiguration( new SelectFormElementConfiguration() );
		box.setControlName( "boxName" );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='bootstrap-select' name='boxName' class='form-control' data-bootstrapui-select='{&quot;dropupAuto&quot;:false}' />"
		);
	}

	@Test
	public void multiple() {
		SelectFormElement box = new SelectFormElement();
		box.setMultiple( true );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' class='form-control' multiple='multiple' />"
		);
	}

	@Test
	public void disabledAndReadonly() {
		SelectFormElement box = new SelectFormElement();
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' class='form-control' disabled='disabled' />"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' class='form-control' readonly='readonly' />"
		);
	}

	@Test
	public void options() {
		SelectFormElement box = new SelectFormElement();

		SelectFormElement.Option one = new SelectFormElement.Option();
		one.setValue( "one" );
		one.setText( "Inner text" );

		SelectFormElement.Option two = new SelectFormElement.Option();
		two.setLabel( "Short two" );
		two.setText( "Some text" );
		two.setSelected( true );
		two.setDisabled( true );

		SelectFormElement.Option three = new SelectFormElement.Option();
		three.setValue( 123 );
		three.setLabel( "Label only" );
		three.addCssClass( "one", "two" );

		box.addChild( one );
		box.addChild( two );
		box.addChild( three );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' class='form-control'>" +
						"<option value='one'>Inner text</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"<option class='one two' value='123'>Label only</option>" +
						"</select>"
		);
	}

	@Test
	public void optionGroups() {
		SelectFormElement box = new SelectFormElement();
		box.setMultiple( true );
		box.setName( "internalName" );
		box.setControlName( "controlName" );
		box.setReadonly( true );

		SelectFormElement.OptionGroup group = new SelectFormElement.OptionGroup();
		SelectFormElement.Option one = new SelectFormElement.Option();
		one.setValue( "one" );
		one.setText( "Inner text" );

		SelectFormElement.Option two = new SelectFormElement.Option();
		two.setLabel( "Short two" );
		two.setText( "Some text" );
		two.setSelected( true );
		two.setDisabled( true );

		group.addChild( one );
		group.addChild( two );

		SelectFormElement.OptionGroup groupTwo = new SelectFormElement.OptionGroup();
		groupTwo.setDisabled( true );
		groupTwo.setLabel( "some label" );

		box.addChild( two );
		box.addChild( group );
		box.addChild( groupTwo );
		box.addChild( one );

		renderAndExpect(
				box,
				"<select data-bootstrapui-adapter-type='select' id='controlName' class='form-control' name='controlName' multiple='multiple' readonly='readonly'>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"<optgroup>" +
						"<option value='one'>Inner text</option>" +
						"<option label='Short two' selected='selected' disabled='disabled'>Some text</option>" +
						"</optgroup>" +
						"<optgroup disabled='disabled' label='some label'></optgroup>" +
						"<option value='one'>Inner text</option>" +
						"</select>"
		);
	}

	@Test
	public void updateControlName() {
		SelectFormElement control = new SelectFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<select data-bootstrapui-adapter-type='select' name='two' id='two' class='form-control' />"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new SelectFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<select data-bootstrapui-adapter-type='select' name='prefix.one' id='prefix.one' class='form-control' />"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
