package com.foreach.across.modules.web.ui;

import com.foreach.across.modules.web.ui.elements.builder.TextViewElementBuilder;

/**
 * Standard implementation of {@link com.foreach.across.modules.web.ui.ViewElementBuilderFactory} that supports
 * the most basic {@link ViewElementBuilder} types.
 */
public class StandardViewElementBuilderFactory implements ViewElementBuilderFactory
{
	@Override
	public TextViewElementBuilder text() {
		return new TextViewElementBuilder();
	}

	@Override
	public TextViewElementBuilder text( String text ) {
		return new TextViewElementBuilder().text( text );
	}

	@Override
	public TextViewElementBuilder html() {
		return new TextViewElementBuilder().escapeXml( false );
	}

	@Override
	public TextViewElementBuilder html( String html ) {
		return new TextViewElementBuilder().html( html );
	}
}
