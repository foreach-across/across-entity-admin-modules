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

package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.*;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.Printer;
import org.springframework.stereotype.Service;

import java.text.Format;

/**
 * @author Arne Vandamme
 */
@Service
@RequiredArgsConstructor
public class EntityViewElementBuilderFactoryHelper
{
	private ConversionService mvcConversionService;
	private EntityRegistry entityRegistry;

	/**
	 * Create a {@link com.foreach.across.modules.web.ui.ViewElementPostProcessor} for a single
	 * {@link EntityPropertyDescriptor} that will convert a property value to text for a
	 * {@link com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement}.  The actual implementation
	 * depends on the presence of attributes on the descriptor.
	 *
	 * @param descriptor for which to create default post processor
	 * @return default postprocessor
	 */
	public <V extends ConfigurableTextViewElement> AbstractValueTextPostProcessor<V> createDefaultValueTextPostProcessor(
			EntityPropertyDescriptor descriptor ) {
		if ( descriptor.hasAttribute( Printer.class ) ) {
			return new PrinterValueTextPostProcessor<>( descriptor, descriptor.getAttribute( Printer.class ) );
		}
		if ( descriptor.hasAttribute( Format.class ) ) {
			return new FormatValueTextPostProcessor<>( descriptor, descriptor.getAttribute( Format.class ) );
		}

		Class<?> propertyType = descriptor.getPropertyType();

		if ( propertyType != null ) {
			EntityConfiguration<?> entityConfiguration = entityRegistry.getEntityConfiguration( propertyType );

			if ( entityConfiguration != null && entityConfiguration.hasEntityModel() ) {
				return new EntityModelTextPostProcessor<>( descriptor, entityConfiguration.getEntityModel() );
			}

			if ( propertyType.isEnum() ) {
				return new EnumValueTextPostProcessor<>( descriptor, propertyType.asSubclass( Enum.class ) );
			}
		}

		return new ConversionServiceValueTextPostProcessor<>( descriptor, mvcConversionService );
	}

	@Autowired
	void setMvcConversionService( ConversionService mvcConversionService ) {
		this.mvcConversionService = mvcConversionService;
	}

	@Autowired
	void setEntityRegistry( EntityRegistry entityRegistry ) {
		this.entityRegistry = entityRegistry;
	}
}
