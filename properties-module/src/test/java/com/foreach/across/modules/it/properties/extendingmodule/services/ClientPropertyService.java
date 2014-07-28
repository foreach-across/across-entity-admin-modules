package com.foreach.across.modules.it.properties.extendingmodule.services;

import com.foreach.across.modules.it.properties.extendingmodule.business.ClientProperties;
import com.foreach.across.modules.it.properties.extendingmodule.registry.ClientPropertyRegistry;
import com.foreach.across.modules.it.properties.extendingmodule.repositories.ClientPropertiesRepository;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class ClientPropertyService extends AbstractEntityPropertiesService<ClientProperties, Long>
{
	@Autowired
	public ClientPropertyService( ClientPropertyRegistry clientPropertyRegistry,
	                              ClientPropertiesRepository clientPropertiesRepository ) {
		super( clientPropertyRegistry, clientPropertiesRepository );
	}

	@Override
	protected ClientProperties createEntityProperties( Long entityId,
	                                                   PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                   ConversionService conversionService,
	                                                   StringPropertiesSource source ) {
		return new ClientProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
