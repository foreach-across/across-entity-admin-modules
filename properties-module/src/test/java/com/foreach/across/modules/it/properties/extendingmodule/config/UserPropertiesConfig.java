package com.foreach.across.modules.it.properties.extendingmodule.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.modules.it.properties.definingmodule.registry.UserPropertyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Registers the custom properties.
 *
 * @author Arne Vandamme
 */
@Configuration
public class UserPropertiesConfig
{
	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModule currentModule;

	public static final String BOOLEAN = "extending.booleanProperty";
	public static final String DATE = "extending.dateProperty";
	public static final String DECIMAL = "extending.decimalProperty";

	@Autowired
	private UserPropertyRegistry userPropertyRegistry;

	@PostConstruct
	private void registerProperties() {
		userPropertyRegistry.register( currentModule, BOOLEAN, Boolean.class, false );
		userPropertyRegistry.register( currentModule, DATE, Date.class );
		userPropertyRegistry.register( currentModule, DECIMAL, BigDecimal.class, new BigDecimal( 0 ) );
	}
}
