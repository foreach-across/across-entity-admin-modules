package com.foreach.across.testapplication.application.domain.user;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes.EditableValue.LIST_VIEW_EDITABLE_VALUES;

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
		        .attribute( LIST_VIEW_EDITABLE_VALUES, true )
		        .listView( lvb -> lvb.showProperties( "name", "dateOfBirth", "company" ) )
		        .updateFormView( fvb -> fvb.viewElementMode( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW() ) )
		        .postProcessor( mec -> {
			        EntityViewFactory listView = mec.getViewFactory( EntityView.LIST_VIEW_NAME );
			        if ( listView instanceof DispatchingEntityViewFactory ) {
				        EntityViewProcessorRegistry registry = ( (DispatchingEntityViewFactory) listView ).getProcessorRegistry();
				        registry.getProcessor( SortableTableRenderingViewProcessor.class.getName(), SortableTableRenderingViewProcessor.class )
				                .ifPresent( p -> p.setViewElementMode(
						                ViewElementMode.FORM_READ.withChildMode( FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
						                                                         WebUtilityViewElementMode.EDITABLE_LIST_VALUE ) ) );
			        }
		        } );
	}
}
