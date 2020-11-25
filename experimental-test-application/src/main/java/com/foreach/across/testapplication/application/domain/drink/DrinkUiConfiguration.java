package com.foreach.across.testapplication.application.domain.drink;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.modals.support.ModalConfigurers;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.dependsOn;
import static com.foreach.across.modules.experimental.webutility.support.WebUtilityConfigurers.onProperties;
import static com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.EditableValueViewElementBuilderFactory.EDITABLE_VALUE_INCLUDE_ACTIONS;

@Configuration
public class DrinkUiConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Drink.class )
		        .and( ModalConfigurers.createViewAsModal() )
		        .properties(
				        // only show the linked feature selection when dealing with a derived feature
				        onProperties( "alcoholPercentage" ).enable(
						        dependsOn( dependsOn -> dependsOn.viewElementName( "entity.containsAlcohol" ).isChecked() )
				        )
		        )
		        .detailView( fvb -> fvb.viewElementMode( ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE ) )
		                               .properties(
				                               props -> props.property( "containsAlcohol" )
				                                             .attribute( EDITABLE_VALUE_INCLUDE_ACTIONS, false )
		                               )
		        )
		        .updateFormView(
				        fvb -> fvb.viewElementMode( ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE ) ) );
	}
}
