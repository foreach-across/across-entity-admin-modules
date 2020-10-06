package com.foreach.across.testapplication.application.domain.user;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( User.class )
		        .listView(
				        lvb -> lvb.showProperties( "name", "dateOfBirth", "company" )
		        )
		        .updateFormView( fvb -> fvb.viewElementMode( ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE ) ) );
	}
}
