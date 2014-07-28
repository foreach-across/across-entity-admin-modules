package com.foreach.across.modules.properties.repositories;

import com.foreach.across.modules.properties.business.StringPropertiesSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for persitence of StringTypedPropertyMap instances.
 *
 * @author Arne Vandamme
 */
public class EntityPropertiesRepository<T>
{
	private final String SQL_INSERT_PROPERTY;
	private final String SQL_SELECT_PROPERTIES;
	private final String SQL_DROP_PROPERTIES;

	private final JdbcTemplate jdbcTemplate;

	public EntityPropertiesRepository( DataSource dataSource,
	                                   String table,
	                                   String keyColumn ) {
		jdbcTemplate = new JdbcTemplate( dataSource );

		SQL_INSERT_PROPERTY = String.format( "INSERT INTO %s (%s,property_name,property_value) VALUES (?,?,?)", table,
		                                     keyColumn );
		SQL_SELECT_PROPERTIES = String.format( "SELECT property_name, property_value FROM %s WHERE %s = ?", table,
		                                       keyColumn );
		SQL_DROP_PROPERTIES = String.format( "DELETE FROM %s WHERE %s = ?", table, keyColumn );
	}

	@Transactional(readOnly = true)
	public StringPropertiesSource loadProperties( T entityId ) {
		List<Map<String, Object>> properties = jdbcTemplate.queryForList( SQL_SELECT_PROPERTIES, entityId );

		Map<String, String> sourceMap = new HashMap<>();

		for ( Map<String, Object> entry : properties ) {
			sourceMap.put( (String) entry.get( "property_name" ), (String) entry.get( "property_value" ) );
		}

		return new StringPropertiesSource( sourceMap );
	}

	@Transactional
	public void saveProperties( T entityId, StringPropertiesSource properties ) {
		deleteProperties( entityId );

		for ( Map.Entry<String, ?> entry : properties.getProperties().entrySet() ) {
			jdbcTemplate.update( SQL_INSERT_PROPERTY, entityId, entry.getKey(), entry.getValue() );
		}
	}

	@Transactional
	public void deleteProperties( T entityId ) {
		jdbcTemplate.update( SQL_DROP_PROPERTIES, entityId );
	}
}
