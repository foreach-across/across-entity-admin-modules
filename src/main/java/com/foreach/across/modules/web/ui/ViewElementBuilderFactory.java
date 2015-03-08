package com.foreach.across.modules.web.ui;

import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;

public interface ViewElementBuilderFactory
{
	TextViewElementBuilder text();

	TextViewElementBuilder text( String text );

	TextViewElementBuilder html();

	TextViewElementBuilder html( String html );
}
