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

package it.com.foreach.across.modules.entity.views.bootstrapui.options;

import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.views.bootstrapui.options.EntityQueryOptionIterableBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestEntityQueryOptionIterableBuilder
{
	private final Entity ONE = new Entity( "one" );
	private final Entity TWO = new Entity( "two" );
	private final Entity THREE = new Entity( "three" );

	private EntityQueryOptionIterableBuilder iterableBuilder;
	private ViewElementBuilderContext elementBuilderContext;
	private EntityQueryExecutor entityQueryExecutor;
	private EntityQueryParser entityQueryParser;

	private Map<String, OptionFormElementBuilder> options = new HashMap<>();

	@Before
	@SuppressWarnings("unchecked")
	public void before() {
		EntityModel entityModel = mock( EntityModel.class );
		entityQueryExecutor = mock( EntityQueryExecutor.class );
		entityQueryParser = mock( EntityQueryParser.class );

		iterableBuilder = new EntityQueryOptionIterableBuilder();
		iterableBuilder.setEntityModel( entityModel );
		iterableBuilder.setEntityQueryExecutor( entityQueryExecutor );
		iterableBuilder.setEntityQueryParser( entityQueryParser );

		elementBuilderContext = new DefaultViewElementBuilderContext();

		when( entityQueryExecutor.findAll( any( EntityQuery.class ) ) ).thenReturn( Arrays.asList( ONE, TWO, THREE ) );

		when( entityModel.getLabel( anyObject() ) )
				.thenAnswer( invocation -> ( (Entity) invocation.getArguments()[0] ).name );

		when( entityModel.getId( anyObject() ) )
				.thenAnswer( invocation -> StringUtils.upperCase( ( (Entity) invocation.getArguments()[0] ).name ) );

		options.clear();
	}

	@Test
	public void defaultOptions() {
		build();
		assertOptions( ONE, TWO, THREE );
	}

	@Test
	public void sortedIfSortPredicateOnEQL() {
		assertFalse( iterableBuilder.isSorted() );

		iterableBuilder.setEntityQuery( "order by name desc" );
		assertTrue( iterableBuilder.isSorted() );

		iterableBuilder.setEntityQuery( "id > 10 order by id asc, name desc" );
		assertTrue( iterableBuilder.isSorted() );

		iterableBuilder.setEntityQuery( "id > 10" );
		assertFalse( iterableBuilder.isSorted() );
	}

	@Test
	public void customEntityQuery() {
		EntityQuery query = EntityQuery.or( new EntityQueryCondition( "name", EntityQueryOps.EQ, "test" ) );
		iterableBuilder.setEntityQuery( query );

		when( entityQueryParser.prepare( query ) ).thenReturn( query );

		build();

		verify( entityQueryExecutor ).findAll( query );
	}

	@Test
	public void customEntityQueryAsEQL() {
		iterableBuilder.setEntityQuery( "deleted = 1" );

		EntityQuery query = mock( EntityQuery.class );
		when( entityQueryParser.prepare( EntityQuery.parse( "deleted = 1" ) ) ).thenReturn( query );

		build();

		verify( entityQueryExecutor ).findAll( query );
	}

	private void assertOptions( Entity... entities ) {
		for ( Entity entity : entities ) {
			assertFalse( options.get( entity.name ).isSelected() );
		}
	}

	private void build() {
		options.clear();

		Iterable<OptionFormElementBuilder> iterable = iterableBuilder.buildOptions( elementBuilderContext );

		List<OptionFormElementBuilder> optionsInOrder = new ArrayList<>( 3 );

		for ( OptionFormElementBuilder option : iterable ) {
			optionsInOrder.add( option );
			options.put( option.getLabel(), option );
		}

		assertEquals( 3, optionsInOrder.size() );

		assertEquals( ONE.name, optionsInOrder.get( 0 ).getLabel() );
		assertEquals( StringUtils.upperCase( ONE.name ), optionsInOrder.get( 0 ).getValue() );
		assertEquals( ONE, optionsInOrder.get( 0 ).getRawValue() );

		assertEquals( TWO.name, optionsInOrder.get( 1 ).getLabel() );
		assertEquals( StringUtils.upperCase( TWO.name ), optionsInOrder.get( 1 ).getValue() );
		assertEquals( TWO, optionsInOrder.get( 1 ).getRawValue() );

		assertEquals( THREE.name, optionsInOrder.get( 2 ).getLabel() );
		assertEquals( StringUtils.upperCase( THREE.name ), optionsInOrder.get( 2 ).getValue() );
		assertEquals( THREE, optionsInOrder.get( 2 ).getRawValue() );
	}

	static class Entity
	{
		private String name;

		public Entity( String name ) {
			this.name = name;
		}
	}
}
