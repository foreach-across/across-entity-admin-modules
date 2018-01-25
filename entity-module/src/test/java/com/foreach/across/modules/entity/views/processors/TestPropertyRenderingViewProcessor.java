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

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_BUILDER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestPropertyRenderingViewProcessor
{
	@Mock
	private EntityViewElementBuilderService builderService;

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private EntityPropertyRegistry propertyRegistry;

	@Mock
	private EntityView entityView;

	@Mock
	private EntityPropertyDescriptor propOne;

	@Mock
	private EntityPropertyDescriptor propTwo;

	@Mock
	private ViewElementBuilder builderOne;

	@Mock
	private ViewElementBuilder builderTwo;

	@InjectMocks
	private PropertyRenderingViewProcessor processor;

	private Map<String, Object> model;

	@Before
	public void setUp() throws Exception {
		model = new HashMap<>();

		ViewElementBuilderContextHolder.setViewElementBuilderContext( mock( ViewElementBuilderContext.class ) );

		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( viewRequest.getWebRequest() ).thenReturn( mock( NativeWebRequest.class ) );
		when( viewContext.getPropertyRegistry() ).thenReturn( propertyRegistry );

		when( propOne.getName() ).thenReturn( "prop-one" );
		when( propTwo.getName() ).thenReturn( "prop-two" );

		when( entityView.asMap() ).thenReturn( model );
	}

	@After
	public void tearDown() throws Exception {
		ViewElementBuilderContextHolder.clearViewElementBuilderContext();
	}

	@Test
	public void defaultPropertiesAndElementMode() {
		when( builderService.getElementBuilder( propOne, ViewElementMode.FORM_READ ) ).thenReturn( builderOne );
		when( builderService.getElementBuilder( propTwo, ViewElementMode.FORM_READ ) ).thenReturn( builderTwo );

		when( propertyRegistry.select( EntityPropertySelector.of( EntityPropertySelector.READABLE ) ) ).thenReturn( Arrays.asList( propOne, propTwo ) );

		processor.preRender( viewRequest, entityView );

		ViewElementBuilderMap expected = new ViewElementBuilderMap();
		expected.put( "prop-one", builderOne );
		expected.put( "prop-two", builderTwo );

		assertEquals( expected, model.get( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS ) );
	}

	@Test
	public void customPropertiesAndElementMode() {
		when( builderService.getElementBuilder( propOne, ViewElementMode.VALUE ) ).thenReturn( builderOne );

		when( propertyRegistry.select( EntityPropertySelector.of( "one" ) ) ).thenReturn( Collections.singletonList( propOne ) );

		processor.setSelector( EntityPropertySelector.of( "one" ) );
		processor.setViewElementMode( ViewElementMode.VALUE );
		processor.preRender( viewRequest, entityView );

		ViewElementBuilderMap expected = new ViewElementBuilderMap();
		expected.put( "prop-one", builderOne );

		assertEquals( expected, model.get( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS ) );
	}

	@Test
	public void existingViewElementBuilderMapIsUsed() {
		ViewElementBuilderMap builders = new ViewElementBuilderMap();
		ViewElementBuilder existing = mock( ViewElementBuilder.class );
		builders.put( "existing", existing );

		model.put( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS, builders );

		when( builderService.getElementBuilder( propOne, ViewElementMode.FORM_READ ) ).thenReturn( builderOne );
		when( builderService.getElementBuilder( propTwo, ViewElementMode.FORM_READ ) ).thenReturn( builderTwo );

		when( propertyRegistry.select( EntityPropertySelector.of( EntityPropertySelector.READABLE ) ) ).thenReturn( Arrays.asList( propOne, propTwo ) );

		processor.preRender( viewRequest, entityView );

		ViewElementBuilderMap expected = new ViewElementBuilderMap();
		expected.put( "existing", existing );
		expected.put( "prop-one", builderOne );
		expected.put( "prop-two", builderTwo );

		assertEquals( expected, model.get( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS ) );
	}

	@Test
	public void propertyElementsAreBuiltAndAddedToTheDefaultContainer() {
		ContainerViewElementBuilder containerBuilder = mock( ContainerViewElementBuilder.class );
		when( entityView.getAttribute( ATTRIBUTE_CONTAINER_BUILDER, ContainerViewElementBuilderSupport.class ) ).thenReturn( containerBuilder );

		ViewElementBuilderMap builders = new ViewElementBuilderMap();
		builders.put( "one", builderOne );
		builders.put( "two", builderTwo );

		when( entityView.removeAttribute( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS, ViewElementBuilderMap.class ) ).thenReturn( builders );

		ViewElement elementOne = mock( ViewElement.class );
		ViewElement elementTwo = mock( ViewElement.class );
		when( builderOne.build( any() ) ).thenReturn( elementOne );
		when( builderTwo.build( any() ) ).thenReturn( elementTwo );

		processor.render( viewRequest, entityView );

		verify( containerBuilder ).add( elementOne );
		verify( containerBuilder ).add( elementTwo );
	}

	@Test
	public void customContainerIsUsedIfPresent() {
		ContainerViewElementBuilder containerBuilder = mock( ContainerViewElementBuilder.class );
		when( entityView.getAttribute( ATTRIBUTE_CONTAINER_BUILDER, ContainerViewElementBuilderSupport.class ) ).thenReturn( containerBuilder );

		ContainerViewElementBuilder propertiesContainerBuilder = mock( ContainerViewElementBuilder.class );

		ViewElementBuilderMap builders = new ViewElementBuilderMap();
		builders.put( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER, propertiesContainerBuilder );
		model.put( "entityViewBuilderMap", builders );

		ViewElementBuilderMap propertyBuilders = new ViewElementBuilderMap();
		propertyBuilders.put( "one", builderOne );
		propertyBuilders.put( "two", builderTwo );

		when( entityView.removeAttribute( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS, ViewElementBuilderMap.class ) ).thenReturn(
				propertyBuilders );

		ViewElement elementOne = mock( ViewElement.class );
		ViewElement elementTwo = mock( ViewElement.class );
		when( builderOne.build( any() ) ).thenReturn( elementOne );
		when( builderTwo.build( any() ) ).thenReturn( elementTwo );

		processor.render( viewRequest, entityView );

		verify( propertiesContainerBuilder ).add( elementOne );
		verify( propertiesContainerBuilder ).add( elementTwo );
	}

	@Test
	public void equalsIfSameElementModeAndSelector() {
		PropertyRenderingViewProcessor one = new PropertyRenderingViewProcessor();
		PropertyRenderingViewProcessor two = new PropertyRenderingViewProcessor();

		one.setSelector( EntityPropertySelector.of( "one", "two" ) );
		one.setViewElementMode( ViewElementMode.FORM_WRITE );
		two.setSelector( EntityPropertySelector.of( "one", "two" ) );
		two.setViewElementMode( ViewElementMode.FORM_WRITE );
		assertEquals( one, two );

		one.setViewElementMode( ViewElementMode.CONTROL );
		assertNotEquals( one, two );

		two.setViewElementMode( ViewElementMode.CONTROL );
		two.setSelector( EntityPropertySelector.of( "*" ) );
		assertNotEquals( one, two );
	}
}
