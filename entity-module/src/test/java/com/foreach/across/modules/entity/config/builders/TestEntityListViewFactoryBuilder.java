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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyComparators;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.EntityViewViewFactory;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;

import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestEntityListViewFactoryBuilder
{
	@Mock
	private AutowireCapableBeanFactory beanFactory;

	private EntityListViewFactoryBuilder builder;

	@Before
	public void before() {
		builder = new EntityListViewFactoryBuilder( beanFactory );
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildRequiresAFactoryToBeSet() {
		builder.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void factoryMustBeListViewFactory() {
		builder.factory( mock( EntityViewViewFactory.class ) ).build();
	}

	@Test
	public void applyListViewFactory() {
		EntityListViewFactory factory = mock( EntityListViewFactory.class );
		MutableEntityPropertyRegistry propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		when( factory.getPropertyRegistry() ).thenReturn( propertyRegistry );

		EntityViewProcessor one = mock( EntityViewProcessor.class );
		EntityViewProcessor two = mock( EntityViewProcessor.class );

		builder.template( "template" )
		       .showProperties( "one", "two" )
		       .properties( props -> props.property( "name" ).displayName( "test" ) )
		       .viewProcessor( one )
		       .viewProcessor( two )
		       .defaultSort( "name" )
		       .pageFetcher( mock( EntityListViewPageFetcher.class ) )
		       .showResultNumber( false )
		       .pageSize( 25 )
		       .sortableOn( "two" )
		       .apply( factory );

		verify( factory ).setTemplate( "template" );
		verify( factory ).setProcessors( new LinkedHashSet<>( Arrays.asList( one, two ) ) );
		verify( factory ).setPropertyComparator( EntityPropertyComparators.ordered( "one", "two" ) );
		verify( propertyRegistry ).register( any() );
		verify( factory ).setDefaultSort( new Sort( ASC, "name" ) );
		verify( factory ).setPageSize( 25 );
		verify( factory ).setPageFetcher( any() );
		verify( factory ).setShowResultNumber( false );
		verify( factory ).setSortableProperties( Arrays.asList( "two" ) );
	}

	@Test
	public void customFilter() {
		EntityQueryFilterProcessor filter = mock( EntityQueryFilterProcessor.class );
		EntityListViewFactory factory = mock( EntityListViewFactory.class );

		builder.filter( filter ).apply( factory );

		verify( factory ).setProcessors( Collections.singleton( filter ) );
		verify( factory ).setPageFetcher( filter );
	}

	@Test
	public void entityQueryFilterEnabled() {
		EntityQueryFilterProcessor filter = mock( EntityQueryFilterProcessor.class );
		when( beanFactory.getBean( EntityQueryFilterProcessor.class ) ).thenReturn( filter );

		EntityListViewFactory factory = mock( EntityListViewFactory.class );

		builder.entityQueryFilter( true ).apply( factory );

		verify( factory ).setProcessors( Collections.singleton( filter ) );
		verify( factory ).setPageFetcher( filter );
	}

	@Test
	public void entityQueryFilterDisabledAgain() {
		EntityQueryFilterProcessor filter = mock( EntityQueryFilterProcessor.class );
		when( beanFactory.getBean( EntityQueryFilterProcessor.class ) ).thenReturn( filter );

		EntityListViewFactory factory = mock( EntityListViewFactory.class );

		builder.entityQueryFilter( true )
		       .entityQueryFilter( false )
		       .apply( factory );

		verify( factory, never() ).setProcessors( any() );
		verify( factory, never() ).setPageFetcher( any() );
	}

	@Test
	public void customPageFetcherIsKept() {
		EntityQueryFilterProcessor filter = mock( EntityQueryFilterProcessor.class );
		when( beanFactory.getBean( EntityQueryFilterProcessor.class ) ).thenReturn( filter );

		EntityListViewFactory factory = mock( EntityListViewFactory.class );
		EntityListViewPageFetcher pageFetcher = mock( EntityListViewPageFetcher.class );
		EntityViewProcessor one = mock( EntityViewProcessor.class );

		builder.entityQueryFilter( true )
		       .viewProcessor( one )
		       .pageFetcher( pageFetcher )
		       .entityQueryFilter( false )
		       .apply( factory );

		verify( factory ).setProcessors( Collections.singleton( one ) );
		verify( factory ).setPageFetcher( pageFetcher );
	}
}
