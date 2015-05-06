/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;
import org.springframework.util.Assert;

/**
 * Represents a Bootstrap button.
 *
 * @author Arne Vandamme
 */
public class ButtonViewElement extends NodeViewElementSupport implements ConfigurableTextViewElement
{
	public enum Type
	{
		BUTTON,
		BUTTON_SUBMIT,
		BUTTON_RESET,
		INPUT,
		INPUT_SUBMIT,
		INPUT_RESET,
		LINK
	}

	public enum State
	{
		ACTIVE,
		DISABLED
	}

	public static final String ELEMENT_TYPE = BootstrapUiElements.BUTTON;

	private String text, title, url = "#";
	private Style style = Style.Button.DEFAULT;
	private Type type = Type.BUTTON;
	private State state;
	private Size size;
	private ViewElement icon;

	public ButtonViewElement() {
		super( ELEMENT_TYPE );
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText( String text ) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Set the title attribute for the element.
	 *
	 * @param title text
	 */
	public void setTitle( String title ) {
		this.title = title;
	}

	public ViewElement getIcon() {
		return icon;
	}

	/**
	 * Set the icon to be aligned on the left-hand side of the text (if there is any text).
	 *
	 * @param icon ViewElement
	 */
	public void setIcon( ViewElement icon ) {
		this.icon = icon;
	}

	public Type getType() {
		return type;
	}

	public void setType( Type type ) {
		Assert.notNull( type );
		this.type = type;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle( Style style ) {
		this.style = style;
	}

	public State getState() {
		return state;
	}

	public void setState( State state ) {
		this.state = state;
	}

	public Size getSize() {
		return size;
	}

	public void setSize( Size size ) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl( String url ) {
		this.url = url;
	}
}
