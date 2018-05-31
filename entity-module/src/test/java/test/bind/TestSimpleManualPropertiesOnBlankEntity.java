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
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.api.AbstractObjectAssert;
import org.assertj.core.api.IterableAssert;
import org.assertj.core.api.MapAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;

import java.util.*;
import java.util.stream.Collectors;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test property binding of a simple properties, manually defined in a property registry.
 * A simple property is one with only a single level for binding, eg a String, Object or Collection.
 * A complex property to bind would be a Collection of Collection type where you can bind members both
 * on the first and on the second level.
 *
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class TestSimpleManualPropertiesOnBlankEntity
{
	private static final String ENTITY = "someEntity";

	private ConversionService conversionService = new DefaultConversionService();

	private DefaultEntityPropertyRegistry propertyRegistry;
	private EntityPropertiesBinder propertyValues;
	private DataBinder dataBinder;

	@Before
	public void before() {
		propertyRegistry = new DefaultEntityPropertyRegistry();
		registeredProperties().forEach( b -> propertyRegistry.register( b.build() ) );

		propertyValues = new EntityPropertiesBinder( propertyRegistry );
		propertyValues.setBinderPrefix( "properties" );
		propertyValues.setConversionService( conversionService );
		propertyValues.setEntity( ENTITY );

		dataBinder = new DataBinder( propertyValues );
		dataBinder.setConversionService( conversionService );

	}

	private Collection<EntityPropertyDescriptorBuilder> registeredProperties() {
		return Arrays.asList(
				builder( "text" ).propertyType( String.class ),
				builder( "number" ).propertyType( Long.class ),
				builder( "dummyNonEmbedded" ).propertyType( Dummy.class ),
				builder( "dummyEmbedded" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "longArray" ).propertyType( long[].class ),
				builder( "longCollection" ).propertyType( TypeDescriptor.collection( Collection.class, TypeDescriptor.valueOf( Long.class ) ) ),
				builder( "dummyList" ).propertyType( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "dummyList[]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "stringLongMap" )
						.propertyType( TypeDescriptor.map( Map.class, TypeDescriptor.valueOf( String.class ), TypeDescriptor.valueOf( Long.class ) ) ),
				builder( "longDummyMap" )
						.propertyType( TypeDescriptor.map( TreeMap.class, TypeDescriptor.valueOf( Long.class ), TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "longDummyMap[v]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "dummyDummyMap" )
						.propertyType(
								TypeDescriptor.map( LinkedHashMap.class, TypeDescriptor.valueOf( Dummy.class ), TypeDescriptor.valueOf( Dummy.class ) ) ),
				builder( "dummyDummyMap[k]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "dummyDummyMap[v]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) )
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
		propertyValues.resetForBinding();

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

		propertyValues.resetForBinding();
		bind( "properties[longCollection].bound=1" );

		assertCollection( "longCollection" )
				.isInstanceOf( LinkedHashSet.class )
				.isEmpty();

		// explicit sorting
		propertyValues.resetForBinding();
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
	public void setProperty() {
		bind( "properties[dummyList].bound=1",
		      "properties[dummyList].items[1].value.id=10",
		      "properties[dummyList].items[1].value.name=Hello",
		      "properties[dummyList].items[1].sortIndex=2000",
		      "properties[dummyList].items[2].value.id=20",
		      "properties[dummyList].items[2].value.name=Olleh",
		      "properties[dummyList].items[2].sortIndex=1000",
		      "properties[dummyList].items[3].value=",
		      "properties[dummyList].items[3].sortIndex=1500" );

		assertCollection( "dummyList" )
				.containsExactly( new Dummy( 20, "Olleh" ), null, new Dummy( 10, "Hello" ) )
				.isInstanceOf( ArrayList.class );
	}

	@Test
	public void mapWithSimpleKeyAndSimpleValueType() {
		bind( "properties[stringLongMap].items[x].value=1",
		      "properties[stringLongMap].items[y].key=hello",
		      "properties[stringLongMap].items[y].value=2",
		      "properties[stringLongMap].items[z].key=olleh",
		      "properties[stringLongMap].items[z].value=",
		      "properties[stringLongMap].items[z].sortIndex=-1" );

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
		bind( "properties[longDummyMap].items[x].key=1",
		      "properties[longDummyMap].items[x].value=1",
		      "properties[longDummyMap].items[y].key=-1",
		      "properties[longDummyMap].items[y].value=",
		      "properties[longDummyMap].items[z].key=2",
		      "properties[longDummyMap].items[z].value.id=5",
		      "properties[longDummyMap].items[z].value.name=Hello" );

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
		bind( "properties[dummyDummyMap].items[x].key=1",
		      "properties[dummyDummyMap].items[x].value=1",
		      "properties[dummyDummyMap].items[y].key.id=55",
		      "properties[dummyDummyMap].items[y].value=",
		      "properties[dummyDummyMap].items[z].key.id=33",
		      "properties[dummyDummyMap].items[z].key.name=Olleh",
		      "properties[dummyDummyMap].items[z].value.id=5",
		      "properties[dummyDummyMap].items[z].value.name=Hello" );

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

	private void bind( String... inlinedProperties ) {
		MutablePropertyValues pv = new MutablePropertyValues( TestPropertySourceUtils.convertInlinedPropertiesToMap( inlinedProperties ) );
		dataBinder.bind( pv );

		if ( dataBinder.getBindingResult().hasErrors() ) {
			fail( dataBinder.getBindingResult().getAllErrors().stream().map( ObjectError::getDefaultMessage ).collect( Collectors.joining( "\n" ) ) );
		}
	}

	private AbstractObjectAssert<?, ?> assertProperty( String property ) {
		return assertThat( propertyValues.get( property ).getValue() );
	}

	@SuppressWarnings("unchecked")
	private IterableAssert<Object> assertCollection( String property ) {
		return assertThat( (Collection) propertyValues.get( property ).getValue() );
	}

	@SuppressWarnings("unchecked")
	private MapAssert<Object, Object> assertMap( String property ) {
		return assertThat( (Map) propertyValues.get( property ).getValue() );
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
