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

package test.binding;

import com.foreach.across.modules.entity.config.builders.EntityPropertyDescriptorBuilder;
import com.foreach.across.modules.entity.registry.properties.DefaultEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.processors.support.EntityPropertiesBinder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.assertj.core.api.AbstractObjectAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.validation.DataBinder;

import java.util.Arrays;
import java.util.Collection;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;

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
				builder( "dummyEmbedded" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) )
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
	public void listProperty() {
		// sorted
	}

	@Test
	public void setProperty() {
		// sorted (?)
	}

	@Test
	public void mapPropertyWithSimpleTypeKey() {
		// sorted
	}

	@Test
	public void mapPropertyWithCustomTypeKey() {
		// sorted
	}

	private void bind( String... inlinedProperties ) {
		MutablePropertyValues pv = new MutablePropertyValues( TestPropertySourceUtils.convertInlinedPropertiesToMap( inlinedProperties ) );
		dataBinder.bind( pv );
	}

	private AbstractObjectAssert<?, ?> assertProperty( String property ) {
		return assertThat( propertyValues.get( property ).getValue() );
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class Dummy
	{
		private int id;
		private String name;

		// Used by ConversionService
		@SuppressWarnings( "unused" )
		public static Dummy from( String name ) {
			Dummy dummy = new Dummy();
			dummy.setName( name );
			dummy.setId( Integer.parseInt( name ) );
			return dummy;
		}
	}
}
