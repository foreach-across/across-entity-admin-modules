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

package com.foreach.across.modules.entity.views.helpers;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.web.support.LocalizedTextResolver;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContextHolder;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewElementBatch
{
	@Mock
	private EntityViewElementBuilderService builderService;

	@Mock
	private EntityPropertyRegistry propertyRegistry;

	@Mock
	private LocalizedTextResolver textResolver;

	private EntityViewElementBatch<?> batch;

	@Before
	public void before() {
		batch = new EntityViewElementBatch( builderService );
		batch.setPropertyRegistry( propertyRegistry );

		EntityPropertyDescriptor descriptor = mock( EntityPropertyDescriptor.class );
		when( descriptor.getName() ).thenReturn( "my property" );
		when( propertyRegistry.select( any() ) ).thenReturn( Collections.singletonList( descriptor ) );

		batch.setBuilderHints( Collections.singletonMap(
				"my property",
				(ViewElementBuilder<TextViewElement>) builderContext ->
						TextViewElement.text(
								builderContext.resolveText( builderContext.getAttribute( "parent.code", String.class ) )
										+ ":" +
										builderContext.resolveText( builderContext.getAttribute( "override.code", String.class ) )
										+ ":" +
										builderContext.resolveText( builderContext.getAttribute( "child.code", String.class ) )
						)
		) );
	}

	@Test
	public void noParentAndNoGlobalContextHasDefaultTextResolving() {
		batch.setAttribute( "parent.code", "batch" );
		batch.setAttribute( "override.code", "batch" );
		batch.setAttribute( "child.code", "batch" );
		assertThat( batch.resolveText( "#{code}" ) ).isEqualTo( "code" );

		assertText( "batch:batch:batch", batch.build() );
	}

	@Test
	public void globalContextValuesAreUsedIfAvailable() {
		DefaultViewElementBuilderContext global = new DefaultViewElementBuilderContext();
		global.setAttribute( "parent.code", "global" );
		global.setAttribute( "override.code", "global" );
		global.setAttribute( LocalizedTextResolver.class, textResolver );

		batch.setAttribute( "override.code", "batch" );
		batch.setAttribute( "child.code", "batch" );

		when( textResolver.resolveText( "global" ) ).thenReturn( "from-global" );
		when( textResolver.resolveText( "batch" ) ).thenReturn( "from-batch" );

		try {
			ViewElementBuilderContextHolder.setViewElementBuilderContext( global );

			assertThat( batch.resolveText( "#{code}" ) ).isEqualTo( "code" );
			assertText( "from-global:from-batch:from-batch", batch.build() );
		}
		finally {
			ViewElementBuilderContextHolder.clearViewElementBuilderContext();
		}
	}

	@Test
	public void parentContextSetOnBatch() {
		DefaultViewElementBuilderContext parent = new DefaultViewElementBuilderContext();
		parent.setAttribute( "parent.code", "parent" );
		parent.setAttribute( "override.code", "parent" );
		parent.setAttribute( LocalizedTextResolver.class, textResolver );

		batch.setAttribute( "override.code", "batch" );
		batch.setAttribute( "child.code", "batch" );
		batch.setParentViewElementBuilderContext( parent );

		when( textResolver.resolveText( "parent" ) ).thenReturn( "from-parent" );
		when( textResolver.resolveText( "batch" ) ).thenReturn( "from-batch" );

		assertThat( batch.resolveText( "batch" ) ).isEqualTo( "from-batch" );
		assertText( "from-parent:from-batch:from-batch", batch.build() );
	}

	@Test
	public void parentContextSetWhenBuildingIgnoresTheOneSetOnBatch() {
		DefaultViewElementBuilderContext parent = new DefaultViewElementBuilderContext();
		parent.setAttribute( "parent.code", "parent" );
		parent.setAttribute( "override.code", "parent" );
		parent.setAttribute( LocalizedTextResolver.class, textResolver );

		DefaultViewElementBuilderContext current = new DefaultViewElementBuilderContext();
		current.setAttribute( "parent.code", "current" );
		current.setAttribute( "override.code", "current" );
		current.setAttribute( LocalizedTextResolver.class, textResolver );

		batch.setAttribute( "override.code", "batch" );
		batch.setAttribute( "child.code", "batch" );
		batch.setParentViewElementBuilderContext( parent );

		when( textResolver.resolveText( "current" ) ).thenReturn( "from-current" );
		when( textResolver.resolveText( "batch" ) ).thenReturn( "from-batch" );

		assertThat( batch.resolveText( "batch" ) ).isEqualTo( "from-batch" );
		assertText( "from-current:from-batch:from-batch", batch.build( current ) );
	}

	private void assertText( String expected, Map<String, ViewElement> elements ) {
		TextViewElement text = (TextViewElement) elements.get( "my property" );
		assertThat( text ).isNotNull();
		assertThat( text.getText() ).isEqualTo( expected );
	}
}
