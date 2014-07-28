package com.foreach.across.modules.properties.services;

import com.foreach.across.modules.properties.business.EntityProperties;

/**
 * @author Arne Vandamme
 */
public interface EntityPropertiesService<T extends EntityProperties<U>, U>
{
	T getProperties( U entityId );

	void saveProperties( T entityProperties );

	void deleteProperties( U entityId );
}
