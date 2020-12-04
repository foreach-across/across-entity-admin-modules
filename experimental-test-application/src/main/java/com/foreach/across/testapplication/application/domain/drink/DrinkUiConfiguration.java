package com.foreach.across.testapplication.application.domain.drink;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers;
import com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
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
						        dependsOn( dependsOn -> dependsOn.viewElementName( "entity.containsAlcohol" ).isChecked() )
				        )
		        )
		        .detailView( fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW() )
		                               .properties(
				                               props -> props.property( "containsAlcohol" )
				                                             .attribute( WebUtilityModuleAttributes.EditableValue.INCLUDE_ACTIONS, false )
		                               )
		        )
		        .updateFormView(
				        fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW() ) )
		        .and( WebUtilityConfigurers.cancelEditableValueViewActionsCustomization( EntityView.DETAIL_VIEW_NAME ) );
	}
}
