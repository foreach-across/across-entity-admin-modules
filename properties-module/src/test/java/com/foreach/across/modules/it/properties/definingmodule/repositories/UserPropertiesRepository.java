package com.foreach.across.modules.it.properties.definingmodule.repositories;

import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Repository
public class UserPropertiesRepository extends EntityPropertiesRepository<Long>
{
	@Autowired
	public UserPropertiesRepository( DataSource dataSource ) {
		super( dataSource, "user_properties", "user_id" );
	}
}
