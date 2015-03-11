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
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Arne Vandamme
 */
public class TestFormGroupElement extends AbstractBootstrapViewElementTest
{
	private FormGroupElement group;

	@Before
	public void before() {
		group = new FormGroupElement();

		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "control" );

		LabelFormElement label = new LabelFormElement();
		label.setTarget( textbox );
		label.setText( "title" );

		group.setLabel( label );
		group.setControl( textbox );
	}

	@Test
	public void simpleGroup() {
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void withHelpText() {
		NodeViewElement help = NodeViewElement.forTag( "p" );
		help.setAttribute( "class", "help-block" );
		help.add( new TextViewElement( "example help text" ) );

		group.add( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='control-label'>title</label>" +
						"<input type='text' class='form-control' name='control' id='control' />" +
						"<p class='help-block'>example help text</p>" +
						"</div>"
		);
	}
}
