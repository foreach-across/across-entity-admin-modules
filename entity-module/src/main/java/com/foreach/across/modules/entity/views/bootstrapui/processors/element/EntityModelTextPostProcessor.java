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

import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import org.springframework.util.Assert;

import java.util.Locale;

/**
 * Expects the property value to be of the entity type represented by the entity model.
 * Will use the {@link EntityModel#getLabel(Object, Locale)} method to convert to text.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings( "unchecked" )
public final class EntityModelTextPostProcessor<T extends ConfigurableTextViewElement> extends AbstractValueTextPostProcessor<T>
{
	private final EntityModel entityModel;

	public EntityModelTextPostProcessor( EntityPropertyDescriptor propertyDescriptor, EntityModel entityModel ) {
		super( propertyDescriptor );
		Assert.notNull( entityModel );
		this.entityModel = entityModel;
	}

	@Override
	protected String print( Object value, Locale locale ) {
		return entityModel.getLabel( value, locale );
	}
}
