package com.foreach.across.modules.properties.config;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.core.database.HasSchemaConfiguration;
import com.foreach.across.modules.properties.PropertiesModule;
import com.foreach.across.modules.properties.repositories.PropertyTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;

import javax.sql.DataSource;

/**
 * Base configuration for EntityProperties tables, using the primary DataSource, configured ConversionService of
 * the PropertiesModule and allowing table rename if the module defines a SchemaConfiguration.
 *
 * @author Arne Vandamme
 */
public abstract class AbstractEntityPropertiesConfiguration implements EntityPropertiesDescriptor
{
	private final boolean allowTableConfiguration;

	@Autowired
	@Qualifier(AcrossContext.DATASOURCE)
	private DataSource primaryDataSource;

	@Autowired
	@Module(PropertiesModule.NAME)
	private AcrossModuleInfo propertiesModule;

	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	protected AcrossModule currentModule;

	protected AbstractEntityPropertiesConfiguration() {
		this( true );
	}

	protected AbstractEntityPropertiesConfiguration( boolean allowTableConfiguration ) {
		this.allowTableConfiguration = allowTableConfiguration;
	}

	@Override
	public DataSource dataSource() {
		return primaryDataSource;
	}

	/**
	 * @return The ConversionService attached to the properties module.
	 */
	public ConversionService conversionService() {
		return (ConversionService) propertiesModule.getApplicationContext().getBean(
				ConversionServiceConfiguration.CONVERSION_SERVICE_BEAN );
	}

	@Override
	public String tableName() {
		if ( currentModule instanceof HasSchemaConfiguration && allowTableConfiguration ) {
			return ( (HasSchemaConfiguration) currentModule ).getSchemaConfiguration().getCurrentTableName(
					originalTableName() );
		}

		return originalTableName();
	}

	@Override
	public PropertyTrackingRepository trackingRepository() {
		return propertiesModule.getApplicationContext().getBean( PropertyTrackingRepository.class );
	}

	protected abstract String originalTableName();
}
