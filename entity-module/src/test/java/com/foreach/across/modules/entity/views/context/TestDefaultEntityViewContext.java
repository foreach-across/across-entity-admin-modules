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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.spring.security.actions.AllowableActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestDefaultEntityViewContext
{
	private ConfigurableEntityViewContext ctx;

	@BeforeEach
	public void setUp() throws Exception {
		ctx = new DefaultEntityViewContext();
	}

	@Test
	public void entityIsNull() {
		assertFalse( ctx.holdsEntity() );
		assertNull( ctx.getEntity( String.class ) );
		assertNull( ctx.getEntity( Long.class ) );
	}

	@Test
	public void wrongEntityType() {
		assertThrows( ClassCastException.class, () -> {
			ctx.setEntity( "test" );
			ctx.getEntity( Long.class );
		} );
	}

	@Test
	public void holdsEntityIfEntityNotNull() {
		assertFalse( ctx.holdsEntity() );
		ctx.setEntity( 123L );
		assertTrue( ctx.holdsEntity() );
		ctx.setEntity( null );
		assertFalse( ctx.holdsEntity() );
	}

	@Test
	public void isForAssociation() {
		assertFalse( ctx.isForAssociation() );
		ctx.setEntityAssociation( mock( EntityAssociation.class ) );
		assertTrue( ctx.isForAssociation() );
	}

	@Test
	public void entityLabelIsNullIfNoEntityOrEntityConfiguration() {
		assertNull( ctx.getEntityLabel() );
		ctx.setEntity( "test" );
		assertNull( ctx.getEntityLabel() );
		ctx.setEntity( null );
		EntityConfiguration<?> entityConfiguration = mock( EntityConfiguration.class );
		ctx.setEntityConfiguration( entityConfiguration );
		assertNull( ctx.getEntityLabel() );
		verify( entityConfiguration, never() ).getLabel( any() );
	}

	@Test
	public void entityLabelIsCachedUntilEitherEntityOrConfigurationChanges() {
		EntityConfiguration<?> entityConfiguration = mock( EntityConfiguration.class );
		ctx.setEntity( "one" );
		ctx.setEntityConfiguration( entityConfiguration );
		when( entityConfiguration.getLabel( any() ) ).thenReturn( "label" );

		assertEquals( "label", ctx.getEntityLabel() );
		assertEquals( "label", ctx.getEntityLabel() );
		verify( entityConfiguration, times( 1 ) ).getLabel( any() );

		ctx.setEntity( "two" );
		assertEquals( "label", ctx.getEntityLabel() );
		verify( entityConfiguration, times( 2 ) ).getLabel( any() );

		ctx.setEntityConfiguration( entityConfiguration );
		assertEquals( "label", ctx.getEntityLabel() );
		verify( entityConfiguration, times( 3 ) ).getLabel( any() );
	}

	@Test
	public void allowableActionsIsNullIfNoEntityConfiguration() {
		assertNull( ctx.getAllowableActions() );
		ctx.setEntity( 123L );
		assertNull( ctx.getAllowableActions() );
	}

	@Test
	public void defaultAllowableActionsAreFetchedFromEntityConfiguration() {
		EntityConfiguration<?> entityConfiguration = mock( EntityConfiguration.class );
		AllowableActions global = mock( AllowableActions.class );
		when( entityConfiguration.getAllowableActions() ).thenReturn( global );
		AllowableActions forEntity = mock( AllowableActions.class );
		when( entityConfiguration.getAllowableActions( any() ) ).thenReturn( forEntity );
		ctx.setEntityConfiguration( entityConfiguration );

		assertSame( global, ctx.getAllowableActions() );
		assertSame( global, ctx.getAllowableActions() );
		verify( entityConfiguration, times( 1 ) ).getAllowableActions();
		verify( entityConfiguration, never() ).getAllowableActions( any() );

		ctx.setEntity( 123L );
		assertSame( forEntity, ctx.getAllowableActions() );
		assertSame( forEntity, ctx.getAllowableActions() );
		verify( entityConfiguration, times( 1 ) ).getAllowableActions();
		verify( entityConfiguration, times( 1 ) ).getAllowableActions( any() );

		ctx.setEntityConfiguration( null );
		assertNull( ctx.getAllowableActions() );
	}

	@Test
	public void fixedAllowableActionsAlwaysRemain() {
		AllowableActions fixed = mock( AllowableActions.class );
		EntityConfiguration<?> entityConfiguration = mock( EntityConfiguration.class );
		when( entityConfiguration.getAllowableActions() ).thenReturn( mock( AllowableActions.class ) );
		when( entityConfiguration.getAllowableActions( any() ) ).thenReturn( mock( AllowableActions.class ) );
		ctx.setEntityConfiguration( entityConfiguration );
		ctx.setAllowableActions( fixed );

		assertSame( fixed, ctx.getAllowableActions() );
		verify( entityConfiguration, never() ).getAllowableActions();
		verify( entityConfiguration, never() ).getAllowableActions( any() );

		ctx.setEntity( 123L );
		assertSame( fixed, ctx.getAllowableActions() );
		verify( entityConfiguration, never() ).getAllowableActions();
		verify( entityConfiguration, never() ).getAllowableActions( any() );

		ctx.setEntityConfiguration( null );
		assertSame( fixed, ctx.getAllowableActions() );
		verify( entityConfiguration, never() ).getAllowableActions();
		verify( entityConfiguration, never() ).getAllowableActions( any() );

		AllowableActions otherFixed = mock( AllowableActions.class );
		ctx.setAllowableActions( otherFixed );
		assertSame( otherFixed, ctx.getAllowableActions() );

		ctx.setAllowableActions( null );
		ctx.setEntityConfiguration( entityConfiguration );
		assertNotNull( ctx.getAllowableActions() );
		assertNotSame( otherFixed, ctx.getAllowableActions() );
	}

}
