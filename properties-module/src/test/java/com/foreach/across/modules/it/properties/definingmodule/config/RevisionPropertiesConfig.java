package com.foreach.across.modules.it.properties.definingmodule.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.definingmodule.registry.RevisionPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.RevisionPropertiesRepository;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class RevisionPropertiesConfig extends AbstractEntityPropertiesConfiguration
{
	@Override
	protected String originalTableName() {
		return "revision_properties";
	}

	@Override
	public String propertiesId() {
		return "DefiningModule.RevisionProperties";
	}

	@Override
	public String keyColumnName() {
		return "revision_id";
	}

	@Bean
	public RevisionPropertiesRepository revisionPropertiesRepository() {
		return new RevisionPropertiesRepository( this );
	}

	@Bean
	@Exposed
	public RevisionPropertyRegistry revisionPropertyRegistry() {
		return new RevisionPropertyRegistry( this );
	}

}
