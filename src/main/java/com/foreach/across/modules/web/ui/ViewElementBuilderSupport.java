package com.foreach.across.modules.web.ui;

public abstract class ViewElementBuilderSupport<T extends ViewElement, SELF extends ViewElementBuilder>
		implements ViewElementBuilder<T>
{
	protected String name, customTemplate;

	@SuppressWarnings("unchecked")
	public SELF name( String name ) {
		this.name = name;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF customTemplate( String template ) {
		this.customTemplate = template;
		return (SELF) this;
	}

	protected <V extends ViewElementSupport> V apply( V viewElement ) {
		if ( name != null ) {
			viewElement.setName( name );
		}
		if ( customTemplate != null ) {
			viewElement.setCustomTemplate( customTemplate );
		}

		return viewElement;
	}
}
