package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.builder.*;
import com.foreach.across.modules.web.ui.StandardViewElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;

public class BootstrapUiFactoryImpl extends StandardViewElementBuilderFactory implements BootstrapUiFactory
{
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
}
