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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

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

	/*
	@Test
	public void defaultCreatesSingleEntityViewFactory() {
		builder.factoryType( EntityListViewFactory.class );

		EntityViewFactory factory = mock( EntityViewFactory.class );
		when( beanFactory.createBean( EntityViewFactory.class ) ).thenReturn( factory );

		assertSame( builder.build(), factory );
	}

	@Test
	public void specificTypeCreation() {
		EntityViewViewFactory f = mock( EntityViewViewFactory.class );
		when( beanFactory.createBean( EntityViewViewFactory.class ) ).thenReturn( f );

		builder.factoryType( EntityListViewFactory.class )
		       .template( "templateName" );

		EntityViewFactory built = builder.build();

		assertSame( built, f );
		verify( f ).setTemplate( "templateName" );
	}

*/

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
		verify( factory ).setProcessors( Arrays.asList( one, two ) );
		verify( factory ).setPropertyComparator( EntityPropertyComparators.ordered( "one", "two" ) );
		verify( propertyRegistry ).register( any() );
		verify( factory ).setDefaultSort( new Sort( ASC, "name" ) );
		verify( factory ).setPageSize( 25 );
		verify( factory ).setPageFetcher( any() );
		verify( factory ).setShowResultNumber( false );
		verify( factory ).setSortableProperties( Arrays.asList( "two" ) );
	}
}
