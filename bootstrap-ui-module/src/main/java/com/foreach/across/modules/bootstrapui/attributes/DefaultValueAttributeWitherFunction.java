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
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.support.AttributeWitherFunction;

/**
 * @author Arne Vandamme
 * @since 2.3.0
 */
public class DefaultValueAttributeWitherFunction<T> extends AttributeWitherFunction<T> implements ViewElement.WitherSetter<HtmlViewElement>
{
	private final String attributeKey;
	private final T defaultValue;

	public DefaultValueAttributeWitherFunction( String attributeKey, T defaultValue ) {
		super( attributeKey );
		this.attributeKey = attributeKey;
		this.defaultValue = defaultValue;
	}

	@Override
	public void applyTo( HtmlViewElement target ) {
		target.setAttribute( attributeKey, defaultValue );
	}
}
