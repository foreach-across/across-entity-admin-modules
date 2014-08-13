package com.foreach.across.modules.it.properties.definingmodule.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;

/**
 * @author Arne Vandamme
 */
public class UserPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public UserPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}
}
