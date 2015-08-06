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

import com.foreach.across.modules.bootstrapui.elements.InputGroupFormElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

/**
 * @author Arne Vandamme
 */
public class InputGroupFormElementBuilder extends AbstractNodeViewElementBuilder<InputGroupFormElement, InputGroupFormElementBuilder>
{
	private ElementOrBuilder addonBefore, addonAfter, control;

	public InputGroupFormElementBuilder addonBefore( ViewElement element ) {
		addonBefore = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public InputGroupFormElementBuilder addonBefore( ViewElementBuilder element ) {
		addonBefore = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public InputGroupFormElementBuilder addonAfter( ViewElement element ) {
		addonAfter = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public InputGroupFormElementBuilder addonAfter( ViewElementBuilder element ) {
		addonAfter = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public InputGroupFormElementBuilder control( ViewElement element ) {
		control = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	public InputGroupFormElementBuilder control( ViewElementBuilder element ) {
		control = element != null ? ElementOrBuilder.wrap( element ) : null;
		return this;
	}

	@Override
	protected InputGroupFormElement createElement( ViewElementBuilderContext builderContext ) {
		InputGroupFormElement group = apply( create(), builderContext );

		if ( control != null ) {
			group.setControl( control.get( builderContext ) );
		}
		if ( addonBefore != null ) {
			group.setAddonBefore( addonBefore.get( builderContext ) );
		}
		if ( addonAfter != null ) {
			group.setAddonAfter( addonAfter.get( builderContext ) );
		}

		return group;
	}

	protected InputGroupFormElement create() {
		return new InputGroupFormElement();
	}
}
