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

import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * Extends the default {@link EntityViewFactoryBuilder} with properties for a list view of entities
 * instead of a single entity view.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class EntityListViewFactoryBuilder extends EntityViewFactoryBuilder
{
	private Boolean showResultNumber;
	private Integer pageSize;
	private Sort defaultSort;
	private Collection<String> sortableProperties;
	private EntityListViewPageFetcher pageFetcher;

	@Autowired
	public EntityListViewFactoryBuilder( AutowireCapableBeanFactory beanFactory ) {
		super( beanFactory );
	}

	@Override
	public EntityListViewFactoryBuilder factoryType( Class<? extends EntityViewFactory> factoryType ) {
		return (EntityListViewFactoryBuilder) super.factoryType( factoryType );
	}

	@Override
	public EntityListViewFactoryBuilder template( String template ) {
		return (EntityListViewFactoryBuilder) super.template( template );
	}

	@Override
	public EntityListViewFactoryBuilder properties( Consumer<EntityPropertyRegistryBuilder> registryConsumer ) {
		return (EntityListViewFactoryBuilder) super.properties( registryConsumer );
	}

	@Override
	public EntityListViewFactoryBuilder showProperties( String... propertyNames ) {
		return (EntityListViewFactoryBuilder) super.showProperties( propertyNames );
	}

	@Override
	public EntityListViewFactoryBuilder viewProcessor( EntityViewProcessor processor ) {
		return (EntityListViewFactoryBuilder) super.viewProcessor( processor );
	}

	/**
	 * Configure the page fetcher on the view.
	 *
	 * @param pageFetcher instance - may not be null
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder pageFetcher( EntityListViewPageFetcher pageFetcher ) {
		Assert.notNull( pageFetcher );
		this.pageFetcher = pageFetcher;
		return this;
	}

	/**
	 * @param pageSize number of results per page.
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder pageSize( int pageSize ) {
		this.pageSize = pageSize;
		return this;
	}

	/**
	 * @param propertyNames of properties that can be sorted on
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder sortableOn( String... propertyNames ) {
		this.sortableProperties = Arrays.asList( propertyNames );
		return this;
	}

	/**
	 * Set the default sort to ascending order of the property specified.
	 *
	 * @param property name
	 * @return current builder
	 * @see #defaultSort(Sort)
	 */
	public EntityListViewFactoryBuilder defaultSort( String property ) {
		return defaultSort( new Sort( Sort.Direction.ASC, property ) );
	}

	/**
	 * @param sort default sort instance that should be applied when fetching
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder defaultSort( Sort sort ) {
		this.defaultSort = sort;
		return this;
	}

	/**
	 * @param showResultNumber true if result numbers should be shown in the list
	 * @return current builder
	 */
	public EntityListViewFactoryBuilder showResultNumber( boolean showResultNumber ) {
		this.showResultNumber = showResultNumber;
		return this;
	}

	@Override
	void apply( EntityViewFactory rawViewFactory ) {

		super.apply( rawViewFactory );

		if ( rawViewFactory instanceof EntityListViewFactory ) {
			EntityListViewFactory viewFactory = (EntityListViewFactory) rawViewFactory;
			if ( pageFetcher != null ) {
				viewFactory.setPageFetcher( pageFetcher );
			}
			if ( pageSize != null ) {
				viewFactory.setPageSize( pageSize );
			}
			if ( sortableProperties != null ) {
				viewFactory.setSortableProperties( sortableProperties );
			}
			if ( defaultSort != null ) {
				viewFactory.setDefaultSort( defaultSort );
			}
			if ( showResultNumber != null ) {
				viewFactory.setShowResultNumber( showResultNumber );
			}
		}
		else {
			throw new IllegalArgumentException( "Registered view factory was not of type EntityListViewFactory" );
		}
	}
}
