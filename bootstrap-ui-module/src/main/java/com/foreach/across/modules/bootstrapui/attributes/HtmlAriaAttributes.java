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
import lombok.NonNull;

/**
 * HTML {@code aria-} attributes.
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class HtmlAriaAttributes
{
	public final static HtmlAriaAttributes aria = new HtmlAriaAttributes();

	public final DefaultValueAttributeWitherFunction<Boolean> hasPopup = of( "haspopup", true );
	public final AttributeWitherFunction<Boolean> expanded = of( "expanded" );
	public final AttributeWitherFunction<String> label = of( "label" );
	public final DefaultValueAttributeWitherFunction<Boolean> hidden = of( "hidden", true );

	public ViewElement.WitherSetter hasPopup( boolean value ) {
		return hasPopup.withValue( value );
	}

	public ViewElement.WitherSetter expanded( boolean value ) {
		return expanded.withValue( value );
	}

	public ViewElement.WitherSetter hidden( boolean value ) {
		return hidden.withValue( value );
	}

	public ViewElement.WitherSetter label( @NonNull String text ) {
		return label.withValue( text );
	}

	public <S> AttributeWitherFunction<S> of( @NonNull String attributeName ) {
		return new AttributeWitherFunction<S>( "aria-" + attributeName );
	}

	public <S> DefaultValueAttributeWitherFunction<S> of( @NonNull String attributeName, S defaultValue ) {
		return new DefaultValueAttributeWitherFunction<>( "aria-" + attributeName, defaultValue );
	}
}
