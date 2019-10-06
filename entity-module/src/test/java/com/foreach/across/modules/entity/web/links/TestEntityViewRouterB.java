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

package com.foreach.across.modules.entity.web.links;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
class TestEntityViewRouterB
{
	@Mock
	private SecurityPrincipal principal;

	@Mock
	private IdBasedEntity idBasedEntity;

	@Mock(lenient = true)
	private EntityRegistry entityRegistry;

	@Mock(lenient = true)
	private EntityConfiguration entityConfiguration;

	@Mock(lenient = true)
	private EntityConfiguration targetConfiguration;

	@Mock(lenient = true)
	private EntityAssociation entityAssociation;

	@Mock
	private WebAppLinkBuilder webAppLinkBuilder;

	private EntityViewLinks links;

	private EntityViewRouterB router = new EntityViewRouterB();

	@BeforeEach
	@SuppressWarnings("unchecked")
	void before() {
		links = new EntityViewLinks( "/test/entities", entityRegistry );
		links.setConversionService( new DefaultConversionService() );
		links.setWebAppLinkBuilder( webAppLinkBuilder );
//		doAnswer( invocation -> "/ctx" + invocation.getArgument( 0 ) ).when( webAppLinkBuilder ).buildLink( any(), eq( false ) );

		RequestContextHolder.setRequestAttributes( mock( RequestAttributes.class ) );
//
		when( entityAssociation.getName() ).thenReturn( "idBasedEntity" );
		when( entityAssociation.getAssociationType() ).thenReturn( EntityAssociation.Type.EMBEDDED );
		when( entityAssociation.isHidden() ).thenReturn( true );
		when( entityAssociation.getTargetEntityConfiguration() ).thenReturn( targetConfiguration );
//		when( entityAssociation.getTargetProperty() )
//				.thenReturn(
//						EntityPropertyDescriptor.builder( "backRef" )
//						                        .attribute( EntityPropertyHandlingType.class, EntityPropertyHandlingType.DIRECT )
//						                        .build()
//				);

//		when( targetConfiguration.getName() ).thenReturn( "targetEntity" );
//
		when( entityConfiguration.getName() ).thenReturn( "principal" );
		when( entityConfiguration.association( "idBasedEntity" ) ).thenReturn( entityAssociation );
		when( entityConfiguration.getAssociations() ).thenReturn( Collections.singletonList( entityAssociation ) );
//
//		when( entityRegistry.getEntityConfiguration( "securityPrincipal" ) ).thenReturn( entityConfiguration );
//		when( entityRegistry.getEntityConfiguration( SecurityPrincipal.class ) ).thenReturn( entityConfiguration );
//		when( entityRegistry.getEntityConfiguration( any( SecurityPrincipal.class ) ) ).thenReturn( entityConfiguration );
//
//		when( entityRegistry.getEntityConfiguration( "targetEntity" ) ).thenReturn( targetConfiguration );
		doReturn( targetConfiguration ).when( entityRegistry ).getEntityConfiguration( IdBasedEntity.class );
		doReturn( targetConfiguration ).when( entityRegistry ).getEntityConfiguration( any( IdBasedEntity.class ) );
//
		when( entityConfiguration.getId( principal ) ).thenReturn( 10 );
		when( targetConfiguration.getId( idBasedEntity ) ).thenReturn( 20 );
	}

	@Test
	void defaultRouter() {
		EntityViewLinkBuilder users = router.createLinkBuilder( links, entityConfiguration );

		assertThat( users.toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( users.listView().toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( users.createView().toString() ).isEqualTo( "/test/entities/principal/create" );
		assertThat( users.withId( 66 ).toString() ).isEqualTo( "/test/entities/principal/66" );
		assertThat( users.forInstance( principal ).toString() ).isEqualTo( "/test/entities/principal/10" );
		assertThat( users.forInstance( principal ).updateView().toString() ).isEqualTo( "/test/entities/principal/10/update" );
		assertThat( users.forInstance( principal ).deleteView().toString() ).isEqualTo( "/test/entities/principal/10/delete" );

		assertThat( users.withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( users.withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).listView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/create" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/33" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20/update" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20/delete" );
	}

	@Test
	void customizedEntityRootOnly() {
		EntityViewLinkBuilder users = new EntityViewRouterB()
				.entityRoot( "/users" )
				.createLinkBuilder( links, entityConfiguration );

		assertThat( users.toString() ).isEqualTo( "/users" );
		assertThat( users.listView().toString() ).isEqualTo( "/users" );
		assertThat( users.createView().toString() ).isEqualTo( "/users/create" );
		assertThat( users.withId( 66 ).toString() ).isEqualTo( "/users/66" );
		assertThat( users.forInstance( principal ).toString() ).isEqualTo( "/users/10" );
		assertThat( users.forInstance( principal ).updateView().toString() ).isEqualTo( "/users/10/update" );
		assertThat( users.forInstance( principal ).deleteView().toString() ).isEqualTo( "/users/10/delete" );

		assertThat( users.withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/users/66/associations/idBasedEntity" );
		assertThat( users.withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/users/66/associations/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).listView().toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/create" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/33" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/20/update" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/users/10/associations/idBasedEntity/20/delete" );
	}

	@Test
	void customizedEntityAndAssociationRoots() {
		EntityViewLinkBuilder users = new EntityViewRouterB()
				.entityRoot( "/users" )
				.associationRoot( "" )
				.createLinkBuilder( links, entityConfiguration );

		assertThat( users.withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/users/66/idBasedEntity" );
		assertThat( users.withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/users/66/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).listView().toString() )
				.isEqualTo( "/users/10/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo( "/users/10/idBasedEntity/create" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/users/10/idBasedEntity/33" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/users/10/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/users/10/idBasedEntity/20" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/users/10/idBasedEntity/20/update" );
		assertThat( users.forInstance( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/users/10/idBasedEntity/20/delete" );

		users = new EntityViewRouterB()
				.entityRoot( "/users" )
				.associationRoot( "relations/type" )
				.createLinkBuilder( links, entityConfiguration );

		assertThat( users.withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/users/66/relations/type/idBasedEntity" );
		assertThat( users.forInstance( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/users/10/relations/type/idBasedEntity/20" );
	}

	@Test
	void customRouter() {
		EntityViewLinkBuilder users = new EntityViewRouterB()
				.entityRoot( "/users" )
				.associationRoot( "" )
				//.mapView("create", "new")
				//.mapAssociation("car.owner", "/cars")
				.createLinkBuilder( links, entityConfiguration );

		assertThat( users.toString() ).isEqualTo( "/users" );
		assertThat( users.withId( 66 ).toString() ).isEqualTo( "/users/66" );
	}

	@Nested
	@DisplayName( "Test apply methods semantics" )
	class RouterApplyMethods
	{
		private UriComponentsBuilder uri = UriComponentsBuilder.fromPath( "/test" );

		@Test
		void applyEntityRootUsesConfigurationNameIfNoneConfigured() {
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "/test/principal" );
		}

		@Test
		void applyEntityRootAppendsToRootPath() {
			router.entityRoot( "principals" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "/test/principals" );

			resetUri();

			router.entityRoot( "my/principals/" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "/test/my/principals/" );
		}

		@Test
		void applyEntityRootWillReplacePreviousPathIfStartingWithLeadingSlash() {
			UriComponentsBuilder uri = UriComponentsBuilder.fromPath( "/test" );

			router.entityRoot( "/principals" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "/principals" );

			router.entityRoot( "/my/principals/" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "/my/principals/" );

			router.entityRoot( "@adminWeb:/users" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "@adminWeb:/users" );

			router.entityRoot( "~/users" );
			assertThat( router.applyEntityRoot( uri, entityConfiguration ) ).isSameAs( uri );
			assertThat( uri.toUriString() ).isEqualTo( "~/users" );
		}

		@Test
		void applyAssociationUsesDefaultAssociationRoot() {
			router.applyAssociationPath( uri, entityAssociation );
		}

		private void resetUri() {
			uri = UriComponentsBuilder.fromPath( "/test" );
		}
	}
}
