package com.foreach.across.modules.it.properties.extendingmodule.business;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.it.properties.definingmodule.business.User;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * Custom client properties (where client is also a user but the property collection is different).
 *
 * @author Arne Vandamme
 */
public class ClientProperties extends EntityProperties<Long>
{
	private final long userId;

	public ClientProperties(
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
