package com.foreach.across.testapplication.application.domain.user;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserUiConfiguration implements EntityConfigurer
{
	private final AutoSuggestDataAttributeRegistrar autoSuggestData;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( User.class )
		        .properties(
				        props -> props.property( "mentor" )
				                      .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST )
				                      .attribute( autoSuggestData.entityQuery( "name ilike '%{0}%'" )
				                                                 .control( ctrl -> ctrl.minLength( 2 ) ) )
		        )
		        .listView(
				        lvb -> lvb.showProperties( "name", "dateOfBirth", "company" )
		        )
		        .updateFormView( fvb -> fvb.viewElementMode( ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE ) ) );
	}
}
