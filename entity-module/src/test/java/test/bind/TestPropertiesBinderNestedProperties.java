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
import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;
import test.bind.TestPropertiesBinderSimpleProperties.Dummy;

import java.util.*;

import static com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor.builder;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestPropertiesBinderNestedProperties extends AbstractEntityPropertiesBinderTest
{
	@Override
	protected Collection<EntityPropertyDescriptorBuilder> registeredProperties() {
		return Arrays.asList(
				builder( "holder" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),
				builder( "holder.dummies[key]" ).propertyType( String.class ),
				builder( "holder.dummies[value]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),

				// list
				builder( "holder.holders[]" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),

				// map property on list item
				builder( "holder.holders[].dummies" )
						.propertyType(
								TypeDescriptor.map( LinkedHashMap.class, TypeDescriptor.valueOf( String.class ), TypeDescriptor.valueOf( Dummy.class ) )
						)
						.controller(
								c -> c.withTarget( DummiesHolder.class, Map.class )
								      .applyValueConsumer( ( holder, v ) -> holder.setDummies( v.getNewValue() ) )
						),
				builder( "holder.holders[].dummies[key]" ).propertyType( String.class ),
				builder( "holder.holders[].dummies[value]" ).propertyType( Dummy.class ).controller( c -> c.createValueSupplier( Dummy::new ) ),
				builder( "holder.holders[].holders[]" ).propertyType( DummiesHolder.class ).controller( c -> c.createValueSupplier( DummiesHolder::new ) ),

				// list of list
				builder( "listOfList" ).propertyType( List.class ),
				builder( "listOfList[]" ).propertyType( List.class ),
				builder( "listOfList[][]" ).propertyType( String.class ),

				// list of map
				builder( "listOfMap" ).propertyType( List.class ),
				builder( "listOfMap[]" ).propertyType( Map.class ),
				builder( "listOfMap[][key]" ).propertyType( Integer.class ),
				builder( "listOfMap[][value]" ).propertyType( Long.class ),

				// map with list of list of key and map value
				builder( "complexMap" ).propertyType( Map.class ),
				builder( "complexMap[key]" ).propertyType( List.class ),
				builder( "complexMap[key][]" ).propertyType( List.class ),
				builder( "complexMap[key][][]" ).propertyType( String.class ),
				builder( "complexMap[value]" ).propertyType( Map.class ),
				builder( "complexMap[value][key]" ).propertyType( String.class ),
				builder( "complexMap[value][value]" ).propertyType( List.class ),
				builder( "complexMap[value][value][]" ).propertyType( String.class )
		);
	}

	@Test
	public void bindingMapPropertyOnCustomTypeProperty() {
		bind(
				"properties[holder].properties[id].value=10",
				"properties[holder].properties[dummies].entries[1].key.value=x",
				"properties[holder].properties[dummies].entries[1].value.value=1",
				"properties[holder].properties[dummies].entries[2].key.value=y",
				"properties[holder].properties[dummies].entries[2].value.properties[id].value=5",
				"properties[holder].properties[dummies].entries[2].value.properties[name].value=Hello"
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
		      "properties[holder].properties[holders].items[1].initializedValue.id=10",
		      "properties[holder].properties[holders].items[1].properties[dummies].entries[1].key.value=Test",
		      "properties[holder].properties[holders].items[1].properties[dummies].entries[1].value.properties[id].value=33",
		      "properties[holder].properties[holders].items[1].properties[dummies].entries[1].value.properties[name].value=Test dummy",
		      "properties[holder].properties[holders].items[2].initializedValue.id=15",
		      "properties[holder].properties[holders].items[2].properties[holders].items[1].initializedValue.id=20"
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
		      "properties[listOfList].items[x].items[y].value=list-1-item-2",
		      "properties[listOfList].items[x].items[y].sortIndex=-1",
		      "properties[listOfList].items[y].items[x].value=list-2-item-1",
		      "properties[listOfList].items[y].sortIndex=-1" );

		assertProperty( "listOfList" )
				.isEqualTo(
						Arrays.asList( Collections.singletonList( "list-2-item-1" ), Arrays.asList( "list-1-item-2", "list-1-item-1" ) )
				);
	}

	@Test
	public void bindingListOfMapStructure() {
		bind( "properties[listOfMap].items[x].entries[x].key.value=366",
		      "properties[listOfMap].items[x].entries[x].value.value=663",
		      "properties[listOfMap].items[x].entries[y].key.value=1",
		      "properties[listOfMap].items[x].entries[y].value.value=46",
		      "properties[listOfMap].items[x].entries[y].sortIndex=-1",
		      "properties[listOfMap].items[y].entries[x].key.value=5",
		      "properties[listOfMap].items[y].entries[x].value.value=33",
		      "properties[listOfMap].items[y].sortIndex=-1" );

		Map<Integer, Long> one = Collections.singletonMap( 5, 33L );
		Map<Integer, Long> two = ImmutableMap.<Integer, Long>builder().put( 1, 46L ).put( 366, 663L ).build();

		assertProperty( "listOfMap" ).isEqualTo( Arrays.asList( one, two ) );
	}

	@Test
	public void bindingMapStructureWithListOfListKeyAndMapOfMapValue() {
		bind( "properties[complexMap].entries[x].key.items[1].items[A].value=key-item-1",
		      "properties[complexMap].entries[x].value.entries[1].key.value=value-1",
		      "properties[complexMap].entries[x].value.entries[1].value.items[A].value=value-item-1" );

		assertProperty( "complexMap" )
				.isEqualTo( Collections.singletonMap( Collections.singletonList( Collections.singletonList( "key-item-1" ) ),
				                                      Collections.singletonMap( "value-1", Collections.singletonList( "value-item-1" ) ) ) );
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
