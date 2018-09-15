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

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewElementBuilderHelper
{
	@Mock
	private EntityViewContext viewContext;

	@Mock
	private BeanFactory beanFactory;

	@InjectMocks
	private EntityViewElementBuilderHelper builderHelper;

	@Test(expected = IllegalArgumentException.class)
	public void nullEntityViewContext() {
		builderHelper.createSortableTableBuilder( (EntityViewContext) null );
	}

	@Test
	public void sortableTableForEntityViewContext() {
		SortableTableBuilder expected = mock( SortableTableBuilder.class );
		when( beanFactory.getBean( SortableTableBuilder.class ) ).thenReturn( expected );

		EntityConfiguration entityConfiguration = mock( EntityConfiguration.class );
		when( viewContext.getEntityConfiguration() ).thenReturn( entityConfiguration );
		EntityMessages entityMessages = mock( EntityMessages.class );
		when( viewContext.getEntityMessages() ).thenReturn( entityMessages );
		EntityPropertyRegistry propertyRegistry = mock( EntityPropertyRegistry.class );
		when( viewContext.getPropertyRegistry() ).thenReturn( propertyRegistry );

		SortableTableBuilder actual = builderHelper.createSortableTableBuilder( viewContext );
		assertSame( expected, actual );

		verify( actual ).entityConfiguration( entityConfiguration );
		verify( actual ).pagingMessages( entityMessages );
		verify( actual ).propertyRegistry( propertyRegistry );
	}
}
