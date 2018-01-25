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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SimpleEntityViewProcessorAdapter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.Ordered;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityViewProcessorRegistry
{
	private EntityViewProcessorRegistry registry;

	@Mock
	private EntityViewProcessor one;

	@Mock
	private EntityViewProcessor two;

	@Mock
	private EntityViewProcessor three;

	@Mock
	private EntityViewProcessor four;

	@Before
	public void setUp() throws Exception {
		registry = new EntityViewProcessorRegistry();
	}

	@Test
	public void getProcessorsReturnsProcessorsInOrder() {
		registry.addProcessor( one );
		registry.addProcessor( "two", two );
		assertEquals( Arrays.asList( one, two ), registry.getProcessors() );

		registry.addProcessor( "three", three, 1 );
		assertEquals( Arrays.asList( three, one, two ), registry.getProcessors() );

		registry.addProcessor( "four", four, 1001 );
		assertEquals( Arrays.asList( three, one, two, four ), registry.getProcessors() );
	}

	@Test
	public void getProcessorNamesReturnsProcessorNamesInOrder() {
		registry.addProcessor( one );
		registry.addProcessor( "two", two );
		assertEquals( Arrays.asList( one.getClass().getName(), "two" ), registry.getProcessorNames() );

		registry.addProcessor( "three", three, 1 );
		assertEquals( Arrays.asList( "three", one.getClass().getName(), "two" ), registry.getProcessorNames() );

		registry.addProcessor( "four", four, 1001 );
		assertEquals( Arrays.asList( "three", one.getClass().getName(), "two", "four" ), registry.getProcessorNames() );
	}

	@Test
	public void getProcessorRegistrationAllowsChangingValues() {
		assertEquals( Optional.empty(), registry.getProcessorRegistration( "one" ) );

		registry.addProcessor( "one", one );
		registry.addProcessor( "two", two );
		registry.addProcessor( "three", three );
		assertEquals( Arrays.asList( "one", "two", "three" ), registry.getProcessorNames() );

		Optional<EntityViewProcessorRegistry.EntityViewProcessorRegistration> registration = registry.getProcessorRegistration( "one" );
		assertTrue( registration.isPresent() );

		registration.ifPresent(
				r -> {
					assertEquals( "one", r.getProcessorName() );
					assertSame( one, r.getProcessor() );
					assertEquals( EntityViewProcessorRegistry.DEFAULT_ORDER, r.getOrder() );

					r.setProcessor( two );
					r.setOrder( Ordered.LOWEST_PRECEDENCE );
				}
		);

		assertEquals( Arrays.asList( "two", "three", "one" ), registry.getProcessorNames() );
		assertEquals( Arrays.asList( two, three, two ), registry.getProcessors() );
	}

	@Test
	public void containsReturnsTrueIfProcessorWithNameIsPresent() {
		assertFalse( registry.contains( "one" ) );
		assertFalse( registry.contains( two.getClass().getName() ) );

		registry.addProcessor( "one", three );
		assertTrue( registry.contains( "one" ) );
		assertFalse( registry.contains( two.getClass().getName() ) );

		registry.addProcessor( two );
		assertTrue( registry.contains( "one" ) );
		assertTrue( registry.contains( two.getClass().getName() ) );
	}

	@Test(expected = IllegalArgumentException.class)
	public void addingProcessorWithTheSameNameThrowsException() {
		registry.addProcessor( "one", one );
		registry.addProcessor( "one", two );
	}

	@Test
	public void sameProcessorButWithDifferentNameIsAllowed() {
		registry.addProcessor( "one", one );
		registry.addProcessor( "two", one );
		assertEquals( Arrays.asList( one, one ), registry.getProcessors() );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void getProcessorByNameAndType() {
		assertEquals( Optional.empty(), registry.getProcessor( "one", EntityViewProcessor.class ) );

		registry.addProcessor( "one", one );
		Optional<EntityViewProcessor> processor = registry.getProcessor( "one", EntityViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertSame( one, p ) );

		processor = registry.getProcessor( "one", (Class<EntityViewProcessor>) one.getClass() );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertSame( one, p ) );
	}

	@Test(expected = ClassCastException.class)
	public void getProcessorByNameAndWrongTypeShouldThrowClassCastException() {
		registry.addProcessor( "one", one );
		registry.getProcessor( "one", EntityViewProcessorAdapter.class );
	}

	@Test
	public void removeOnRegistration() {
		registry.addProcessor( "one", one );
		assertTrue( registry.contains( "one" ) );
		registry.getProcessorRegistration( "one" ).ifPresent( EntityViewProcessorRegistry.EntityViewProcessorRegistration::remove );
		assertFalse( registry.contains( "one" ) );
	}

	@Test
	public void removeProcessorByName() {
		registry.addProcessor( "one", one );
		assertTrue( registry.contains( "one" ) );
		assertTrue( registry.remove( "one" ) );
		assertFalse( registry.contains( "one" ) );
		assertFalse( registry.remove( "one" ) );

	}

	@SuppressWarnings("unchecked")
	@Test
	public void dispatchToProcessorsShouldCallProcessorsInOrder() {
		registry.addProcessor( one );
		registry.addProcessor( "two", two );

		Consumer c = mock( Consumer.class );
		registry.dispatch( c );

		InOrder ordered = inOrder( c );
		ordered.verify( c ).accept( one );
		ordered.verify( c ).accept( two );
	}

	@SuppressWarnings("unchecked")
	@Test
	public void dispatchToSpecificTypeShouldOnlyCallMatchingProcessorsInOrder() {
		SimpleEntityViewProcessorAdapter adapter = mock( SimpleEntityViewProcessorAdapter.class );
		registry.addProcessor( one );
		registry.addProcessor( adapter );

		Consumer c = mock( Consumer.class );
		registry.dispatch( c, SimpleEntityViewProcessorAdapter.class );

		InOrder ordered = inOrder( c );
		ordered.verify( c ).accept( adapter );

		verify( c, never() ).accept( one );
	}
}
