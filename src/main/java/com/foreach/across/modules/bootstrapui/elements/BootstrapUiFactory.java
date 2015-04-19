package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;

public interface BootstrapUiFactory extends ViewElementBuilderFactory
{
	FormViewElementBuilder form();

	LabelFormElementBuilder label();

	LabelFormElementBuilder label( String labelText );

	FormGroupElementBuilder formGroup();

	FormGroupElementBuilder formGroup( ViewElementBuilder label, ViewElementBuilder control );

	ButtonViewElementBuilder button();

	TextboxFormElementBuilder textbox();

	TextboxFormElementBuilder textarea();

	TextboxFormElementBuilder password();

	TableViewElementBuilder table();
}
