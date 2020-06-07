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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSetRegistry;
import com.foreach.across.modules.bootstrapui.elements.icons.SimpleIconSet;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Stijn Vanhoof
 */
class TestBootstrapViewElements
{
	@Test
	void alert() {
		assertThat( bootstrap.alert() ).isInstanceOf( AlertViewElement.class );
		AlertViewElement alertWithStyling = bootstrap.alert( css.alert.warning );
		assertThat( alertWithStyling ).isInstanceOf( AlertViewElement.class );
		hasClass( alertWithStyling, "alert alert-warning" );
	}

	@Test
	void autoSuggest() {
		assertThat( bootstrap.autoSuggest( bootstrap.textbox(), bootstrap.hidden() ) ).isInstanceOf( AutoSuggestFormElement.class );
	}

	@Test
	void button() {
		ButtonViewElement buttonWithStyling = bootstrap.button( css.button.primary );
		assertThat( buttonWithStyling ).isInstanceOf( ButtonViewElement.class );
		hasClass( buttonWithStyling, "btn btn-primary" );
	}

	@Test
	void checkbox() {
		assertThat( bootstrap.checkbox() ).isInstanceOf( CheckboxFormElement.class );
	}

	@Test
	void fieldSet() {
		assertThat( bootstrap.fieldset() ).isInstanceOf( FieldsetFormElement.class );
	}

	@Test
	void fieldSetLegend() {
		assertThat( bootstrap.fieldset.legend() ).isInstanceOf( FieldsetFormElement.Legend.class );
	}

	@Test
	void fileUpload() {
		assertThat( bootstrap.fileUpload() ).isInstanceOf( FileUploadFormElement.class );
	}

	@Test
	void form() {
		assertThat( bootstrap.form() ).isInstanceOf( FormViewElement.class );
	}

	@Test
	void formGroup() {
		assertThat( bootstrap.formGroup() ).isInstanceOf( FormGroupElement.class );
	}

	@Test
	void helpBlock() {
		NodeViewElement helpBLock = bootstrap.helpBlock();
		assertThat( helpBLock ).isInstanceOf( NodeViewElement.class );
		assertThat( helpBLock.getTagName() ).isEqualTo( "span" );
		hasClass( helpBLock, "help-block" );
	}

	@Test
	void hidden() {
		assertThat( bootstrap.hidden() ).isInstanceOf( HiddenFormElement.class );
	}

	@Test
	void icon() {
		SimpleIconSet mutableIconSet = new SimpleIconSet();
		mutableIconSet.setDefaultIconResolver( ( iconName ) -> html.i( HtmlViewElement.Functions.css( " fa-" + iconName ) ) );
		IconSetRegistry.addIconSet( "iconSetName", mutableIconSet );
		assertThat( bootstrap.icon( "iconSetName", "iconName" ) ).isInstanceOf( AbstractNodeViewElement.class );
	}

	@Test
	void label() {
		assertThat( bootstrap.label() ).isInstanceOf( LabelFormElement.class );
	}

	@Test
	void link() {
		assertThat( bootstrap.link() ).isInstanceOf( LinkViewElement.class );
	}

	@Test
	void numeric() {
		assertThat( bootstrap.numeric() ).isInstanceOf( NumericFormElement.class );
	}

	@Test
	void password() {
		TextboxFormElement password = bootstrap.password();
		assertThat( password ).isInstanceOf( TextboxFormElement.class );
		assertThat( password.getType() ).isEqualTo( TextboxFormElement.Type.PASSWORD );
	}

	@Test
	void radio() {
		assertThat( bootstrap.radio() ).isInstanceOf( RadioFormElement.class );
	}

	@Test
	void select() {
		assertThat( bootstrap.select() ).isInstanceOf( SelectFormElement.class );
	}

	@Test
	void selectOption() {
		assertThat( bootstrap.select.option() ).isInstanceOf( SelectFormElement.Option.class );
	}

	@Test
	void selectOptionGroup() {
		assertThat( bootstrap.select.optionGroup() ).isInstanceOf( SelectFormElement.OptionGroup.class );
	}

	@Test
	void script() {
		assertThat( bootstrap.script() ).isInstanceOf( ScriptViewElement.class );
	}

	@Test
	void table() {
		assertThat( bootstrap.table() ).isInstanceOf( TableViewElement.class );
	}

	@Test
	void tableBody() {
		assertThat( bootstrap.table.body() ).isInstanceOf( TableViewElement.Body.class );
	}

	@Test
	void tableCaption() {
		assertThat( bootstrap.table.caption() ).isInstanceOf( TableViewElement.Caption.class );
	}

	@Test
	void tableCell() {
		assertThat( bootstrap.table.cell() ).isInstanceOf( TableViewElement.Cell.class );
	}

	@Test
	void tableHeaderCell() {
		assertThat( bootstrap.table.headerCell() )
				.isInstanceOf( TableViewElement.Cell.class )
				.matches( TableViewElement.Cell::isHeading );
	}

	@Test
	void tableColumnGroup() {
		assertThat( bootstrap.table.columnGroup() ).isInstanceOf( TableViewElement.ColumnGroup.class );
	}

	@Test
	void tableFooter() {
		assertThat( bootstrap.table.footer() ).isInstanceOf( TableViewElement.Footer.class );
	}

	@Test
	void tableHeader() {
		assertThat( bootstrap.table.header() ).isInstanceOf( TableViewElement.Header.class );
	}

	@Test
	void tableRow() {
		assertThat( bootstrap.table.row() ).isInstanceOf( TableViewElement.Row.class );
	}

	@Test
	void textArea() {
		assertThat( bootstrap.textarea() ).isInstanceOf( TextareaFormElement.class );
	}

	@Test
	void textbox() {
		assertThat( bootstrap.textbox() ).isInstanceOf( TextboxFormElement.class );
	}

	@Test
	void toggle() {
		assertThat( bootstrap.toggle() ).isInstanceOf( ToggleFormElement.class );
	}

	@Test
	void tooltip() {
		assertThat( bootstrap.tooltip() ).isInstanceOf( TooltipViewElement.class );
	}

	private void hasClass( AbstractNodeViewElement element, String aClass ) {
		assertThat( element.getAttribute( "class" ) ).isEqualTo( aClass );
	}
}
