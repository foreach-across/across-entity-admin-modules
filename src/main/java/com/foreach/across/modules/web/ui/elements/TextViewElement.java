package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.StandardViewElements;
import com.foreach.across.modules.web.ui.ViewElementSupport;

public class TextViewElement extends ViewElementSupport
{
	public static final String TYPE = StandardViewElements.TEXT;

	private String text;
	boolean escapeXml = true;

	public TextViewElement() {
		this( null, true );
	}

	public TextViewElement( String text ) {
		this( text, true );
	}

	public TextViewElement( String name, String text ) {
		this( text, true );
		setName( name );
	}

	public TextViewElement( String text, boolean escapeXml ) {
		super( TYPE );
		this.text = text;
		this.escapeXml = escapeXml;
	}

	public TextViewElement( String name, String text, boolean escapeXml ) {
		super( TYPE );
		this.text = text;
		this.escapeXml = escapeXml;
		setName( name );
	}

	public String getText() {
		return text;
	}

	public void setText( String text ) {
		this.text = text;
	}

	public boolean isEscapeXml() {
		return escapeXml;
	}

	public void setEscapeXml( boolean escapeXml ) {
		this.escapeXml = escapeXml;
	}
}


