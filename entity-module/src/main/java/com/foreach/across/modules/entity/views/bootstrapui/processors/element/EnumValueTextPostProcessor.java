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
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

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
	@SuppressWarnings("unchecked")
	public void postProcess( ViewElementBuilderContext builderContext, T element ) {
		Object entity = EntityViewElementUtils.currentEntity( builderContext );
		EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );
		ValueFetcher valueFetcher = getPropertyDescriptor().getValueFetcher();

		if ( entity != null && valueFetcher != null ) {
			Enum propertyValue = (Enum) valueFetcher.getValue( entity );

			if ( propertyValue != null ) {
				element.setText( convert( propertyValue, LocaleContextHolder.getLocale(), codeResolver ) );
			}
		}
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return null;
	}

	private String convert( Enum value, Locale locale, EntityMessageCodeResolver codeResolver ) {
		if ( codeResolver != null ) {
			String messageCode = "enums." + enumType.getSimpleName() + "." + value.name();
			String defaultLabel = EntityUtils.generateDisplayName( value.name() );

			return codeResolver.getMessageWithFallback( messageCode, defaultLabel, locale );
		}

		return value.name();
	}
}
