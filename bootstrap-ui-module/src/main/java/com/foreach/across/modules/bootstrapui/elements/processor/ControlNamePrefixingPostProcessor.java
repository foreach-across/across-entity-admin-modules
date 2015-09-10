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
package com.foreach.across.modules.bootstrapui.elements.processor;

import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

/**
 * Prefixes the control name of any {@link FormInputElement}.
 *
 * @author Arne Vandamme
 */
public class ControlNamePrefixingPostProcessor<T extends ViewElement> implements ViewElementPostProcessor<T>
{
	private String prefix;
	private boolean alwaysPrefix;

	public ControlNamePrefixingPostProcessor( String prefix ) {
		this( prefix, false );
	}

	public ControlNamePrefixingPostProcessor( String prefix, boolean alwaysPrefix ) {
		setPrefix( prefix );
		setAlwaysPrefix( alwaysPrefix );
	}

	public void setPrefix( String prefix ) {
		Assert.notNull( prefix );
		this.prefix = prefix;
	}

	/**
	 * Set to {@code true} if a control name should always be prefixed, even if it already starts
	 * with the specified prefix.
	 *
	 * @param alwaysPrefix true if prefixing should always be performed
	 */
	public void setAlwaysPrefix( boolean alwaysPrefix ) {
		this.alwaysPrefix = alwaysPrefix;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, ViewElement element ) {
		if ( element instanceof FormInputElement ) {
			FormInputElement input = (FormInputElement) element;
			String controlName = input.getControlName();

			if ( controlName != null && ( alwaysPrefix || !StringUtils.startsWith( controlName, prefix ) ) ) {
				input.setControlName( prefix + controlName );
			}
		}
	}
}
