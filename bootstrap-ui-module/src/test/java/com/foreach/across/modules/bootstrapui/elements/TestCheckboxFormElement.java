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
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestCheckboxFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void defaultBox() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='custom-control-label' for='boxName'>label text</label>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void defaultBoxAsNonCustom() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setRenderAsCustomControl( false );

		renderAndExpect(
				box,
				"<div class='form-check' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='form-check-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='form-check-label' for='boxName'>label text</label>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void unwrappedWithLabel() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label for='boxName'>label text</label>" +
						"<input type='hidden' name='_boxName' value='on' />"
		);
	}

	@Test
	public void unwrappedWithoutLabel() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<input type='hidden' name='_boxName' value='on' />"
		);
	}

	@Test
	public void withoutLabel() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input position-static' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label for='boxName' class='custom-control-label'></label>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void checked() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setValue( true );
		box.setChecked( true );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input position-static' type='checkbox' value='true' checked='checked' />" +
						"<label class='custom-control-label'></label>" +
						"</div>"
		);
	}

	@Test
	public void disabled() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "myBox" );
		box.setValue( "on" );
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input position-static' type='checkbox' name='myBox' id='myBox' value='on' disabled='disabled' />" +
						"<label for='myBox' class='custom-control-label'></label>" +
						"<input type='hidden' name='_myBox' value='on' disabled='disabled' />" +
						"</div>"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input position-static' type='checkbox' name='myBox' id='myBox' value='on' readonly='readonly' />" +
						"<label for='myBox' class='custom-control-label'></label>" +
						"<input type='hidden' name='_myBox' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void additionalLabelText() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.addChild( new NodeViewElement( "strong" ) );

		renderAndExpect(
				box,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='custom-control-label' for='boxName'>label text</label>" +
						"<strong></strong>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheWrapperIfPresent() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );

		renderAndExpect(
				box,
				"<div class='one two custom-control custom-checkbox' data-role='item' data-bootstrapui-adapter-type='checkbox'>" +
						"<input class='custom-control-input' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label class='custom-control-label' for='boxName'>label text</label>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}

	@Test
	public void attributesAreAddedToTheInputIfNotWrapped() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setText( "label text" );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' class='one two' data-role='item' type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<label for='boxName'>label text</label>" +
						"<input type='hidden' name='_boxName' value='on' />"
		);
	}

	@Test
	public void attributesAreAddedToTheInputIfNotWrappedAndNoLabel() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setControlName( "boxName" );
		box.setValue( 123 );
		box.setAttribute( "data-role", "item" );
		box.addCssClass( "one", "two" );
		box.setWrapped( false );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='checkbox' type='checkbox' id='boxName' name='boxName' class='one two' data-role='item' value='123' />" +
						"<input type='hidden' name='_boxName' value='on' />"
		);
	}

	@Test
	public void updateControlName() {
		CheckboxFormElement control = new CheckboxFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input class='custom-control-input position-static' type='checkbox' id='two' name='two' />"
						+ "<label for='two' class='custom-control-label'></label>"
						+ "<input type='hidden' name='_two' value='on' />"
						+ "</div>"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new CheckboxFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>"
						+ "<input class='custom-control-input position-static' type='checkbox' id='prefix.one' name='prefix.one' />"
						+ "<label for='prefix.one' class='custom-control-label'></label>"
						+ "<input type='hidden' name='_prefix.one' value='on' />"
						+ "</div>"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
