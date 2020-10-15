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

package com.foreach.across.modules.entity.views.context;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.function.Consumer;

import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class TestEntityViewContextLoader
{
	@Mock
	private EntityRegistry entityRegistry;

	@Mock
	private EntityConfiguration entityConfiguration;

	@Mock
	private ConfigurableEntityViewContext context;

	@InjectMocks
	private EntityViewContextLoader loader;

	@Test
	public void nullEntityIsNotAllowed() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			loader.loadForEntity( context, null );
		} );
	}

	@Test
	public void nullEntityNameIsNotAllowed() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			loader.loadForEntityConfiguration( context, (String) null );
		} );
	}

	@Test
	public void nullEntityConfigurationIsNotAllowed() {
		Assertions.assertThrows( IllegalArgumentException.class, () -> {
			loader.loadForEntityConfiguration( context, (EntityConfiguration<?>) null );
		} );
	}

	@Test
	public void loadingEntityName() {
		mockAndLoad( ctx -> loader.loadForEntityConfiguration( ctx, "entityName" ) );
	}

	@Test
	public void loadingEntityConfiguration() {
		mockAndLoad( ctx -> loader.loadForEntityConfiguration( ctx, entityConfiguration ) );
	}

	@Test
	public void loadingEntity() {
		when( entityRegistry.getEntityConfiguration( 123L ) ).thenReturn( entityConfiguration );

		mockAndLoad( ctx -> loader.loadForEntity( ctx, 123L ) );
	}

	private void mockAndLoad( Consumer<ConfigurableEntityViewContext> caller ) {
		EntityMessageCodeResolver messageCodeResolver = mock( EntityMessageCodeResolver.class );
		EntityViewLinkBuilder linkBuilder = mock( EntityViewLinkBuilder.class );
		EntityModel entityModel = mock( EntityModel.class );
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );

		when( entityRegistry.getEntityConfiguration( "entityName" ) ).thenReturn( entityConfiguration );
		when( entityConfiguration.getEntityModel() ).thenReturn( entityModel );
		when( entityConfiguration.getEntityMessageCodeResolver() ).thenReturn( messageCodeResolver );
		when( entityConfiguration.getAttribute( EntityViewLinkBuilder.class ) ).thenReturn( linkBuilder );
		when( entityConfiguration.getPropertyRegistry() ).thenReturn( propertyRegistry );

		caller.accept( context );

		verify( context ).setEntityConfiguration( entityConfiguration );
		verify( context ).setEntityModel( entityModel );
		verify( context ).setLinkBuilder( linkBuilder );
		verify( context ).setMessageCodeResolver( messageCodeResolver );
		verify( context ).setEntityMessages( any( EntityMessages.class ) );
		verify( context ).setPropertyRegistry( propertyRegistry );
	}
}
