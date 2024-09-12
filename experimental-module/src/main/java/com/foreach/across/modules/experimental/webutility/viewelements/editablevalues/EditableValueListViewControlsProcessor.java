package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.FormInputElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.autosuggest.AutoSuggestFormElement;
import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.DispatchingEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FormGroupElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.viewelements.WebUtilityViewElementMode;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ScopedAttributesViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.WebDataBinder;

import java.util.concurrent.atomic.AtomicInteger;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;

@RequiredArgsConstructor
public class EditableValueListViewControlsProcessor extends EntityViewProcessorAdapter
{
	private final EntityPropertyRegistryProvider propertyRegistryProvider;
	private final EntityPropertyDescriptorFactory propertyDescriptorFactory;

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		EntityViewFactory viewFactory = entityViewRequest.getViewFactory();
		if ( viewFactory instanceof DispatchingEntityViewFactory ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			ViewFactoryUtils.getViewProcessorFromView( viewFactory, SortableTableRenderingViewProcessor.class )
			                .flatMap( ViewFactoryUtils::resolvePropertySelector )
			                .ifPresent( propertySelection -> {
				                MergingEntityPropertyRegistry propertyRegistry = new MergingEntityPropertyRegistry(
						                entityViewContext.getPropertyRegistry(),
						                propertyRegistryProvider, propertyDescriptorFactory );
				                if ( entityViewContext instanceof ConfigurableEntityViewContext context ) {
					                context.setPropertyRegistry( propertyRegistry );
					                propertyRegistry.select( propertySelection )
					                                .stream()
					                                .map( MutableEntityPropertyDescriptor.class::cast )
					                                .forEach( this::configureProperty );
				                }
			                } );
		}
	}

	private void configureProperty( MutableEntityPropertyDescriptor propertyDescriptor ) {
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

		new EntityPropertyDescriptorBuilder( propertyDescriptor.getName() )
				.viewElementPostProcessor( ViewElementMode.FORM_READ
						                           .withChildMode( FormGroupElementBuilderFactory.CONTROL_CHILD_MODE,
						                                           WebUtilityViewElementMode.EDITABLE_LIST_VALUE ),
				                           formGroupElementViewElementPostProcessor )
				.viewElementPostProcessor( ViewElementMode.CONTROL, controlNamePostProcessor )
				.apply( propertyDescriptor );
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		SortableTableBuilder tableBuilder = builderMap.get( SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class );
		if ( tableBuilder != null ) {
			AtomicInteger idPrefix = new AtomicInteger( 1 );
			tableBuilder.valueRowProcessor( configureHtmlIdPrefixes( idPrefix ) );
			tableBuilder.valueRowProcessor( configureDatepickerPopUp() );
			tableBuilder.valueRowProcessor( configureAutoSuggestPopUp() );
		}
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add( WebResource.javascript( "@static:experimental/web/datepicker-positioning.js" ) )
				               .withKey( "ax-exp:datepicker-positioning" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				WebResourceRule.add( WebResource.javascript( "@static:experimental/web/autosuggest-positioning.js" ) )
				               .withKey( "ax-exp:autosuggest-positioning" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
	}

	private ViewElementPostProcessor<TableViewElement.Row> configureHtmlIdPrefixes( AtomicInteger idPrefix ) {
		return ( builderContext, element ) -> ContainerViewElementUtils.findAll( element, FormInputElement.class )
		                                                               .forEach( fie -> {
			                                                               String currentId = fie.getHtmlId();
			                                                               fie.setHtmlId(
					                                                               idPrefix.getAndIncrement() + "-" + currentId );
		                                                               } );
	}

	private ViewElementPostProcessor<TableViewElement.Row> configureDatepickerPopUp() {
		return ( builderContext, element ) -> {
			ContainerViewElementUtils.findAll( element, DateTimeFormElement.class )
			                         .forEach( dp -> {
				                         dp.getConfiguration()
				                           .put( "widgetParent", ".pcs-body-section" );
			                         } );
		};
	}

	private ViewElementPostProcessor<TableViewElement.Row> configureAutoSuggestPopUp() {
		return ( builderContext, element ) -> {
			ContainerViewElementUtils.findAll( element, AutoSuggestFormElement.class )
			                         .forEach( dp -> {
				                         dp.getConfiguration()
				                           .put( "menu", ".pcs-body-section" );
			                         } );
		};
	}
}
