package com.foreach.across.modules.it.properties.definingmodule.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
public class UserPropertiesRepository extends EntityPropertiesRepository<Long>
{
	public UserPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		super( configuration );
	}
}
