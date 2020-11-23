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
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Arne Vandamme
 */
public class TestHiddenFormElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void simple() {
		HiddenFormElement hidden = new HiddenFormElement();

		renderAndExpect(
				hidden,
				"<input type='hidden' />"
		);
		renderAndExpect(
				hidden.toFormControl(),
				"<input type='hidden' />"
		);

		assertNull( hidden.getValue() );
	}

	@Test
	public void withAttributes() {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( "inputName" );
		hidden.setValue( 123 );
		hidden.setHtmlId( "_id" );
		hidden.setDisabled( true );

		renderAndExpect(
				hidden,
				"<input type='hidden' id='_id' name='inputName' value='123' disabled='disabled' />"
		);

		assertEquals( Integer.valueOf( 123 ), hidden.getValue( Integer.class ) );

		renderAndExpect(
				hidden.toFormControl(),
				"<input type='hidden' id='_id' name='inputName' value='123' disabled='disabled' />"
		);
	}

	@Test
	public void updateControlName() {
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( "one" );
		render( hidden );
		hidden.setControlName( "two" );
		renderAndExpect(
				hidden,
				"<input type='hidden' name='two' />"
		);

		assertEquals( "two", hidden.getControlName() );

		renderAndExpect(
				hidden.toFormControl(),
				"<input type='hidden' name='two' id='two' />"
		);
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		HiddenFormElement hidden = new HiddenFormElement();
		hidden.setControlName( "one" );
		render( hidden );
		container.addChild( hidden );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				hidden,
				"<input type='hidden' name='prefix.one' />"
		);

		assertEquals( "prefix.one", hidden.getControlName() );

		container = new ContainerViewElement();
		hidden = new HiddenFormElement();
		hidden.setControlName( "one" );
		FormControlElement control = hidden.toFormControl();
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<input type='hidden' name='prefix.one' id='prefix.one' />"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
