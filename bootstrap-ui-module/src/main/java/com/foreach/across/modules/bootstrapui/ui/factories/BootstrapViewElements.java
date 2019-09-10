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
import com.foreach.across.modules.bootstrapui.ui.factories.support.BootstrapFieldSetElementFactoryHelper;
import com.foreach.across.modules.bootstrapui.ui.factories.support.BootstrapSelectElementFactoryHelper;
import com.foreach.across.modules.bootstrapui.ui.factories.support.BootstrapTableElementFactoryHelper;
import com.foreach.across.modules.bootstrapui.elements.icons.IconSet;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;

/**
 * Entry point for creating a bootstrap {@link ViewElement}. An equivalent {@link BootstrapViewElementBuilders} class has been provided
 * to create {@link com.foreach.across.modules.web.ui.ViewElementBuilder} and this is available as {@link BootstrapViewElements#builders}.
 * <p>
 * For example to create a select element you can use: {@code Bootstrap.bootstrap.select()}
 * </p>
 *
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
public class BootstrapViewElements
{
	/**
	 * Static import handle for the {@link BootstrapViewElements} implementations.
	 */
	public static BootstrapViewElements bootstrap = new BootstrapViewElements();

	/**
	 * Import handle for the equivalent element builders.
	 */
	public final BootstrapViewElementBuilders builders = new BootstrapViewElementBuilders();

	public final BootstrapSelectElementFactoryHelper select = new BootstrapSelectElementFactoryHelper();
	public final BootstrapTableElementFactoryHelper table = new BootstrapTableElementFactoryHelper();
	public final BootstrapFieldSetElementFactoryHelper fieldset = new BootstrapFieldSetElementFactoryHelper();

	public AlertViewElement alert() {
		return new AlertViewElement();
	}

	public AlertViewElement alert( ViewElement.WitherSetter... setters ) {
		return alert().set( setters );
	}

	public AutoSuggestFormElement autoSuggest( TextboxFormElement textbox, HiddenFormElement valueControl ) {
		return new AutoSuggestFormElement( textbox, valueControl );
	}

	public AutoSuggestFormElement autoSuggest( TextboxFormElement textbox, HiddenFormElement valueControl, ViewElement.WitherSetter... setters ) {
		return autoSuggest( textbox, valueControl ).set( setters );
	}

	public ButtonViewElement button() {
		return new ButtonViewElement();
	}

	public ButtonViewElement button( ViewElement.WitherSetter... setters ) {
		return button().set( setters );
	}

	public CheckboxFormElement checkbox() {
		return new CheckboxFormElement();
	}

	public CheckboxFormElement checkbox( ViewElement.WitherSetter... setters ) {
		return checkbox().set( setters );
	}

	public DateTimeFormElement dateTime() {
		return new DateTimeFormElement();
	}

	public DateTimeFormElement dateTime( ViewElement.WitherSetter... setters ) {
		return dateTime().set( setters );
	}

	public FieldsetFormElement fieldSet() {
		return new FieldsetFormElement();
	}

	public FieldsetFormElement fieldSet( ViewElement.WitherSetter... setters ) {
		return fieldSet().set( setters );
	}

	public FileUploadFormElement fileUpload() {
		return new FileUploadFormElement();
	}

	public FileUploadFormElement fileUpload( ViewElement.WitherSetter... setters ) {
		return fileUpload().set( setters );
	}

	public FormViewElement form() {
		return new FormViewElement();
	}

	public FormViewElement form( ViewElement.WitherSetter... setters ) {
		return form().set( setters );
	}

	public FormGroupElement formGroup() {
		return new FormGroupElement();
	}

	public FormGroupElement formGroup( ViewElement.WitherSetter... setters ) {
		return formGroup().set( setters );
	}

	public NodeViewElement helpBlock() {
		NodeViewElement helpBlock = new NodeViewElement( "span" );
		helpBlock.setAttribute( "class", "help-block" );
		return helpBlock;
	}

	public HiddenFormElement hidden() {
		return new HiddenFormElement();
	}

	public HiddenFormElement hidden( ViewElement.WitherSetter... setters ) {
		return hidden().set( setters );
	}

	public AbstractNodeViewElement icon( String iconSetName, String iconName ) {
		return IconSet.iconSet( iconSetName ).icon( iconName );
	}

	public AbstractNodeViewElement icon( String iconSetName, String iconName, ViewElement.WitherSetter... setters ) {
		return icon( iconSetName, iconSetName ).set( setters );
	}

	public InputGroupFormElement inputGroup() {
		return new InputGroupFormElement();
	}

	public InputGroupFormElement inputGroup( ViewElement.WitherSetter... setters ) {
		return inputGroup().set( setters );
	}

	public LabelFormElement label() {
		return new LabelFormElement();
	}

	public LabelFormElement label( ViewElement.WitherSetter... setters ) {
		return label().set( setters );
	}

	public LinkViewElement link() {
		return new LinkViewElement();
	}

	public LinkViewElement link( ViewElement.WitherSetter... setters ) {
		return link().set( setters );
	}

	public NumericFormElement numeric() {
		return new NumericFormElement();
	}

	public NumericFormElement numeric( ViewElement.WitherSetter... setters ) {
		return numeric().set( setters );
	}

	public TextboxFormElement password() {
		return textbox().setType( TextboxFormElement.Type.PASSWORD );
	}

	public TextboxFormElement password( ViewElement.WitherSetter... setters ) {
		return password().setType( TextboxFormElement.Type.PASSWORD ).set( setters );
	}

	public RadioFormElement radio() {
		return new RadioFormElement();
	}

	public RadioFormElement radio( ViewElement.WitherSetter... setters ) {
		return radio().set( setters );
	}

	public SelectFormElement select() {
		return new SelectFormElement();
	}

	public SelectFormElement select( ViewElement.WitherSetter... setters ) {
		return select().set( setters );
	}

	public ScriptViewElement script() {
		return new ScriptViewElement();
	}

	public ScriptViewElement script( ViewElement.WitherSetter... setters ) {
		return script().set( setters );
	}

	public TableViewElement table() {
		return new TableViewElement();
	}

	public TableViewElement table( ViewElement.WitherSetter... setters ) {
		return table().set( setters );
	}

	public TextareaFormElement textArea() {
		return new TextareaFormElement();
	}

	public TextareaFormElement textArea( ViewElement.WitherSetter... setters ) {
		return textArea().set( setters );
	}

	public TextboxFormElement textbox() {
		return new TextboxFormElement();
	}

	public TextboxFormElement textbox( ViewElement.WitherSetter... setters ) {
		return textbox().set( setters );
	}

	public ToggleFormElement toggle() {
		return new ToggleFormElement();
	}

	public ToggleFormElement toggle( ViewElement.WitherSetter... setters ) {
		return toggle().set( setters );
	}

	public TooltipViewElement tooltip() {
		return new TooltipViewElement();
	}

	public TooltipViewElement tooltip( ViewElement.WitherSetter... setters ) {
		return tooltip().set( setters );
	}
}
