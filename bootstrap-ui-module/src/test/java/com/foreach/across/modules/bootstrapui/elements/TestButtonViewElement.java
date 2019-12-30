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

import com.foreach.across.modules.bootstrapui.elements.builder.ButtonViewElementBuilder;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 */
public class TestButtonViewElement extends AbstractBootstrapViewElementTest
{
	@Test
	public void customCssWithStyleRule() {
		renderAndExpect(
				bootstrap.button( css.button.outline.primary ).setText( "button" ),
				"<button type='button' class='btn btn-outline-primary'>button</button>"
		);
	}

	@Test
	public void styleAndStyleRuleInteroperability() {
		ButtonViewElement button = bootstrap.button();
		assertThat( button.getStyle() ).isEqualTo( Style.Button.DEFAULT );
		renderAndExpect( button, "<button type='button' class='btn'></button>" );

		button.setStyle( null );
		assertThat( button.getStyle() ).isNull();
		renderAndExpect( button, "<button type='button' class=''></button>" );

		button.set( css.button.warning );
		assertThat( button.getStyle() ).isEqualTo( Style.WARNING );
		renderAndExpect( button, "<button type='button' class='btn btn-warning'></button>" );

		button.setStyle( Style.INFO );
		assertThat( button.getStyle() ).isEqualTo( Style.INFO );
		renderAndExpect( button, "<button type='button' class='btn btn-info'></button>" );

		button.set( css.button.outline.primary, css.button.dark, css.button.outline.success );
		assertThat( button.getStyle() ).isEqualTo( Style.SUCCESS );
		renderAndExpect( button, "<button type='button' class='btn btn-outline-success'></button>" );
	}

	@Test
	public void buttonTypes() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );

		renderAndExpect( button, "<button type='button' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		renderAndExpect( button, "<button type='submit' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_RESET );
		renderAndExpect( button, "<button type='reset' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button, "<input type='button' class='btn' value='Link button' />" );

		button.setType( ButtonViewElement.Type.INPUT_SUBMIT );
		renderAndExpect( button, "<input type='submit' class='btn' value='Link button' />" );

		button.setType( ButtonViewElement.Type.INPUT_RESET );
		renderAndExpect( button, "<input type='reset' class='btn' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void nameAndValue() {
		ButtonViewElement button = new ButtonViewElement();
		button.setControlName( "bouton" );
		button.setValue( "123" );
		button.setText( "Link button" );

		renderAndExpect( button,
		                 "<button id='bouton' type='button' value='123' name='bouton' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		renderAndExpect( button,
		                 "<button id='bouton' name='bouton' value='123' type='submit' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.BUTTON_RESET );
		renderAndExpect( button,
		                 "<button id='bouton' name='bouton' value='123' type='reset' class='btn'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button,
		                 "<input id='bouton' name='bouton' type='button' class='btn' value='123' />" );

		button.setType( ButtonViewElement.Type.INPUT_SUBMIT );
		renderAndExpect( button,
		                 "<input id='bouton' name='bouton' type='submit' class='btn' value='123' />" );

		button.setType( ButtonViewElement.Type.INPUT_RESET );
		renderAndExpect( button,
		                 "<input id='bouton' name='bouton' type='reset' class='btn' value='123' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button,
		                 "<a id='bouton' data-value='123' class='btn' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void buttonStyles() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );

		button.setStyle( Style.Button.DANGER );
		renderAndExpect( button, "<button type='button' class='btn btn-danger'>Link button</button>" );

		button.setStyle( null );
		button.set( css.button, css.of( "custom-style" ) );
		renderAndExpect( button, "<button type='button' class='btn custom-style'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setStyle( Style.Button.PRIMARY );
		button.remove( css.of( "custom-style" ) );
		renderAndExpect( button, "<input type='button' class='btn btn-primary' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setStyle( Style.Button.LINK );
		renderAndExpect( button, "<a class='btn btn-link' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void buttonStylesWithBuilder() {
		ButtonViewElement button = new ButtonViewElementBuilder()
				.style( Style.DANGER )
				.css( "test", "extra-css", "other" )
				.style( Style.SECONDARY )
				.removeCss( "other" )
				.build();

		renderAndExpect(
				button,
				"<button type='button' class='btn btn-secondary test extra-css' />"
		);
	}

	@Test
	public void buttonSizes() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );
		button.setStyle( Style.Button.DANGER );

		button.setSize( Size.LARGE );
		renderAndExpect( button, "<button type='button' class='btn btn-danger btn-lg'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setSize( Size.EXTRA_SMALL );
		renderAndExpect( button, "<input type='button' class='btn btn-danger btn-xs' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setSize( Size.SMALL );
		renderAndExpect( button, "<a class='btn btn-danger btn-sm' href='#' role='button'>Link button</a>" );

		button.setSize( Size.DEFAULT );
		renderAndExpect( button, "<a class='btn btn-danger' href='#' role='button'>Link button</a>" );

		button.setSize( null );
		renderAndExpect( button, "<a class='btn btn-danger' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void blockLevel() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "Link button" );
		button.setStyle( Style.Button.DANGER );

		button.setSize( Size.LARGE.asBlock() );
		renderAndExpect( button, "<button type='button' class='btn btn-danger btn-lg btn-block'>Link button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		button.setSize( Size.EXTRA_SMALL.asBlock() );
		renderAndExpect( button,
		                 "<input type='button' class='btn btn-danger btn-xs btn-block' value='Link button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		button.setSize( Size.SMALL.asBlock() );
		renderAndExpect( button, "<a class='btn btn-danger btn-sm btn-block' href='#' role='button'>Link button</a>" );

		button.setSize( Size.BLOCK );
		renderAndExpect( button, "<a class='btn btn-danger btn-block' href='#' role='button'>Link button</a>" );

		button.setSize( Size.BLOCK.asBlock() );
		renderAndExpect( button, "<a class='btn btn-danger btn-block' href='#' role='button'>Link button</a>" );
	}

	@Test
	public void activeState() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "active button" );
		button.setState( ButtonViewElement.State.ACTIVE );

		renderAndExpect( button, "<button type='button' class='btn active'>active button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button, "<input type='button' class='btn active' value='active button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn active' href='#' role='button'>active button</a>" );
	}

	@Test
	public void disabledState() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "disabled button" );
		button.setState( ButtonViewElement.State.DISABLED );

		renderAndExpect( button,
		                 "<button type='button' class='btn' disabled='disabled'>disabled button</button>" );

		button.setType( ButtonViewElement.Type.INPUT );
		renderAndExpect( button,
		                 "<input type='button' class='btn' disabled='disabled' value='disabled button' />" );

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button, "<a class='btn disabled' href='#' role='button'>disabled button</a>" );

		button.setDisabled( false );
		assertThat( button.getState() ).isNull();

		button.setDisabled( true );
		assertThat( button.getState() ).isEqualTo( ButtonViewElement.State.DISABLED );
	}

	@Test
	public void url() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( "click me" );
		button.setUrl( "http://go-somewhere.com" );

		renderAndExpect(
				button,
				"<button type='button' data-url='http://go-somewhere.com' class='btn'>click me</button>"
		);

		button.setType( ButtonViewElement.Type.LINK );
		renderAndExpect( button,
		                 "<a class='btn' href='http://go-somewhere.com' role='button'>click me</a>" );
	}

	@Test
	public void icon() {
		ButtonViewElement button = new ButtonViewElement();
		button.setText( " icon button" );
		button.setIcon( html.i( css.fa.brands( "barcode" ) ) );

		renderAndExpect( button,
		                 "<button type='button' class='btn'>" +
				                 "<i class='fab fa-barcode'></i> icon button" +
				                 "</button>" );

		button.setTitle( "icon button" );
		button.setText( null );

		renderAndExpect( button,
		                 "<button type='button' class='btn' title='icon button'>" +
				                 "<i class='fab fa-barcode'></i>" +
				                 "</button>" );
	}

	@Test
	public void updateControlName() {
		ButtonViewElement control = new ButtonViewElement();
		control.setControlName( "one" );
		render( control );
		control.setControlName( "two" );
		renderAndExpect(
				control,
				"<button id='two' type='button' name='two' class='btn' />"
		);

		assertThat( control.getControlName() ).isEqualTo( "two" );
	}

	@Test
	public void updateControlNameThroughContainer() {
		ContainerViewElement container = new ContainerViewElement();
		FormInputElement control = new ButtonViewElement();
		control.setControlName( "one" );
		render( control );
		container.addChild( control );

		BootstrapElementUtils.prefixControlNames( "prefix.", container );

		renderAndExpect(
				control,
				"<button id='prefix.one' type='button' name='prefix.one' class='btn' />"
		);

		assertThat( control.getControlName() ).isEqualTo( "prefix.one" );
	}
}
