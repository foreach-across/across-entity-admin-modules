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

import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.web.ui.StandardViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;

public class BootstrapUiFactoryImpl extends StandardViewElementBuilderFactory implements BootstrapUiFactory
{
	@Override
	public HiddenFormElementBuilder hidden() {
		return new HiddenFormElementBuilder();
	}

	@Override
	public NodeViewElementBuilder div() {
		return node( "div" );
	}

	@Override
	public NodeViewElementBuilder span() {
		return node( "span" );
	}

	@Override
	public NodeViewElementBuilder paragraph() {
		return node( "p" );
	}

	@Override
	public LinkViewElementBuilder link() {
		return new LinkViewElementBuilder();
	}

	@Override
	public FormViewElementBuilder form() {
		return new FormViewElementBuilder();
	}

	@Override
	public LabelFormElementBuilder label( String labelText ) {
		return label().text( labelText );
	}

	@Override
	public LabelFormElementBuilder label() {
		return new LabelFormElementBuilder();
	}

	@Override
	public FormGroupElementBuilder formGroup( ViewElementBuilder label, ViewElementBuilder control ) {
		return formGroup().label( label ).control( control );
	}

	@Override
	public FormGroupElementBuilder formGroup() {
		return new FormGroupElementBuilder();
	}

	@Override
	public ButtonViewElementBuilder button() {
		return new ButtonViewElementBuilder();
	}

	@Override
	public TextboxFormElementBuilder password() {
		return textbox().password();
	}

	@Override
	public FieldsetFormElementBuilder fieldset() {
		return new FieldsetFormElementBuilder();
	}

	@Override
	public FieldsetFormElementBuilder fieldset( String legendText ) {
		return new FieldsetFormElementBuilder().legend( legendText );
	}

	@Override
	public FileUploadFormElementBuilder file() {
		return new FileUploadFormElementBuilder();
	}

	@Override
	public TextboxFormElementBuilder textarea() {
		return textbox().multiLine();
	}

	@Override
	public TextboxFormElementBuilder textbox() {
		return new TextboxFormElementBuilder();
	}

	@Override
	public TableViewElementBuilder table() {
		return new TableViewElementBuilder();
	}

	@Override
	public TableViewElementBuilder.Header tableHeader() {
		return TableViewElementBuilder.createHeader();
	}

	@Override
	public TableViewElementBuilder.Body tableBody() {
		return TableViewElementBuilder.createBody();
	}

	@Override
	public TableViewElementBuilder.Footer tableFooter() {
		return TableViewElementBuilder.createFooter();
	}

	@Override
	public TableViewElementBuilder.Caption tableCaption() {
		return TableViewElementBuilder.createCaption();
	}

	@Override
	public TableViewElementBuilder.Cell tableCell() {
		return new TableViewElementBuilder.Cell();
	}

	@Override
	public TableViewElementBuilder.Cell tableHeaderCell() {
		return new TableViewElementBuilder.Cell().heading( true );
	}

	@Override
	public TableViewElementBuilder.Row tableRow() {
		return new TableViewElementBuilder.Row();
	}

	@Override
	public NodeViewElementBuilder row() {
		return new NodeViewElementBuilder( "div" ).attribute( "class", "row" );
	}

	@Override
	public NodeViewElementBuilder helpBlock( String text ) {
		return helpBlock().add( text( text ) );
	}

	@Override
	public NodeViewElementBuilder helpBlock() {
		return new NodeViewElementBuilder( "span" ).attribute( "class", "help-block" );
	}

	@Override
	public OptionsFormElementBuilder options() {
		return new OptionsFormElementBuilder();
	}

	@Override
	public OptionFormElementBuilder<CheckboxFormElement> checkbox() {
		return new OptionFormElementBuilder<CheckboxFormElement>().checkbox();
	}

	@Override
	public OptionFormElementBuilder<RadioFormElement> radio() {
		return new OptionFormElementBuilder<RadioFormElement>().radio();
	}

	@Override
	public OptionFormElementBuilder option() {
		return new OptionFormElementBuilder();
	}

	@Override
	public ColumnViewElementBuilder column( Grid.DeviceGridLayout... layouts ) {
		return new ColumnViewElementBuilder().layout( layouts );
	}

	@Override
	public InputGroupFormElementBuilder inputGroup( ViewElementBuilder control ) {
		return inputGroup().control( control );
	}

	@Override
	public InputGroupFormElementBuilder inputGroup() {
		return new InputGroupFormElementBuilder();
	}

	@Override
	public DateTimeFormElementBuilder datetime() {
		return new DateTimeFormElementBuilder().datetime();
	}

	@Override
	public NumericFormElementBuilder numeric() {
		return new NumericFormElementBuilder();
	}

	@Override
	public AlertViewElementBuilder alert() {
		return new AlertViewElementBuilder();
	}

	@Override
	public FaIcon faIcon( String icon ) {
		return new FaIcon( icon );
	}

	@Override
	public AutoSuggestFormElementBuilder autosuggest() {
		return new AutoSuggestFormElementBuilder();
	}
}
