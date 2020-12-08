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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;

import java.util.Locale;
import java.util.Objects;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
public final class BooleanValueTextProcessor<T extends ConfigurableTextViewElement> extends AbstractValueTextPostProcessor<T>
{
	public BooleanValueTextProcessor( EntityPropertyDescriptor propertyDescriptor ) {
		super( propertyDescriptor );
	}

	@Override
	protected String print( Object value, Locale locale, ViewElementBuilderContext builderContext ) {
		return convert( builderContext, (Boolean) value );
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return print( value, locale, Objects.requireNonNull( ViewElementBuilderContext.retrieveGlobalBuilderContext().orElse( null ) ) );
	}

	private String convert( ViewElementBuilderContext builderContext, Boolean propertyValue ) {
		String messageCodePath = "#{properties." + getPropertyDescriptor().getName() + ".value";
		if ( propertyValue == null ) {
			String empty = builderContext.resolveText( "#{EntityModule.controls.options[empty]}", "" );
			return builderContext.resolveText( messageCodePath + "[empty]}", empty );
		}
		else if ( propertyValue ) {
			String yes = builderContext.resolveText( "#{EntityModule.controls.options[true]}", "Yes" );
			return builderContext.resolveText( messageCodePath + "[true]}", yes );
		}
		String no = builderContext.resolveText( "#{EntityModule.controls.options[false]}", "No" );
		return builderContext.resolveText( messageCodePath + "[false]}", no );
	}
}
