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

package com.foreach.across.modules.bootstrapui.attributes;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.support.AttributeWitherFunction;

/**
 * Regular HTML attributes.
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapAttributes
{
	public final static BootstrapAttributes attribute = new BootstrapAttributes();

	public final BootstrapDataAttributes data = new BootstrapDataAttributes();
	public final HtmlAriaAttributes aria = new HtmlAriaAttributes();
	public final AttributeWitherFunction<String> role = new AttributeWitherFunction<>( "role" );

	public ViewElement.WitherSetter role( String value ) {
		return role.withValue( value );
	}

	public <S> AttributeWitherFunction<S> data( String attributeName ) {
		return data.of( attributeName );
	}

	public ViewElement.WitherSetter data( String attributeName, Object value ) {
		return data.of( attributeName ).withValue( value );
	}

	public <S> AttributeWitherFunction<S> aria( String attributeName ) {
		return aria.of( attributeName );
	}


	public <S> AttributeWitherFunction<S> of( String attributeName ) {
		return new AttributeWitherFunction<>( attributeName );
	}
}
