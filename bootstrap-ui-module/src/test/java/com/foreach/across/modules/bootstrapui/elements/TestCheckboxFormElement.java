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

import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.Test;

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
				"<div class='checkbox'><label for='boxName'>" +
						"<input type='checkbox' id='boxName' name='boxName' value='123' />label text" +
						"</label>" +
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
				"<label for='boxName'>" +
						"<input type='checkbox' id='boxName' name='boxName' value='123' />label text" +
						"</label>" +
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
				"<input type='checkbox' id='boxName' name='boxName' value='123' />" +
						"<input type='hidden' name='_boxName' value='on' />"
		);
	}

	@Test
	public void checked() {
		CheckboxFormElement box = new CheckboxFormElement();
		box.setValue( true );
		box.setChecked( true );

		renderAndExpect(
				box,
				"<div class='checkbox'>" +
						"<input type='checkbox' value='true' checked='checked' />" +
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
				"<div class='checkbox disabled'>" +
						"<input type='checkbox' name='myBox' id='myBox' value='on' disabled='disabled' />" +
						"<input type='hidden' name='_myBox' value='on' disabled='disabled' />" +
						"</div>"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<div class='checkbox'>" +
						"<input type='checkbox' name='myBox' id='myBox' value='on' readonly='readonly' />" +
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
				"<div class='checkbox'><label for='boxName'>" +
						"<input type='checkbox' id='boxName' name='boxName' value='123' />label text<strong></strong>" +
						"</label>" +
						"<input type='hidden' name='_boxName' value='on' />" +
						"</div>"
		);
	}
}
