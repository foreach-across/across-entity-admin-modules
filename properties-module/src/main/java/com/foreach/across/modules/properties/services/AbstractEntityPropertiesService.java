package com.foreach.across.modules.properties.services;

import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertiesService<T extends EntityProperties<U>, U> implements EntityPropertiesService<T, U>
{
	private final EntityPropertiesRegistry entityPropertiesRegistry;
	private final EntityPropertiesRepository<U> entityPropertiesRepository;

	protected AbstractEntityPropertiesService( EntityPropertiesRegistry entityPropertiesRegistry,
	                                           EntityPropertiesRepository<U> entityPropertiesRepository ) {
		this.entityPropertiesRegistry = entityPropertiesRegistry;
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	public T getProperties( U entityId ) {
		StringPropertiesSource source = entityPropertiesRepository.loadProperties( entityId );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               entityPropertiesRegistry.getConversionService(),
		                               source );
	}

	public void saveProperties( T entityProperties ) {
		entityPropertiesRepository.saveProperties( entityProperties.getId(),
		                                           entityProperties.getSource() );
	}

	public void deleteProperties( U entityId ) {
		entityPropertiesRepository.deleteProperties( entityId );
	}

	protected abstract T createEntityProperties( U entityId,
	                                             PropertyTypeRegistry<String> propertyTypeRegistry,
	                                             ConversionService conversionService,
	                                             StringPropertiesSource source );
}
