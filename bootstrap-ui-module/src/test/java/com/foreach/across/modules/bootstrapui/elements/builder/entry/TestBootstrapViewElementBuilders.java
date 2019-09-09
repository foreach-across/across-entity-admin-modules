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

package com.foreach.across.modules.bootstrapui.elements.builder.entry;

import com.foreach.across.modules.bootstrapui.components.builder.BreadcrumbNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements.bootstrap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stijn Vanhoof
 */
public class TestBootstrapViewElementBuilders
{
	@Test
	public void alert() {
		assertThat( bootstrap.bootstrap.builders.alert() ).isInstanceOf( AlertViewElementBuilder.class );
	}

	@Test
	public void autoSuggest() {
		assertThat( bootstrap.builders.autosuggest() ).isInstanceOf( AutoSuggestFormElementBuilder.class );
	}

	@Test
	public void breadcrumbNavigation() {
		assertThat( bootstrap.builders.breadcrumb() ).isInstanceOf( BreadcrumbNavComponentBuilder.class );
	}

	@Test
	public void button() {
		assertThat( bootstrap.builders.button() ).isInstanceOf( ButtonViewElementBuilder.class );
	}

	@Test
	public void checkbox() {
		assertThat( bootstrap.builders.checkbox() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void dateTime() {
		assertThat( bootstrap.builders.dateTime() ).isInstanceOf( DateTimeFormElementBuilder.class );
	}

	@Test
	public void fieldSet() {
		assertThat( bootstrap.builders.fieldset() ).isInstanceOf( FieldsetFormElementBuilder.class );
	}

	@Test
	public void fieldSetLegend() {
		assertThat( bootstrap.builders.fieldset.legend( bootstrap.builders.fieldset() ) ).isInstanceOf( FieldsetFormElementBuilder.Legend.class );
	}

	@Test
	public void fileUpload() {
		assertThat( bootstrap.builders.file() ).isInstanceOf( FileUploadFormElementBuilder.class );
	}

	@Test
	public void form() {
		assertThat( bootstrap.builders.form() ).isInstanceOf( FormViewElementBuilder.class );
	}

	@Test
	public void formGroup() {
		assertThat( bootstrap.builders.formGroup() ).isInstanceOf( FormGroupElementBuilder.class );
	}

	@Test
	public void helpBlock() {
		assertThat( bootstrap.builders.helpBlock() ).isInstanceOf( NodeViewElementBuilder.class );
	}

	@Test
	public void helpBlockWithText() {
		assertThat( bootstrap.builders.helpBlock() ).isInstanceOf( NodeViewElementBuilder.class );
	}

	@Test
	public void hidden() {
		assertThat( bootstrap.builders.hidden() ).isInstanceOf( HiddenFormElementBuilder.class );
	}

	@Test
	public void inputGroup() {
		assertThat( bootstrap.builders.inputGroup() ).isInstanceOf( InputGroupFormElementBuilder.class );
	}

	@Test
	public void label() {
		assertThat( bootstrap.builders.label() ).isInstanceOf( LabelFormElementBuilder.class );
	}

	@Test
	public void link() {
		assertThat( bootstrap.builders.link() ).isInstanceOf( LinkViewElementBuilder.class );
	}

	@Test
	public void multiCheckbox() {
		assertThat( bootstrap.builders.checkboxList() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	public void multiCheckboxOption() {
		assertThat( bootstrap.builders.options.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void navigation() {
		assertThat( bootstrap.builders.nav() ).isInstanceOf( DefaultNavComponentBuilder.class );
	}

	@Test
	public void option() {
		assertThat( bootstrap.builders.options.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void options() {
		assertThat( bootstrap.builders.options.options() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	public void password() {
		assertThat( bootstrap.builders.password() ).isInstanceOf( TextboxFormElementBuilder.class );
	}

	@Test
	public void panels() {
		assertThat( bootstrap.builders.panels() ).isInstanceOf( PanelsNavComponentBuilder.class );
	}

	@Test
	public void radio() {
		assertThat( bootstrap.builders.radio() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void radioOption() {
		assertThat( bootstrap.builders.options.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void radioList() {
		assertThat( bootstrap.builders.radioList() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	public void select() {
		assertThat( bootstrap.builders.select() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	public void selectOption() {
		assertThat( bootstrap.builders.select.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void table() {
		assertThat( bootstrap.builders.table() ).isInstanceOf( TableViewElementBuilder.class );
	}

	@Test
	public void tableBody() {
		assertThat( bootstrap.builders.table.body() ).isInstanceOf( TableViewElementBuilder.Body.class );
	}

	@Test
	public void tableCaption() {
		assertThat( bootstrap.builders.table.caption() ).isInstanceOf( TableViewElementBuilder.Caption.class );
	}

	@Test
	public void tableCell() {
		assertThat( bootstrap.builders.table.cell() ).isInstanceOf( TableViewElementBuilder.Cell.class );
	}

	@Test
	public void tableFooter() {
		assertThat( bootstrap.builders.table.footer() ).isInstanceOf( TableViewElementBuilder.Footer.class );
	}

	@Test
	public void tableHeader() {
		assertThat( bootstrap.builders.table.header() ).isInstanceOf( TableViewElementBuilder.Header.class );
	}

	@Test
	public void tableRow() {
		assertThat( bootstrap.builders.table.row() ).isInstanceOf( TableViewElementBuilder.Row.class );
	}

	@Test
	public void textbox() {
		assertThat( bootstrap.builders.textbox() ).isInstanceOf( TextboxFormElementBuilder.class );
	}

	@Test
	public void toggle() {
		assertThat( bootstrap.builders.toggle() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void toggleOption() {
		assertThat( bootstrap.builders.options.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	public void toggleList() {
		assertThat( bootstrap.builders.toggleList() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	public void tooltip() {
		assertThat( bootstrap.builders.tooltip() ).isInstanceOf( TooltipViewElementBuilder.class );
	}

}
