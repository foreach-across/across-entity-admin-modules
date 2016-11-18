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

package it.com.foreach.across.modules.entity.views;

import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.core.events.AcrossEventPublisher;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.query.AssociatedEntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQuery;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityDeleteViewFactory;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.entity.views.events.BuildEntityDeleteViewEvent;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.findParent;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@AcrossWebAppConfiguration
public class TestEntityDeleteViewFactory
{
	private final String entity = "my entity";

	@Autowired
	private EntityDeleteViewFactory<ViewCreationContext> deleteViewFactory;

	@Autowired
	private AcrossEventPublisher eventPublisher;

	private ModelMap model;

	@Before
	public void prepareTest() {
		reset( eventPublisher );

		model = new ModelMap();
		model.addAttribute( EntityView.ATTRIBUTE_ENTITY, entity );
	}

	@Test
	public void deleteIsPossibleByDefault() {
		AtomicBoolean eventPublished = new AtomicBoolean( false );

		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					eventPublished.set( true );
					assertArrayEquals(
							new ResolvableType[] { ResolvableType.forClass( String.class ) },
							event.getEventGenericTypes()
					);
					assertSame( entity, event.getEntity() );
					assertFalse( event.isDeleteDisabled() );
					assertNotNull( event.getBuilderContext() );
					assertNotNull( event.associations() );
					assertNotNull( event.messages() );
					assertTrue( find( event.messages(), "associations" ).isPresent() );
					assertEquals(
							find( event.messages(), "associations" ),
							findParent( event.messages(), event.associations() )
					);
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();

		assertTrue( eventPublished.get() );
		assertTrue( find( view.getViewElements(), "btn-delete" ).isPresent() );
		assertFalse( find( view.getViewElements(), "associations" ).isPresent() );
	}

	@Test
	public void deleteSuppressedThroughEvent() {
		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					event.setDeleteDisabled( true );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();
		assertFalse( find( view.getViewElements(), "btn-delete" ).isPresent() );
	}

	@Test
	public void customAssociationButNoMessageAdded() {
		TextViewElement association = new TextViewElement( "assoc", "assoc" );
		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					event.associations().addChild( association );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();

		assertTrue( find( view.getViewElements(), "assoc" ).isPresent() );
		Optional<ContainerViewElement> parent
				= find( view.getViewElements(), "associations", ContainerViewElement.class );
		assertTrue( parent.isPresent() );
		assertTrue( find( parent.get(), association.getName() ).isPresent() );
	}

	@Test
	public void customMessageButNoAssociation() {
		TextViewElement msg = new TextViewElement( "msg", "msg" );
		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					event.messages().addChild( msg );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView();

		assertTrue( find( view.getViewElements(), "msg" ).isPresent() );
		assertFalse( find( view.getViewElements(), "associations" ).isPresent() );
	}

	@Test
	public void customMessageAndAssociation() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		EntityAssociation association = mock( EntityAssociation.class );
		when( association.getName() ).thenReturn( "someAssociation" );
		when( entityConfiguration.getAssociations() ).thenReturn( Collections.singleton( association ) );

		AssociatedEntityQueryExecutor qe = mock( AssociatedEntityQueryExecutor.class );
		when( association.getAttribute( AssociatedEntityQueryExecutor.class ) ).thenReturn( qe );
		when( qe.findAll( anyObject(), any( EntityQuery.class ) ) ).thenReturn( Arrays.asList( 1, 2 ) );

		EntityConfiguration target = mock( EntityConfiguration.class );
		EntityMessageCodeResolver codeResolver = mock( EntityMessageCodeResolver.class );
		when( target.getEntityMessageCodeResolver() ).thenReturn( codeResolver );
		when( association.getTargetEntityConfiguration() ).thenReturn( target );

		EntityLinkBuilder parentLinkBuilder = mock( EntityLinkBuilder.class );
		EntityLinkBuilder lb = mock( EntityLinkBuilder.class );
		when( association.getAttribute( EntityLinkBuilder.class ) ).thenReturn( parentLinkBuilder );
		when( parentLinkBuilder.asAssociationFor( any( EntityLinkBuilder.class ), eq( entity ) ) ).thenReturn( lb );

		TextViewElement msg = new TextViewElement( "msg", "msg" );
		doAnswer(
				invocation -> {
					BuildEntityDeleteViewEvent event = (BuildEntityDeleteViewEvent) invocation.getArguments()[0];
					event.messages().addChild( msg );
					return null;
				}
		).when( eventPublisher ).publish( any( BuildEntityDeleteViewEvent.class ) );

		EntityView view = buildView( entityConfiguration );

		assertTrue( find( view.getViewElements(), "msg" ).isPresent() );
		assertTrue( find( view.getViewElements(), "associations" ).isPresent() );
		assertTrue( find( view.getViewElements(), "someAssociation" ).isPresent() );
	}

	private EntityView buildView() {
		return buildView( mock( EntityConfiguration.class ) );
	}

	private EntityView buildView( EntityConfiguration entityConfiguration ) {
		ViewCreationContext creationContext = mock( ViewCreationContext.class );
		when( creationContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		EntityViewCommand cmd = mock( EntityViewCommand.class );

		deleteViewFactory.prepareModelAndCommand( "deleteView", creationContext, cmd, model );
		EntityView view = deleteViewFactory.create( "deleteView", creationContext, model );
		assertNotNull( view );
		assertNotNull( view.getViewElements() );

		return view;
	}

	@Configuration
	@EnableAcrossContext(modules = BootstrapUiModule.NAME)
	protected static class Config
	{
		@Bean
		public AcrossEventPublisher eventPublisher() {
			return mock( AcrossEventPublisher.class );
		}

		@Bean
		public EntityDeleteViewFactory<ViewCreationContext> entityDeleteViewFactory() {
			EntityMessageCodeResolver messageCodeResolver = mock( EntityMessageCodeResolver.class );
			when( messageCodeResolver.prefixedResolver( anyVararg() ) ).thenReturn( messageCodeResolver );

			EntityDeleteViewFactory<ViewCreationContext> entityDeleteViewFactory = new EntityDeleteViewFactory<>();
			entityDeleteViewFactory.setMessageCodeResolver( messageCodeResolver );

			EntityLinkBuilder linkBuilder = mock( EntityLinkBuilder.class );
			entityDeleteViewFactory.setEntityLinkBuilder( linkBuilder );

			return entityDeleteViewFactory;
		}
	}
}
