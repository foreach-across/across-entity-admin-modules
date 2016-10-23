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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.views.EntityViewFactory;
import com.foreach.across.modules.entity.views.EntityViewViewFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityViewFactoryBuilder
{
	private EntityViewFactoryBuilder<EntityViewFactory> builder;
	private EntityViewFactory factory;

	private AutowireCapableBeanFactory beanFactory;

	@Before
	public void before() {
		beanFactory = mock( AutowireCapableBeanFactory.class );
		builder = new EntityViewFactoryBuilder<>( beanFactory );
	}

	@Test(expected = IllegalArgumentException.class)
	public void buildRequiresAFactoryToBeSet() {
		build();
	}

	@Test
	public void defaultCreatesSingleEntityViewFactory() {
		EntityViewViewFactory f = new EntityViewViewFactory();
		when( beanFactory.createBean( EntityViewViewFactory.class ) ).thenReturn( f );

		builder.factory( EntityViewViewFactory.class );

		build();
		assertSame( f, factory );
	}

	@Test
	public void simpleEntityViewBuilder() {
		EntityViewViewFactory f = new EntityViewViewFactory();
		when( beanFactory.createBean( EntityViewViewFactory.class ) ).thenReturn( f );

		builder.factory( EntityViewViewFactory.class )
		       .template( "templateName" );
		build();

		assertEquals( "templateName", f.getTemplate() );
	}

	private void build() {
		factory = builder.build();
	}

}
