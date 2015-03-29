package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;

public class TestFormGroupElementBuilder extends AbstractViewElementBuilderTest<FormGroupElementBuilder, FormGroupElement>
{
	@Override
	protected FormGroupElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new FormGroupElementBuilder();
	}
}
