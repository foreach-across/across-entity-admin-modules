package com.foreach.across.testapplication.application.domain.company;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CompanyUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Company.class )
		        .properties(
				        props -> props.property( "workRegulations" )
				                      .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER )
		        )
		        .updateFormView( fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW ) )
		        .association(
				        ab -> ab.name( "user.company" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .updateFormView(
						                fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW )
				                )
		        );
	}
}
