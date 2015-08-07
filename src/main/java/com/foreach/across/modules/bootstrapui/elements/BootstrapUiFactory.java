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

import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;

public interface BootstrapUiFactory extends ViewElementBuilderFactory
{
	HiddenFormElementBuilder hidden();

	NodeViewElementBuilder div();

	NodeViewElementBuilder span();

	NodeViewElementBuilder paragraph();

	FormViewElementBuilder form();

	LabelFormElementBuilder label();

	LabelFormElementBuilder label( String labelText );

	FormGroupElementBuilder formGroup();

	FormGroupElementBuilder formGroup( ViewElementBuilder label, ViewElementBuilder control );

	ButtonViewElementBuilder button();

	TextboxFormElementBuilder textbox();

	TextboxFormElementBuilder textarea();

	TextboxFormElementBuilder password();

	FieldsetFormElementBuilder fieldset();

	FieldsetFormElementBuilder fieldset( String legendText );

	TableViewElementBuilder table();

	NodeViewElementBuilder row();

	NodeViewElementBuilder helpBlock();

	NodeViewElementBuilder helpBlock( String text );

	OptionsFormElementBuilder options();

	OptionFormElementBuilder option();

	OptionFormElementBuilder<CheckboxFormElement> checkbox();

	OptionFormElementBuilder<RadioFormElement> radio();

	ColumnViewElementBuilder column( Grid.DeviceGridLayout... layouts );

	InputGroupFormElementBuilder inputGroup();

	InputGroupFormElementBuilder inputGroup( ViewElementBuilder control );

	DateTimeFormElementBuilder datetime();
}
