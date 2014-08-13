package com.foreach.across.modules.it.properties.extendingmodule.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;

/**
 * @author Arne Vandamme
 */
public class ClientPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public ClientPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}
}
