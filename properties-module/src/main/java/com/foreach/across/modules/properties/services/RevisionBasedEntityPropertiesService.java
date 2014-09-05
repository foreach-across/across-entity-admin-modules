package com.foreach.across.modules.properties.services;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.modules.properties.business.EntityProperties;

/**
 * @author Arne Vandamme
 */
public interface RevisionBasedEntityPropertiesService<T extends EntityProperties<U>, U, R extends Revision>
{
	T getProperties( U entityId, R revision );

	void saveProperties( T entityProperties, R revision );

	void deleteProperties( U entityId );
}
