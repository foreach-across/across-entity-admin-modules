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
import com.foreach.across.modules.entity.views.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Arrays;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class TestEntityViewFactoryBuilder
{
	@Mock
	private AutowireCapableBeanFactory beanFactory;

	private EntityViewFactoryBuilder builder;

	@Before
	public void before() {
		builder = new EntityViewFactoryBuilder( beanFactory );
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildRequiresAFactoryToBeSet() {
		builder.build();
	}

	@Test
	public void defaultCreatesSingleEntityViewFactory() {
		builder.factoryType( EntityViewFactory.class );

		EntityViewFactory factory = mock( EntityViewFactory.class );
		when( beanFactory.createBean( EntityViewFactory.class ) ).thenReturn( factory );

		assertSame( builder.build(), factory );
	}

	@Test
	public void specificTypeCreation() {
		EntityViewViewFactory f = mock( EntityViewViewFactory.class );
		when( beanFactory.createBean( EntityViewViewFactory.class ) ).thenReturn( f );

		builder.factoryType( EntityViewViewFactory.class )
		       .template( "templateName" );

		EntityViewFactory built = builder.build();

		assertSame( built, f );
		verify( f ).setTemplate( "templateName" );
	}

	@Test
	public void factoryInstanceTakePrecedence() {
		EntityViewFactory expected = mock( EntityViewViewFactory.class );

		builder.factoryType( EntityViewViewFactory.class )
		       .factory( expected );

		EntityViewFactory built = builder.build();
		assertSame( expected, built );
		verify( beanFactory, never() ).createBean( any() );
	}

	@Test
	public void applySimpleEntityViewFactory() {
		SimpleEntityViewFactorySupport factory = mock( SimpleEntityViewFactorySupport.class );

		EntityViewProcessor one = mock( EntityViewProcessor.class );
		EntityViewProcessor two = mock( EntityViewProcessor.class );

		builder.template( "template" )
		       .showProperties( "one", "two" )
		       .properties( props -> props.property( "name" ).displayName( "test" ) )
		       .viewProcessor( one )
		       .viewProcessor( two )
		       .apply( factory );

		verify( factory ).setTemplate( "template" );
		verify( factory ).setProcessors( Arrays.asList( one, two ) );
		verifyNoMoreInteractions( factory );
	}

	@Test
	public void applyConfigurablePropertiesViewFactory() {
		ConfigurablePropertiesEntityViewFactorySupport factory = mock(
				ConfigurablePropertiesEntityViewFactorySupport.class );
		MutableEntityPropertyRegistry propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		when( factory.getPropertyRegistry() ).thenReturn( propertyRegistry );

		EntityViewProcessor one = mock( EntityViewProcessor.class );
		EntityViewProcessor two = mock( EntityViewProcessor.class );

		builder.template( "template" )
		       .showProperties( "one", "two" )
		       .properties( props -> props.property( "name" ).displayName( "test" ) )
		       .viewProcessor( one )
		       .viewProcessor( two )
		       .apply( factory );

		verify( factory ).setTemplate( "template" );
		verify( factory ).setProcessors( Arrays.asList( one, two ) );
		verify( factory ).setPropertyComparator( EntityPropertyComparators.ordered( "one", "two" ) );
		verify( propertyRegistry ).register( any() );
	}
}
