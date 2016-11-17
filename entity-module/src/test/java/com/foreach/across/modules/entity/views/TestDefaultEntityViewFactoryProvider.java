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

package com.foreach.across.modules.entity.views;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistryProvider;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Collections;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestDefaultEntityViewFactoryProvider
{
	@Mock
	private AutowireCapableBeanFactory beanFactory;

	@Mock
	private EntityPropertyRegistryProvider propertyRegistryProvider;

	@Mock
	private EntityConfiguration config;

	@Mock
	private EntityPropertyRegistry configPropertyRegistry;

	@Mock
	private MutableEntityPropertyRegistry subPropertyRegistry;

	private DefaultEntityViewFactoryProvider provider;

	@Before
	public void reset() {
		when( beanFactory.getBean( EntityPropertyRegistryProvider.class ) ).thenReturn( propertyRegistryProvider );
		provider = new DefaultEntityViewFactoryProvider( beanFactory );

		when( config.getEntityType() ).thenReturn( String.class );
		when( config.getPropertyRegistry() ).thenReturn( configPropertyRegistry );
		when( propertyRegistryProvider.createForParentRegistry( configPropertyRegistry ) )
				.thenReturn( subPropertyRegistry );
	}

	@Test
	public void formView() {
		BiConsumer consumer = mock( BiConsumer.class );
		provider.setConfigurationViewFactoryPostProcessors( Collections.singleton( consumer ) );

		EntityFormViewFactory expected = mock( EntityFormViewFactory.class );
		when( beanFactory.createBean( EntityFormViewFactory.class ) ).thenReturn( expected );

		EntityFormViewFactory factory = provider.create( config, EntityFormViewFactory.class );
		assertSame( expected, factory );

		verify( factory ).setTemplate( EntityFormView.VIEW_TEMPLATE );
		verify( factory ).setMessagePrefixes( "entityViews" );
		verify( factory ).setPropertyRegistry( subPropertyRegistry );
		verify( consumer ).accept( config, factory );
		verifyNoMoreInteractions( factory );
	}

	@Test
	public void listView() {
		BiConsumer consumer = mock( BiConsumer.class );
		provider.setConfigurationViewFactoryPostProcessors( Collections.singleton( consumer ) );

		EntityListViewFactory expected = mock( EntityListViewFactory.class );
		when( beanFactory.createBean( EntityListViewFactory.class ) ).thenReturn( expected );

		EntityListViewFactory factory = provider.create( config, EntityListViewFactory.class );
		assertSame( expected, factory );

		verify( factory ).setTemplate( EntityListView.VIEW_TEMPLATE );
		verify( factory ).setMessagePrefixes( "entityViews" );
		verify( factory ).setPropertyRegistry( subPropertyRegistry );
		verify( factory ).setPropertyFilter( any() );
		verify( factory ).setPropertyComparator( any() );
		verify( factory ).setDefaultSort( any() );
	}
}
