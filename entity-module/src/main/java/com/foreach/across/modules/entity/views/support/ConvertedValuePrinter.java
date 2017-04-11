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

package com.foreach.across.modules.entity.views.support;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Printer;

import java.util.Locale;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class ConvertedValuePrinter implements Printer
{
	private final ConversionService conversionService;
	private final EntityPropertyDescriptor descriptor;

	public ConvertedValuePrinter( ConversionService conversionService, EntityPropertyDescriptor descriptor ) {
		this.conversionService = conversionService;
		this.descriptor = descriptor;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String print( Object object, Locale locale ) {
		Object value = descriptor.getValueFetcher().getValue( object );
		return value != null ? conversionService.convert( value, String.class ) : "";
	}
}
