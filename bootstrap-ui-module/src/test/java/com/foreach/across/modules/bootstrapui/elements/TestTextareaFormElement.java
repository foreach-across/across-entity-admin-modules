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
public class TestTextareaFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simpleElement() {
		TextareaFormElement box = new TextareaFormElement();
		box.setPlaceholder( "Text input" );
		box.setText( "some <strong class=\"test\">html</strong>" );

		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' placeholder='Text input' rows='3'>" +
						"some &lt;strong class=&quot;test&quot;&gt;html&lt;/strong&gt;" +
						"</textarea>"
		);
	}

	@Test
	public void transientErrorValueGetsRenderedOnceInsteadOfText() {
		TextareaFormElement box = new TextareaFormElement();
		box.setText( "text" );
		box.setAttribute( TextboxFormElementModelWriter.TRANSIENT_ERROR_VALUE_ATTRIBUTE, "bad value" );

		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3'>bad value</textarea>"
		);
		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3'>text</textarea>"
		);
	}

	@Test
	public void namedTextarea() {
		TextareaFormElement box = new TextareaFormElement();
		box.setName( "internalName" );
		box.setAutoSize( false );

		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' id='internalName' class='form-control' rows='3' name='internalName' />"
		);

		box.setControlName( "controlName" );
		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' id='controlName' class='form-control' rows='3' name='controlName' />"
		);
	}

	@Test
	public void disabledAndReadonly() {
		TextareaFormElement box = new TextareaFormElement();
		box.setDisabled( true );

		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3' disabled='disabled' />"
		);

		box.setDisabled( false );
		box.setReadonly( true );

		renderAndExpect(
				box,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3' readonly='readonly' />"
		);
	}

	@Test
	public void updateControlName() {
		TextareaFormElement control = new TextareaFormElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3' id='two' name='two' />"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new TextareaFormElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<textarea data-bootstrapui-adapter-type='basic' class='form-control js-autosize' rows='3' id='prefix.one' name='prefix.one' />"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
