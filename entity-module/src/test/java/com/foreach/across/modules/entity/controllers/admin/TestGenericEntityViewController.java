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

package com.foreach.across.modules.entity.controllers.admin;

import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.context.EntityViewContextLoader;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.context.AcrossWebArgumentResolver;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.web.EntityViewModel.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestGenericEntityViewController
{
	@Mock
	private ConfigurableEntityViewContext viewContext;

	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContextLoader viewContextLoader;

	@Mock
	private PageContentStructure pageContentStructure;

	@Mock
	private EntityConfiguration entityConfiguration;

	@Mock
	private EntityViewFactory viewFactory;

	@Mock
	private EntityModel entityModel;

	@Mock
	private EntityViewCommand command;

	@Mock
	private EntityView entityView;

	@Mock
	private WebAppPathResolver webAppPathResolver;

	@Mock
	private WebResourceRegistry webResourceRegistry;

	@InjectMocks
	private GenericEntityViewController controller;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup( controller )
		                         .setCustomArgumentResolvers( new AcrossWebArgumentResolver() )
		                         .addInterceptors( new HandlerInterceptorAdapter()
		                         {
			                         @Override
			                         public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception {
				                         WebResourceUtils.storeRegistry( webResourceRegistry, request );
				                         return true;
			                         }
		                         } )
		                         .build();

		when( entityModel.getIdType() ).thenReturn( String.class );
		when( entityModel.findOne( "123" ) ).thenReturn( "some entity" );
		when( viewContext.getEntityModel() ).thenReturn( entityModel );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		when( viewRequest.getCommand() ).thenReturn( command );
		when( viewRequest.getViewFactory() ).thenReturn( viewFactory );

		when( entityConfiguration.getViewFactory( anyString() ) ).thenReturn( viewFactory );

		when( viewFactory.createView( viewRequest ) ).thenReturn( entityView );
		when( viewFactory.attributeMap() ).thenReturn( new HashMap<>() );
		when( entityView.getTemplate() ).thenReturn( "view-template" );

		controller.setMvcConversionService( new DefaultConversionService() );
	}

	@Test
	public void webResourcePackageShouldBeAdded() throws Exception {
		mockMvc.perform( get( "/entities/type?view=someListView" ) )
		       .andExpect( status().isOk() );

		verify( webResourceRegistry ).addPackage( EntityModuleWebResources.NAME );
	}

	@Test
	public void defaultViewName() throws Exception {
		when( entityView.getTemplate() ).thenReturn( null );

		mockMvc.perform( get( "/entities/type?view=someListView" ) )
		       .andExpect( status().isOk() )
		       .andExpect( view().name( PageContentStructure.TEMPLATE ) );
	}

	@Test
	public void redirect() throws Exception {
		when( entityView.isRedirect() ).thenReturn( true );
		when( entityView.getRedirectUrl() ).thenReturn( "url" );
		when( webAppPathResolver.redirect( "url" ) ).thenReturn( "redirect:url" );

		mockMvc.perform( get( "/entities/type" ) )
		       .andExpect( status().is3xxRedirection() )
		       .andExpect( view().name( "redirect:url" ) );
	}

	@Test
	public void customView() throws Exception {
		when( entityView.isCustomView() ).thenReturn( true );
		when( entityView.getCustomView() ).thenReturn( "custom view" );

		mockMvc.perform( get( "/entities/type" ) )
		       .andExpect( status().isOk() )
		       .andExpect( view().name( "custom view" ) );
	}

	@Test
	public void customViewAndPartialParameter() throws Exception {
		mockMvc.perform( get( "/entities/type?view=someListView" ) )
		       .andExpect( status().isOk() );
		verify( viewRequest ).setViewName( "someListView" );

		mockMvc.perform( get( "/entities/type/create?view=otherView&_partial=header" ) )
		       .andExpect( status().isOk() );
		verify( viewRequest ).setViewName( "otherView" );
		verify( viewRequest ).setPartialFragment( "header" );
	}

	@Test
	public void originalConfigurationAttributesCannotBeModified() throws Exception {
		viewRequest = new EntityViewRequest();
		controller.setEntityViewRequest( viewRequest );
		when( viewFactory.createView( viewRequest ) ).thenReturn( entityView );

		Map<String, Object> original = viewFactory.attributeMap();
		original.put( "key", "value" );

		mockMvc.perform( get( "/entities/type" ) ).andExpect( status().isOk() );

		Map<String, Object> copied = viewRequest.getConfigurationAttributes();
		assertNotNull( copied );
		assertEquals( original, copied );

		copied.remove( "key" );
		assertNotEquals( original, copied );
		assertTrue( copied.isEmpty() );
		assertEquals( "value", original.get( "key" ) );
	}

	@Test
	public void listView() throws Exception {
		mockMvc.perform( get( "/entities/type" ) )
		       .andExpect( status().isOk() )
		       .andExpect( model().attribute( VIEW_CONTEXT, viewContext ) )
		       .andExpect( model().attribute( VIEW_REQUEST, viewRequest ) )
		       .andExpect( model().attribute( VIEW_COMMAND, command ) )
		       .andExpect( view().name( "view-template" ) );

		verify( viewContextLoader ).loadForEntityConfiguration( viewContext, "type" );
		verify( viewContext, never() ).setEntity( any() );

		verify( viewRequest ).setModel( any( ModelMap.class ) );
		verify( viewRequest ).setRedirectAttributes( any( RedirectAttributes.class ) );
		verify( viewRequest ).setEntityViewContext( viewContext );
		verify( viewRequest ).setPageContentStructure( pageContentStructure );
		verify( viewRequest ).setCommand( any( EntityViewCommand.class ) );
		verify( viewRequest ).setViewName( EntityView.LIST_VIEW_NAME );
		verify( viewRequest ).setWebRequest( any( NativeWebRequest.class ) );
		verify( viewRequest ).setHttpMethod( HttpMethod.GET );
		verify( viewRequest ).setBindingResult( any( BindingResult.class ) );
		verify( viewRequest ).setViewFactory( viewFactory );

		verifyViewFactoryCalls();
	}

	@Test
	public void createView() throws Exception {
		mockMvc.perform( get( "/entities/type/create" ) )
		       .andExpect( status().isOk() )
		       .andExpect( model().attribute( VIEW_CONTEXT, viewContext ) )
		       .andExpect( model().attribute( VIEW_REQUEST, viewRequest ) )
		       .andExpect( model().attribute( VIEW_COMMAND, command ) )
		       .andExpect( view().name( "view-template" ) );

		verify( viewContextLoader ).loadForEntityConfiguration( viewContext, "type" );
		verify( viewContext, never() ).setEntity( any() );

		verify( viewRequest ).setModel( any( ModelMap.class ) );
		verify( viewRequest ).setRedirectAttributes( any( RedirectAttributes.class ) );
		verify( viewRequest ).setEntityViewContext( viewContext );
		verify( viewRequest ).setPageContentStructure( pageContentStructure );
		verify( viewRequest ).setCommand( any( EntityViewCommand.class ) );
		verify( viewRequest ).setViewName( EntityView.CREATE_VIEW_NAME );
		verify( viewRequest ).setWebRequest( any( NativeWebRequest.class ) );
		verify( viewRequest ).setHttpMethod( HttpMethod.GET );
		verify( viewRequest ).setBindingResult( any( BindingResult.class ) );
		verify( viewRequest ).setViewFactory( viewFactory );

		verifyViewFactoryCalls();
	}

	@Test
	public void genericView() throws Exception {
		when( viewContext.holdsEntity() ).thenReturn( true );

		mockMvc.perform( post( "/entities/type/123" ) )
		       .andExpect( status().isOk() )
		       .andExpect( model().attribute( VIEW_CONTEXT, viewContext ) )
		       .andExpect( model().attribute( VIEW_REQUEST, viewRequest ) )
		       .andExpect( model().attribute( VIEW_COMMAND, command ) )
		       .andExpect( view().name( "view-template" ) );

		verify( viewContextLoader ).loadForEntityConfiguration( viewContext, "type" );
		verify( viewContext ).setEntity( "some entity" );

		verify( viewRequest ).setModel( any( ModelMap.class ) );
		verify( viewRequest ).setRedirectAttributes( any( RedirectAttributes.class ) );
		verify( viewRequest ).setEntityViewContext( viewContext );
		verify( viewRequest ).setPageContentStructure( pageContentStructure );
		verify( viewRequest ).setCommand( any( EntityViewCommand.class ) );
		verify( viewRequest ).setViewName( "view" );
		verify( viewRequest ).setWebRequest( any( NativeWebRequest.class ) );
		verify( viewRequest ).setHttpMethod( HttpMethod.POST );
		verify( viewRequest ).setBindingResult( any( BindingResult.class ) );
		verify( viewRequest ).setViewFactory( viewFactory );

		verifyViewFactoryCalls();
	}

	@Test
	public void updateView() throws Exception {
		when( viewContext.holdsEntity() ).thenReturn( true );

		mockMvc.perform( get( "/entities/type/123/update" ) )
		       .andExpect( status().isOk() )
		       .andExpect( model().attribute( VIEW_CONTEXT, viewContext ) )
		       .andExpect( model().attribute( VIEW_REQUEST, viewRequest ) )
		       .andExpect( model().attribute( VIEW_COMMAND, command ) )
		       .andExpect( view().name( "view-template" ) );

		verify( viewContextLoader ).loadForEntityConfiguration( viewContext, "type" );
		verify( viewContext ).setEntity( "some entity" );

		verify( viewRequest ).setModel( any( ModelMap.class ) );
		verify( viewRequest ).setRedirectAttributes( any( RedirectAttributes.class ) );
		verify( viewRequest ).setEntityViewContext( viewContext );
		verify( viewRequest ).setPageContentStructure( pageContentStructure );
		verify( viewRequest ).setCommand( any( EntityViewCommand.class ) );
		verify( viewRequest ).setViewName( EntityView.UPDATE_VIEW_NAME );
		verify( viewRequest ).setWebRequest( any( NativeWebRequest.class ) );
		verify( viewRequest ).setHttpMethod( HttpMethod.GET );
		verify( viewRequest ).setBindingResult( any( BindingResult.class ) );
		verify( viewRequest ).setViewFactory( viewFactory );

		verifyViewFactoryCalls();
	}

	@Test
	public void deleteView() throws Exception {
		when( viewContext.holdsEntity() ).thenReturn( true );

		mockMvc.perform( get( "/entities/type/123/delete" ) )
		       .andExpect( status().isOk() )
		       .andExpect( model().attribute( VIEW_CONTEXT, viewContext ) )
		       .andExpect( model().attribute( VIEW_REQUEST, viewRequest ) )
		       .andExpect( model().attribute( VIEW_COMMAND, command ) )
		       .andExpect( view().name( "view-template" ) );

		verify( viewContextLoader ).loadForEntityConfiguration( viewContext, "type" );
		verify( viewContext ).setEntity( "some entity" );

		verify( viewRequest ).setModel( any( ModelMap.class ) );
		verify( viewRequest ).setRedirectAttributes( any( RedirectAttributes.class ) );
		verify( viewRequest ).setEntityViewContext( viewContext );
		verify( viewRequest ).setPageContentStructure( pageContentStructure );
		verify( viewRequest ).setCommand( any( EntityViewCommand.class ) );
		verify( viewRequest ).setViewName( EntityView.DELETE_VIEW_NAME );
		verify( viewRequest ).setWebRequest( any( NativeWebRequest.class ) );
		verify( viewRequest ).setHttpMethod( HttpMethod.GET );
		verify( viewRequest ).setBindingResult( any( BindingResult.class ) );
		verify( viewRequest ).setViewFactory( viewFactory );

		verifyViewFactoryCalls();
	}

	private void verifyViewFactoryCalls() {
		InOrder inOrder = inOrder( viewFactory );
		inOrder.verify( viewFactory ).attributeMap();
		inOrder.verify( viewFactory ).prepareEntityViewContext( viewContext );
		inOrder.verify( viewFactory ).authorizeRequest( viewRequest );
		inOrder.verify( viewFactory ).initializeCommandObject( same( viewRequest ), same( command ), any() );
		inOrder.verify( viewFactory ).createView( viewRequest );
	}
}
