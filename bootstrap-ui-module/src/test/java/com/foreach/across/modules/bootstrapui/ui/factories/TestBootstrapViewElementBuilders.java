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

package com.foreach.across.modules.bootstrapui.ui.factories;

import com.foreach.across.modules.bootstrapui.components.builder.BreadcrumbNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElementBuilder;
import com.foreach.across.modules.bootstrapui.styles.BootstrapStyles;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stijn Vanhoof
 */
class TestBootstrapViewElementBuilders
{
	@Test
	void alert() {
		assertThat( bootstrap.builders.alert() ).isInstanceOf( AlertViewElementBuilder.class );
	}

	@Test
	void autoSuggest() {
		assertThat( bootstrap.builders.autoSuggest() ).isInstanceOf( AutoSuggestFormElementBuilder.class );
	}

	@Test
	void breadcrumbNavigation() {
		assertThat( bootstrap.builders.breadcrumb() ).isInstanceOf( BreadcrumbNavComponentBuilder.class );
	}

	@Test
	void button() {
		assertThat( bootstrap.builders.button() ).isInstanceOf( ButtonViewElementBuilder.class );
	}

	@Test
	void checkbox() {
		assertThat( bootstrap.builders.checkbox() )
				.isInstanceOf( OptionFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.CHECKBOX.equals( o.getType() ) );
	}

	@Test
	void dateTime() {
		assertThat( bootstrap.builders.datetime() ).isInstanceOf( DateTimeFormElementBuilder.class );
	}

	@Test
	void fieldSet() {
		assertThat( bootstrap.builders.fieldset() ).isInstanceOf( FieldsetFormElementBuilder.class );
	}

	@Test
	void fileUpload() {
		assertThat( bootstrap.builders.fileUpload() ).isInstanceOf( FileUploadFormElementBuilder.class );
	}

	@Test
	void form() {
		assertThat( bootstrap.builders.form() ).isInstanceOf( FormViewElementBuilder.class );
	}

	@Test
	void formGroup() {
		assertThat( bootstrap.builders.formGroup() ).isInstanceOf( FormGroupElementBuilder.class );
	}

	@Test
	void helpBlock() {
		assertThat( bootstrap.builders.helpBlock() ).isInstanceOf( NodeViewElementBuilder.class );
	}

	@Test
	void helpBlockWithText() {
		assertThat( bootstrap.builders.helpBlock() ).isInstanceOf( NodeViewElementBuilder.class );
	}

	@Test
	void hidden() {
		assertThat( bootstrap.builders.hidden() ).isInstanceOf( HiddenFormElementBuilder.class );
	}

	@Test
	void inputGroup() {
		assertThat( bootstrap.builders.inputGroup() ).isInstanceOf( InputGroupFormElementBuilder.class );
	}

	@Test
	void label() {
		assertThat( bootstrap.builders.label() ).isInstanceOf( LabelFormElementBuilder.class );
	}

	@Test
	void link() {
		assertThat( bootstrap.builders.link() ).isInstanceOf( LinkViewElementBuilder.class );
	}

	@Test
	void multiCheckbox() {
		assertThat( bootstrap.builders.checkboxList() ).matches( o -> OptionsFormElementBuilder.Type.CHECKBOX.equals( o.getType() ) );
	}

	@Test
	void navigation() {
		assertThat( bootstrap.builders.nav() ).isInstanceOf( DefaultNavComponentBuilder.class );
	}

	@Test
	void options() {
		assertThat( bootstrap.builders.options() )
				.isInstanceOf( OptionsFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.SELECT.equals( o.getType() ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	void option() {
		assertThat( bootstrap.builders.option() ).isInstanceOf( OptionFormElementBuilder.class );
	}

	@Test
	void password() {
		assertThat( bootstrap.builders.password() ).isInstanceOf( TextboxFormElementBuilder.class );
	}

	@Test
	void panels() {
		assertThat( bootstrap.builders.panels() ).isInstanceOf( PanelsNavComponentBuilder.class );
	}

	@Test
	void radio() {
		assertThat( bootstrap.builders.radio() )
				.isInstanceOf( OptionFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.RADIO.equals( o.getType() ) );
	}

	@Test
	void radioList() {
		assertThat( bootstrap.builders.radioList() )
				.isInstanceOf( OptionsFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.RADIO.equals( o.getType() ) );
	}

	@Test
	void row() {
		NodeViewElementBuilder rowBuilder = bootstrap.builders.row();
		NodeViewElement rowElement = rowBuilder.build();
		assertThat( rowBuilder ).isInstanceOf( NodeViewElementBuilder.class );
		assertThat( rowElement.matches( BootstrapStyles.css.grid.row ) ).isTrue();
		assertThat( rowElement.getTagName() ).isEqualTo( "div" );
	}

	@Test
	void select() {
		assertThat( bootstrap.builders.select() ).isInstanceOf( OptionsFormElementBuilder.class );
	}

	@Test
	void table() {
		assertThat( bootstrap.builders.table() ).isInstanceOf( TableViewElementBuilder.class );
	}

	@Test
	void tableBody() {
		assertThat( bootstrap.builders.table.body() ).isInstanceOf( TableViewElementBuilder.Body.class );
	}

	@Test
	void tableCaption() {
		assertThat( bootstrap.builders.table.caption() ).isInstanceOf( TableViewElementBuilder.Caption.class );
	}

	@Test
	void tableCell() {
		assertThat( bootstrap.builders.table.cell() ).isInstanceOf( TableViewElementBuilder.Cell.class );
	}

	@Test
	void tableHeaderCell() {
		TableViewElementBuilder.Cell actual = bootstrap.builders.table.headerCell();
		assertThat( actual ).isInstanceOf( TableViewElementBuilder.Cell.class );
		assertThat( actual.build().isHeading() ).isTrue();
	}

	@Test
	void tableFooter() {
		assertThat( bootstrap.builders.table.footer() ).isInstanceOf( TableViewElementBuilder.Footer.class );
	}

	@Test
	void tableHeader() {
		assertThat( bootstrap.builders.table.header() ).isInstanceOf( TableViewElementBuilder.Header.class );
	}

	@Test
	void tableRow() {
		assertThat( bootstrap.builders.table.row() ).isInstanceOf( TableViewElementBuilder.Row.class );
	}

	@Test
	void textbox() {
		assertThat( bootstrap.builders.textbox() ).isInstanceOf( TextboxFormElementBuilder.class );
	}

	@Test
	void toggle() {
		assertThat( bootstrap.builders.toggle() )
				.isInstanceOf( OptionFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.TOGGLE.equals( o.getType() ) );
	}

	@Test
	void toggleList() {
		assertThat( bootstrap.builders.toggleList() )
				.isInstanceOf( OptionsFormElementBuilder.class )
				.matches( o -> OptionsFormElementBuilder.Type.TOGGLE.equals( o.getType() ) );
	}

	@Test
	void tooltip() {
		assertThat( bootstrap.builders.tooltip() ).isInstanceOf( TooltipViewElementBuilder.class );
	}
}
