package com.foreach.across.modules.web.ui;

public abstract class ViewElementSupport implements ViewElement
{
	private String name, customTemplate, elementType;

	protected ViewElementSupport( String elementType ) {
		this.elementType = elementType;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName( String name ) {
		this.name = name;
	}

	@Override
	public String getCustomTemplate() {
		return customTemplate;
	}

	public void setCustomTemplate( String customTemplate ) {
		this.customTemplate = customTemplate;
	}

	@Override
	public String getElementType() {
		return elementType;
	}

	protected void setElementType( String elementType ) {
		this.elementType = elementType;
	}
}
