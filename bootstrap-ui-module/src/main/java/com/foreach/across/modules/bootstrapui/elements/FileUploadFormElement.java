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
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class FileUploadFormElement extends FormControlElementSupport
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.FILE_UPLOAD;

	public FileUploadFormElement() {
		super( ELEMENT_TYPE );

		setAttribute( "type", "file" );
	}

	/**
	 * @param accept format that is accepted
	 */
	public void setAccept( String accept ) {
		setAttribute( "accept", accept );
	}

	/**
	 * @param multiple true if multiple files are allowed
	 */
	public void setMultiple( boolean multiple ) {
		setAttribute( "multiple", multiple ? true : null );
	}

	/**
	 * @param value selected in the file upload (usually not supported)
	 */
	public void setValue( String value ) {
		setAttribute( "value", value );
	}
}
