package com.foreach.across.modules.web.ui.elements;

import com.foreach.across.modules.web.ui.StandardViewElements;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElements;

/**
 * Simplest implementation of {@link com.foreach.across.modules.web.ui.ViewElements} that also implements
 * {@link com.foreach.across.modules.web.ui.ViewElement}.  A container is a named
 * collection of elements that allows configuration of a custom template for rendering.
 * <p/>
 * Unless a custom template is being used, a collection does not add additional output but simply renders
 * its children in order.
 *
 * @author Arne Vandamme
 */

public class ContainerViewElement extends ViewElements implements ViewElement
{
	public static final String TYPE = StandardViewElements.CONTAINER;

	private String name, customTemplate, elementType;

	public ContainerViewElement() {
		setElementType( TYPE );
	}

	public ContainerViewElement( String name ) {
		this.name = name;
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
