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

package com.foreach.across.modules.entity.views.processors.query;

import com.foreach.across.modules.entity.query.EQString;
import com.foreach.across.modules.entity.query.EQValue;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.ui.ViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor.*;

/**
 * @author Steven Gentens
 * @since 3.3.0
 */
public class EntityQueryFilterControlUtils
{
	public static final List<? extends Class<? extends Serializable>> TEXT_VALUE_TYPES = Arrays.asList( String.class, Date.class, LocalDate.class,
	                                                                                                    LocalTime.class, LocalDateTime.class );

	public interface FilterControlAttributes
	{
		String OPERAND = "entity-query-operand";
		String PROPERTY_NAME = "entity-query-property";
		String TYPE = "entity-query-type";
	}

	public static void configureControlSettings( ViewElementBuilderSupport.ElementOrBuilder elementOrBuilder, EntityPropertyDescriptor propertyDescriptor ) {
		setOperand( elementOrBuilder, propertyDescriptor );
		setPropertyName( elementOrBuilder, propertyDescriptor );
		setEQType( elementOrBuilder, propertyDescriptor );
	}

	public static void setOperand( ViewElementBuilderSupport.ElementOrBuilder elementOrBuilder, EntityPropertyDescriptor propertyDescriptor ) {
		EntityQueryOps operand = propertyDescriptor.getAttribute( ENTITY_QUERY_OPERAND, EntityQueryOps.class );
		if ( operand != null ) {
			setAttribute( elementOrBuilder, FilterControlAttributes.OPERAND, operand.name() );
		}
	}

	public static void setPropertyName( ViewElementBuilderSupport.ElementOrBuilder elementOrBuilder, EntityPropertyDescriptor propertyDescriptor ) {
		setAttribute( elementOrBuilder, FilterControlAttributes.PROPERTY_NAME, propertyDescriptor.getName() );
	}

	public static void setEQType( ViewElementBuilderSupport.ElementOrBuilder elementOrBuilder, EntityPropertyDescriptor propertyDescriptor ) {
		Object attribute = propertyDescriptor.getAttribute( ENTITY_QUERY_PROPERTY_TEXT_VALUE );
		if ( attribute == null && isTextType( propertyDescriptor ) ) {
			attribute = EQString.class.getSimpleName();
		}

		if ( attribute == null ) {
			attribute = EQValue.class.getSimpleName();
		}
		setAttribute( elementOrBuilder, FilterControlAttributes.TYPE, attribute );
	}

	private static boolean isTextType( EntityPropertyDescriptor propertyDescriptor ) {
		return TEXT_VALUE_TYPES.stream().anyMatch( clazz -> clazz.equals( propertyDescriptor.getPropertyType() ) );
	}

	public static void setAttribute( ViewElementBuilderSupport.ElementOrBuilder wrapper, String attribute, Object value ) {
		Object elementOrBuilder = wrapper.getSource();
		if ( wrapper.isBuilder() ) {
			if ( AbstractNodeViewElementBuilder.class.isAssignableFrom( elementOrBuilder.getClass() ) ) {
				( (AbstractNodeViewElementBuilder) elementOrBuilder ).data( attribute, value );
			}
		}
		else {
			if ( AbstractNodeViewElement.class.isAssignableFrom( elementOrBuilder.getClass() ) ) {
				( (AbstractNodeViewElement) elementOrBuilder ).setAttribute( "data-" + attribute, value );
			}
		}
	}
}
