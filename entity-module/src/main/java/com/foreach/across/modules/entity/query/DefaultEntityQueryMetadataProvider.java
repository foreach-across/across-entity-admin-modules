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

package com.foreach.across.modules.entity.query;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.convert.TypeDescriptor;

import java.time.temporal.Temporal;
import java.util.Date;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;

/**
 * Default implementation of {@link EntityQueryMetadataProvider} that validates properties
 * using a {@link EntityPropertyRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class DefaultEntityQueryMetadataProvider implements EntityQueryMetadataProvider
{
	public static final EntityQueryOps[] STRING_OPS = new EntityQueryOps[] { EQ, NEQ, IN, NOT_IN, LIKE, NOT_LIKE };
	public static final EntityQueryOps[] NUMBER_OPS = new EntityQueryOps[] { EQ, NEQ, IN, NOT_IN, GT, GE, LT, LE };
	public static final EntityQueryOps[] COLLECTION_OPS = new EntityQueryOps[] { CONTAINS, NOT_CONTAINS };
	public static final EntityQueryOps[] ENTITY_OPS = new EntityQueryOps[] { EQ, NEQ, IN, NOT_IN };

	private static final TypeDescriptor EQ_GROUP_TYPE = TypeDescriptor.valueOf( EQGroup.class );
	private static final TypeDescriptor EQ_FUNCTION_TYPE = TypeDescriptor.valueOf( EQFunction.class );

	private final EntityPropertyRegistry propertyRegistry;

	public DefaultEntityQueryMetadataProvider( EntityPropertyRegistry propertyRegistry ) {
		this.propertyRegistry = propertyRegistry;
	}

	@Override
	public boolean isValidProperty( String property ) {
		return propertyRegistry.contains( property );
	}

	@Override
	public boolean isValidOperatorForProperty( EntityQueryOps operator, String property ) {
		EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( property );
		return ArrayUtils.contains( retrieveOperandsForType( descriptor.getPropertyTypeDescriptor() ), operator );
	}

	@Override
	public boolean isValidValueForPropertyAndOperator( Object value, String property, EntityQueryOps operator ) {
		EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( property );
		TypeDescriptor type = descriptor.getPropertyTypeDescriptor();
//		Class<?> objectType = type.getObjectType();
		TypeDescriptor valueType = TypeDescriptor.forObject( value );

		if ( !isValidGroupOrNonGroupOperation( valueType, operator ) ) {
			return false;
		}

		return true;
	}

	private boolean isValidGroupOrNonGroupOperation( TypeDescriptor valueType, EntityQueryOps operator ) {
		if ( operator == IN || operator == NOT_IN ) {
			return EQ_GROUP_TYPE.equals( valueType ) || EQ_FUNCTION_TYPE.equals( valueType );
		}

		return !EQ_GROUP_TYPE.equals( valueType );
	}

	private EntityQueryOps[] retrieveOperandsForType( TypeDescriptor type ) {
		Class<?> objectType = type.getObjectType();

		if ( String.class.equals( objectType ) ) {
			return STRING_OPS;
		}
		if ( type.isPrimitive()
				|| Number.class.isAssignableFrom( objectType )
				|| Date.class.isAssignableFrom( objectType )
				|| Temporal.class.isAssignableFrom( objectType ) ) {
			return NUMBER_OPS;
		}
		if ( type.isArray() || type.isCollection() || type.isMap() ) {
			return COLLECTION_OPS;
		}

		return ENTITY_OPS;
	}

}
