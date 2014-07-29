package com.foreach.across.modules.properties.config;

import com.foreach.across.modules.properties.repositories.PropertyTrackingRepository;
import org.springframework.core.convert.ConversionService;

import javax.sql.DataSource;

/**
 * Configuration descriptor for an EntityProperties instance.
 *
 * @author Arne Vandamme
 */
public interface EntityPropertiesDescriptor
{
	/**
	 * @return Unique id of the properties registry.
	 */
	String propertiesId();

	/**
	 * @return Database table name where the properties are persisted.
	 */
	String tableName();

	/**
	 * @return Name of the key column in the database table.
	 */
	String keyColumnName();

	/**
	 * @return DataSource in which to find the properties table.
	 */
	DataSource dataSource();

	/**
	 * @return ConversionService to use for the (de-)serializing.
	 */
	ConversionService conversionService();

	/**
	 * @return The repository in which to track the registered properties.
	 */
	PropertyTrackingRepository trackingRepository();
}
