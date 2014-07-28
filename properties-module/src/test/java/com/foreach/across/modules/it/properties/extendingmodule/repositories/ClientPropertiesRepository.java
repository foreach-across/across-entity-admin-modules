package com.foreach.across.modules.it.properties.extendingmodule.repositories;

import com.foreach.across.modules.properties.repositories.EntityPropertiesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Repository
public class ClientPropertiesRepository extends EntityPropertiesRepository<Long>
{
	@Autowired
	public ClientPropertiesRepository( DataSource dataSource ) {
		super( dataSource, "client_properties", "client_id" );
	}
}
