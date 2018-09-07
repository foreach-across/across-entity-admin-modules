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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.core.support.ReadableAttributes;
import com.foreach.across.modules.entity.registry.ConfigurableEntityViewRegistry;
import com.foreach.across.modules.entity.registry.EntityViewRegistry;
import com.foreach.across.modules.entity.views.DefaultEntityViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewFactoryAttributes;
import lombok.NonNull;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Support class for builders building types that implement {@link ConfigurableEntityViewRegistry}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public abstract class AbstractWritableAttributesAndViewsBuilder<T extends ReadableAttributes> extends AbstractWritableAttributesBuilder<T>
{
	private final Map<String, Collection<Consumer<EntityListViewFactoryBuilder>>> listViewConsumers
			= new LinkedHashMap<>();
	private final Map<String, Collection<Consumer<EntityViewFactoryBuilder>>> deleteViewConsumers
			= new LinkedHashMap<>();
	private final Map<String, Collection<Consumer<EntityViewFactoryBuilder>>> formViewConsumers
			= new LinkedHashMap<>();
	private final Map<String, Collection<Consumer<EntityViewFactoryBuilder>>> customViewConsumers
			= new LinkedHashMap<>();

	/**
	 * Configure a default list view builder for the entity being configured.
	 * Does not customize the builder but ensures the view gets created using the default builder.
	 *
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder listView() {
		return listView( triggerBuild() );
	}

	/**
	 * Configure the default list view builder for the entity being configured.
	 * A default list view is usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder listView( Consumer<EntityListViewFactoryBuilder> consumer ) {
		return listView( EntityView.LIST_VIEW_NAME, consumer );
	}

	/**
	 * Returns the named list view builder for the entity being configured.
	 * If the view is not available, it will be created.
	 *
	 * @param viewName name of the view
	 * @param consumer for configuring the view builder
	 * @return builder instance
	 */
	public AbstractWritableAttributesAndViewsBuilder listView( @NonNull String viewName,
	                                                           @NonNull Consumer<EntityListViewFactoryBuilder> consumer ) {
		listViewConsumers.computeIfAbsent( viewName, k -> new ArrayDeque<>() ).add( consumer );
		return this;
	}

	/**
	 * Configure the form view builder for the default create and update forms of the entity being configured.
	 * Create and update forms are usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder createOrUpdateFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		createFormView( consumer );
		return updateFormView( consumer );
	}

	/**
	 * Configure a default create form view builder for the entity being configured.
	 * Does not customize the builder but ensures the view gets created using the default builder.
	 *
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder createFormView() {
		return createFormView( triggerBuild() );
	}

	/**
	 * Configure the create form view builder for the entity being configured.
	 * A default create form view is usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder createFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return formView( EntityView.CREATE_VIEW_NAME, consumer );
	}

	/**
	 * Configure a default update form view builder for the entity being configured.
	 * Does not customize the builder but ensures the view gets created using the default builder.
	 *
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder updateFormView() {
		return updateFormView( triggerBuild() );
	}

	/**
	 * Configure the default update form view builder for the entity being configured.
	 * A default update form view is usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder updateFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return formView( EntityView.UPDATE_VIEW_NAME, consumer );
	}

	/**
	 * Configure a default delete view builder for the entity being configured.
	 * Does not customize the builder but ensures the view gets created using the default builder.
	 *
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder deleteFormView() {
		return deleteFormView( triggerBuild() );
	}

	/**
	 * Configure the default delete form view builder for the entity being configured.
	 * A default delete form view is usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder deleteFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		deleteViewConsumers.computeIfAbsent( EntityView.DELETE_VIEW_NAME, k -> new ArrayDeque<>() ).add( consumer );
		return this;
	}

	/**
	 * Configure a default delete view builder for the entity being configured.
	 * Does not customize the builder but ensures the view gets created using the default builder.
	 *
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder readonlyFormView() {
		return readonlyFormView( triggerBuild() );
	}

	/**
	 * Configure the default delete form view builder for the entity being configured.
	 * A default delete form view is usually available.
	 *
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder readonlyFormView( Consumer<EntityViewFactoryBuilder> consumer ) {
		return formView( EntityView.READONLY_UPDATE_VIEW_NAME, consumer );
	}

	/**
	 * Configure the named form view builder for the entity being configured.
	 * If the view is not available, it will be created.
	 *
	 * @param viewName name of the view
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder formView( String viewName,
	                                                           Consumer<EntityViewFactoryBuilder> consumer ) {
		formViewConsumers.computeIfAbsent( viewName, k -> new ArrayDeque<>() ).add( consumer );
		return this;
	}

	/**
	 * Configure the named simple view builder for the entity being configured.
	 * If the view is not available, it will be created.
	 *
	 * @param viewName name of the view
	 * @param consumer for configuring the view builder
	 * @return current builder
	 */
	public AbstractWritableAttributesAndViewsBuilder view( String viewName,
	                                                       Consumer<EntityViewFactoryBuilder> consumer ) {
		customViewConsumers.computeIfAbsent( viewName, k -> new ArrayDeque<>() ).add( consumer );
		return this;
	}

	protected void applyViews( ConfigurableEntityViewRegistry viewRegistry ) {
		registerViews( EntityListViewFactoryBuilder.class, EntityView.LIST_VIEW_NAME, listViewConsumers, viewRegistry );
		registerViews( EntityViewFactoryBuilder.class, EntityView.UPDATE_VIEW_NAME, formViewConsumers, viewRegistry );
		registerViews( EntityViewFactoryBuilder.class, EntityView.DELETE_VIEW_NAME, deleteViewConsumers, viewRegistry );
		registerViews( EntityViewFactoryBuilder.class, EntityView.GENERIC_VIEW_NAME, customViewConsumers, viewRegistry );
	}

	@SuppressWarnings("unchecked")
	private <U extends EntityViewFactoryBuilder, V extends EntityViewFactory> void registerViews(
			Class<U> builderType,
			String builderTemplateName,
			Map<String, Collection<Consumer<U>>> consumers,
			ConfigurableEntityViewRegistry viewRegistry
	) {
		consumers.forEach( ( viewName, list ) -> {
			U builder = createViewFactoryBuilder( builderType );

			V viewFactory;

			if ( viewRegistry.hasView( viewName ) ) {
				viewFactory = viewRegistry.getViewFactory( viewName );
				list.forEach( c -> c.accept( builder ) );
				builder.apply( viewFactory );
			}
			else {
				builder.attribute( EntityViewFactoryAttributes.VIEW_NAME, viewName );
				builder.attribute( EntityViewRegistry.class, viewRegistry );
				builder.factoryType( DefaultEntityViewFactory.class );
				initializeViewFactoryBuilder( viewName, builderTemplateName, builder );
				builder.messagePrefix( "views[" + viewName + "]" );

				list.forEach( c -> c.accept( builder ) );
				viewRegistry.registerView( viewName, builder.build() );
			}
		} );
	}

	protected abstract <U extends EntityViewFactoryBuilder> U createViewFactoryBuilder( Class<U> builderType );

	protected abstract <U extends EntityViewFactoryBuilder> void initializeViewFactoryBuilder( String viewName, String templateName, U builder );

	private static <U> Consumer<U> triggerBuild() {
		return ( x ) -> {
		};
	}
}
