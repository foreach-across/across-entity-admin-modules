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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.access.AccessDeniedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestActionAllowedAuthorizationViewProcessor
{
	@Mock
	private EntityViewRequest viewRequest;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private EntityConfiguration configuration;

	@Mock
	private EntityAssociation association;

	@Mock
	private AllowableActions allowableActions;

	private ActionAllowedAuthorizationViewProcessor processor;

	@Before
	public void setUp() throws Exception {
		when( viewRequest.getEntityViewContext() ).thenReturn( viewContext );
		when( viewContext.getEntityConfiguration() ).thenReturn( configuration );
		when( viewContext.getEntityAssociation() ).thenReturn( association );
		when( viewContext.getAllowableActions() ).thenReturn( allowableActions );

		when( association.getSourceEntityConfiguration() ).thenReturn( mock( EntityConfiguration.class ) );

		processor = new ActionAllowedAuthorizationViewProcessor();
	}

	@Test(expected = AccessDeniedException.class)
	public void hiddenEntityConfigurationThrowsAccessDenied() {
		when( configuration.isHidden() ).thenReturn( true );
		processor.authorizeRequest( viewRequest );
	}

	@Test(expected = AccessDeniedException.class)
	public void hiddenEntityAssociationThrowsAccessDenied() {
		when( viewContext.isForAssociation() ).thenReturn( true );
		when( association.isHidden() ).thenReturn( true );
		processor.authorizeRequest( viewRequest );
	}

	@Test(expected = AccessDeniedException.class)
	public void actionNotAllowedThrowsException() {
		processor.setRequiredAllowableAction( AllowableAction.CREATE );
		processor.authorizeRequest( viewRequest );
	}

	@Test
	public void visibleEntityConfigurationAndNoRequired() {
		processor.authorizeRequest( viewRequest );
	}

	@Test
	public void visibleEntityAssociationAndAllowedAction() {
		processor.setRequiredAllowableAction( AllowableAction.CREATE );
		when( allowableActions.contains( AllowableAction.CREATE ) ).thenReturn( true );

		processor.authorizeRequest( viewRequest );
	}
}
