package com.foreach.across.testapplication.application.domain.company;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes.EditableValue.LIST_VIEW_EDITABLE_VALUES;

@Configuration
public class CompanyUiConfiguration implements EntityConfigurer
{
	private final AcrossModuleInfo moduleInfo;

	public CompanyUiConfiguration( @Module(EntityModule.NAME) AcrossModuleInfo moduleInfo ) {
		this.moduleInfo = moduleInfo;
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Company.class )
		        .properties(
				        props -> props.property( "workRegulations" )
				                      .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.BINDER )
		        )
		        .updateFormView( fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW() ) )
		        .association(
				        ab -> ab.name( "user.company" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .updateFormView(
						                fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW() )
				                )
		        );

		entities.withType( Company.class )
		        .association(
				        ab -> ab.name( "user.company" )
				                .attribute( LIST_VIEW_EDITABLE_VALUES, true )
				                .listView( lvb -> lvb.showProperties( "name", "dateOfBirth", "company.name", "mentor" )
				                                     .properties( props -> props.property( "company.name" ).displayName( "Company name" ) ) )
		        );
	}
}
