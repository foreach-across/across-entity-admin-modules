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
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.helpers.PropertyViewElementBuilderWrapper;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.TypeDescriptor;

import java.util.Collections;
import java.util.Set;

import static com.foreach.across.modules.entity.views.ViewElementMode.CONTROL;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestEntityViewElementBuilderService
{
	@Mock
	private EntityRegistry entityRegistry;

	@Mock
	private ViewElementLookupRegistry propertyLookupRegistry;

	@Mock
	private ViewElementLookupRegistry entityLookupRegistry;

	@Mock
	private ViewElementTypeLookupStrategy lookupStrategy;

	@Mock
	private EntityViewElementBuilderFactory builderFactory;

	@Mock
	private ViewElementBuilder actualBuilder;

	@Mock
	private EntityConfiguration<String> entityConfiguration;

	@InjectMocks
	private EntityViewElementBuilderServiceImpl builderService;

	private EntityPropertyDescriptor propertyDescriptor;

	@BeforeEach
	public void setup() {
		builderService.setElementTypeLookupStrategies( Collections.singleton( lookupStrategy ) );
		builderService.setBuilderFactories( Collections.singleton( builderFactory ) );

		when( builderFactory.supports( anyString() ) ).thenReturn( true );

		when( entityRegistry.getEntityConfiguration( String.class ) ).thenReturn( entityConfiguration );
		when( entityConfiguration.getAttribute( ViewElementLookupRegistry.class ) ).thenReturn( entityLookupRegistry );

		propertyDescriptor = EntityPropertyDescriptor.builder( "property" )
		                                             .propertyType( TypeDescriptor.collection( Set.class, TypeDescriptor.valueOf( String.class ) ) )
		                                             .attribute( ViewElementLookupRegistry.class, propertyLookupRegistry )
		                                             .build();
	}

	@Test
	public void exceptionIsThrownIfNoValidBuilderForElementType() {
		when( lookupStrategy.findElementType( propertyDescriptor, CONTROL ) ).thenReturn( "resolvedType" );
		when( builderFactory.supports( "resolvedType" ) ).thenReturn( false );

		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> builderService.createElementBuilder( propertyDescriptor, CONTROL ) )
				.withMessage( "Unknown ViewElement type 'resolvedType' for property 'property', CONTROL mode" );
	}

	@Test
	public void elementTypeIsResolvedIfNeitherPropertyNorEntityLookupRegistryContainsTypeOrBuilder() {
		when( lookupStrategy.findElementType( propertyDescriptor, CONTROL ) ).thenReturn( "resolvedType" );
		when( builderFactory.createBuilder( propertyDescriptor, CONTROL, "resolvedType" ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );
	}

	@Test
	public void propertyElementTypeIsUsedIfPresent() {
		when( propertyLookupRegistry.getViewElementType( CONTROL ) ).thenReturn( "configuredPropertyType" );
		when( builderFactory.createBuilder( propertyDescriptor, CONTROL, "configuredPropertyType" ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );

		verify( entityLookupRegistry ).getViewElementPostProcessors( CONTROL );
		verifyNoMoreInteractions( entityLookupRegistry, lookupStrategy );
	}

	@Test
	public void entityElementTypeIsUsedIfNoPropertyElementType() {
		when( entityLookupRegistry.getViewElementType( CONTROL.forMultiple() ) ).thenReturn( "configuredEntityType" );
		when( builderFactory.createBuilder( propertyDescriptor, CONTROL, "configuredEntityType" ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );

		verifyNoMoreInteractions( lookupStrategy );
	}

	@Test
	public void propertyBuilderIsUsedIfPresent() {
		when( propertyLookupRegistry.getViewElementBuilder( CONTROL ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );

		verify( entityLookupRegistry ).getViewElementPostProcessors( CONTROL );
		verifyNoMoreInteractions( builderFactory, entityLookupRegistry, lookupStrategy );
	}

	@Test
	public void entityBuilderIsUsedIfNoPropertyBuilderAndNoType() {
		when( entityLookupRegistry.getViewElementBuilder( CONTROL.forMultiple() ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );

		verifyNoMoreInteractions( builderFactory, lookupStrategy );
	}

	@Test
	public void propertyElementTypeTakesPrecedenceOverEntityConfiguration() {
		when( propertyLookupRegistry.getViewElementType( CONTROL ) ).thenReturn( "configuredPropertyType" );
		when( builderFactory.createBuilder( propertyDescriptor, CONTROL, "configuredPropertyType" ) ).thenReturn( actualBuilder );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertWrapper( builder );

		verify( entityLookupRegistry ).getViewElementPostProcessors( CONTROL );
		verifyNoMoreInteractions( entityLookupRegistry, lookupStrategy );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void postProcessorsAreAlwaysApplied() {
		ViewElementPostProcessor fromEntity = mock( ViewElementPostProcessor.class );
		ViewElementPostProcessor fromProperty = mock( ViewElementPostProcessor.class );

		ViewElement element = mock( ViewElement.class );
		ViewElementBuilderContext builderContext = mock( ViewElementBuilderContext.class );

		when( propertyLookupRegistry.getViewElementPostProcessors( CONTROL ) ).thenReturn( Collections.singleton( fromProperty ) );
		when( entityLookupRegistry.getViewElementPostProcessors( CONTROL ) ).thenReturn( Collections.singleton( fromEntity ) );
		when( propertyLookupRegistry.getViewElementBuilder( CONTROL ) ).thenReturn( actualBuilder );
		when( actualBuilder.build( builderContext ) ).thenReturn( element );

		val builder = builderService.createElementBuilder( propertyDescriptor, CONTROL );
		assertSame( element, builder.build( builderContext ) );

		InOrder inOrder = inOrder( fromEntity, fromProperty );
		inOrder.verify( fromEntity ).postProcess( builderContext, element );
		inOrder.verify( fromProperty ).postProcess( builderContext, element );
	}

	private void assertWrapper( ViewElementBuilder builder ) {
		assertTrue( builder instanceof PropertyViewElementBuilderWrapper );
		PropertyViewElementBuilderWrapper wrapper = (PropertyViewElementBuilderWrapper) builder;
		assertSame( actualBuilder, wrapper.getTargetBuilder() );
		assertSame( propertyDescriptor, wrapper.getPropertyDescriptor() );
	}
}
