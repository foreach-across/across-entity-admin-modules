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

import org.junit.Test;

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
				"<label class='control-label'>Component name</label>"
		);
	}

	@Test
	public void fixedTargetId() {
		LabelFormElement box = new LabelFormElement();
		box.setTarget( "fixedId" );
		box.setText( "Component name" );

		renderAndExpect(
				box,
				"<label for='fixedId' class='control-label'>Component name</label>"
		);
	}

	@Test
	public void targetFormElement() {

	}

	@Test
	public void targetFormElementRenderedBefore() {

	}

	@Test
	public void targetFormElementRenderedAfter() {

	}

	@Test
	public void customViewElementTarget() {

	}
}
