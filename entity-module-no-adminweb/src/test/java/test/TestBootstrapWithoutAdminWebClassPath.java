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

package test;

import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.BootstrapUiElementTypeLookupStrategy;
import com.foreach.across.test.AcrossTestContext;
import com.foreach.across.testmodules.springdata.SpringDataJpaModule;
import com.foreach.across.testmodules.springdata.business.Group;
import com.foreach.across.testmodules.springdata.repositories.GroupRepository;
import org.junit.jupiter.api.Test;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.util.ClassUtils.isPresent;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestBootstrapWithoutAdminWebClassPath
{
	@Test
	public void classesShouldNotBeOnTheClassPath() {
		assertThat( isPresent( "com.foreach.across.modules.adminweb.AdminWebModule", currentThread().getContextClassLoader() ) ).isFalse();
	}

	@Test
	public void emptyBootstrap() {
		try (AcrossTestContext context = web( false )
				.modules( EntityModule.NAME, BootstrapUiModule.NAME )
				.build()) {
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, GenericEntityViewController.class ) ).isEmpty();
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, BootstrapUiElementTypeLookupStrategy.class ) ).isNotEmpty();
		}
	}

	@Test
	public void bootstrapWithDataModule() {
		try (AcrossTestContext context = web()
				.property( "acrossHibernate.hibernate.ddl-auto", "create-drop" )
				.modules( EntityModule.NAME, BootstrapUiModule.NAME )
				.modules( new SpringDataJpaModule() )
				.build()) {
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, GenericEntityViewController.class ) ).isEmpty();
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, BootstrapUiElementTypeLookupStrategy.class ) ).isNotEmpty();

			GroupRepository groupRepository = context.getBeanOfType( GroupRepository.class );
			Group group = new Group();
			group.setName( "my-group" );
			assertThat( group.isNew() ).isTrue();
			groupRepository.save( group );
			assertThat( group.isNew() ).isFalse();

			EntityRegistry entityRegistry = context.getBeanOfType( EntityRegistry.class );
			EntityConfiguration<Group> entityConfiguration = entityRegistry.getEntityConfiguration( group );
			Group other = entityConfiguration.getEntityModel().findOne( group.getId() );

			assertThat( other ).isEqualTo( group );
			assertThat( other.getName() ).isEqualTo( group.getName() );

			assertThat( entityConfiguration.getPropertyRegistry().getRegisteredDescriptors() ).isNotEmpty();
			assertThat( entityConfiguration.getViewNames() ).isEmpty();

			EntityViewElementBuilderService builderService = context.getBeanOfType( EntityViewElementBuilderService.class );
			assertThat(
					builderService.getElementBuilder( entityConfiguration.getPropertyRegistry().getProperty( "name" ), ViewElementMode.CONTROL )
			).isNotNull();
		}
	}
}
