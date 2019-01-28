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
public class TestRadioFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void defaultBox() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setText( "label text" );
		box.setValue( 123 );

		renderAndExpect(
				box,
				"<div class='radio' data-bootstrapui-adapter-type='checkbox'><label for='boxName'>" +
						"<input type='radio' id='boxName' name='boxName' value='123' />label text" +
						"</label></div>"
		);
	}

	@Test
	public void unwrappedWithLabel() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<label data-bootstrapui-adapter-type='checkbox' for='boxName'>" +
						"<input type='radio' id='boxName' name='boxName' value='123' />label text" +
						"</label>"
		);
	}

	@Test
	public void unwrappedWithoutLabel() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='radio' id='boxName' name='boxName' value='123' />"
		);
	}

	@Test
	public void checked() {
		RadioFormElement box = new RadioFormElement();
		box.setValue( true );
		box.setChecked( true );

		renderAndExpect(
				box,
				"<div class='radio' data-bootstrapui-adapter-type='checkbox'>" +
						"<input type='radio' value='true' checked='checked' />" +
						"</div>"
		);
	}

	@Test
	public void disabled() {
		RadioFormElement box = new RadioFormElement();
		box.setValue( "on" );
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<div class='radio disabled' data-bootstrapui-adapter-type='checkbox'>" +
						"<input type='radio' value='on' disabled='disabled' />" +
						"</div>"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<div class='radio' data-bootstrapui-adapter-type='checkbox'>" +
						"<input type='radio' value='on' readonly='readonly' />" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheWrapperIfPresent() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );

		renderAndExpect(
				box,
				"<div class='one two radio' data-role='item' data-bootstrapui-adapter-type='checkbox'><label for='boxName'>" +
						"<input type='radio' id='boxName' name='boxName' value='123' />label text</label>" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheLabelIfNotWrapped() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<label data-bootstrapui-adapter-type='checkbox' for='boxName' class='one two' data-role='item'>" +
						"<input type='radio' id='boxName' name='boxName' value='123' />label text</label>"
		);
	}

	@Test
	public void attributesAreAddedToTheInputIfNotWrappedAndNoLabel() {
		RadioFormElement box = new RadioFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='radio' id='boxName' name='boxName' class='one two' data-role='item' value='123' />"
		);
	}

	@Test
	public void updateControlName() {
		RadioFormElement control = new RadioFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<div class='radio' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input data-bootstrapui-adapter-type='checkbox' type='radio' id='two' name='two' />"
						+ "</div>"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new RadioFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<div class='radio' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input type='radio' id='prefix.one' name='prefix.one' />"
						+ "</div>"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
