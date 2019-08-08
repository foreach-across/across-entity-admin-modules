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

import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author Arne Vandamme
 */
@Accessors(chain = true)
@Getter
@Setter
public abstract class FormControlElementSupport extends AbstractNodeViewElement implements FormControlElement
{
	private boolean disabled, readonly, required;
	private String controlName;

	@Getter(value = AccessLevel.NONE)
	@Setter(value = AccessLevel.NONE)
	private boolean htmlIdSpecified;

	protected FormControlElementSupport( String elementType ) {
		super( "input" );
		setElementType( elementType );
	}

	@Override
	public FormControlElementSupport setName( String name ) {
		super.setName( name );
		if ( controlName == null ) {
			setControlName( name );
		}
		return this;
	}

	@Override
	public String getControlName() {
		return controlName;
	}

	@Override
	public FormControlElementSupport setControlName( String controlName ) {
		this.controlName = controlName;
		if ( !htmlIdSpecified ) {
			super.setHtmlId( controlName );
		}
		return this;
	}

	@Override
	public FormControlElementSupport setHtmlId( String htmlId ) {
		this.htmlIdSpecified = true;
		super.setHtmlId( htmlId );
		return this;
	}
}
