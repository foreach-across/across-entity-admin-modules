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

package com.foreach.across.modules.entity.views;

import com.foreach.across.config.EnableAcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.web.EntityLinkBuilder;
import com.foreach.across.test.AcrossWebAppConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ModelMap;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 */
@RunWith(SpringJUnit4ClassRunner.class)
@AcrossWebAppConfiguration
public class TestEntityDeleteViewFactory
{
	@Autowired
	private EntityDeleteViewFactory<ViewCreationContext> deleteViewFactory;

	@Test
	public void createSimpleDeleteView() {
		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );

		ViewCreationContext creationContext = mock( ViewCreationContext.class );
		when( creationContext.getEntityConfiguration() ).thenReturn( entityConfiguration );

		ModelMap model = new ModelMap();
		deleteViewFactory.prepareModelAndCommand( "deleteView", creationContext, mock( EntityViewCommand.class ),
		                                          model );
		EntityView view = deleteViewFactory.create( "deleteView", creationContext, model );

		assertNotNull( view );
	}

	@Configuration
	@EnableAcrossContext(modules = BootstrapUiModule.NAME)
	protected static class Config
	{
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
