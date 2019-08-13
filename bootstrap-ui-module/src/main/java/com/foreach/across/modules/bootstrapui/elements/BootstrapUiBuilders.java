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

import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory;
import com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactoryImpl;
import com.foreach.across.modules.bootstrapui.components.builder.BreadcrumbNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.DefaultNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.components.builder.PanelsNavComponentBuilder;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElementBuilder;
import com.foreach.across.modules.web.menu.Menu;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ViewElementGeneratorBuilder;
import lombok.NonNull;
import org.springframework.http.MediaType;

/**
 * Static facade for {@link BootstrapUiFactory} and {@link com.foreach.across.modules.bootstrapui.components.BootstrapUiComponentFactory}.
 *
 * @author Arne Vandamme
 * @since 1.2.0
 */
public class BootstrapUiBuilders
{
	private static final BootstrapUiFactory FACTORY = new BootstrapUiFactoryImpl();
	private static final BootstrapUiComponentFactory COMPONENT_FACTORY = new BootstrapUiComponentFactoryImpl();

	public static ContainerViewElementBuilder container() {
		return new ContainerViewElementBuilder();
	}

	public static <ITEM, VIEW_ELEMENT extends ViewElement> ViewElementGeneratorBuilder<ITEM, VIEW_ELEMENT> generator(
			Class<ITEM> itemClass, Class<VIEW_ELEMENT> viewElementClass ) {
		return FACTORY.generator( itemClass, viewElementClass );
	}

	public static TextViewElementBuilder text() {
		return FACTORY.text();
	}

	public static TextViewElementBuilder text( String text ) {
		return FACTORY.text( text );
	}

	public static TextViewElementBuilder html() {
		return FACTORY.html();
	}

	public static TextViewElementBuilder html( String html ) {
		return FACTORY.html( html );
	}

	public static NodeViewElementBuilder node( String tagName ) {
		return FACTORY.node( tagName );
	}

	public static HiddenFormElementBuilder hidden() {
		return FACTORY.hidden();
	}

	public static NodeViewElementBuilder div( ViewElement.WitherSetter... setters ) {
		return FACTORY.div().with( setters );
	}

	public static NodeViewElementBuilder span( ViewElement.WitherSetter... setters ) {
		return FACTORY.span().with( setters );
	}

	public static NodeViewElementBuilder paragraph() {
		return FACTORY.paragraph();
	}

	public static LinkViewElementBuilder link() {
		return FACTORY.link();
	}

	public static FormViewElementBuilder form() {
		return FACTORY.form();
	}

	public static LabelFormElementBuilder label( String labelText ) {
		return FACTORY.label( labelText );
	}

	public static LabelFormElementBuilder label() {
		return FACTORY.label();
	}

	public static FormGroupElementBuilder formGroup( ViewElementBuilder label, ViewElementBuilder control ) {
		return FACTORY.formGroup( label, control );
	}

	public static FormGroupElementBuilder formGroup() {
		return FACTORY.formGroup();
	}

	public static ButtonViewElementBuilder button() {
		return FACTORY.button();
	}

	public static TextboxFormElementBuilder password() {
		return FACTORY.password();
	}

	public static FieldsetFormElementBuilder fieldset() {
		return FACTORY.fieldset();
	}

	public static FieldsetFormElementBuilder fieldset( String legendText ) {
		return FACTORY.fieldset( legendText );
	}

	public static FileUploadFormElementBuilder file() {
		return FACTORY.file();
	}

	public static TextboxFormElementBuilder textarea() {
		return FACTORY.textarea();
	}

	public static TextboxFormElementBuilder textbox() {
		return FACTORY.textbox();
	}

	public static TableViewElementBuilder table() {
		return FACTORY.table();
	}

	public static TableViewElementBuilder.Header tableHeader() {
		return FACTORY.tableHeader();
	}

	public static TableViewElementBuilder.Body tableBody() {
		return FACTORY.tableBody();
	}

	public static TableViewElementBuilder.Footer tableFooter() {
		return FACTORY.tableFooter();
	}

	public static TableViewElementBuilder.Caption tableCaption() {
		return FACTORY.tableCaption();
	}

	public static TableViewElementBuilder.Cell tableCell() {
		return FACTORY.tableCell();
	}

	public static TableViewElementBuilder.Cell tableHeaderCell() {
		return FACTORY.tableHeaderCell();
	}

	public static TableViewElementBuilder.Row tableRow() {
		return FACTORY.tableRow();
	}

	public static NodeViewElementBuilder row() {
		return FACTORY.row();
	}

	@Deprecated
	public static NodeViewElementBuilder helpBlock( String text ) {
		return FACTORY.helpBlock( text );
	}

	@Deprecated
	public static NodeViewElementBuilder helpBlock() {
		return FACTORY.helpBlock();
	}

	public static OptionsFormElementBuilder options() {
		return FACTORY.options();
	}

	public static OptionFormElementBuilder<CheckboxFormElement> checkbox() {
		return FACTORY.checkbox();
	}

	public static OptionFormElementBuilder<RadioFormElement> radio() {
		return FACTORY.radio();
	}

	public static OptionFormElementBuilder option() {
		return FACTORY.option();
	}

	public static ColumnViewElementBuilder column( Grid.DeviceGridLayout... layouts ) {
		return FACTORY.column( layouts );
	}

	public static InputGroupFormElementBuilderSupport inputGroup( ViewElementBuilder control ) {
		return FACTORY.inputGroup( control );
	}

	public static InputGroupFormElementBuilderSupport inputGroup() {
		return FACTORY.inputGroup();
	}

	public static DateTimeFormElementBuilder datetime() {
		return FACTORY.datetime();
	}

	public static NumericFormElementBuilder numeric() {
		return FACTORY.numeric();
	}

	public static AlertViewElementBuilder alert() {
		return FACTORY.alert();
	}

	public static FaIcon faIcon( String icon ) {
		return FACTORY.faIcon( icon );
	}

	public static AutoSuggestFormElementBuilder autosuggest() {
		return FACTORY.autosuggest();
	}

	public static DefaultNavComponentBuilder nav( Menu menu ) {
		return COMPONENT_FACTORY.nav( menu );
	}

	public static PanelsNavComponentBuilder panels( Menu menu ) {
		return COMPONENT_FACTORY.panels( menu );
	}

	public static BreadcrumbNavComponentBuilder breadcrumb( Menu menu ) {
		return COMPONENT_FACTORY.breadcrumb( menu );
	}

	/**
	 * Create a new builder for a {@link com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElement}.
	 *
	 * @return tooltip builder
	 */
	public static TooltipViewElementBuilder tooltip() {
		return new TooltipViewElementBuilder();
	}

	/**
	 * Create a new builder for a {@link com.foreach.across.modules.bootstrapui.elements.tooltip.TooltipViewElement}.
	 *
	 * @param tooltipText text for the tooltip
	 * @return tooltip builder
	 */
	public static TooltipViewElementBuilder tooltip( String tooltipText ) {
		return new TooltipViewElementBuilder().text( tooltipText );
	}

	/**
	 * Create a new builder for a {@link ScriptViewElement} with the given media type.
	 *
	 * @param type of the script
	 * @return builder
	 */
	public static ScriptViewElementBuilder script( @NonNull MediaType type ) {
		return script().type( type );
	}

	/**
	 * Create a new builder for a {@link ScriptViewElement}.
	 *
	 * @return builder
	 */
	public static ScriptViewElementBuilder script() {
		return new ScriptViewElementBuilder();
	}
}
