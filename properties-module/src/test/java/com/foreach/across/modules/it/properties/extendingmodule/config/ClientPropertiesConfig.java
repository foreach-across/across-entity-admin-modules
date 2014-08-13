package com.foreach.across.modules.it.properties.extendingmodule.config;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.it.properties.extendingmodule.registry.ClientPropertyRegistry;
import com.foreach.across.modules.it.properties.extendingmodule.repositories.ClientPropertiesRepository;
import com.foreach.across.modules.properties.config.AbstractEntityPropertiesConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 */
@Configuration
public class ClientPropertiesConfig extends AbstractEntityPropertiesConfiguration
{
	public static final String BOOLEAN = "extending.booleanProperty";

	@Override
	protected String originalTableName() {
		return "client_properties";
	}

	@Override
	public String propertiesId() {
		return "ExtendingModule.ClientProperties";
	}

	@Override
	public String keyColumnName() {
		return "client_id";
	}

	@Bean
	public ClientPropertiesRepository clientPropertiesRepository() {
		return new ClientPropertiesRepository( this );
	}

	@Bean
	@Exposed
	public ClientPropertyRegistry clientPropertyRegistry() {
		ClientPropertyRegistry registry = new ClientPropertyRegistry( this );

		registry.register( currentModule, BOOLEAN, Boolean.class, true );

		return registry;
	}
}
