package com.foreach.across.modules.it.properties.definingmodule.services;

import com.foreach.across.modules.it.properties.definingmodule.business.EntityRevision;
import com.foreach.across.modules.it.properties.definingmodule.business.RevisionProperties;
import com.foreach.across.modules.it.properties.definingmodule.registry.RevisionPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.RevisionPropertiesRepository;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractRevisionBasedEntityPropertiesService;
import com.foreach.common.spring.util.PropertyTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class RevisionPropertyService extends AbstractRevisionBasedEntityPropertiesService<RevisionProperties, Long, EntityRevision>
{
	@Autowired
	public RevisionPropertyService( RevisionPropertyRegistry revisionPropertyRegistry,
	                                RevisionPropertiesRepository revisionPropertiesRepository ) {
		super( revisionPropertyRegistry, revisionPropertiesRepository );
	}

	@Override
	protected RevisionProperties createEntityProperties( Long entityId,
	                                                     PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                     ConversionService conversionService,
	                                                     StringPropertiesSource source ) {
		return new RevisionProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
