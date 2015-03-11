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

import com.foreach.across.modules.web.ui.elements.NodeViewElementSupport;

/**
 * @author Arne Vandamme
 */
public abstract class FormControlElementSupport extends NodeViewElementSupport
{
	private boolean disabled, readonly;
	private String controlName, htmlId;

	private boolean htmlIdSpecified;

	protected FormControlElementSupport( String elementType ) {
		super( elementType );
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled( boolean disabled ) {
		this.disabled = disabled;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly( boolean readonly ) {
		this.readonly = readonly;
	}

	@Override
	public void setName( String name ) {
		super.setName( name );
		if ( controlName == null ) {
			setControlName( name );
		}
	}

	public String getControlName() {
		return controlName;
	}

	public void setControlName( String controlName ) {
		this.controlName = controlName;
		if ( !htmlIdSpecified ) {
			this.htmlId = controlName;
		}
	}

	public String getHtmlId() {
		return htmlId;
	}

	public void setHtmlId( String htmlId ) {
		this.htmlId = htmlId;
		this.htmlIdSpecified = true;
	}
}
