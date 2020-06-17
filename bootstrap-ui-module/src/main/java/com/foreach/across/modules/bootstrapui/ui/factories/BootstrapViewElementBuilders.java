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
import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElementBuilder;
import com.foreach.across.modules.bootstrapui.ui.factories.support.builders.BootstrapTableBuilderFactoryHelper;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ViewElementGeneratorBuilder;

/**
 * A helper class for creating a {@link com.foreach.across.modules.web.ui.ViewElementBuilder} for all main Bootstrap 4 components.
 * This class should not be used directly but instead use the provided  {@link BootstrapViewElements#builders}.
 * <p>
 * For example to create a button element you can use: {@code BootstrapViewElements.builders.button()}.
 * </p>
 *
 * @author Stijn Vanhoof
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapViewElementBuilders
{
	public final BootstrapTableBuilderFactoryHelper table = new BootstrapTableBuilderFactoryHelper();

	public AlertViewElementBuilder alert() {
		return new AlertViewElementBuilder();
	}

	public AlertViewElementBuilder alert( ViewElement.WitherSetter... setters ) {
		return alert().with( setters );
	}

	public AutoSuggestFormElementBuilder autoSuggest() {
		return new AutoSuggestFormElementBuilder();
	}

	public AutoSuggestFormElementBuilder autoSuggest( ViewElement.WitherSetter... setters ) {
		return autoSuggest().with( setters );
	}

	public BreadcrumbNavComponentBuilder breadcrumb() {
		return new BreadcrumbNavComponentBuilder();
	}

	public BreadcrumbNavComponentBuilder breadcrumb( ViewElement.WitherSetter... setters ) {
		return breadcrumb().with( setters );
	}

	public ButtonViewElementBuilder button() {
		return new ButtonViewElementBuilder();
	}

	public ButtonViewElementBuilder button( ViewElement.WitherSetter... setters ) {
		return button().with( setters );
	}

	/**
	 * Builds a {@link com.foreach.across.modules.web.ui.ViewElementBuilder} for a {@link CheckboxFormElement}
	 */
	@SuppressWarnings("unchecked")
	public OptionFormElementBuilder<CheckboxFormElement> checkbox() {
		return option().checkbox();
	}

	public OptionFormElementBuilder<CheckboxFormElement> checkbox( ViewElement.WitherSetter... setters ) {
		return checkbox().with( setters );
	}

	/**
	 * Creates a {@link OptionsFormElementBuilder} for a list of {@link CheckboxFormElement}.
	 *
	 * <pre>
	 * {@code
	 * BootstrapViewElementBuilders.builders.checkboxList()
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 * }
	 * </pre>
	 */
	public OptionsFormElementBuilder checkboxList() {
		return options().checkbox();
	}

	public OptionsFormElementBuilder checkboxList( ViewElement.WitherSetter... setters ) {
		return checkboxList().with( setters );
	}

	@Deprecated
	public ColumnViewElementBuilder column( Grid.DeviceGridLayout... layouts ) {
		return new ColumnViewElementBuilder().layout( layouts );
	}

	public DateTimeFormElementBuilder datetime() {
		return new DateTimeFormElementBuilder().datetime();
	}

	public DateTimeFormElementBuilder datetime( ViewElement.WitherSetter... setters ) {
		return datetime().with( setters );
	}

	public FieldsetFormElementBuilder fieldset() {
		return new FieldsetFormElementBuilder();
	}

	public FieldsetFormElementBuilder fieldset( ViewElement.WitherSetter... setters ) {
		return fieldset().with( setters );
	}

	public FileUploadFormElementBuilder fileUpload() {
		return new FileUploadFormElementBuilder();
	}

	public FileUploadFormElementBuilder fileUpload( ViewElement.WitherSetter... setters ) {
		return fileUpload().with( setters );
	}

	public FormViewElementBuilder form() {
		return new FormViewElementBuilder();
	}

	public FormViewElementBuilder form( ViewElement.WitherSetter... setters ) {
		return form().with( setters );
	}

	public FormGroupElementBuilder formGroup() {
		return new FormGroupElementBuilder();
	}

	public FormGroupElementBuilder formGroup( ViewElement.WitherSetter... setters ) {
		return formGroup().with( setters );
	}

	@SuppressWarnings("unused")
	public <ITEM, VIEW_ELEMENT extends ViewElement> ViewElementGeneratorBuilder<ITEM, VIEW_ELEMENT> generator(
			Class<ITEM> itemClass, Class<VIEW_ELEMENT> viewElementClass ) {
		return new ViewElementGeneratorBuilder<>();
	}

	public NodeViewElementBuilder helpBlock() {
		return new NodeViewElementBuilder( "span" ).attribute( "class", "help-block" );
	}

	public NodeViewElementBuilder helpBlock( ViewElement.WitherSetter... setters ) {
		return helpBlock().with( setters );
	}

	public HiddenFormElementBuilder hidden() {
		return new HiddenFormElementBuilder();
	}

	public HiddenFormElementBuilder hidden( ViewElement.WitherSetter... setters ) {
		return hidden().with( setters );
	}

	public InputGroupFormElementBuilder inputGroup() {
		return new InputGroupFormElementBuilder();
	}

	public InputGroupFormElementBuilder inputGroup( ViewElement.WitherSetter... setters ) {
		return inputGroup().with( setters );
	}

	public LabelFormElementBuilder label() {
		return new LabelFormElementBuilder();
	}

	public LabelFormElementBuilder label( String labelText ) {
		return label().text( labelText );
	}

	public LinkViewElementBuilder link() {
		return new LinkViewElementBuilder();
	}

	public LinkViewElementBuilder link( ViewElement.WitherSetter... setters ) {
		return link().with( setters );
	}

	public DefaultNavComponentBuilder nav() {
		return new DefaultNavComponentBuilder();
	}

	public DefaultNavComponentBuilder nav( ViewElement.WitherSetter... setters ) {
		return nav().with( setters );
	}

	public NumericFormElementBuilder numeric() {
		return new NumericFormElementBuilder();
	}

	public NumericFormElementBuilder numeric( ViewElement.WitherSetter... setters ) {
		return numeric().with( setters );
	}

	public TextboxFormElementBuilder password() {
		return textbox().type( TextboxFormElement.Type.PASSWORD );
	}

	public TextboxFormElementBuilder password( ViewElement.WitherSetter... setters ) {
		return password().with( setters );
	}

	public PanelsNavComponentBuilder panels() {
		return new PanelsNavComponentBuilder();
	}

	public PanelsNavComponentBuilder panels( ViewElement.WitherSetter... setters ) {
		return panels().with( setters );
	}

	public OptionFormElementBuilder<RadioFormElement> radio( ViewElement.WitherSetter... setters ) {
		return radio().with( setters );
	}

	/**
	 * Builds a {@link com.foreach.across.modules.web.ui.ViewElementBuilder} for a single {@link RadioFormElement}
	 */
	@SuppressWarnings("unchecked")
	public OptionFormElementBuilder<RadioFormElement> radio() {
		return option().radio();
	}

	public OptionsFormElementBuilder radioList( ViewElement.WitherSetter... setters ) {
		return radioList().with( setters );
	}

	/**
	 * Creates a {@link OptionsFormElementBuilder} for a list of {@link RadioFormElement}.
	 *
	 * <pre>
	 * {@code
	 * BootstrapViewElementBuilders.builders.radioList()
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 * }
	 * </pre>
	 */
	public OptionsFormElementBuilder radioList() {
		return options().radio();
	}

	public NodeViewElementBuilder row() {
		return new NodeViewElementBuilder( "div" ).attribute( "class", "row" );
	}

	public NodeViewElementBuilder row( ViewElement.WitherSetter... setters ) {
		return row().with( setters );
	}

	/**
	 * Creates a {@link OptionsFormElementBuilder} for a {@link SelectFormElement}.
	 *
	 * <pre>
	 * {@code
	 * BootstrapViewElementBuilders.builders.select()
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 * }
	 * </pre>
	 */
	public OptionsFormElementBuilder select() {
		return options().select();
	}

	public OptionsFormElementBuilder select( ViewElement.WitherSetter... setters ) {
		return select().with( setters );
	}

	public ScriptViewElementBuilder script() {
		return new ScriptViewElementBuilder();
	}

	public ScriptViewElementBuilder script( ViewElement.WitherSetter... setters ) {
		return script().with( setters );
	}

	public TableViewElementBuilder table() {
		return new TableViewElementBuilder();
	}

	public TableViewElementBuilder table( ViewElement.WitherSetter... setters ) {
		return table().with( setters );
	}

	public TextboxFormElementBuilder textbox() {
		return new TextboxFormElementBuilder();
	}

	public TextboxFormElementBuilder textbox( ViewElement.WitherSetter... setters ) {
		return textbox().with( setters );
	}

	/**
	 * Builds a {@link com.foreach.across.modules.web.ui.ViewElementBuilder} for a single {@link ToggleFormElement}
	 */
	public OptionFormElementBuilder<ToggleFormElement> toggle() {
		return new OptionFormElementBuilder<ToggleFormElement>().toggle();
	}

	public OptionFormElementBuilder<ToggleFormElement> toggle( ViewElement.WitherSetter... setters ) {
		return toggle().with( setters );
	}

	/**
	 * Creates a {@link OptionsFormElementBuilder} for a list of {@link ToggleFormElement}.
	 *
	 * <pre>
	 * {@code
	 * BootstrapViewElementBuilders.builders.toggleList()
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 *  .add( BootstrapViewElementBuilders.builders.option() )
	 * }
	 * </pre>
	 */
	public OptionsFormElementBuilder toggleList() {
		return options().toggle();
	}

	public OptionsFormElementBuilder toggleList( ViewElement.WitherSetter... setters ) {
		return toggleList().with( setters );
	}

	public TooltipViewElementBuilder tooltip() {
		return new TooltipViewElementBuilder();
	}

	public TooltipViewElementBuilder tooltip( ViewElement.WitherSetter... setters ) {
		return tooltip().with( setters );
	}

	/**
	 * Create a new options list. Options can be visualized as select, checkbox, radio or toggle controls.
	 *
	 * @return options builder
	 * @see OptionsFormElementBuilder
	 * @see #radioList()
	 * @see #select()
	 * @see #checkboxList()
	 * @see #toggleList()
	 * @see #option()
	 */
	public OptionsFormElementBuilder options() {
		return new OptionsFormElementBuilder();
	}

	/**
	 * Create a new options list. Options can be visualized as select, checkbox, radio or toggle controls.
	 *
	 * @return options builder
	 * @see OptionsFormElementBuilder
	 * @see #radioList()
	 * @see #select()
	 * @see #checkboxList()
	 * @see #toggleList()
	 * @see #option()
	 */
	public OptionsFormElementBuilder options( ViewElement.WitherSetter... setters ) {
		return new OptionsFormElementBuilder().with( setters );
	}

	/**
	 * Create a new option to add to an {@link OptionsFormElementBuilder}.
	 *
	 * @return option builder
	 * @see #options()
	 * @see #select()
	 * @see #checkboxList()
	 * @see #radioList()
	 * @see #toggleList()
	 */
	public OptionFormElementBuilder option() {
		return new OptionFormElementBuilder();
	}

	/**
	 * Create a new option to add to an {@link OptionsFormElementBuilder}.
	 *
	 * @return option builder
	 * @see #options()
	 * @see #select()
	 * @see #checkboxList()
	 * @see #radioList()
	 * @see #toggleList()
	 */
	public OptionFormElementBuilder option( ViewElement.WitherSetter... setters ) {
		return new OptionFormElementBuilder().with( setters );
	}
}
