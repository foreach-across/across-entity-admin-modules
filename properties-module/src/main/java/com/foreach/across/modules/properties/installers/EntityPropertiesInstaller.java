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
package com.foreach.across.modules.properties.installers;

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;
import liquibase.exception.LiquibaseException;

import java.util.Collections;

/**
 * @author Arne Vandamme
 */
public abstract class EntityPropertiesInstaller extends AcrossLiquibaseInstaller
{
	private final SchemaConfiguration schemaConfiguration = new SchemaConfiguration(
			Collections.<SchemaObject>emptyList() );

	protected EntityPropertiesInstaller() {
		this( false );
	}

	protected EntityPropertiesInstaller( boolean revisionBased ) {
		super( revisionBased
				       ? "classpath:com/foreach/across/modules/properties/installers/RevisionBasedEntityPropertiesInstaller.xml"
				       : "classpath:com/foreach/across/modules/properties/installers/EntityPropertiesInstaller.xml" );
		setSchemaConfiguration( schemaConfiguration );
	}

	protected abstract String getTableName();

	protected abstract String getKeyColumnName();

	@Override
	public void install() throws LiquibaseException {
		schemaConfiguration.setProperty( "table.entity_properties", getTableName() );
		schemaConfiguration.setProperty( "column.entity_id", getKeyColumnName() );

		super.install();
	}
}
