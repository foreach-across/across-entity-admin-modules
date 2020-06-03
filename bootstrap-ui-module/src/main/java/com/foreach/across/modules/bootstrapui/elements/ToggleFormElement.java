/*
 * Copyright 2019 the original author or authors
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
 * Represents a <a href="https://getbootstrap.com/docs/4.3/components/forms/#switches">bootstrap switch</a> element.
 *
 * @author Steven Gentens
 * @since 3.0.0
 */
public class ToggleFormElement extends CheckboxFormElement
{
	public static final String ELEMENT_TYPE = BootstrapUiElements.TOGGLE;

	public ToggleFormElement() {
		setElementType( ELEMENT_TYPE );
	}

	@Override
	public ToggleFormElement set( WitherSetter... setters ) {
		super.set( setters );
		return this;
	}

	@Override
	public ToggleFormElement remove( WitherRemover... functions ) {
		super.remove( functions );
		return this;
	}
}
