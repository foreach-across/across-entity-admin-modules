package com.foreach.across.testapplication.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.testapplication.application.dto.CarResource;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarEntityUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.create().entityType( CarResource.class, true );

		entities.withType( CarResource.class )
		        .properties( props -> props.property( "owner" ).propertyType( String.class ).valueFetcher( f -> "Jef Plessers" ) )
		        .formView( "custom", vb ->
				        vb.properties( props -> props.property( "owner" )
				                                     .propertyType( Integer.class )
				                                     .valueFetcher( f -> 1959 )
				        )
		        );
	}
}
