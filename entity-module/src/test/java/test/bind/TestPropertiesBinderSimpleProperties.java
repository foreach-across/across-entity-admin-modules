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

package test.bind;

import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.util.*;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test property binding of a simple properties, manually defined in a property registry.
 * A simple property is one with only a single level for binding, eg a String, Object or Collection.
 * A complex property to bind would be a Collection of Collection type where you can bind members both
 * on the first and on the second level.
 *
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestPropertiesBinderSimpleProperties extends AbstractEntityPropertiesBinderTest
{
	@Override
	protected Collection<EntityPropertyDescriptorBuilder> registeredProperties() {
		return Arrays.asList(
				builder( "text" ).propertyType( String.class ),
				builder( "number" ).propertyType( Long.class ),
				builder( "dummyNonEmbedded" ).propertyType( Dummy.class ),
				builder( "dummyEmbedded" ).propertyType( Dummy.class )
				                          .controller( c -> c.withTarget( String.class, Dummy.class ).createValueSupplier( Dummy::new ) ),
				builder( "longArray" ).propertyType( long[].class ),
				builder( "longCollection" ).propertyType( TypeDescriptor.collection( Collection.class, TypeDescriptor.valueOf( Long.class ) ) ),
				builder( "dummyList" ).propertyType( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "dummyList[]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "stringLongMap" )
						.propertyType( TypeDescriptor.map( Map.class, TypeDescriptor.valueOf( String.class ), TypeDescriptor.valueOf( Long.class ) ) ),
				builder( "longDummyMap" )
						.propertyType( TypeDescriptor.map( TreeMap.class, TypeDescriptor.valueOf( Long.class ), TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "longDummyMap[value]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "dummyDummyMap" )
						.propertyType(
								TypeDescriptor.map( LinkedHashMap.class, TypeDescriptor.valueOf( Dummy.class ), TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "dummyDummyMap[key]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "dummyDummyMap[value]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) )
		);
	}

	@Test
	public void simpleTypeProperty() {
		bind(
				"properties[text].value=hello",
				"properties[number].value=15"
		);

		assertProperty( "text" ).isEqualTo( "hello" );
		assertProperty( "number" ).isEqualTo( 15L );
	}

	@Test
	public void customTypeNonEmbeddedProperty() {
		bind( "properties[dummyNonEmbedded].value=2" );
		assertProperty( "dummyNonEmbedded" ).isEqualTo( new Dummy( 2, "2" ) );

		bind( "properties[dummyNonEmbedded].value=3",
		      "properties[dummyNonEmbedded].value.id=4",
		      "properties[dummyNonEmbedded].value.name=Updated name" );
		assertProperty( "dummyNonEmbedded" ).isEqualTo( new Dummy( 4, "Updated name" ) );

		// if a property has been bound, but no values set, it should be reset to null
		resetForBinding();

		bind( "properties[dummyNonEmbedded].bound=1" );
		assertProperty( "dummyNonEmbedded" ).isNull();
	}

	@Test
	public void customTypeEmbeddedProperty() {
		bind( "properties[dummyEmbedded].initializedValue.id=5",
		      "properties[dummyEmbedded].initializedValue.name=Embedded value" );

		assertProperty( "dummyEmbedded" ).isEqualTo( new Dummy( 5, "Embedded value" ) );
	}

	@Test
	public void customTypeEmbeddedPropertyUsingChildBinder() {
		bind( "properties[dummyEmbedded].properties[id].value=4",
		      "properties[dummyEmbedded].properties[name].value=Updated name" );
		assertProperty( "dummyEmbedded" ).isEqualTo( new Dummy( 4, "Updated name" ) );
	}

	@Test
	public void directCollectionProperties() {
		bind( "properties[longArray].value=5,3",
		      "properties[longCollection].value=1,2",
		      "properties[dummyList].value=10,20" );

		assertProperty( "longArray" ).isEqualTo( new long[] { 5L, 3L } );

		assertCollection( "longCollection" )
				.containsExactly( 1L, 2L )
				.isInstanceOf( LinkedHashSet.class );

		assertCollection( "dummyList" )
				.containsExactly( new Dummy( 10, "10" ), new Dummy( 20, "20" ) )
				.isInstanceOf( ArrayList.class );

		bind( "properties[longArray].value=",
		      "properties[longCollection].value=",
		      "properties[dummyList].value=" );

		assertProperty( "longArray" ).isEqualTo( new long[0] );

		assertCollection( "longCollection" )
				.isInstanceOf( LinkedHashSet.class )
				.isEmpty();

		assertCollection( "dummyList" )
				.isInstanceOf( ArrayList.class )
				.isEmpty();
	}

	@Test
	public void simpleTypeEmbeddedListProperty() {
		bind( "properties[longCollection].items[2].value=3",
		      "properties[longCollection].items[1].value=4",
		      "properties[longCollection].items[3].value=" );

		assertCollection( "longCollection" )
				.containsExactly( 4L, 3L, null )
				.isInstanceOf( LinkedHashSet.class );

		// bound can be set safely if tracking is not reset
		bind( "properties[longCollection].bound=1" );
		assertCollection( "longCollection" )
				.containsExactly( 4L, 3L, null )
				.isInstanceOf( LinkedHashSet.class );

		resetForBinding();
		bind( "properties[longCollection].bound=1" );

		assertCollection( "longCollection" )
				.isInstanceOf( LinkedHashSet.class )
				.isEmpty();

		// explicit sorting
		resetForBinding();
		bind( "properties[longCollection].bound=1",
		      "properties[longCollection].items[2].value=3",
		      "properties[longCollection].items[2].sortIndex=1000",
		      "properties[longCollection].items[1].value=4",
		      "properties[longCollection].items[1].sortIndex=2000" );

		assertCollection( "longCollection" )
				.containsExactly( 3L, 4L )
				.isInstanceOf( LinkedHashSet.class );

		// clear the collection
		bind( "properties[longCollection].value=" );
		assertCollection( "longCollection" )
				.isInstanceOf( LinkedHashSet.class )
				.isEmpty();
	}

	@Test
	public void listWithCustomType() {
		bind( "properties[dummyList].bound=1",
		      "properties[dummyList].items[1].initializedValue.id=10",
		      "properties[dummyList].items[1].initializedValue.name=Hello",
		      "properties[dummyList].items[1].sortIndex=2000",
		      "properties[dummyList].items[2].initializedValue.id=20",
		      "properties[dummyList].items[2].initializedValue.name=Olleh",
		      "properties[dummyList].items[2].sortIndex=1000",
		      "properties[dummyList].items[3].value=",
		      "properties[dummyList].items[3].sortIndex=1500" );

		assertCollection( "dummyList" )
				.containsExactly( new Dummy( 20, "Olleh" ), null, new Dummy( 10, "Hello" ) )
				.isInstanceOf( ArrayList.class );
	}

	@Test
	public void listWithCustomTypeUsingChildBinder() {
		bind( "properties[dummyList].bound=1",
		      "properties[dummyList].items[1].properties[id].value=10",
		      "properties[dummyList].items[1].properties[name].value=Hello",
		      "properties[dummyList].items[1].sortIndex=2000",
		      "properties[dummyList].items[2].properties[id].value=20",
		      "properties[dummyList].items[2].properties[name].value=Olleh",
		      "properties[dummyList].items[2].sortIndex=1000" );

		assertCollection( "dummyList" )
				.containsExactly( new Dummy( 20, "Olleh" ), new Dummy( 10, "Hello" ) )
				.isInstanceOf( ArrayList.class );
	}

	@Test
	public void mapWithSimpleKeyAndSimpleValueType() {
		bind( "properties[stringLongMap].entries[x].value.value=1",
		      "properties[stringLongMap].entries[y].key.value=hello",
		      "properties[stringLongMap].entries[y].value.value=2",
		      "properties[stringLongMap].entries[z].key.value=olleh",
		      "properties[stringLongMap].entries[z].value.value=",
		      "properties[stringLongMap].entries[z].sortIndex=-1" );

		assertMap( "stringLongMap" )
				.hasSize( 3 )
				.containsEntry( "x", 1L )
				.containsEntry( "hello", 2L )
				.containsEntry( "olleh", null )
				.satisfies( map -> assertThat( map.keySet() ).containsExactly( "olleh", "x", "hello" ) )
				.isInstanceOf( LinkedHashMap.class );
	}

	@Test
	public void mapWithSimpleKeyAndCustomValueType() {
		bind( "properties[longDummyMap].entries[x].key.value=1",
		      "properties[longDummyMap].entries[x].value.value=1",
		      "properties[longDummyMap].entries[y].key.value=-1",
		      "properties[longDummyMap].entries[y].value.value=",
		      "properties[longDummyMap].entries[z].key.value=2",
		      "properties[longDummyMap].entries[z].value.initializedValue.id=5",
		      "properties[longDummyMap].entries[z].value.initializedValue.name=Hello" );

		assertMap( "longDummyMap" )
				.hasSize( 3 )
				.containsEntry( 1L, new Dummy( 1, "1" ) )
				.containsEntry( -1L, null )
				.containsEntry( 2L, new Dummy( 5, "Hello" ) )
				.satisfies( map -> assertThat( map.keySet() ).containsExactly( -1L, 1L, 2L ) )
				.isInstanceOf( TreeMap.class );
	}

	@Test
	public void mapWithCustomKeyAndCustomValueType() {
		bind( "properties[dummyDummyMap].entries[x].key.value=1",
		      "properties[dummyDummyMap].entries[x].value.value=1",
		      "properties[dummyDummyMap].entries[y].key.initializedValue.id=55",
		      "properties[dummyDummyMap].entries[y].value.value=",
		      "properties[dummyDummyMap].entries[z].key.initializedValue.id=33",
		      "properties[dummyDummyMap].entries[z].key.initializedValue.name=Olleh",
		      "properties[dummyDummyMap].entries[z].value.initializedValue.id=5",
		      "properties[dummyDummyMap].entries[z].value.initializedValue.name=Hello" );

		assertMap( "dummyDummyMap" )
				.hasSize( 3 )
				.containsEntry( new Dummy( 1, "1" ), new Dummy( 1, "1" ) )
				.containsEntry( new Dummy( 55, null ), null )
				.containsEntry( new Dummy( 33, "Olleh" ), new Dummy( 5, "Hello" ) )
				.satisfies(
						map -> assertThat( map.keySet() )
								.containsExactly( new Dummy( 1, "1" ), new Dummy( 55, null ), new Dummy( 33, "Olleh" ) )
				)
				.isInstanceOf( LinkedHashMap.class );
	}

	@Test
	public void mapWithCustomKeyAndCustomValueTypeUsingChildBinder() {
		bind( "properties[dummyDummyMap].entries[y].key.properties[id].value=55",
		      "properties[dummyDummyMap].entries[y].value.value=",
		      "properties[dummyDummyMap].entries[z].key.properties[id].value=33",
		      "properties[dummyDummyMap].entries[z].key.properties[name].value=Olleh",
		      "properties[dummyDummyMap].entries[z].value.properties[id].value=5",
		      "properties[dummyDummyMap].entries[z].value.properties[name].value=Hello" );

		assertMap( "dummyDummyMap" )
				.hasSize( 2 )
				.containsEntry( new Dummy( 55, null ), null )
				.containsEntry( new Dummy( 33, "Olleh" ), new Dummy( 5, "Hello" ) )
				.satisfies(
						map -> assertThat( map.keySet() )
								.containsExactly( new Dummy( 55, null ), new Dummy( 33, "Olleh" ) )
				)
				.isInstanceOf( LinkedHashMap.class );
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Dummy
	{
		private int id;
		private String name;

		// Used by ConversionService
		@SuppressWarnings("unused")
		public static Dummy from( String name ) {
			Dummy dummy = new Dummy();
			dummy.setName( name );
			dummy.setId( Integer.parseInt( name ) );
			return dummy;
		}
	}
}
