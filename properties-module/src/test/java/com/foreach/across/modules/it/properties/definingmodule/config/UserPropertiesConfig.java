package com.foreach.across.modules.it.properties.definingmodule.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.definingmodule.registry.UserPropertyRegistry;
import com.foreach.across.modules.it.properties.definingmodule.repositories.UserPropertiesRepository;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class UserPropertiesConfig extends AbstractEntityPropertiesConfiguration
{
	@Override
	protected String originalTableName() {
		return "user_properties";
	}

	@Override
	public String propertiesId() {
		return "DefiningModule.UserProperties";
	}

	@Override
	public String keyColumnName() {
		return "user_id";
	}

	@Bean
	public UserPropertiesRepository userPropertiesRepository() {
		return new UserPropertiesRepository( this );
	}

	@Bean
	@Exposed
	public UserPropertyRegistry userPropertyRegistry() {
		return new UserPropertyRegistry( this );
	}

}
