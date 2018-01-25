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

package com.foreach.across.modules.entity.registry.processors;

import com.foreach.across.modules.entity.registry.DefaultEntityModel;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TestEnumEntityModelProcessor
{
	private EnumEntityModelProcessor processor;

	@Mock
	private MutableEntityConfiguration configuration;

	@Mock
	private EntityMessageCodeResolver codeResolver;

	private MutableEntityPropertyRegistry propertyRegistry;

	@Before
	public void setUp() throws Exception {
		processor = new EnumEntityModelProcessor( new DefaultConversionService() );
		propertyRegistry = new DefaultEntityPropertyRegistry();

		SimpleEntityPropertyDescriptor labelProperty = new SimpleEntityPropertyDescriptor( EntityPropertyRegistry.LABEL );
		propertyRegistry.register( labelProperty );

		when( configuration.getEntityType() ).thenReturn( Country.class );
		when( configuration.getEntityMessageCodeResolver() ).thenReturn( codeResolver );
		when( configuration.getPropertyRegistry() ).thenReturn( propertyRegistry );
	}

	@Test
	public void codeResolverIsUpdated() {
		processor.accept( configuration );

		verify( codeResolver ).setPrefixes( "enums.Country" );
		verify( codeResolver ).setFallbackCollections( "enums" );
	}

	@Test
	public void existingEntityModelIsNotModified() {
		when( configuration.hasEntityModel() ).thenReturn( true );
		processor.accept( configuration );
		verify( configuration, never() ).setEntityModel( any() );
	}

	@Test
	public void defaultEntityModelIsCreatedIfEnum() {
		AtomicReference<DefaultEntityModel> modelRef = new AtomicReference<>();

		doAnswer( invocationOnMock -> {
			DefaultEntityModel model = invocationOnMock.getArgument( 0 );
			modelRef.set( model );
			return null;
		} )
				.when( configuration ).setEntityModel( any() );

		processor.accept( configuration );

		EntityModel model = modelRef.get();
		assertNotNull( model );

		assertEquals( Country.class, model.getJavaType() );
		assertEquals( String.class, model.getIdType() );
		assertEquals( "BELGIUM", model.getId( Country.BELGIUM ) );
		assertEquals( Country.FRANCE, model.findOne( "FRANCE" ) );
		assertFalse( model.isNew( Country.FRANCE ) );

		when( codeResolver.getMessageWithFallback( "BELGIUM", "Belgium" ) ).thenReturn( "tada" );
		assertEquals( "tada", model.getLabel( Country.BELGIUM ) );
	}

	@Test
	public void entityModelNotCreatedIfNoEnum() {
		when( configuration.getEntityType() ).thenReturn( String.class );
		processor.accept( configuration );
		verify( configuration ).getEntityType();
		verifyNoMoreInteractions( configuration );
	}

	enum Country
	{
		BELGIUM,
		FRANCE
	}
}
