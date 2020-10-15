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

import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityPropertyControlNamePostProcessor;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.*;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;

import static com.foreach.across.modules.entity.views.DefaultEntityViewFactory.ATTRIBUTE_CONTAINER_BUILDER;
import static com.foreach.across.modules.entity.views.processors.PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_DESCRIPTORS;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

	private ViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();

	@InjectMocks
	private PropertyRenderingViewProcessor processor;

	private ModelMap model;

	@BeforeEach
	public void setUp() throws Exception {
		model = new ModelMap();

		ViewElementBuilderContextHolder.setViewElementBuilderContext( builderContext );

		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( viewRequest.getWebRequest() ).thenReturn( mock( NativeWebRequest.class ) );
		when( viewRequest.getModel() ).thenReturn( model );
		when( viewContext.getPropertyRegistry() ).thenReturn( propertyRegistry );

		when( propOne.getName() ).thenReturn( "prop-one" );
		when( propTwo.getName() ).thenReturn( "prop-two" );

		when( entityView.getModel() ).thenReturn( model );
		when( entityView.asMap() ).thenReturn( model );
	}

	@AfterEach
	public void tearDown() {
		ViewElementBuilderContextHolder.clearViewElementBuilderContext();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void propertyDescriptorsAreRegisteredEarlyOn() {
		when( propertyRegistry.select( EntityPropertySelector.of( EntityPropertySelector.READABLE ) ) ).thenReturn( Arrays.asList( propOne, propTwo ) );

		processor.initializeCommandObject( viewRequest, mock( EntityViewCommand.class ), mock( WebDataBinder.class ) );

		Assertions.assertThat( (LinkedHashMap<String, EntityPropertyDescriptor>) viewRequest.getModel().get( ATTRIBUTE_PROPERTY_DESCRIPTORS ) )
		          .containsExactly( entry( "prop-one", propOne ), entry( "prop-two", propTwo ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void customSelectorIsBeingUsed() {
		when( propertyRegistry.select( EntityPropertySelector.of( "two", "one" ) ) ).thenReturn( Arrays.asList( propOne, propTwo ) );

		processor.setSelector( EntityPropertySelector.of( "two", "one" ) );
		processor.initializeCommandObject( viewRequest, mock( EntityViewCommand.class ), mock( WebDataBinder.class ) );

		Assertions.assertThat( (LinkedHashMap<String, EntityPropertyDescriptor>) viewRequest.getModel().get( ATTRIBUTE_PROPERTY_DESCRIPTORS ) )
		          .containsExactly( entry( "prop-one", propOne ), entry( "prop-two", propTwo ) );
	}

	@Test
	public void noPropertyDescriptors() {
		processor.preRender( viewRequest, entityView );

		ViewElementBuilderMap expected = new ViewElementBuilderMap();

		assertEquals( expected, model.get( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS ) );
	}

	@Test
	public void defaultViewElementMode() {
		when( builderService.getElementBuilder( propOne, ViewElementMode.FORM_READ ) ).thenReturn( builderOne );
		when( builderService.getElementBuilder( propTwo, ViewElementMode.FORM_READ ) ).thenReturn( builderTwo );

		ImmutableMap<String, EntityPropertyDescriptor> descriptorMap = ImmutableMap.<String, EntityPropertyDescriptor>builder()
				.put( "prop-two", propTwo )
				.put( "prop-one", propOne )
				.build();

		model.put( ATTRIBUTE_PROPERTY_DESCRIPTORS, descriptorMap );

		processor.preRender( viewRequest, entityView );

		ViewElementBuilderMap expected = new ViewElementBuilderMap();
		expected.put( "prop-one", builderOne );
		expected.put( "prop-two", builderTwo );

		assertEquals( expected, model.get( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_BUILDERS ) );
	}

	@Test
	public void customViewElementMode() {
		when( builderService.getElementBuilder( propOne, ViewElementMode.VALUE ) ).thenReturn( builderOne );

		model.put( ATTRIBUTE_PROPERTY_DESCRIPTORS, Collections.singletonMap( "prop-one", propOne ) );

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

		ImmutableMap<String, EntityPropertyDescriptor> descriptorMap = ImmutableMap.<String, EntityPropertyDescriptor>builder()
				.put( "prop-two", propTwo )
				.put( "prop-one", propOne )
				.build();

		model.put( ATTRIBUTE_PROPERTY_DESCRIPTORS, descriptorMap );

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

		doAnswer( invocationOnMock -> {
			ViewElementBuilderContext builderContext = invocationOnMock.getArgument( 0 );
			boolean b = builderContext.getAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES, Boolean.class );
			assertFalse( b );
			assertNotNull( builderContext.getAttribute( EntityPropertyControlName.class ) );

			return elementTwo;
		} ).when( builderTwo ).build( any() );

		processor.render( viewRequest, entityView );

		verify( containerBuilder ).add( elementOne );
		verify( containerBuilder ).add( elementTwo );

		assertFalse( builderContext.hasAttribute( EntityPropertyControlNamePostProcessor.PREFIX_CONTROL_NAMES ) );
		assertFalse( builderContext.hasAttribute( EntityPropertyControlName.class ) );
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
