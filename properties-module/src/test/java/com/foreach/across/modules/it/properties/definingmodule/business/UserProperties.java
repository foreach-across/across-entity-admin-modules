package com.foreach.across.modules.it.properties.definingmodule.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * Custom user properties.
 *
 * @author Arne Vandamme
 */
public class UserProperties extends EntityProperties<Long>
{
	private final long userId;

	public UserProperties(
			long userId,
			PropertyTypeRegistry<String> propertyTypeRegistry,
			ConversionService conversionService,
			StringPropertiesSource source ) {
		super( propertyTypeRegistry, conversionService, source );

		this.userId = userId;
	}

	@Override
	public Long getId() {
		return userId;
	}
}
