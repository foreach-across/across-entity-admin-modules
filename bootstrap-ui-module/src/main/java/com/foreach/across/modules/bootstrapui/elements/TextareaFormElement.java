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

/**
 * Represents HTML textarea.
 *
 * @author Arne Vandamme
 */
public class TextareaFormElement extends TextboxFormElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TEXTAREA;
	public static final String CSS_AUTOSIZE = "js-autosize";

	public static class Type
	{
		public static TextboxFormElement.Type TEXTAREA = new TextboxFormElement.Type( "textarea" );
	}

	private int rows = 3;
	private boolean autoSize = true;

	public TextareaFormElement() {
		setTagName( "textarea" );
		setElementType( ELEMENT_TYPE );
		setType( Type.TEXTAREA );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "textbox" );
	}

	public int getRows() {
		return rows;
	}

	public void setRows( int rows ) {
		this.rows = rows;
	}

	public boolean isAutoSize() {
		return autoSize;
	}

	public void setAutoSize( boolean autoSize ) {
		this.autoSize = autoSize;
	}
}
