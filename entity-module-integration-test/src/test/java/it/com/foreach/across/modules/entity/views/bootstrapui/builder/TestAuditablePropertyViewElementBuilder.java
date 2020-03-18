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

package it.com.foreach.across.modules.entity.views.bootstrapui.builder;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.SimpleEntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.bootstrapui.elements.builder.AuditablePropertyViewElementBuilder;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolverStrategy;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@ContextConfiguration
public class TestAuditablePropertyViewElementBuilder extends AbstractViewElementTemplateTest
{
	private final Date dateCreated = new Date();
	private final Date dateLastModified = new Date( System.currentTimeMillis() + 1000 );

	private Entity entity;
	private AuditablePropertyViewElementBuilder builder;
	private DefaultViewElementBuilderContext builderContext;
	@Mock
	private ConversionService conversionService;

	@Before
	public void before() {
		builder = new AuditablePropertyViewElementBuilder();

		SecurityPrincipalLabelResolverStrategy resolverStrategy = mock( SecurityPrincipalLabelResolverStrategy.class );
		builder.setSecurityPrincipalLabelResolverStrategy( resolverStrategy );

		when( resolverStrategy.resolvePrincipalLabel( "admin" ) ).thenReturn( "Administrator" );
		when( resolverStrategy.resolvePrincipalLabel( "system" ) ).thenReturn( "System Machine" );

		when( conversionService.convert( dateCreated, String.class ) ).thenReturn( "creationDate" );
		when( conversionService.convert( dateLastModified, String.class ) ).thenReturn( "modificationDate" );

		builder.setConversionService( conversionService );

		entity = new Entity();
		entity.setCreatedBy( "admin" );
		entity.setCreatedDate( dateCreated );
		entity.setLastModifiedBy( "system" );
		entity.setLastModifiedDate( dateLastModified );

		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setAttribute( EntityViewModel.ENTITY, entity );
	}

	@Test
	public void creationDateWithPrincipal() {
		expect( "creationDate by Administrator" );
	}

	@Test
	public void modificationDateWithPrincipal() {
		builder.setForLastModifiedProperty( true );
		expect( "modificationDate by System Machine" );
	}

	@Test
	public void creationDateWithoutPrincipal() {
		entity.setCreatedBy( null );
		expect( "creationDate" );
	}

	@Test
	public void modificationDateWithoutPrincipal() {
		builder.setForLastModifiedProperty( true );
		entity.setLastModifiedBy( null );
		expect( "modificationDate" );
	}

	@Test
	public void resolvesForNestedProperty() {
		WrappedEntity wrapped = new WrappedEntity();
		wrapped.setChild( entity );
		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setAttribute( EntityViewModel.ENTITY, wrapped );

		configurePropertiesOnBuilderContext( wrapped );
		expect( "creationDate by Administrator" );
		builder.setForLastModifiedProperty( true );
		expect( "modificationDate by System Machine" );
	}

	@Test
	public void resolvesToAuditableOfRequestedPropertyWithFallbackToEntity() {
		WrappedAuditableEntity wrapped = new WrappedAuditableEntity();
		wrapped.setCreatedBy( "admin" );
		wrapped.setLastModifiedBy( "system" );
		Date dateCreated = new Date( System.currentTimeMillis() - 50000 );
		Date dateLastModified = new Date( System.currentTimeMillis() - 60000 );
		wrapped.setCreatedDate( dateCreated );
		wrapped.setLastModifiedDate( dateLastModified );
		when( conversionService.convert( dateCreated, String.class ) ).thenReturn( "creationDateForWrapper" );
		when( conversionService.convert( dateLastModified, String.class ) ).thenReturn( "modificationDateForWrapper" );

		wrapped.setChild( entity );
		builderContext = new DefaultViewElementBuilderContext();
		builderContext.setAttribute( EntityViewModel.ENTITY, wrapped );

		// falls back to entity
		expect( "creationDateForWrapper by Administrator" );
		builder.setForLastModifiedProperty( true );
		expect( "modificationDateForWrapper by System Machine" );

		SimpleEntityPropertyDescriptor propertyDescriptor = new SimpleEntityPropertyDescriptor( "lastModified" );
		builderContext.setAttribute( EntityPropertyDescriptor.class, propertyDescriptor );
		// falls back to entity as it is not a nested auditable
		builder.setForLastModifiedProperty( false );
		expect( "creationDateForWrapper by Administrator" );
		builder.setForLastModifiedProperty( true );
		expect( "modificationDateForWrapper by System Machine" );

		// uses nested auditable
		configurePropertiesOnBuilderContext( wrapped );
		builder.setForLastModifiedProperty( false );
		expect( "creationDate by Administrator" );
		builder.setForLastModifiedProperty( true );
		expect( "modificationDate by System Machine" );
	}

	private <T extends WrappedEntity> void configurePropertiesOnBuilderContext( T wrapped ) {
		SimpleEntityPropertyDescriptor parentDescriptor = new SimpleEntityPropertyDescriptor( "child" );
		parentDescriptor.setPropertyType( Entity.class );
		parentDescriptor.setController( new EntityPropertyController()
		{
			@Override
			public Object fetchValue( EntityPropertyBindingContext context ) {
				return wrapped.getChild();
			}
		} );
		SimpleEntityPropertyDescriptor propertyDescriptor = new SimpleEntityPropertyDescriptor( "lastModified" );
		propertyDescriptor.setParentDescriptor( parentDescriptor );
		builderContext.setAttribute( EntityPropertyDescriptor.class, propertyDescriptor );
	}

	@Test
	public void messageSourceIsUsed() {
		MessageSource messageSource = mock( MessageSource.class );
		when( messageSource.getMessage( "Auditable.created", new Object[] { dateCreated, "Administrator" }, "", LocaleContextHolder.getLocale() ) )
				.thenReturn( "created" );
		when( messageSource.getMessage( "Auditable.createdDate", new Object[] { dateCreated, null }, "", LocaleContextHolder.getLocale() ) )
				.thenReturn( "created-date" );
		when( messageSource.getMessage( "Auditable.lastModified", new Object[] { dateLastModified, "System Machine" }, "", LocaleContextHolder.getLocale() ) )
				.thenReturn( "last-modified" );
		when( messageSource.getMessage( "Auditable.lastModifiedDate", new Object[] { dateLastModified, null }, "", LocaleContextHolder.getLocale() ) )
				.thenReturn( "last-modified-date" );

		builder.setMessageSource( messageSource );
		expect( "created" );

		entity.setCreatedBy( null );
		expect( "created-date" );

		builder.setForLastModifiedProperty( true );
		expect( "last-modified" );

		entity.setLastModifiedBy( null );
		expect( "last-modified-date" );
	}

	private void expect( String output ) {
		renderAndExpect( builder.build( builderContext ), output );
	}

	@Getter
	@Setter
	static class WrappedEntity
	{
		private Entity child;
	}

	@Getter
	@Setter
	static class WrappedAuditableEntity extends WrappedEntity implements Auditable<String>
	{
		private Date createdDate, lastModifiedDate;
		private String createdBy, lastModifiedBy;
	}

	static class Entity implements Auditable<String>
	{
		private Date createdDate, lastModifiedDate;
		private String createdBy, lastModifiedBy;

		@Override
		public Date getCreatedDate() {
			return createdDate;
		}

		@Override
		public void setCreatedDate( Date createdDate ) {
			this.createdDate = createdDate;
		}

		@Override
		public Date getLastModifiedDate() {
			return lastModifiedDate;
		}

		@Override
		public void setLastModifiedDate( Date lastModifiedDate ) {
			this.lastModifiedDate = lastModifiedDate;
		}

		@Override
		public String getCreatedBy() {
			return createdBy;
		}

		@Override
		public void setCreatedBy( String createdBy ) {
			this.createdBy = createdBy;
		}

		@Override
		public String getLastModifiedBy() {
			return lastModifiedBy;
		}

		@Override
		public void setLastModifiedBy( String lastModifiedBy ) {
			this.lastModifiedBy = lastModifiedBy;
		}
	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
		}
	}
}