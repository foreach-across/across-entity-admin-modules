package com.foreach.across.modules.it.properties.extendingmodule.config;

import com.foreach.across.core.AcrossModule;
import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.it.properties.extendingmodule.registry.ClientPropertyRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Arne Vandamme
 */
@Configuration
public class ClientPropertiesConfig
{
	@Autowired
	@Module(AcrossModule.CURRENT_MODULE)
	private AcrossModule currentModule;

	public static final String BOOLEAN = "extending.booleanProperty";

	@Autowired
	private ClientPropertyRegistry clientPropertyRegistry;

	@PostConstruct
	private void registerProperties()
	{
		clientPropertyRegistry.register( currentModule, BOOLEAN, Boolean.class, true );
	}
}
