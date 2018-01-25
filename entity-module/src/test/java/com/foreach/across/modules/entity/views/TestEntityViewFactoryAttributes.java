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

import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.foreach.across.modules.entity.views.EntityViewFactoryAttributes.defaultAccessValidator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewFactoryAttributes
{
	@Mock
	private EntityViewFactory viewFactory;

	@Mock
	private EntityViewContext viewContext;

	@Mock
	private AllowableActions allowableActions;

	@Test
	public void defaultAccessIsTrueIfThereIsNoAllowableAction() {
		assertThat( defaultAccessValidator().test( viewFactory, null ) ).isTrue();
	}

	@Test
	public void defaultAccessIsFalseIfAllowableActionDoesNotMatch() {
		when( viewFactory.getAttribute( AllowableAction.class ) ).thenReturn( AllowableAction.UPDATE );
		when( viewContext.getAllowableActions() ).thenReturn( allowableActions );
		assertThat( defaultAccessValidator().test( viewFactory, viewContext ) ).isFalse();
		verify( allowableActions ).contains( AllowableAction.UPDATE );
	}

	@Test
	public void defaultAccessIsFalseIfNoViewContextAndAllowableAction() {
		when( viewFactory.getAttribute( AllowableAction.class ) ).thenReturn( AllowableAction.UPDATE );
		assertThat( defaultAccessValidator().test( viewFactory, null ) ).isFalse();
	}

	@Test
	public void defaultAccessIsTrueIfAllowableActionMatches() {
		when( viewFactory.getAttribute( AllowableAction.class ) ).thenReturn( AllowableAction.UPDATE );
		when( allowableActions.contains( AllowableAction.UPDATE ) ).thenReturn( true );
		when( viewContext.getAllowableActions() ).thenReturn( allowableActions );
		assertThat( defaultAccessValidator().test( viewFactory, viewContext ) ).isTrue();
	}
}
