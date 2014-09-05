package com.foreach.across.modules.it.properties.definingmodule.repositories;

import com.foreach.across.modules.it.properties.definingmodule.business.EntityRevision;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.RevisionBasedEntityPropertiesRepository;

/**
 * @author Arne Vandamme
 */
public class RevisionPropertiesRepository extends RevisionBasedEntityPropertiesRepository<Long, EntityRevision>
{
	public RevisionPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}
}
