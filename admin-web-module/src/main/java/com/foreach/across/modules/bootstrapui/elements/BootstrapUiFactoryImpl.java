package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.web.ui.StandardViewElementBuilderFactory;

public class BootstrapUiFactoryImpl extends StandardViewElementBuilderFactory implements BootstrapUiFactory
{
	public FormViewElementBuilder form() {
		return new FormViewElementBuilder();
	}
}
