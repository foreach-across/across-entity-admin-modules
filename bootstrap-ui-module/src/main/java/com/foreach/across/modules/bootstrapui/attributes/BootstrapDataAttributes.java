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

import com.foreach.across.modules.bootstrapui.attributes.data.ToggleAttribute;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.support.AttributeWitherFunction;
import lombok.NonNull;

/**
 * HTML {@code data-} attributes.
 *
 * @author Arne Vandamme
 * @since 2.3.0
 */
@SuppressWarnings("WeakerAccess")
public class BootstrapDataAttributes
{
	public final static BootstrapDataAttributes data = new BootstrapDataAttributes();

	public final ToggleAttribute toggle = new ToggleAttribute();

	public ViewElement.WitherSetter toggle( String value ) {
		return toggle.withValue( value );
	}

	public <S> AttributeWitherFunction<S> of( @NonNull String attributeName ) {
		return new AttributeWitherFunction<>( "data-" + attributeName );
	}
}
