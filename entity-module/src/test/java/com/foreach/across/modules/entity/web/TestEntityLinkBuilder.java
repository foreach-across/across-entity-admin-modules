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
package com.foreach.across.modules.entity.web;

import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.web.context.WebAppPathResolver;
import org.junit.Test;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Deprecated
public class TestEntityLinkBuilder
{
	private static final WebAppPathResolver TEST_RESOLVER = new WebAppPathResolver()
	{
		@Override
		public String path( String path ) {
			return "/test" + path;
		}

		@Override
		public String redirect( String path ) {
			return null;
		}
	};

	@Test
	@SuppressWarnings("unchecked")
	public void withWebAppPathResolver() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( any() ) ).thenReturn( new BigDecimal( "100.01" ) );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( new BigDecimal( "100.01" ), String.class ) ).thenReturn( "10001" );

		EntityConfigurationLinkBuilder url = new EntityConfigurationLinkBuilder(
				GenericEntityViewController.ROOT_PATH, entityConfiguration, conversionService, TEST_RESOLVER
		);

		assertEquals( "/test/entities/basicPrincipal", url.overview() );
		assertEquals( "/test/entities/basicPrincipal/create", url.create() );
		assertEquals( "/test/entities/basicPrincipal/10001", url.view( "someEntity" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/update", url.update( "someEntity" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/delete", url.delete( "someEntity" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/associations", url.associations( "someEntity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void generatedPathsWithNumberId() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( any() ) ).thenReturn( new BigDecimal( "100.01" ) );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( new BigDecimal( "100.01" ), String.class ) ).thenReturn( "10001" );

		EntityConfigurationLinkBuilder url = new EntityConfigurationLinkBuilder(
				GenericEntityViewController.ROOT_PATH, entityConfiguration, conversionService
		);

		assertEquals( "/entities/basicPrincipal", url.overview() );
		assertEquals( "/entities/basicPrincipal/create", url.create() );
		assertEquals( "/entities/basicPrincipal/10001", url.view( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/update", url.update( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/delete", url.delete( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/10001/associations", url.associations( "someEntity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void generatedPathsWithStringId() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getName() ).thenReturn( "basicPrincipal" );

		EntityModel model = mock( EntityModel.class );
		when( model.getId( any() ) ).thenReturn( "someStringId" );
		when( model.getIdType() ).thenReturn( String.class );

		when( entityConfiguration.getEntityModel() ).thenReturn( model );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( "someStringId", String.class ) ).thenReturn( "someStringId" );

		EntityLinkBuilder url =
				new EntityConfigurationLinkBuilder( GenericEntityViewController.ROOT_PATH, entityConfiguration,
				                                    conversionService );

		assertEquals( "/entities/basicPrincipal", url.overview() );
		assertEquals( "/entities/basicPrincipal/create", url.create() );
		assertEquals( "/entities/basicPrincipal/someStringId", url.view( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/update", url.update( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/delete", url.delete( "someEntity" ) );
		assertEquals( "/entities/basicPrincipal/someStringId/associations", url.associations( "someEntity" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void embeddedAssociationLinkBuilder() {
		EntityConfiguration parentConfig = mock( EntityConfiguration.class );
		when( parentConfig.getName() ).thenReturn( "basicPrincipal" );

		EntityConfiguration associatedConfig = mock( EntityConfiguration.class );
		when( associatedConfig.getName() ).thenReturn( "user" );

		EntityModel parentModel = mock( EntityModel.class );
		when( parentModel.getId( "someEntity" ) ).thenReturn( 10001 );
		when( parentConfig.getEntityModel() ).thenReturn( parentModel );

		EntityModel associationModel = mock( EntityModel.class );
		when( associationModel.getId( any() ) ).thenReturn( 123 );
		when( associatedConfig.getEntityModel() ).thenReturn( associationModel );

		EntityAssociation association = mock( EntityAssociation.class );
		when( association.getName() ).thenReturn( "company.user" );
		when( association.getSourceEntityConfiguration() ).thenReturn( parentConfig );
		when( association.getTargetEntityConfiguration() ).thenReturn( associatedConfig );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( 10001, String.class ) ).thenReturn( "10001" );
		when( conversionService.convert( 123, String.class ) ).thenReturn( "123" );

		EntityLinkBuilder parent =
				new EntityConfigurationLinkBuilder( GenericEntityViewController.ROOT_PATH, parentConfig,
				                                    conversionService, TEST_RESOLVER );

		EntityAssociationLinkBuilder url = new EntityAssociationLinkBuilder( association, conversionService );
		url.setAssociationsPath( "{0}/{1}/others/{2}" );

		assertEquals( "/company.user", url.overview() );
		assertEquals( "/company.user/create", url.create() );
		assertEquals( "/company.user/123", url.view( "someEntity" ) );
		assertEquals( "/company.user/123/update", url.update( "someEntity" ) );
		assertEquals( "/company.user/123/delete", url.delete( "someEntity" ) );
		assertEquals( "/company.user/others/123", url.associations( "someEntity" ) );

		when( association.getAssociationType() ).thenReturn( EntityAssociation.Type.EMBEDDED );
		EntityLinkBuilder scoped = url.asAssociationFor( parent, "someEntity" );

		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user", scoped.overview() );
		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user/create", scoped.create() );
		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user/123", scoped.view( "assoc" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user/123/update",
		              scoped.update( "assoc" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user/123/delete",
		              scoped.delete( "assoc" ) );
		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user/others/123",
		              scoped.associations( "assoc" ) );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void linkedAssociationLinkBuilder() {
		EntityConfiguration parentConfig = mock( EntityConfiguration.class );
		when( parentConfig.getName() ).thenReturn( "basicPrincipal" );

		EntityConfiguration associatedConfig = mock( EntityConfiguration.class );
		when( associatedConfig.getName() ).thenReturn( "user" );

		EntityModel parentModel = mock( EntityModel.class );
		when( parentModel.getId( "someEntity" ) ).thenReturn( 10001 );
		when( parentConfig.getEntityModel() ).thenReturn( parentModel );

		EntityModel associationModel = mock( EntityModel.class );
		when( associationModel.getId( any() ) ).thenReturn( 123 );
		when( associatedConfig.getEntityModel() ).thenReturn( associationModel );

		EntityPropertyDescriptor targetProperty = mock( EntityPropertyDescriptor.class );
		when( targetProperty.getName() ).thenReturn( "sourceName" );

		EntityAssociation association = mock( EntityAssociation.class );
		when( association.getName() ).thenReturn( "company.user" );
		when( association.getSourceEntityConfiguration() ).thenReturn( parentConfig );
		when( association.getTargetEntityConfiguration() ).thenReturn( associatedConfig );
		when( association.getTargetProperty() ).thenReturn( targetProperty );

		EntityLinkBuilder targetLinkBuilder = mock( EntityLinkBuilder.class );
		when( targetLinkBuilder.create() ).thenReturn( "/test/entities/user/create" );
		when( associatedConfig.getAttribute( EntityLinkBuilder.class ) ).thenReturn( targetLinkBuilder );

		ConversionService conversionService = mock( ConversionService.class );
		when( conversionService.convert( 10001, String.class ) ).thenReturn( "10001" );
		when( conversionService.convert( 123, String.class ) ).thenReturn( "123" );

		EntityLinkBuilder parent =
				new EntityConfigurationLinkBuilder( GenericEntityViewController.ROOT_PATH, parentConfig,
				                                    conversionService, TEST_RESOLVER );

		EntityAssociationLinkBuilder url = new EntityAssociationLinkBuilder( association, conversionService );
		url.setAssociationsPath( "{0}/{1}/others/{2}" );

		assertEquals( "/company.user", url.overview() );
		assertEquals( "/company.user/create", url.create() );
		assertEquals( "/company.user/123", url.view( "someEntity" ) );
		assertEquals( "/company.user/123/update", url.update( "someEntity" ) );
		assertEquals( "/company.user/123/delete", url.delete( "someEntity" ) );
		assertEquals( "/company.user/others/123", url.associations( "someEntity" ) );

		when( association.getAssociationType() ).thenReturn( EntityAssociation.Type.LINKED );
		EntityLinkBuilder scoped = url.asAssociationFor( parent, "someEntity" );

		when( targetLinkBuilder.view( "assoc" ) ).thenReturn( "/test/entities/user/123" );
		when( targetLinkBuilder.update( "assoc" ) ).thenReturn( "/test/entities/user/123/update" );

		assertEquals( "/test/entities/basicPrincipal/10001/associations/company.user", scoped.overview() );
		assertEquals(
				"/test/entities/user/create?entity.sourceName=10001&from=/test/entities/basicPrincipal/10001/associations/company.user",
				scoped.create()
		);
		assertEquals(
				"/test/entities/user/123?from=/test/entities/basicPrincipal/10001/associations/company.user",
				scoped.view( "assoc" )
		);
		assertEquals(
				"/test/entities/user/123/update?from=/test/entities/basicPrincipal/10001/associations/company.user",
				scoped.update( "assoc" )
		);
		assertEquals(
				"/test/entities/basicPrincipal/10001/associations/company.user/123/delete",
				scoped.delete( "assoc" )
		);
		assertEquals(
				"/test/entities/basicPrincipal/10001/associations/company.user/others/123",
				scoped.associations( "assoc" )
		);
	}
}
