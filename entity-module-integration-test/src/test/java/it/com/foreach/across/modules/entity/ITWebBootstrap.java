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

package it.com.foreach.across.modules.entity;

import com.foreach.across.core.context.registry.AcrossContextBeanRegistry;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.test.AcrossTestConfiguration;
import com.foreach.across.test.AcrossWebAppConfiguration;
import com.foreach.across.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.testmodules.springdata.business.Client;
import com.foreach.across.testmodules.springdata.business.Company;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.function.Function;

import static com.foreach.across.core.context.bootstrap.AcrossBootstrapConfigurer.CONTEXT_POSTPROCESSOR_MODULE;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
@AcrossWebAppConfiguration
@TestPropertySource(properties = "adminWebModule.root-path=/hidden")
public class ITWebBootstrap
{
	// Declared for conversion test
	@SuppressWarnings("unused")
	@NotNull
	private Client clientProperty;

	@Autowired
	private AcrossContextBeanRegistry beanRegistry;

	@Autowired
	private ConversionService mvcConversionService;

	@Autowired
	private Function<Serializable, Client> mockClientFinder;

	@Autowired
	private EntityViewLinks entityViewLinks;

	@Autowired
	private EntityRegistry entityRegistry;

	@Test
	public void bootstrappedOk() {
		assertNotNull( beanRegistry.getBeanOfType( EntityRegistry.class ) );
	}

	@Test
	public void springDataWebSupportShouldBeEnabled() {
		assertNotNull( beanRegistry.getBeanOfTypeFromModule( CONTEXT_POSTPROCESSOR_MODULE, PageableHandlerMethodArgumentResolver.class ) );
		assertTrue( mvcConversionService.canConvert( Long.class, Client.class ) );
	}

	@Test
	public void conversionServiceShouldUseTheEntityConverter() {
		assertNull( mvcConversionService.convert( "123", Client.class ) );
		verify( mockClientFinder ).apply( 123L );
	}

	@Test
	public void entityConverterShouldNotConvertActualEntities() {
		Client client = new Client();
		Field fld = FieldUtils.getDeclaredField( ITWebBootstrap.class, "clientProperty", true );
		TypeDescriptor target = new TypeDescriptor( fld );

		assertSame( client, mvcConversionService.convert( client, TypeDescriptor.valueOf( Client.class ), target ) );
	}

	@Test
	public void linksShouldBeRegistered() {
		EntityLinkBuilder clientLinkBuilder = entityRegistry.getEntityConfiguration( Client.class ).getAttribute( EntityLinkBuilder.class );
		EntityLinkBuilder companyLinkBuilder = entityRegistry.getEntityConfiguration( Company.class ).getAttribute( EntityLinkBuilder.class );
		EntityLinkBuilder associatedLinkBuilder = entityRegistry.getEntityConfiguration( Company.class )
		                                                        .association( "client.company" )
		                                                        .getAttribute( EntityLinkBuilder.class );

		assertEquals( "@adminWeb:/entities/client", entityViewLinks.linkTo( Client.class ).toString() );
		assertEquals( "/hidden/entities/client", entityViewLinks.linkTo( Client.class ).toUriString() );
		assertEquals( "/hidden/entities/client", clientLinkBuilder.overview() );

		Client client = new Client();
		client.setId( 55L );
		assertEquals( entityViewLinks.linkTo( client ).toUriString(), clientLinkBuilder.view( client ) );

		Company company = new Company();
		company.setId( "123" );

		assertEquals(
				"/hidden/entities/client/55?from=/hidden/entities/company/123/associations/client.company",
				entityViewLinks.linkTo( company ).association( client ).toUriString()
		);
		assertEquals(
				"/hidden/entities/client/55?from=/hidden/entities/company/123/associations/client.company",
				associatedLinkBuilder.asAssociationFor( companyLinkBuilder, company ).view( client )
		);
	}

	@Configuration
	@AcrossTestConfiguration(modules = { EntityModule.NAME, SpringSecurityModule.NAME, AdminWebModule.NAME })
	protected static class Config implements EntityConfigurer
	{
		@Bean
		public AcrossHibernateJpaModule acrossHibernateJpaModule() {
			AcrossHibernateJpaModule hibernateModule = new AcrossHibernateJpaModule();
			hibernateModule.setHibernateProperty( "hibernate.hbm2ddl.auto", "create-drop" );
			return hibernateModule;
		}

		@Bean
		public SpringDataJpaModule springDataJpaModule() {
			return new SpringDataJpaModule();
		}

		@Bean
		@SuppressWarnings("unchecked")
		public Function<Serializable, Client> mockClientFinder() {
			return mock( Function.class );
		}

		@Override
		public void configure( EntitiesConfigurationBuilder entities ) {
			entities.withType( Client.class )
			        .entityModel( model -> model.findOneMethod( mockClientFinder() ) );
		}
	}
}
