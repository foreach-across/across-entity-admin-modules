package com.foreach.across.modules.properties.config;

import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.properties.repositories.PropertyTrackingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Arne Vandamme
 */
@Configuration
public class PropertyTrackingConfiguration
{
	@Autowired
	@Qualifier(AcrossContext.DATASOURCE)
	private DataSource dataSource;

	@Bean
	public PropertyTrackingRepository propertyTrackingRepository() {
		return new PropertyTrackingRepository( dataSource );
	}
}
