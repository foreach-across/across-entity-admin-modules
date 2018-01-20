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

package com.foreach.across.modules.entity.views.menu;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.context.DefaultEntityViewContext;
import com.foreach.across.modules.web.events.BuildMenuEvent;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import lombok.val;
import org.junit.Test;
import org.springframework.core.ResolvableType;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.core.ResolvableType.forClass;
import static org.springframework.core.ResolvableType.forClassWithGenerics;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestEntityAdminMenuEvent
{
	@Test
	public void resolvableTypeMatchesWithDeprecatedClasses() {
		val event = event( ResolvableType.forClass( ArrayList.class ) );

		val eventType = event.getResolvableType();
		assertTrue( forClassWithGenerics( EntityAdminMenuEvent.class, ArrayList.class ).isAssignableFrom( eventType ) );
		assertTrue( forClass( EntityAdminMenuEvent.class ).isAssignableFrom( eventType ) );
		assertTrue( forClass( com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent.class ).isAssignableFrom( eventType ) );
		assertTrue(
				forClassWithGenerics( com.foreach.across.modules.adminweb.menu.EntityAdminMenuEvent.class, ArrayList.class ).isAssignableFrom( eventType )
		);

		assertTrue(
				forClassWithGenerics( BuildMenuEvent.class,
				                      forClassWithGenerics( com.foreach.across.modules.adminweb.menu.EntityAdminMenu.class, ArrayList.class ) )
						.isAssignableFrom( eventType )
		);

		// This currently does not match because of the typing in the old classes
		assertFalse(
				forClassWithGenerics( BuildMenuEvent.class, forClassWithGenerics( EntityAdminMenu.class, ArrayList.class ) ).isAssignableFrom( eventType )
		);
	}

	@SuppressWarnings("all")
	private EntityAdminMenuEvent event( ResolvableType resolvableType ) {
		EntityConfiguration config = mock( EntityConfiguration.class );
		when( config.getEntityType() ).thenReturn( resolvableType.getRawClass() );
		DefaultEntityViewContext context = new DefaultEntityViewContext();
		context.setEntityConfiguration( config );

		EntityAdminMenu menu = new EntityAdminMenu( resolvableType.getRawClass(), "myEntity", context, null );
		return new EntityAdminMenuEvent( menu, mock( PathBasedMenuBuilder.class ) );
	}

}
