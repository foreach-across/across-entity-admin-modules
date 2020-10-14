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

import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.jupiter.api.Test;

/**
 * @author Arne Vandamme
 */
public class TestLabelFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		LabelFormElement box = new LabelFormElement();
		box.setText( "Component name" );

		renderAndExpect(
				box,
				"<label>Component name</label>"
		);
	}

	@Test
	public void fixedTargetId() {
		LabelFormElement box = new LabelFormElement();
		box.setTarget( "fixedId" );
		box.setText( "Component name" );

		renderAndExpect(
				box,
				"<label for='fixedId'>Component name</label>"
		);
	}

	@Test
	public void targetFormElement() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		renderAndExpect(
				label,
				"<label for='name'>Textbox title</label>"
		);
	}

	@Test
	public void targetInputGroup() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		InputGroupFormElement inputGroup = new InputGroupFormElement();
		inputGroup.setControl( textbox );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( inputGroup );
		label.setText( "InputGroup title" );

		renderAndExpect(
				label,
				"<label for='name'>InputGroup title</label>"
		);
	}

	@Test
	public void targetFormElementRenderedBefore() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		ContainerViewElement container = new ContainerViewElement();
		container.addChild( textbox );
		container.addChild( label );

		renderAndExpect(
				container,
				"<input data-bootstrapui-adapter-type='basic' class='form-control' type='text' name='name' id='name' />" +
						"<label for='name'>Textbox title</label>"
		);
	}

	@Test
	public void targetFormElementRenderedAfter() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );

		ContainerViewElement container = new ContainerViewElement();
		container.addChild( label );
		container.addChild( textbox );

		renderAndExpect(
				container,
				"<label for='name'>Textbox title</label>" +
						"<input data-bootstrapui-adapter-type='basic' class='form-control' type='text' name='name' id='name' />"
		);
	}

	@Test
	public void targetFormElementRenderedAsChild() {
		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "name" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "Textbox title" );
		label.addChild( textbox );

		renderAndExpect(
				label,
				"<label for='name'>" +
						"Textbox title" +
						"<input data-bootstrapui-adapter-type='basic' class='form-control' type='text' name='name' id='name' />" +
						"</label>"
		);
	}

	@Test
	public void simpleFormElementRenderedAsChild() {
		LabelFormElement label = new LabelFormElement();
		label.addChild( new TextboxFormElement() );

		renderAndExpect(
				label,
				"<label>" +
						"<input data-bootstrapui-adapter-type='basic' class='form-control' type='text' />" +
						"</label>"
		);
	}

	@Test
	public void customViewElementTarget() {
		StaticFormElement staticContent = new StaticFormElement();
		staticContent.setHtmlId( "static" );
		staticContent.setText( "static content" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( staticContent );
		label.setText( "title" );

		ContainerViewElement container = new ContainerViewElement();
		container.addChild( label );
		container.addChild( staticContent );

		renderAndExpect(
				container,
				"<label for='static'>title</label>" +
						"<p class='form-control-static' id='static'>static content</p>"
		);
	}

	@Test
	public void customForAttribute() {
		LabelFormElement label = new LabelFormElement();
		label.setAttribute( "for", "custom-target" );
		label.setText( "title" );

		renderAndExpect( label, "<label for='custom-target'>title</label>" );
	}
}
