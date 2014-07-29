package com.foreach.across.modules.it.properties.definingmodule.registry;

import com.foreach.across.modules.it.properties.definingmodule.repositories.UserPropertiesRepository;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
public class UserPropertyRegistry extends EntityPropertiesRegistry
{
	public UserPropertyRegistry( EntityPropertiesDescriptor descriptor ) {
		super( descriptor );
	}
}
