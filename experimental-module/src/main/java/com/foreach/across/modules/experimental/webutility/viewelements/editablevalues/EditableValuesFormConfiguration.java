package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.core.annotations.Module;
import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupDescriptionTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupHelpTextPostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.FormGroupTooltipTextPostProcessor;
import com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.ScopedAttributesViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.BiConsumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
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
			if ( viewFactory instanceof DispatchingEntityViewFactory ) {
				EntityViewProcessorRegistry processorRegistry = ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry();
				if ( !processorRegistry.contains( EditableValueViewActionsViewProcessor.class.getName() ) ) {
					processorRegistry.getProcessor( PropertyRenderingViewProcessor.class.getName(), PropertyRenderingViewProcessor.class )
					                 .flatMap( this::resolveViewElementMode )
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
			if ( viewFactory instanceof DispatchingEntityViewFactory ) {
				EntityViewProcessorRegistry processorRegistry = ( (DispatchingEntityViewFactory) viewFactory ).getProcessorRegistry();
				if ( !processorRegistry.contains( EditableValueListViewControlsProcessor.class.getName() ) ) {
					processorRegistry.addProcessor( new EditableValueListViewControlsProcessor(), 1100 );
				}
			}
		}
	}

	/**
	 * Attempts to resolve the {@link ViewElementMode} registered on a {@link PropertyRenderingViewProcessor}.
	 */
	private Optional<ViewElementMode> resolveViewElementMode( PropertyRenderingViewProcessor viewProcessor ) {
		ViewElementMode value = null;
		try {
			Field selector = PropertyRenderingViewProcessor.class.getDeclaredField( "viewElementMode" );
			ReflectionUtils.makeAccessible( selector );
			value = (ViewElementMode) selector.get( viewProcessor );
		}
		catch ( NoSuchFieldException | IllegalAccessException e ) {
		}
		return Optional.ofNullable( value );
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
								                      .properties( props -> configurePropertyLabels(
										                      association.getTargetEntityConfiguration().getPropertyRegistry(), props ) )
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
						            .properties( props -> configurePropertyLabels( entityConfiguration.getPropertyRegistry(), props ) )
				)
				.apply( entityConfiguration );
	}

	private void configurePropertyLabels( EntityPropertyRegistry propertyRegistry, EntityPropertyRegistryBuilder props ) {
		ViewElementPostProcessor<FormGroupElement> formGroupElementViewElementPostProcessor =
				( builderContext, element ) -> element.getLabel().set( css.screenReaderOnly );

		ViewElementPostProcessor<AbstractNodeViewElement> controlNamePostProcessor = ( builderContext, element ) -> {
			if ( FormInputElement.class.isAssignableFrom( element.getClass() ) ) {
				try (ScopedAttributesViewElementBuilderContext ignore = builderContext
						.withAttributeOverride( EntityPropertyControlName.class, EntityPropertyControlName.root( "entity" ) )
						.withAttributeOverride( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, false )) {
					EntityPropertyDescriptor descriptor = EntityViewElementUtils.currentPropertyDescriptor( builderContext );
					String controlName = EntityViewElementUtils.controlName( descriptor, builderContext )
					                                           .asProperty()
					                                           .forHandlingType( EntityPropertyHandlingType.forProperty( descriptor ) )
					                                           .toString();
					( (FormInputElement) element ).setControlName( controlName );
				}
			}
		};

		// todo move this into a viewprocessor which selects the properties that are being rendered by the SortableTableRenderingViewProcessor
		// so that nested (/associated) properties are also configured correctly?
		propertyRegistry.select( EntityPropertySelector.all() )
		                .forEach( p -> props.property( p.getName() )
		                                    .viewElementPostProcessor( ViewElementMode.FORM_READ
				                                                               .withChildMode(
						                                                               FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
						                                                               WebUtilityViewElementMode.EDITABLE_LIST_VALUE ),
		                                                               formGroupElementViewElementPostProcessor )
		                                    .viewElementPostProcessor( ViewElementMode.CONTROL, controlNamePostProcessor )

		                );
	}
}
