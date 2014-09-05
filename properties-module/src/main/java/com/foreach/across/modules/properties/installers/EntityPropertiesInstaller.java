package com.foreach.across.modules.properties.installers;

import com.foreach.across.core.database.SchemaConfiguration;
import com.foreach.across.core.database.SchemaObject;
import com.foreach.across.core.installers.AcrossLiquibaseInstaller;

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
	public void install() {
		schemaConfiguration.setProperty( "table.entity_properties", getTableName() );
		schemaConfiguration.setProperty( "column.entity_id", getKeyColumnName() );

		super.install();
	}
}
