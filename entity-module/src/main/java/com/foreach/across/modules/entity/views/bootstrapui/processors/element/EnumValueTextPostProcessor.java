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
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;

import java.util.Locale;
import java.util.Objects;

/**
 * Converts enum value based on message code if a {@link com.foreach.across.modules.entity.support.EntityMessageCodeResolver} is available on the context.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public final class EnumValueTextPostProcessor<T extends ConfigurableTextViewElement> extends AbstractValueTextPostProcessor<T>
{
	private final Class<? extends Enum> enumType;

	public EnumValueTextPostProcessor( EntityPropertyDescriptor propertyDescriptor, Class<? extends Enum> enumType ) {
		super( propertyDescriptor );
		this.enumType = enumType;
	}

	@Override
	protected String print( Object value, Locale locale, ViewElementBuilderContext builderContext ) {
		return convert( (Enum) value, locale, builderContext.getAttribute( EntityMessageCodeResolver.class ) );
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return print( value, locale, Objects.requireNonNull( ViewElementBuilderContext.retrieveGlobalBuilderContext().orElse( null ) ) );
	}

	private String convert( Enum value, Locale locale, EntityMessageCodeResolver codeResolver ) {
		if ( value == null ) {
			return null;
		}

		if ( codeResolver != null ) {
			String messageCode = "enums." + enumType.getSimpleName() + "." + value.name();
			String defaultLabel = EntityUtils.generateDisplayName( value.name() );

			return codeResolver.getMessageWithFallback( messageCode, defaultLabel, locale );
		}

		return value.name();
	}
}
