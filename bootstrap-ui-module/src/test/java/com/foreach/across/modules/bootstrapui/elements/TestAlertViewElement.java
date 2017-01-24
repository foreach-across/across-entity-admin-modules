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

import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertTrue;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class TestAlertViewElement extends AbstractBootstrapViewElementTest
{
	private AlertViewElement alert;

	@Before
	public void setUp() throws Exception {
		alert = new AlertViewElement();
	}

	@Test
	public void emptyByDefault() {
		renderAndExpect(
				alert,
				"<div class='alert' role='alert'></div>"
		);
	}

	@Test
	public void customChild() {
		alert.setAttribute( "data-test", "text" );
		alert.addChild( new TextViewElement( "custom text child" ) );

		renderAndExpect(
				alert,
				"<div class='alert' role='alert' data-test='text'>custom text child</div>"
		);
	}

	@Test
	public void simpleText() {
		alert.setText( "Simple alert text" );
		assertTrue( alert.hasChildren() );

		renderAndExpect(
				alert,
				"<div class='alert' role='alert'>Simple alert text</div>"
		);
	}

	@Test
	public void alertWithStyle() {
		alert.setStyle( Style.DANGER );
		alert.setText( "another alert text" );

		renderAndExpect( alert, "<div class='alert alert-danger' role='alert'>another alert text</div>" );

		alert.setStyle( Style.WARNING );
		renderAndExpect( alert, "<div class='alert alert-warning' role='alert'>another alert text</div>" );

		alert.setStyle( null );
		renderAndExpect( alert, "<div class='alert' role='alert'>another alert text</div>" );
	}

	@Test
	public void dismissibleWithDefaultLabel() {
		alert.setDismissible( true );
		alert.setText( "alert text" );

		renderAndExpect(
				alert,
				"<div class='alert alert-dismissible' role='alert'>" +
						"<button type='button' class='close' data-dismiss='alert' aria-label='Close'>" +
						"<span aria-hidden='true'>&times;</span>" +
						"</button>" +
						"alert text" +
						"</div>"
		);
	}

	@Test
	public void dismissibleWithCustomChildAndLabel() {
		alert.setDismissible( true );
		alert.setCloseLabel( "Sluiten" );
		alert.addChild( new TextViewElement( "alert text" ) );

		renderAndExpect(
				alert,
				"<div class='alert alert-dismissible' role='alert'>" +
						"<button type='button' class='close' data-dismiss='alert' aria-label='Sluiten'>" +
						"<span aria-hidden='true'>&times;</span>" +
						"</button>" +
						"alert text" +
						"</div>"
		);
	}

	@Override
	protected Collection<String> allowedXmlEntities() {
		return Collections.singleton("times");
	}
}
