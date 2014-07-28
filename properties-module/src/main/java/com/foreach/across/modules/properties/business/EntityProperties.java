package com.foreach.across.modules.properties.business;

import com.foreach.spring.util.PropertiesSource;
import com.foreach.spring.util.PropertyTypeRegistry;
import com.foreach.spring.util.TypedPropertyMap;
import org.springframework.core.convert.ConversionService;

import java.util.Map;

/**
 * @author Arne Vandamme
 */
public abstract class EntityProperties<T> extends TypedPropertyMap<String>
{
	protected EntityProperties( PropertyTypeRegistry<String> propertyTypeRegistry,
	                            ConversionService conversionService,
	                            PropertiesSource source ) {
		super( propertyTypeRegistry, conversionService, source, String.class );
	}

	@Override
	public StringPropertiesSource getSource() {
		return (StringPropertiesSource) super.getSource();
	}

	public abstract T getId();
}
