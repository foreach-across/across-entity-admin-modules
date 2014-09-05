package com.foreach.across.modules.properties.services;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.modules.properties.business.EntityProperties;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import com.foreach.across.modules.properties.repositories.RevisionBasedEntityPropertiesRepository;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.core.convert.ConversionService;

/**
 * @author Arne Vandamme
 */
public abstract class AbstractRevisionBasedEntityPropertiesService<T extends EntityProperties<U>, U, R extends Revision>
		implements RevisionBasedEntityPropertiesService<T, U, R>
{
	private final EntityPropertiesRegistry entityPropertiesRegistry;
	private final RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository;

	protected AbstractRevisionBasedEntityPropertiesService( EntityPropertiesRegistry entityPropertiesRegistry,
	                                                        RevisionBasedEntityPropertiesRepository<U, R> entityPropertiesRepository ) {
		this.entityPropertiesRegistry = entityPropertiesRegistry;
		this.entityPropertiesRepository = entityPropertiesRepository;
	}

	@Override
	public T getProperties( U entityId, R revision ) {

		StringPropertiesSource source = entityPropertiesRepository.loadProperties( entityId, revision );

		return createEntityProperties( entityId,
		                               entityPropertiesRegistry.getPropertyTypeRegistry(),
		                               entityPropertiesRegistry.getConversionService(),
		                               source );
	}

	@Override
	public void saveProperties( T entityProperties, R revision ) {
		entityPropertiesRepository.saveProperties( entityProperties.getId(), revision, entityProperties.getSource() );
	}

	@Override
	public void deleteProperties( U entityId ) {

	}

	protected abstract T createEntityProperties( U entityId,
	                                             PropertyTypeRegistry<String> propertyTypeRegistry,
	                                             ConversionService conversionService,
	                                             StringPropertiesSource source );
}
