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

import com.foreach.across.modules.bootstrapui.elements.thymeleaf.TextboxFormElementModelWriter;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Arne Vandamme
 */
public class TestTextboxFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simpleElement() {
		TextboxFormElement box = new TextboxFormElement();
		box.setPlaceholder( "Text input" );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' placeholder='Text input' />"
		);

		box.setType( TextboxFormElement.Type.EMAIL );
		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='email' class='form-control' placeholder='Text input' />"
		);

		box.setType( TextboxFormElement.Type.URL );
		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='url' class='form-control' placeholder='Text input' />"
		);
	}

	@Test
	public void text() {
		TextboxFormElement box = new TextboxFormElement();
		box.setText( "some \"text</input>" );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' value='some &quot;text&lt;/input&gt;' />"
		);
	}

	@Test
	public void transientErrorValueGetsRenderedOnceInsteadOfText() {
		TextboxFormElement box = new TextboxFormElement();
		box.setText( "text" );
		box.setAttribute( TextboxFormElementModelWriter.TRANSIENT_ERROR_VALUE_ATTRIBUTE, "bad value" );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' value='bad value' />"
		);
		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' value='text' />"
		);
	}

	@Test
	public void namedTextbox() {
		TextboxFormElement box = new TextboxFormElement();
		box.setName( "internalName" );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' id='internalName' class='form-control' name='internalName' />"
		);

		box.setControlName( "controlName" );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' id='controlName' class='form-control' name='controlName' />"
		);
	}

	@Test
	public void maxLength() {
		TextboxFormElement box = new TextboxFormElement();
		box.setMaxLength( 100 );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' maxlength='100' />"
		);
	}

	@Test
	public void disabledReadonlyRequired() {
		TextboxFormElement box = new TextboxFormElement();
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' disabled='disabled' />"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' readonly='readonly' />"
		);

		box.setRequired( true );

		renderAndExpect(
				box,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' readonly='readonly' required='required' />"
		);
	}

	@Test
	public void updateControlName() {
		TextboxFormElement control = new TextboxFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' id='two' name='two' />"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new TextboxFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' id='prefix.one' name='prefix.one' />"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
