package com.foreach.across.modules.it.properties.definingmodule.business;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public class RevisionProperties extends EntityProperties<Long>
{
	private final long entityId;

	public RevisionProperties(
			long entityId,
			PropertyTypeRegistry<String> propertyTypeRegistry,
			ConversionService conversionService,
			StringPropertiesSource source ) {
		super( propertyTypeRegistry, conversionService, source );

		this.entityId = entityId;
	}

	@Override
	public Long getId() {
		return entityId;
	}
}
