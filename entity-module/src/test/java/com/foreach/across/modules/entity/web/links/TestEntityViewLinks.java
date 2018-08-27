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
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.spring.security.infrastructure.business.SecurityPrincipal;
import com.foreach.across.modules.web.context.WebAppLinkBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewLinks
{
	@Mock
	private SecurityPrincipal principal;

	@Mock
	private IdBasedEntity idBasedEntity;

	@Mock
	private EntityRegistry entityRegistry;

	@Mock
	private EntityConfiguration entityConfiguration;

	@Mock
	private EntityConfiguration targetConfiguration;

	@Mock
	private EntityAssociation entityAssociation;

	@Mock
	private WebAppLinkBuilder webAppLinkBuilder;

	private EntityViewLinks links;

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		links = new EntityViewLinks( "/test/entities", entityRegistry );
		links.setConversionService( new DefaultConversionService() );
		links.setWebAppLinkBuilder( webAppLinkBuilder );
		doAnswer( invocation -> "/ctx" + invocation.getArgument( 0 ) ).when( webAppLinkBuilder ).buildLink( any(), eq( false ) );

		RequestContextHolder.setRequestAttributes( mock( RequestAttributes.class ) );

		when( entityAssociation.getName() ).thenReturn( "idBasedEntity" );
		when( entityAssociation.getAssociationType() ).thenReturn( EntityAssociation.Type.EMBEDDED );
		when( entityAssociation.isHidden() ).thenReturn( true );
		when( entityAssociation.getTargetEntityConfiguration() ).thenReturn( targetConfiguration );
		when( entityAssociation.getTargetProperty() ).thenReturn( EntityPropertyDescriptor.builder( "backRef" ).build() );

		when( targetConfiguration.getName() ).thenReturn( "targetEntity" );

		when( entityConfiguration.getName() ).thenReturn( "principal" );
		when( entityConfiguration.association( "idBasedEntity" ) ).thenReturn( entityAssociation );
		when( entityConfiguration.getAssociations() ).thenReturn( Collections.singletonList( entityAssociation ) );

		when( entityRegistry.getEntityConfiguration( "securityPrincipal" ) ).thenReturn( entityConfiguration );
		when( entityRegistry.getEntityConfiguration( SecurityPrincipal.class ) ).thenReturn( entityConfiguration );
		when( entityRegistry.getEntityConfiguration( any( SecurityPrincipal.class ) ) ).thenReturn( entityConfiguration );

		when( entityRegistry.getEntityConfiguration( "targetEntity" ) ).thenReturn( targetConfiguration );
		when( entityRegistry.getEntityConfiguration( IdBasedEntity.class ) ).thenReturn( targetConfiguration );
		when( entityRegistry.getEntityConfiguration( any( IdBasedEntity.class ) ) ).thenReturn( targetConfiguration );

		when( entityConfiguration.getId( principal ) ).thenReturn( 10 );
		when( targetConfiguration.getId( idBasedEntity ) ).thenReturn( 20 );
	}

	@After
	public void after() {
		RequestContextHolder.resetRequestAttributes();
	}

	@Test
	public void defaultViewsLink() {
		assertThat( links.linkTo( entityConfiguration ).toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( SecurityPrincipal.class ).toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( "securityPrincipal" ).toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( entityConfiguration ).listView().toString() ).isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( SecurityPrincipal.class ).createView().toString() ).isEqualTo( "/test/entities/principal/create" );

		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).toString() ).isEqualTo( "/test/entities/principal/66" );
		assertThat( links.linkTo( SecurityPrincipal.class ).forInstance( principal ).toString() ).isEqualTo( "/test/entities/principal/10" );
		assertThat( links.linkTo( principal ).toString() ).isEqualTo( "/test/entities/principal/10" );
		assertThat( links.linkTo( principal ).updateView().toString() ).isEqualTo( "/test/entities/principal/10/update" );
		assertThat( links.linkTo( principal ).deleteView().toString() ).isEqualTo( "/test/entities/principal/10/delete" );
	}

	@Test
	public void queryParameters() {
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .build( "bar" )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bar&me=there%2B" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .encode().buildAndExpand( "bar" )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bar&me=there%2B" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .buildAndExpand( "bar" )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bar&me=there+" );

		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .build( new HashMap<String, String>()
		                 {{
			                 put( "foo", "bear" );
		                 }} )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bear&me=there%2B" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .buildAndExpand( new HashMap<String, String>()
		                 {{
			                 put( "foo", "bear" );
		                 }} )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bear&me=there+" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "me", "there+" ).toUriComponentsBuilder().queryParam( "foo", "{foo}" )
		                 .encode().buildAndExpand( new HashMap<String, String>()
				{{
					put( "foo", "bear" );
				}} )
		                 .toString() )
				.isEqualTo( "/test/entities/principal?foo=bear&me=there%2B" );
		assertThat( links.linkTo( SecurityPrincipal.class ).toUriComponentsBuilder().queryParam( "test", "one+", "two+" ).build().toString() )
				.isEqualTo( "/test/entities/principal?test=one+&test=two+" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withQueryParam( "test", "one+", "two+" ).toString() )
				.isEqualTo( "/test/entities/principal?test=one%2B&test=two%2B" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withPartial( "test" ).toString() )
				.isEqualTo( "/test/entities/principal?_partial=test" );
		assertThat( links.linkTo( principal ).withFromUrl( "coming-from+" ).toString() )
				.isEqualTo( "/test/entities/principal/10?from=coming-from%2B" );
		assertThat( links.linkTo( principal ).withViewName( "myCustomView" ).toString() )
				.isEqualTo( "/test/entities/principal/10?view=myCustomView" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withFromUrl( "original" ).withPartial( "hello" ).withViewName( "myView" ).toString() )
				.isEqualTo( "/test/entities/principal?from=original&_partial=hello&view=myView" );

		assertThat( links.linkTo( SecurityPrincipal.class )
		                 .withQueryParam( "test", "one", "two" )
		                 .withQueryParam( "test", "three" ).toString() )
				.isEqualTo( "/test/entities/principal?test=three" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withPartial( "test" ).withPartial( null ).toString() )
				.isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( principal ).withFromUrl( "coming-from" ).withFromUrl( null ).toString() )
				.isEqualTo( "/test/entities/principal/10" );
		assertThat( links.linkTo( principal ).withViewName( "myCustomView" ).withViewName( null ).toString() )
				.isEqualTo( "/test/entities/principal/10" );
	}

	@Test
	public void embeddedAssociationLinks() {
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).listView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/create" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/33" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20/update" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/test/entities/principal/10/associations/idBasedEntity/20/delete" );
	}

	@Test
	public void rootProperty() {
		assertThat( links.linkTo( IdBasedEntity.class ).root().linkTo( SecurityPrincipal.class ).toString() )
				.isEqualTo( "/test/entities/principal" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).updateView().root().linkTo( SecurityPrincipal.class ).toString() )
				.isEqualTo( "/test/entities/principal" );
	}

	@Test
	public void firstNonHiddenAssociationIsUsedForType() {
		EntityAssociation other = mock( EntityAssociation.class );
		when( other.getName() ).thenReturn( "other" );
		when( other.getTargetEntityConfiguration() ).thenReturn( targetConfiguration );
		when( other.getAssociationType() ).thenReturn( EntityAssociation.Type.EMBEDDED );
		when( other.isHidden() ).thenReturn( false );

		when( entityConfiguration.association( "other" ) ).thenReturn( other );
		when( entityConfiguration.getAssociations() ).thenReturn( Arrays.asList( entityAssociation, other ) );

		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( "other" ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/other" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/other" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/principal/10/associations/other/20" );
	}

	@Test
	public void linkedAssociationLinks() {
		when( entityAssociation.getAssociationType() ).thenReturn( EntityAssociation.Type.LINKED );

		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( "idBasedEntity" ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/test/entities/principal/66/associations/idBasedEntity" );

		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo(
						"/test/entities/targetEntity/create?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity&entity.backRef=10" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/test/entities/targetEntity/33?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/targetEntity/20?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/targetEntity/20?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/test/entities/targetEntity/20/update?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/test/entities/targetEntity/20/delete?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity" );
	}

	@Test
	public void nonExistingAssociationLinks() {
		when( entityConfiguration.getAssociations() ).thenReturn( Collections.emptyList() );

		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( "targetEntity" ).toString() )
				.isEqualTo( "/test/entities/targetEntity?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F66" );
		assertThat( links.linkTo( SecurityPrincipal.class ).withId( 66 ).association( IdBasedEntity.class ).toString() )
				.isEqualTo( "/test/entities/targetEntity?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F66" );

		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).createView().toString() )
				.isEqualTo( "/test/entities/targetEntity/create?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).withId( 33 ).toString() )
				.isEqualTo( "/test/entities/targetEntity/33?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
		assertThat( links.linkTo( principal ).association( IdBasedEntity.class ).forInstance( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/targetEntity/20?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).toString() )
				.isEqualTo( "/test/entities/targetEntity/20?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).updateView().toString() )
				.isEqualTo( "/test/entities/targetEntity/20/update?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
		assertThat( links.linkTo( principal ).association( idBasedEntity ).deleteView().toString() )
				.isEqualTo( "/test/entities/targetEntity/20/delete?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10" );
	}

	@Test
	public void nestedAssociationLinks() {
		SingleEntityViewLinkBuilder base = links.linkTo( principal ).association( idBasedEntity );

		assertThat( base.association( SecurityPrincipal.class ).toString() )
				.isEqualTo( "/test/entities/principal?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity%2F20" );
		assertThat( base.updateView().association( principal ).updateView().toString() )
				.isEqualTo( "/test/entities/principal/10/update?from=%2Fctx%2Ftest%2Fentities%2Fprincipal%2F10%2Fassociations%2FidBasedEntity%2F20%2Fupdate" );
	}

	@Test
	public void deprecatedEntityLinkBuilderCompatibility() {
		EntityLinkBuilder url = links.linkTo( SecurityPrincipal.class );

		assertThat( url.overview() ).isEqualTo( "/ctx/test/entities/principal" );
		assertThat( url.create() ).isEqualTo( "/ctx/test/entities/principal/create" );
		assertThat( url.view( principal ) ).isEqualTo( "/ctx/test/entities/principal/10" );
		assertThat( url.update( principal ) ).isEqualTo( "/ctx/test/entities/principal/10/update" );
		assertThat( url.delete( principal ) ).isEqualTo( "/ctx/test/entities/principal/10/delete" );
		assertThat( url.associations( principal ) ).isEqualTo( "/ctx/test/entities/principal/10/associations" );

		EntityLinkBuilder association = links.linkTo( IdBasedEntity.class );
		EntityLinkBuilder associated = association.asAssociationFor( url, principal );

		assertThat( associated.overview() ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity" );
		assertThat( associated.create() ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity/create" );
		assertThat( associated.view( idBasedEntity ) ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity/20" );
		assertThat( associated.update( idBasedEntity ) ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity/20/update" );
		assertThat( associated.delete( idBasedEntity ) ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity/20/delete" );
		assertThat( associated.associations( idBasedEntity ) ).isEqualTo( "/ctx/test/entities/principal/10/associations/idBasedEntity/20/associations" );
	}
}
