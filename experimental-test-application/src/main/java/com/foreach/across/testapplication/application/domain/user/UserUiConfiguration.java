package com.foreach.across.testapplication.application.domain.user;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

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
				                  .properties( removeLabelsWithinFormGroups( "name", "dateOfBirth", "company" ) )
		        )
		        .updateFormView( fvb -> fvb.viewElementMode( ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE ) ) )
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

	private Consumer<EntityPropertyRegistryBuilder> removeLabelsWithinFormGroups( String... properties ) {
		ViewElementPostProcessor<FormGroupElement> formGroupElementViewElementPostProcessor = ( builderContext, element ) -> {
			element.getLabel().set( css.screenReaderOnly );
		};
		return props -> {
			Arrays.stream( properties )
			      .forEach( p -> {
				      props.property( p )
				           .viewElementPostProcessor( ViewElementMode.FORM_READ.withChildMode( FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
				                                                                               WebUtilityViewElementMode.EDITABLE_LIST_VALUE ),
				                                      formGroupElementViewElementPostProcessor )
				           .viewElementBuilder( ViewElementMode.LABEL, ctx -> new ContainerViewElement() );
			      } );
		};
	}
}
