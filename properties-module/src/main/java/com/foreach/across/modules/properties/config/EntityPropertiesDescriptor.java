/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
