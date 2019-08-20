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

package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestToggleFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void defaultBox() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setText( "label text" );
		box.setValue( 123 );

		renderAndExpect(
				box,
				"<div class='custom-control custom-switch' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='custom-control-label' for='boxName'>label text</label>" +
						"</div>"
		);
	}

	@Test
	public void defaultBoxAsNonCustomControl() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setText( "label text" );
		box.setValue( 123 );
		box.setRenderAsCustomControl( false );

		renderAndExpect(
				box,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='form-check-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='form-check-label' for='boxName'>label text</label>" +
						"</div>"
		);
	}

	@Test
	public void unwrappedWithLabel() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label for='boxName'>label text</label>"
		);
	}

	@Test
	public void unwrappedWithoutLabel() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' value='123' />"
		);
	}

	@Test
	public void checked() {
		ToggleFormElement box = new ToggleFormElement();
		box.setValue( true );
		box.setChecked( true );

		renderAndExpect(
				box,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='form-check-input position-static' type='checkbox' value='true' checked='checked' />" +
						"</div>"
		);
	}

	@Test
	public void disabled() {
		ToggleFormElement box = new ToggleFormElement();
		box.setValue( "on" );
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='form-check-input position-static' type='checkbox' value='on' disabled='disabled' />" +
						"</div>"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='form-check-input position-static' type='checkbox' value='on' readonly='readonly' />" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheWrapperIfPresent() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );

		renderAndExpect(
				box,
				"<div class='one two custom-control custom-switch' data-role='item' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='custom-control-label' for='boxName'>label text</label>" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheInputIfNotWrapped() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' class='one two' type='checkbox' id='boxName' name='boxName' value='123' data-role='item' />" +
						"<label for='boxName'>label text</label>"
		);
	}

	@Test
	public void attributesAreAddedToTheInputIfNotWrappedAndNoLabel() {
		ToggleFormElement box = new ToggleFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' class='one two' data-role='item' value='123' />"
		);
	}

	@Test
	public void updateControlName() {
		ToggleFormElement control = new ToggleFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input class='form-check-input position-static' type='checkbox' id='two' name='two' />"
						+ "</div>"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new ToggleFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input class='form-check-input position-static' type='checkbox' id='prefix.one' name='prefix.one' />"
						+ "</div>"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
