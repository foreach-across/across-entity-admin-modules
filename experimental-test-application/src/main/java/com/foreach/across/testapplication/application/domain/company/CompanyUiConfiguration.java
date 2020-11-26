package com.foreach.across.testapplication.application.domain.company;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;

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
		        .postProcessor(
				        mec -> {
					        MutableEntityConfiguration conf = mec;
					        new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
							        .association(
									        ab -> ab.name( "user.company" )
									                .listView(
											                lvb -> lvb.viewProcessor( vp -> vp.withType( SortableTableRenderingViewProcessor.class )
											                                                  .configure( pr -> pr.setViewElementMode( ViewElementMode.FORM_READ
													                                                                                           .withChildMode(
															                                                                                           FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
															                                                                                           WebUtilityViewElementMode.EDITABLE_LIST_VALUE ) ) ) )
											                          .showProperties( "name", "dateOfBirth", "company.name", "mentor" )
											                          .properties(
													                          removeLabelsWithinFormGroups( "name", "dateOfBirth", "company.name", "mentor" ) )
									                )
							        ).apply( conf );
				        }
		        );
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
