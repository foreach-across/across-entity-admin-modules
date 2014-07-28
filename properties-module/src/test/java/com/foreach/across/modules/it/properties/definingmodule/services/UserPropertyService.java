package com.foreach.across.modules.it.properties.definingmodule.services;

import com.foreach.across.modules.it.properties.definingmodule.business.UserProperties;
import com.foreach.across.modules.it.properties.definingmodule.registry.UserPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.UserPropertiesRepository;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.services.AbstractEntityPropertiesService;
import com.foreach.across.modules.properties.services.EntityPropertiesService;
import com.foreach.spring.util.PropertyTypeRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class UserPropertyService extends AbstractEntityPropertiesService<UserProperties, Long>
{
	@Autowired
	public UserPropertyService( UserPropertyRegistry userPropertyRegistry,
	                            UserPropertiesRepository userPropertiesRepository ) {
		super( userPropertyRegistry, userPropertiesRepository );
	}

	@Override
	protected UserProperties createEntityProperties( Long entityId,
	                                                 PropertyTypeRegistry<String> propertyTypeRegistry,
	                                                 ConversionService conversionService,
	                                                 StringPropertiesSource source ) {
		return new UserProperties( entityId, propertyTypeRegistry, conversionService, source );
	}
}
