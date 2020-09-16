package com.foreach.across.testapplication.application.domain.drink;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.dependsOn;
import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.onProperties;

@Configuration
public class DrinkUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Drink.class )
		        .properties(
				        // only show the linked feature selection when dealing with a derived feature
				        onProperties( "alcoholPercentage" ).enable(
						        dependsOn( dependsOn -> dependsOn.property( "containsAlcohol" ).isChecked() )
				        )
		        );
	}
}
