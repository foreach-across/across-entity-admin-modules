package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupDescriptionTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupHelpTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupTooltipTextPostProcessor;
import com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

import static com.foreach.across.modules.experimental.webutility.support.WebUtilityModuleAttributes.EditableValue.LIST_VIEW_EDITABLE_VALUES;
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
		ViewElementMode editableValueViewElementMode = WebUtilityViewElementMode.EDITABLE_VALUE_VIEW();
		EntityConfigurationBuilder<Object> editableValuesView =
				new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
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

		BiConsumer<MutableEntityConfiguration, EntityAssociation> associationEditableValuesView = ( entityConfiguration, entityAssociation ) -> {
			EntityConfigurationBuilder<Object> configurationBuilder =
					new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
							.association(
									ab -> ab.name( entityAssociation.getName() )
									        .formView( EditableValueViewElementBuilderFactory.REQUIRED_VIEW,
									                   fvb -> {
										                   fvb.requiredAllowableAction( AllowableAction.UPDATE )
										                      .viewElementMode(
												                      editableValueViewElementMode )
										                      .viewProcessor( vb -> vb.createBean( EditableValueControlProcessor.class )
										                                              .order( Ordered.LOWEST_PRECEDENCE ) );
									                   } )
							);
			configurationBuilder.apply( entityConfiguration );
		};

		entities.all()
		        .postProcessor(
				        entityConfiguration -> {
					        if ( !entityConfiguration.hasView( EditableValueViewElementBuilderFactory.REQUIRED_VIEW )
							        && entityConfiguration.hasEntityModel() ) {
						        editableValuesView.apply( entityConfiguration );
					        }

					        if ( entityConfiguration.hasView( EntityView.LIST_VIEW_NAME ) ) {
						        configureListViewWithRefreshableValues( entityConfiguration );
						        if ( Boolean.TRUE.equals( entityConfiguration.hasAttribute( LIST_VIEW_EDITABLE_VALUES ) ) ) {
							        configureListViewWithEditableValues( entityConfiguration );
						        }
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
					        customizeViewActionsIfNecessary( entityConfiguration );

					        entityConfiguration
							        .getAssociations()
							        .forEach( association -> {
								        if ( association.hasView( EntityView.LIST_VIEW_NAME ) ) {
									        configureListViewWithRefreshableValues( entityConfiguration, association );
									        if ( Boolean.TRUE.equals( association.hasAttribute( LIST_VIEW_EDITABLE_VALUES ) ) ) {
										        configureListViewWithEditableValues( entityConfiguration, association );
									        }
								        }

								        if ( !association.hasView( EditableValueViewElementBuilderFactory.REQUIRED_VIEW )
										        && association.getTargetEntityConfiguration().hasEntityModel() ) {
									        associationEditableValuesView.accept( entityConfiguration, association );
								        }
								        customizeViewActionsIfNecessary( association );
							        } );
				        }
		        );
	}

	private void customizeViewActionsIfNecessary( EntityViewRegistry entityViewRegistry ) {
		handleViewActions( entityViewRegistry, EntityView.DETAIL_VIEW_NAME );
		handleViewActions( entityViewRegistry, EntityView.UPDATE_VIEW_NAME );
		handleListViewControls( entityViewRegistry, EntityView.LIST_VIEW_NAME );
	}

	private void handleViewActions( EntityViewRegistry entityViewRegistry, String viewName ) {
		if ( entityViewRegistry.hasView( viewName ) ) {
			EntityViewFactory viewFactory = entityViewRegistry.getViewFactory( viewName );
			if ( viewFactory instanceof DispatchingEntityViewFactory factory ) {
				EntityViewProcessorRegistry processorRegistry = factory.getProcessorRegistry();
				if ( !processorRegistry.contains( EditableValueViewActionsViewProcessor.class.getName() ) ) {
					processorRegistry.getProcessor( PropertyRenderingViewProcessor.class.getName(), PropertyRenderingViewProcessor.class )
					                 .flatMap( ViewFactoryUtils::resolveViewElementMode )
					                 .ifPresent(
							                 vem -> {
								                 if ( WebUtilityViewElementMode.EDITABLE_VALUE_VIEW().equals( vem ) ) {
									                 processorRegistry.addProcessor( new EditableValueViewActionsViewProcessor(), 1100 );
								                 }
							                 }
					                 );
				}

			}
		}
	}

	private void handleListViewControls( EntityViewRegistry entityViewRegistry, String viewName ) {
		if ( entityViewRegistry.hasView( viewName ) ) {
			EntityViewFactory viewFactory = entityViewRegistry.getViewFactory( viewName );
			if ( viewFactory instanceof DispatchingEntityViewFactory factory ) {
				EntityViewProcessorRegistry processorRegistry = factory.getProcessorRegistry();
				if ( !processorRegistry.contains( EditableValueListViewControlsProcessor.class.getName() ) ) {
					EditableValueListViewControlsProcessor processor = moduleInfo.getApplicationContext().getAutowireCapableBeanFactory()
					                                                             .createBean( EditableValueListViewControlsProcessor.class );
					processorRegistry.addProcessor( processor, 1100 );
				}
			}
		}
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void configureListViewWithEditableValues( MutableEntityConfiguration entityConfiguration, EntityAssociation association ) {
		new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
				.association(
						ass -> ass.name( association.getName() )
						          .listView(
								          view -> view.viewProcessor(
										          vp -> vp.withType( SortableTableRenderingViewProcessor.class )
										                  .configure( s -> s.setViewElementMode( ViewElementMode.FORM_READ
												                                                         .withChildMode(
														                                                         FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
														                                                         WebUtilityViewElementMode.EDITABLE_LIST_VALUE ) ) )
								          )
						          )
				)
				.apply( entityConfiguration );
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void configureListViewWithEditableValues( MutableEntityConfiguration entityConfiguration ) {
		new EntityConfigurationBuilder<>( moduleInfo.getApplicationContext().getAutowireCapableBeanFactory() )
				.listView(
						view -> view.viewProcessor(
								vp -> vp.withType( SortableTableRenderingViewProcessor.class )
								        .configure( s -> s.setViewElementMode( ViewElementMode.FORM_READ
										                                               .withChildMode(
												                                               FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
												                                               WebUtilityViewElementMode.EDITABLE_LIST_VALUE ) ) )
						)
				)
				.apply( entityConfiguration );
	}
}
