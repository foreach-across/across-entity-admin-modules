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
import org.junit.Test;
import org.springframework.core.convert.TypeDescriptor;
import test.bind.TestSimpleManualPropertiesOnBlankEntity.Dummy;

import java.util.*;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.1.0
 */
public class TestNestedManualPropertiesOnBlankEntity extends AbstractEntityPropertiesBinderTest
{
	@Override
	protected Collection<EntityPropertyDescriptorBuilder> registeredProperties() {
		return Arrays.asList(
				builder( "holder" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),
				builder( "holder.id" ).propertyType( int.class ).<DummiesHolder, Integer>controller( c -> c.applyValueConsumer( DummiesHolder::setId ) ),

				// map property
				builder( "holder.dummies" )
						.propertyType(
								TypeDescriptor.map( LinkedHashMap.class, TypeDescriptor.valueOf( String.class ), TypeDescriptor.valueOf( Dummy.class ) )
						)
						.<DummiesHolder, Map<String, Dummy>>controller( c -> c.applyValueConsumer( DummiesHolder::setDummies ) )
				,
				builder( "holder.dummies[k]" ).propertyType( String.class ),
				builder( "holder.dummies[v]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "holder.dummies[v].id" ).propertyType( int.class ).<Dummy, Integer>controller( c -> c.applyValueConsumer( Dummy::setId ) ),

				builder( "holder.dummies[v].name" ).propertyType( String.class ).<Dummy, String>controller( c -> c.applyValueConsumer( Dummy::setName ) ),

				// list
				builder( "holder.holders" ).propertyType( List.class ).<DummiesHolder, List<DummiesHolder>>controller(
						c -> c.applyValueConsumer( DummiesHolder::setHolders ) ),
				builder( "holder.holders[]" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),
				builder( "holder.holders[].id" ).propertyType( int.class ).<DummiesHolder, Integer>controller(
						c -> c.applyValueConsumer( DummiesHolder::setId ) ),

				// map property on list item
				builder( "holder.holders[].dummies" )
						.propertyType(
								TypeDescriptor.map( LinkedHashMap.class, TypeDescriptor.valueOf( String.class ), TypeDescriptor.valueOf( Dummy.class ) )
						)
						.<DummiesHolder, Map<String, Dummy>>controller( c -> c.applyValueConsumer( DummiesHolder::setDummies ) )
				,
				builder( "holder.holders[].dummies[k]" ).propertyType( String.class ),
				builder( "holder.holders[].dummies[v]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "holder.holders[].dummies[v].id" ).propertyType( int.class ).<Dummy, Integer>controller( c -> c.applyValueConsumer( Dummy::setId ) ),
				builder( "holder.holders[].dummies[v].name" ).propertyType( String.class ).<Dummy, String>controller(
						c -> c.applyValueConsumer( Dummy::setName ) ),

				builder( "holder.holders[].holders" ).propertyType( List.class ).<DummiesHolder, List<DummiesHolder>>controller(
						c -> c.applyValueConsumer( DummiesHolder::setHolders ) ),
				builder( "holder.holders[].holders[]" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),
				builder( "holder.holders[].holders[].id" ).propertyType( int.class ).<DummiesHolder, Integer>controller(
						c -> c.applyValueConsumer( DummiesHolder::setId ) ),

				// list of list
				builder( "listOfList" ).propertyType( List.class ),
				builder( "listOfList[]" ).propertyType( List.class ).<List, List>controller( c -> c.createValueSupplier( ArrayList::new ) ),
				builder( "listOfList[][]" ).propertyType( String.class )

				// list of map

				// map with list of list of key and map value

		);
	}

	@Test
	public void bindingMapPropertyOnCustomTypeProperty() {
		bind(
				"properties[holder].properties[id].value=10",
				"properties[holder].properties[dummies].items[1].key=x",
				"properties[holder].properties[dummies].items[1].value=1",
				"properties[holder].properties[dummies].items[2].key=y",
				"properties[holder].properties[dummies].items[2].valueProperties[id].value=5",
				"properties[holder].properties[dummies].items[2].valueProperties[name].value=Hello"
		);

		DummiesHolder holder = (DummiesHolder) propertyValues.get( "holder" ).getValue();
		assertThat( holder ).isNotNull();
		assertThat( holder.getId() ).isEqualTo( 10 );
		assertThat( holder.getDummies() )
				.hasSize( 2 )
				.containsEntry( "x", new Dummy( 1, "1" ) )
				.containsEntry( "y", new Dummy( 5, "Hello" ) );
	}

	@Test
	public void bindingMapPropertyOfCustomListPropertyOnCustomType() {
		bind( "properties[holder].properties[id].value=5",
		      "properties[holder].properties[holders].items[1].value.id=10",
		      "properties[holder].properties[holders].items[1].valueProperties[dummies].items[1].key=Test",
		      "properties[holder].properties[holders].items[1].valueProperties[dummies].items[1].valueProperties[id].value=33",
		      "properties[holder].properties[holders].items[1].valueProperties[dummies].items[1].valueProperties[name].value=Test dummy",
		      "properties[holder].properties[holders].items[2].value.id=15",
		      "properties[holder].properties[holders].items[2].valueProperties[holders].items[1].value.id=20"
		);

		DummiesHolder holder = (DummiesHolder) propertyValues.get( "holder" ).getValue();
		assertThat( holder ).isNotNull();
		assertThat( holder.getId() ).isEqualTo( 5 );

		assertThat( holder.getHolders() )
				.hasSize( 2 );

		DummiesHolder one = holder.getHolders().get( 0 );
		assertThat( one.getId() ).isEqualTo( 10 );
		assertThat( one.getDummies() )
				.hasSize( 1 )
				.containsEntry( "Test", new Dummy( 33, "Test dummy" ) );

		DummiesHolder two = holder.getHolders().get( 1 );
		assertThat( two.getId() ).isEqualTo( 15 );
		assertThat( two.getHolders() )
				.hasSize( 1 );

		assertThat( two.getHolders().get( 0 ).getId() ).isEqualTo( 20 );
	}

	@Test
	public void bindingListOfListStructure() {
		bind( "properties[listOfList].items[x].items[x].value=list-1-item-1",
		      "properties[listOfList].items[x].valueItems[y].value=list-1-item-2",
		      "properties[listOfList].items[x].valueItems[y].sortIndex=-1",
		      "properties[listOfList].items[y].valueItems[x].value=list-2-item-1",
		      "properties[listOfList].items[y].sortIndex=-1" );

		assertProperty( "listOfList" )
				.isEqualTo(
						Arrays.asList( Collections.singletonList( "list-2-item-1" ), Arrays.asList( "list-1-item-2", "list-1-item-1" ) )
				);
	}

	@Test
	public void bindingListOfMapStructure() {

	}

	@Test
	public void bindingMapStructureWithListOfListKeyAndMapOfMapValue() {

	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	static class DummiesHolder
	{
		private int id;
		private Map<String, Dummy> dummies;
		private List<DummiesHolder> holders;
	}
}
