package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;

public interface BootstrapUiFactory extends ViewElementBuilderFactory
{
	FormViewElementBuilder form();
}
