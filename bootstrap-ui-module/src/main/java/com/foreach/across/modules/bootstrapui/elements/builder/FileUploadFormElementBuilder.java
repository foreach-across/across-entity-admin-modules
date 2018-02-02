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

package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FileUploadFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Builder for a {@link FileUploadFormElement}.
 *
 * @author Arne Vandamme
 * @see FileUploadFormElement
 * @since 1.0.0
 */
public class FileUploadFormElementBuilder extends FormControlElementBuilderSupport<FileUploadFormElement, FileUploadFormElementBuilder>
{
	public FileUploadFormElementBuilder accept( String... formats ) {
		attribute( "accept", StringUtils.join( formats, "," ) );
		return this;
	}

	public FileUploadFormElementBuilder multiple( boolean multiple ) {
		if ( multiple ) {
			attribute( "multiple", true );
		}
		else {
			removeAttribute( "multiple" );
		}
		return this;
	}

	@Override
	protected FileUploadFormElement createElement( ViewElementBuilderContext viewElementBuilderContext ) {
		return apply( new FileUploadFormElement(), viewElementBuilderContext );
	}
}
