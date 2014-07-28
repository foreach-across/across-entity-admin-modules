package com.foreach.across.modules.it.properties.extendingmodule.registry;

import com.foreach.across.modules.properties.registries.EntityPropertiesRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

/**
 * @author Arne Vandamme
 */
@Service
public class ClientPropertyRegistry extends EntityPropertiesRegistry
{
	@Autowired
	public ClientPropertyRegistry( ConversionService conversionService ) {
		super( conversionService );
	}
}
