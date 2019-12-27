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
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.Before;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestInputGroupFormElement extends AbstractBootstrapViewElementTest
{
	private InputGroupFormElement inputGroup;

	@Before
	public void before() {
		inputGroup = new InputGroupFormElement();
	}

	@Test
	public void emptyInputGroup() {
		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void inputGroupWithOnlyControl() {
		inputGroup.setControl( new SelectFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<select data-bootstrapui-adapter-type='select' class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void simpleAddonBefore() {
		inputGroup.setPrepend( html.i( css.fa.solid( "calendar" ) ) );

		assertNull( inputGroup.getAppend() );
		assertNotNull( inputGroup.getPrepend() );
		assertNotNull( inputGroup.getPrepend( NodeViewElement.class ) );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<div class='input-group-prepend'>"
						+ "<i class='fas fa-calendar'></i>"
						+ "</div>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "</div>"
		);
	}

	@Test
	public void simpleAddonAfter() {
		inputGroup.setAppend( html.i( css.fa.solid( "calendar" ) ) );

		assertNull( inputGroup.getPrepend() );
		assertNotNull( inputGroup.getAppend() );
		assertNotNull( inputGroup.getAppend( NodeViewElement.class ) );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "<div class='input-group-append'>"
						+ "<i class='fas fa-calendar'></i>"
						+ "</div>"
						+ "</div>"
		);
	}

	@Test
	public void textAddonIsWrapped() {
		inputGroup.setPrepend( html.text( "before" ) );
		inputGroup.setAppend( html.text( "after" ) );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<div class='input-group-prepend'><span class='input-group-text'>before</span></div>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "<div class='input-group-append'><span class='input-group-text'>after</span></div>"
						+ "</div>"
		);
	}

	@Test
	public void buttonBefore() {
		inputGroup.setPrepend( new ButtonViewElement() );
		inputGroup.setAppend( html.i( css.fa.solid( "calendar" ) ) );
		inputGroup.setControl( new TextboxFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<div class='input-group-prepend'>"
						+ "<button type='button' class='btn btn-light' />"
						+ "</div>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "<div class='input-group-append'>"
						+ "<i class='fas fa-calendar'></i>"
						+ "</div>"
						+ "</div>"
		);
	}

	@Test
	public void buttonAfter() {
		inputGroup.setAppend( new ButtonViewElement() );
		inputGroup.setPrepend( html.i( css.fa.solid( "calendar" ) ) );
		inputGroup.setControl( new TextboxFormElement() );

		renderAndExpect(
				inputGroup,
				"<div class='input-group'>"
						+ "<div class='input-group-prepend'>"
						+ "<i class='fas fa-calendar'></i>"
						+ "</div>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />"
						+ "<div class='input-group-append'>"
						+ "<button type='button' class='btn btn-light' />"
						+ "</div>"
						+ "</div>"
		);
	}

	@Test
	public void updateControlName() {
		InputGroupFormElement control = inputGroup;
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<div class='input-group'>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='two' id='two' />"
						+ "</div>"
		);

		assertEquals( "two", control.getControlName() );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = inputGroup;
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<div class='input-group'>"
						+ "<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='prefix.one' id='prefix.one' />"
						+ "</div>"
		);

		assertEquals( "prefix.one", control.getControlName() );
	}
}
