/*
 * Copyright 2014 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.processors.support.EmbeddedCollectionsBinder;
import com.foreach.across.modules.entity.views.processors.support.EmbeddedCollectionsBinderValidator;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Renders one or more registered properties from the {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry}
 * attached to the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}.  As these are entity properties,
 * the {@link com.foreach.across.modules.bootstrapui.elements.processor.ControlNamePrefixingPostProcessor} will be added when generating
 * the elements for these properties.
 * <p/>
 * This processor will first create a {@link java.util.Map} of {@link com.foreach.across.modules.web.ui.ViewElementBuilder}s for all properties
 * requested.  These will not be added to the general builderMap however, but will be available as a {@link ViewElementBuilderMap} under the attribute
 * {@link #ATTRIBUTE_PROPERTY_BUILDERS} in the {@link EntityView}.  Following processors can update the builder collection.
 * <p/>
 * During {@link #render(EntityViewRequest, EntityView, ContainerViewElementBuilderSupport, ViewElementBuilderMap, ViewElementBuilderContext)}, the
 * actual {@link ViewElement}s will get created and added to the container.  If the global builderMap contains a builder named
 * {@link #ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER} the elements will get added to that builder, else they will get added to the container passed as argument.
 * <p/>
 * This processor will prefix all properties having an {@link EntityAttributes#NATIVE_PROPERTY_DESCRIPTOR} attribute to have their control names prefixed.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class PropertyRenderingViewProcessor extends EntityViewProcessorAdapter
{
	public static final String ATTRIBUTE_PROPERTY_BUILDERS = "propertyBuildersMap";
	// todo: make configurable
	public static final String ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER = "entityForm-column-0";

	private EntityViewElementBuilderService viewElementBuilderService;
	private EmbeddedCollectionsBinderValidator collectionsBinderValidator;
	private ConversionService conversionService;

	private EntityPropertySelector selector = EntityPropertySelector.of( EntityPropertySelector.READABLE );

	/**
	 * Which type of view elements should be created for the properties?
	 */
	@Setter
	private ViewElementMode viewElementMode = ViewElementMode.FORM_READ;

	/**
	 * Define the properties that should be rendered.
	 * Defaults to all readable properties. A new selector will be combined with the default.  If the new selector
	 * defines the {@link EntityPropertySelector#CONFIGURED} property, the selector will be considered an extension
	 * of the previously registered selector.
	 */
	public void setSelector( EntityPropertySelector selector ) {
		this.selector = this.selector.combine( selector );
	}

	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		EntityPropertiesBinder propertiesBinder = new EntityPropertiesBinder( entityViewRequest.getEntityViewContext().getPropertyRegistry() );
		propertiesBinder.setConversionService( conversionService );
		propertiesBinder.setBinderPrefix( "properties" );
		propertiesBinder.setEntitySupplier( () -> Optional.ofNullable( command.getEntity() ).orElseGet( entityViewRequest.getEntityViewContext()::getEntity ) );

		command.setProperties( propertiesBinder );

		command.addExtensionWithValidator(
				EmbeddedCollectionsBinder.class.getSimpleName(),
				new EmbeddedCollectionsBinder( entityViewRequest.getEntityViewContext().getPropertyRegistry(),
				                               "extensions[" + EmbeddedCollectionsBinder.class.getSimpleName() + "]" ),
				collectionsBinderValidator
		);
	}

	@Override
	protected void preProcess( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command ) {
		EmbeddedCollectionsBinder collections = command.getExtension( EmbeddedCollectionsBinder.class.getSimpleName() );
		collections.apply( command.getEntity() );
	}

	@Override
	protected void prepareViewElementBuilderContext( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderContext builderContext ) {
		builderContext.setAttribute( EmbeddedCollectionsBinder.class,
		                             entityViewRequest.getCommand().getExtension( EmbeddedCollectionsBinder.class.getSimpleName() ) );
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		ViewElementBuilderMap propertyBuilders = retrievePropertiesBuilderMap( entityView );
		List<EntityPropertyDescriptor> properties = entityViewRequest.getEntityViewContext().getPropertyRegistry().select( selector );

		properties.forEach(
				descriptor -> propertyBuilders.put( descriptor.getName(), viewElementBuilderService.getElementBuilder( descriptor, viewElementMode ) )
		);
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		ViewElementBuilderMap propertyBuilders = entityView.removeAttribute( ATTRIBUTE_PROPERTY_BUILDERS, ViewElementBuilderMap.class );

		if ( propertyBuilders != null ) {
			ContainerViewElementBuilderSupport<?, ?> propertiesContainerBuilder
					= builderMap.containsKey( ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER )
					? builderMap.get( ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER, ContainerViewElementBuilderSupport.class )
					: containerBuilder;

			try {
				builderContext.setAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, true );
				propertyBuilders.forEach( ( propertyName, builder ) -> {
					if ( builder == null ) {
						throw new IllegalStateException( "No ViewElementBuilder was registered for property '" + propertyName + "'" );
					}
					propertiesContainerBuilder.add( builder.build( builderContext ) );
				} );
			}
			finally {
				builderContext.removeAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES );
			}
		}
	}

	private ViewElementBuilderMap retrievePropertiesBuilderMap( EntityView entityView ) {
		return (ViewElementBuilderMap) entityView
				.asMap()
				.computeIfAbsent( ATTRIBUTE_PROPERTY_BUILDERS, key -> new ViewElementBuilderMap() );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		PropertyRenderingViewProcessor that = (PropertyRenderingViewProcessor) o;
		return Objects.equals( selector, that.selector ) &&
				Objects.equals( viewElementMode, that.viewElementMode );
	}

	@Override
	public int hashCode() {
		return Objects.hash( selector, viewElementMode );
	}

	@Autowired
	void setViewElementBuilderService( EntityViewElementBuilderService viewElementBuilderService ) {
		this.viewElementBuilderService = viewElementBuilderService;
	}

	@Autowired
	void setCollectionsBinderValidator( EmbeddedCollectionsBinderValidator collectionsBinderValidator ) {
		this.collectionsBinderValidator = collectionsBinderValidator;
	}

	@Autowired
	void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}
}
