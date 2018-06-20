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

import com.foreach.across.core.annotations.RefreshableCollection;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * Responsible for converting raw {@link EQType} instances into strong-typed arguments.
 * Supports {@link EQFunction} types.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EQTypeConverter
{
	private ConversionService conversionService;
	private Collection<EntityQueryFunctionHandler> functionHandlers = Collections.emptyList();

	@Autowired
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	@Autowired
	public void setFunctionHandlers( @NonNull @RefreshableCollection(includeModuleInternals = true) Collection<EntityQueryFunctionHandler> functionHandlers ) {
		this.functionHandlers = functionHandlers;
	}

	@PostConstruct
	public void validateProperties() {
		Assert.notNull( conversionService, "conversionService cannot be null" );
	}

	/**
	 * Converts all values.  If {@param expandGroups} is {@code true}, any value that results in an array will
	 * be flattened in the result array.  The resulting array can be different in length than {@param values} but
	 * no item will be an array. If {@param expandGroups} is {@code false} the return value will have exactly
	 * as many items as the input {@param values}, and items can be arrays.
	 *
	 * @param expectedType type the separate values should be converted to
	 * @param expandGroups {@code true} if groups should be expanded in the flat results array
	 * @param values       to convert
	 * @return converted values
	 */
	public Object[] convertAll( TypeDescriptor expectedType, boolean expandGroups, Object... values ) {
		TypeDescriptor elementType = expectedType.isCollection() ? expectedType.getElementTypeDescriptor() : expectedType;
		List<Object> converted = new ArrayList<>();

		for ( Object argument : values ) {
			Object convertedValue = convert( elementType, argument );

			if ( expandGroups && convertedValue instanceof Object[] ) {
				converted.addAll( Arrays.asList( (Object[]) convertedValue ) );
			}
			else {
				converted.add( convertedValue );
			}
		}

		return converted.toArray();
	}

	/**
	 * Convert a single value to the desired output type.  Input value is expected to be an {@link EQType}.
	 *
	 * @param expectedType type the value should be converted to
	 * @param value        to convert
	 * @return converted value
	 */
	public Object convert( TypeDescriptor expectedType, Object value ) {
		if ( value == null ) {
			return null;
		}

		TypeDescriptor sourceType = TypeDescriptor.forObject( value );

		if ( sourceType.isAssignableTo( expectedType ) ) {
			return value;
		}

		if ( !String.class.equals( expectedType.getObjectType() )
				&& conversionService.canConvert( sourceType, expectedType ) ) {
			// Use directly registered converter
			return conversionService.convert( value, sourceType, expectedType );
		}

		if ( value instanceof EQValue ) {
			return convert( expectedType, ( (EQValue) value ).getValue() );
		}

		if ( value instanceof EQString ) {
			TypeDescriptor stringType = TypeDescriptor.valueOf( String.class );
			if ( !String.class.equals( expectedType.getObjectType() ) && conversionService.canConvert( stringType, expectedType ) ) {
				return conversionService.convert( ( (EQString) value ).getValue(), stringType, expectedType );
			}
			return ( (EQString) value ).getValue();
		}

		if ( value instanceof EQGroup ) {
			EQGroup group = (EQGroup) value;
			Object[] converted = new Object[group.getValues().length];
			for ( int i = 0; i < converted.length; i++ ) {
				converted[i] = convert( expectedType, group.getValues()[i] );
			}
			return converted;
		}

		if ( value instanceof EQFunction ) {
			EQFunction function = (EQFunction) value;
			Optional<EntityQueryFunctionHandler> functionHandler = retrieveFunctionHandler( function.getName(), expectedType );

			if ( functionHandler.isPresent() ) {
				return functionHandler.get().apply( function.getName(), function.getArguments(), expectedType, this );
			}
			else {
				throw new EntityQueryParsingException.IllegalFunction( function.getName() );
			}
		}

		// Unable to convert, return the raw argument
		return value;
	}

	private Optional<EntityQueryFunctionHandler> retrieveFunctionHandler( String name, TypeDescriptor expectedType ) {
		return functionHandlers.stream()
		                       .filter( h -> h.accepts( name, expectedType ) )
		                       .findFirst();
	}
}
