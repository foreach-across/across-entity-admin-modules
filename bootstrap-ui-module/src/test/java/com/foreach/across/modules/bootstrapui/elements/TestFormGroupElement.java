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
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TemplateViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.test.modules.webtest.controllers.RenderViewElementController;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class TestFormGroupElement extends AbstractBootstrapViewElementTest
{
	private static final String INPUT_GROUP_ADDON = "<div class='input-group-prepend'>" +
			"<i class='fas fa-exclamation'></i>" +
			"</div>";

	private FormGroupElement group, groupWithInputGroup;

	@Before
	public void before() {
		group = new FormGroupElement();

		TextboxFormElement textbox = new TextboxFormElement();
		textbox.setName( "control" );

		LabelFormElement label = new LabelFormElement();
		label.setName( "label" );
		label.setTarget( textbox );
		label.setText( "title" );

		group.setLabel( label );
		group.setControl( textbox );

		groupWithInputGroup = new FormGroupElement();

		InputGroupFormElement inputGroupFormElement = new InputGroupFormElement();
		inputGroupFormElement.setControl( textbox );
		inputGroupFormElement.setPrepend( html.i( css.fa.solid( "exclamation" ) ) );

		LabelFormElement inputGroupLabel = new LabelFormElement();
		inputGroupLabel.setTarget( inputGroupFormElement );
		inputGroupLabel.setText( "title input group" );

		groupWithInputGroup.setLabel( inputGroupLabel );
		groupWithInputGroup.setControl( inputGroupFormElement );
	}

	@Test
	public void simpleGroup() {
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void customGroupFromBuilder() {
		renderAndExpect(
				bootstrap.builders.formGroup()
				                  .label( bootstrap.builders.label( "Name" ).target( "firstName" ) )
				                  .add( bootstrap.builders.hidden().controlName( "name" ).disabled( true ).mapToFormControl() )
				                  .add( bootstrap.builders.textbox().controlName( "firstName" ) )
				                  .add( bootstrap.builders.textbox().controlName( "lastName" ) )
				                  .build(),
				"<div class=\"form-group\">\n" +
						"    <label for=\"firstName\">Name</label>\n" +
						"    <input type=\"hidden\" name=\"name\" id=\"name\" disabled=\"disabled\" />\n" +
						"    <input data-bootstrapui-adapter-type='basic' type=\"text\" name=\"firstName\" id=\"firstName\" class=\"form-control\" />\n" +
						"    <input data-bootstrapui-adapter-type='basic' type=\"text\" name=\"lastName\" id=\"lastName\" class=\"form-control\" />\n" +
						"</div>"
		);
	}

	@Test
	public void anonymousControlOnBoundForm() {
		TestClass target = new TestClass( "test value" );
		BindingResult errors = new BeanPropertyBindingResult( target, "item" );

		FormViewElement form = new FormViewElement();
		form.setErrors( errors );

		group = new FormGroupElement();
		group.setControl( new TextboxFormElement() );
		form.addChild( group );

		renderAndExpect(
				form,
				this::sampleModelWithError,
				"<form method='post' role='form'>" +
						"<div class='form-group'>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' />" +
						"</div>" +
						"</form>"
		);
	}

	@Test
	public void formGroupWithoutControl() {
		group.getLabel( LabelFormElement.class ).setTarget( (ViewElement) null );
		group.setControl( new TemplateViewElement( "th/test/template :: value" ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label>title</label>" +
						"static template" +
						"</div>"
		);
	}

	@Test
	public void formGroupWithInputGroup() {
		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void requiredGroup() {
		group.setRequired( true );

		renderAndExpect(
				group,
				"<div class='form-group required'>" +
						"<label for='control'>title<sup class='required'>*</sup></label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void requiredGroupWithTooltip() {
		group.setRequired( true );
		group.setTooltip( TextViewElement.html( "<a>my tooltip</a>" ) );

		renderAndExpect(
				group,
				"<div class='form-group required'>" +
						"<label for='control'>title<sup class='required'>*</sup><a>my tooltip</a></label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void requiredGroupWithInputGroup() {
		groupWithInputGroup.setRequired( true );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group required'>" +
						"<label for='control'>title input group<sup class='required'>*</sup></label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void customTagAppended() {
		NodeViewElement div = new NodeViewElement( "div" );
		div.setAttribute( "class", "some-class" );
		div.addChild( new TextViewElement( "appended div" ) );

		group.addChild( div );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"<div class='some-class'>appended div</div>" +
						"</div>"
		);

		groupWithInputGroup.addChild( div );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"<div class='some-class'>appended div</div>" +
						"</div>"
		);
	}

	@Test
	public void withHelpTextOrDescription() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.addChild( new TextViewElement( "example help text" ) );

		NodeViewElement descr = new NodeViewElement( "p" );
		descr.addChild( TextViewElement.text( "description" ) );

		group.setHelpBlock( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>"
		);

		group.setDescriptionBlock( descr );
		group.setHelpBlock( null );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<p id='control.description'>description</p>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.description' />" +
						"</div>"
		);

		SelectFormElement select = new SelectFormElement();
		select.setName( "list" );

		group.setControl( select );
		group.setHelpBlock( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<p id='list.description'>description</p>" +
						"<select data-bootstrapui-adapter-type='select' class='form-control' name='list' id='list' aria-describedby='list.description list.help' />" +
						"<p class='help-block' id='list.help'>example help text</p>" +
						"</div>"
		);

		groupWithInputGroup.setHelpBlock( help );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"</div>" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormLayoutWithVisibleLabel() {
		group.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormLayoutWithHiddenLabel() {
		group.setFormLayout( FormLayout.inline( false ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='title' />" +
						"</div>"
		);

		group.getControl( TextboxFormElement.class ).setPlaceholder( "some placeholder" );
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='some placeholder' />" +
						"</div>"
		);

		group.getControl( TextboxFormElement.class ).setPlaceholder( "" );
		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='' />" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.inline( false ) );
		groupWithInputGroup.getControl( InputGroupFormElement.class )
		                   .getControl( TextboxFormElement.class )
		                   .setPlaceholder( null );
		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='title input group' />" +
						"</div>" +
						"</div>"
		);

		BootstrapElementUtils.getFormControl( groupWithInputGroup, TextboxFormElement.class )
		                     .setPlaceholder( "some placeholder" );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='some placeholder' />" +
						"</div>" +
						"</div>"
		);

		BootstrapElementUtils.getFormControl( groupWithInputGroup, TextboxFormElement.class )
		                     .setPlaceholder( "" );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title input group</label>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' placeholder='' />" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void inlineFormDoesNotVisuallyRenderHelp() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.addChild( new TextViewElement( "example help text" ) );

		group.setHelpBlock( help );
		group.setFormLayout( FormLayout.inline( true ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block sr-only' id='control.help'>example help text</p>" +
						"</div>"
		);

		group.setFormLayout( FormLayout.inline( false ) );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='sr-only'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.help' placeholder='title' />" +
						"<p class='help-block sr-only' id='control.help'>example help text</p>" +
						"</div>"
		);
	}

	@Test
	public void horizontalFormLayout() {
		NodeViewElement help = new NodeViewElement( "p" );
		help.setAttribute( "class", "help-block" );
		help.addChild( new TextViewElement( "example help text" ) );

		group.setFormLayout( FormLayout.horizontal( 2 ) );
		group.setHelpBlock( help );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<label for='control' class='col-md-2'>title</label>" +
						"<div class='col-md-10'>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' aria-describedby='control.help' />" +
						"<p class='help-block' id='control.help'>example help text</p>" +
						"</div>" +
						"</div>"
		);

		groupWithInputGroup.setFormLayout( FormLayout.horizontal( 6 ) );

		renderAndExpect(
				groupWithInputGroup,
				"<div class='form-group'>" +
						"<label for='control' class='col-md-6'>title input group</label>" +
						"<div class='col-md-6'>" +
						"<div class='input-group'>" +
						INPUT_GROUP_ADDON +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void horizontalFormLayoutWithoutLabel() {
		CheckboxFormElement checkbox = new CheckboxFormElement();
		checkbox.setText( "checkbox value" );

		group = new FormGroupElement();
		group.setFormLayout( FormLayout.horizontal( 2 ) );
		group.setControl( checkbox );

		renderAndExpect(
				group,
				"<div class='form-group'>" +
						"<div class='col-md-10 col-md-offset-2'>" +
						"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input type='checkbox' class='custom-control-input'/>" +
						"<label class='custom-control-label'>checkbox value</label>" +
						"</div>" +
						"</div>" +
						"</div>"
		);
	}

	@Test
	public void horizontalFormLayoutInheritedFromForm() {
		FormViewElement form = new FormViewElement();
		form.setFormLayout( FormLayout.horizontal( 2 ) );

		CheckboxFormElement checkbox = new CheckboxFormElement();
		checkbox.setText( "checkbox value" );

		group = new FormGroupElement();
		group.setControl( checkbox );
		group.setTooltip( TextViewElement.text( "(tooltip)" ) );

		form.addChild( group );

		renderAndExpect(
				form,
				"<form method='post' role='form' class='form-horizontal'><div class='form-group'>" +
						"<div class='col-md-10 col-md-offset-2'>" +
						"<div class='custom-control custom-checkbox' data-bootstrapui-adapter-type='checkbox'>" +
						"<input type=\"checkbox\" class=\"custom-control-input\"></input>" +
						" <label class=\"custom-control-label\">checkbox value(tooltip)</label>" +
						"</div>" +
						"</div>" +
						"</div></form>"
		);
	}

	@Test
	public void errorFromBoundObject() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		RenderViewElementController.Callback callback = this::sampleModelWithError;
		renderAndExpect(
				container,
				callback,
				"<div class='form-group is-invalid'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control is-invalid' name='control' id='control' value='test value' />" +
						"<div class='invalid-feedback'>broken</div>" +
						"</div>"
		);

		group.setDetectFieldErrors( false );
		renderAndExpect(
				container,
				callback,
				"<div class='form-group'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='control' id='control' />" +
						"</div>"
		);
	}

	@Test
	public void controlNameNotOnBoundObject() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		group.getControl( TextboxFormElement.class ).setControlName( "illegalProperty" );
		group.setDetectFieldErrors( false );

		renderAndExpect(
				container,
				this::sampleModelWithError,
				"<div class='form-group'>" +
						"<label for='illegalProperty'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' name='illegalProperty' id='illegalProperty' />" +
						"</div>"
		);
	}

	@Test
	public void complexControlNameWithError() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		group.getControl( TextboxFormElement.class ).setControlName( "values[sub.item].name" );

		renderAndExpect(
				container,
				this::sampleModelWithError,
				"<div class='form-group is-invalid'>" +
						"<label for='values[sub.item].name'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control is-invalid' name='values[sub.item].name' id='values[sub.item].name' />" +
						"<div class='invalid-feedback'>map-broken</div>" +
						"</div>"
		);
	}

	@Test
	public void errorFromFormCommandAttributeOrCommandObject() {
		TestClass target = new TestClass( "test value" );

		FormViewElement form = new FormViewElement();
		form.setCommandAttribute( "item" );
		form.addChild( group );

		assertBoundError( target, form );
	}

	@Test
	public void errorFromFormCommandObjectOnly() {
		TestClass target = new TestClass( "test value" );

		FormViewElement form = new FormViewElement();
		form.setCommandObject( target );
		form.addChild( group );

		assertBoundError( target, form );
	}

	private void assertBoundError( TestClass target, FormViewElement form ) {
		renderAndExpect(
				form,
				model -> {
					BindingResult errors = new BeanPropertyBindingResult( target, "item" );
					errors.rejectValue( "control", "broken", "broken" );

					model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
					model.addAttribute( "item", target );
				},
				"<form role='form' method='post'>" +
						"<div class='form-group is-invalid'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control is-invalid' name='control' id='control' value='test value' />" +
						"<div class='invalid-feedback'>broken</div>" +
						"</div>" +
						"</form>"
		);
	}

	@Test
	public void errorOnForm() {
		TestClass target = new TestClass( "test value" );
		BindingResult errors = new BeanPropertyBindingResult( target, "item" );
		errors.rejectValue( "control", "broken", "broken" );

		FormViewElement form = new FormViewElement();
		form.setErrors( errors );
		form.addChild( group );

		renderAndExpect(
				form,
				"<form role='form' method='post'>" +
						"<div class='form-group is-invalid'>" +
						"<label for='control'>title</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control is-invalid' name='control' id='control' value='test value' />" +
						"<div class='invalid-feedback'>broken</div>" +
						"</div>" +
						"</form>"
		);
	}

	@Test
	public void errorOnHorizontalForm() {
		ContainerViewElement container = new ContainerViewElement();
		container.setCustomTemplate( "th/test/formObject" );

		group.setFormLayout( FormLayout.horizontal( 2 ) );

		renderAndExpect(
				container,
				this::sampleModelWithError,
				"<div class='form-group is-invalid'>" +
						"<label for='control' class=' col-md-2'>title</label>" +
						"<div class='col-md-10'>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control is-invalid' name='control' id='control' value='test value' />" +
						"<div class='invalid-feedback'>broken</div>" +
						"</div>" +
						"</div>"
		);
	}

	private void sampleModelWithError( ModelMap model ) {
		TestClass target = new TestClass( "test value" );
		BindingResult errors = new BeanPropertyBindingResult( target, "item" );
		errors.rejectValue( "control", "broken", "broken" );
		errors.rejectValue( "values[sub.item].name", "map-broken", "map-broken" );

		model.addAttribute( BindingResult.MODEL_KEY_PREFIX + "item", errors );
		model.addAttribute( "item", target );
		model.addAttribute( "formGroup", group );
	}

	@SuppressWarnings("all")
	@Test
	public void propertyMembersAreNotChildrenButCanBeFound() {
		TextViewElement help = new TextViewElement( "help", "help" );
		group.setHelpBlock( help );

		TextViewElement description = new TextViewElement( "description", "description" );
		group.setDescriptionBlock( description );

		TextViewElement tooltip = new TextViewElement( "tooltip", "tooltip" );
		group.setTooltip( tooltip );

		assertSame( group.getControl(), group.find( "control" ).get() );
		assertSame( group.getLabel(), group.find( "label" ).get() );
		assertSame( group.getHelpBlock(), group.find( "help" ).get() );
		assertSame( group.getDescriptionBlock(), group.find( "description" ).get() );
		assertSame( group.getTooltip(), group.find( "tooltip" ).get() );

		assertEquals(
				Arrays.asList( group.getHelpBlock(), group.getLabel(), group.getControl(), group.getTooltip(), group.getDescriptionBlock() ),
				group.removeAllFromTree( "help", "label", "control", "tooltip", "description" ).collect( Collectors.toList() )
		);

		assertNull( group.getControl() );
		assertNull( group.getLabel() );
		assertNull( group.getHelpBlock() );
		assertNull( group.getDescriptionBlock() );
		assertNull( group.getTooltip() );
	}

	@Getter
	@Setter
	public static class TestClass
	{
		private final Map<String, Object> values = Collections.singletonMap( "sub.item", new NamedItem() );
		private String control;

		TestClass( String control ) {
			this.control = control;
		}

		@Getter
		@Setter
		static class NamedItem
		{
			String name;
		}
	}
}
