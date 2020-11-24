package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupDescriptionTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupHelpTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupTooltipTextPostProcessor;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode.REFRESHABLE_LIST_VALUE;

/**
 * Configures the editableValues view on all registered entities that have an entity model.
 *
 * @see EditableValueViewElementBuilderFactory
 * @see EditableValueControlProcessor
 */
@Component
@Order
@SuppressWarnings("unused")
class EditableValuesFormConfiguration implements EntityConfigurer
{
	private final AcrossModuleInfo moduleInfo;

	public EditableValuesFormConfiguration( @Module(EntityModule.NAME) AcrossModuleInfo moduleInfo ) {
		this.moduleInfo = moduleInfo;
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		ViewElementMode editableValueViewElementMode = ViewElementMode.FORM_READ.withChildMode( "control", WebUtilityViewElementMode.EDITABLE_VALUE );
		EntityConfigurationBuilder<Object> editableValuesView = new EntityConfigurationBuilder<>(
				moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
				.formView(
						EditableValueViewElementBuilderFactory.REQUIRED_VIEW,
						fvb -> {
							fvb.requiredAllowableAction( AllowableAction.UPDATE )
							   .viewElementMode(
									   editableValueViewElementMode )
							   .viewProcessor( vb -> vb.createBean( EditableValueControlProcessor.class )
							                           .order( Ordered.LOWEST_PRECEDENCE ) );
						}
				);

		entities.all()
		        .postProcessor(
				        entityConfiguration -> {
					        if ( !entityConfiguration.hasView( EditableValueViewElementBuilderFactory.REQUIRED_VIEW )
							        && entityConfiguration.hasEntityModel() ) {
						        editableValuesView.apply( entityConfiguration );
					        }

					        if ( entityConfiguration.hasView( EntityView.LIST_VIEW_NAME ) ) {
						        configureListViewWithRefreshableValues( entityConfiguration );
					        }

					        entityConfiguration.getPropertyRegistry()
					                           .getProperties()
					                           .stream()
					                           .filter( prop -> prop.hasAttribute( ViewElementLookupRegistry.class ) )
					                           .forEach( prop -> {
						                           ViewElementLookupRegistry attribute = prop.getAttribute( ViewElementLookupRegistry.class );
						                           attribute.addViewElementPostProcessor( editableValueViewElementMode,
						                                                                  new FormGroupTooltipTextPostProcessor<>() );
						                           attribute.addViewElementPostProcessor( editableValueViewElementMode,
						                                                                  new FormGroupHelpTextPostProcessor<>() );
						                           attribute.addViewElementPostProcessor( editableValueViewElementMode,
						                                                                  new FormGroupDescriptionTextPostProcessor<>() );
					                           } );

					        entityConfiguration
							        .getAssociations()
							        .forEach( association -> {
								        if ( association.hasView( EntityView.LIST_VIEW_NAME ) ) {
									        configureListViewWithRefreshableValues( entityConfiguration, association );
								        }
							        } );
				        }
		        );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void configureListViewWithRefreshableValues( MutableEntityConfiguration entityConfiguration, EntityAssociation association ) {
		new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
				.association(
						ass -> ass.name( association.getName() )
						          .listView(
								          view -> view.viewProcessor(
										          vp -> vp.withType( SortableTableRenderingViewProcessor.class )
										                  .configure( s -> s.setViewElementMode( REFRESHABLE_LIST_VALUE ) )
								          )
						          )
				)
				.apply( entityConfiguration );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void configureListViewWithRefreshableValues( MutableEntityConfiguration entityConfiguration ) {
		new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
				.listView(
						view -> view.viewProcessor(
								vp -> vp.withType( SortableTableRenderingViewProcessor.class )
								        .configure( s -> s.setViewElementMode( REFRESHABLE_LIST_VALUE ) )
						)
				)
				.apply( entityConfiguration );
	}
}
