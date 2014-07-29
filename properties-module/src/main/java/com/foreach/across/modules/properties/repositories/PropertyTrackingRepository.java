package com.foreach.across.modules.properties.repositories;

import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.sql.DataSource;
import java.util.Date;
import java.util.UUID;

/**
 * Tracks property registration by other modules in a database table.
 *
 * @author Arne Vandamme
 */
@Repository
public class PropertyTrackingRepository
{
	private static final Logger LOG = LoggerFactory.getLogger( PropertyTrackingRepository.class );

	private static final String SQL_SELECT = "SELECT uuid FROM across_property_tracking WHERE " +
			"hash_code = ? AND module = ? AND properties_id = ? AND database_table = ? AND property_name = ?";
	private static final String SQL_INSERT = "INSERT INTO across_property_tracking " +
			"(uuid, hash_code, module, properties_id, database_table, property_name, first_registration, last_registration) " +
			"VALUES (?,?,?,?,?,?,?,?)";
	private static final String SQL_UPDATE = "UPDATE across_property_tracking SET last_registration = ? WHERE uuid = ?";

	private JdbcTemplate jdbcTemplate;

	public PropertyTrackingRepository( DataSource dataSource ) {
		jdbcTemplate = new JdbcTemplate( dataSource );
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public synchronized void register( String moduleName,
	                                   EntityPropertiesDescriptor descriptor,
	                                   String propertyName ) {
		LOG.debug( "Registering {} property {} for module {}", descriptor.propertiesId(), propertyName, moduleName );

		String hashCode = hashCode( moduleName, descriptor.propertiesId(), descriptor.tableName(), propertyName );
		String id = existingRecordId( hashCode, moduleName, descriptor.propertiesId(), descriptor.tableName(),
		                              propertyName );

		if ( id == null ) {
			createRecord( hashCode, moduleName, descriptor.propertiesId(), descriptor.tableName(), propertyName );
		}
		else {
			updateRegistrationTimestamp( id );
		}
	}

	private void createRecord( String hashCode,
	                           String moduleName,
	                           String propertiesId,
	                           String databaseTable,
	                           String propertyName ) {
		String id = UUID.randomUUID().toString();
		Date timestamp = new Date();

		jdbcTemplate.update( SQL_INSERT, id, hashCode, moduleName, propertiesId, databaseTable, propertyName, timestamp,
		                     timestamp );
	}

	private void updateRegistrationTimestamp( String id ) {
		jdbcTemplate.update( SQL_UPDATE, new Date(), id );
	}

	private String existingRecordId( String hashCode,
	                                 String moduleName,
	                                 String propertiesId,
	                                 String databaseTable,
	                                 String propertyName ) {
		try {
			return jdbcTemplate.queryForObject( SQL_SELECT, String.class,
			                                    hashCode, moduleName, propertiesId, databaseTable, propertyName );
		}
		catch ( EmptyResultDataAccessException erdae ) {
			return null;
		}

	}

	private String hashCode( String moduleName,
	                         String propertiesId,
	                         String databaseTable,
	                         String propertyName ) {
		String hashString = moduleName + propertiesId + databaseTable + propertyName;
		return DigestUtils.md5DigestAsHex( hashString.getBytes() );
	}
}
