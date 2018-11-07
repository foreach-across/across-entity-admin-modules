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

import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.bind.ListEntityPropertyBinder;
import com.foreach.across.modules.entity.bind.MapEntityPropertyBinder;
import com.foreach.across.modules.entity.bind.SingleEntityPropertyBinder;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestPropertiesBinderReadonly extends AbstractEntityPropertyBindingContextTest
{
	@Test
	@SuppressWarnings("unchecked")
	public void allPropertiesAreAvailable() {
		CollectionsHolder collectionsHolder = new CollectionsHolder(
				"testHolder",
				Arrays.asList( new City( "Antwerp" ), new City( "Brussels" ) ),
				ImmutableMap.of( "one", new Address( "street 1" ), "two", new Address( "street 2" ) )
		);

		EntityPropertiesBinder binder = new EntityPropertiesBinder( collectionsProperties );
		binder.setEntity( collectionsHolder );

		assertThat( binder.get( "name" ).getValue() ).isEqualTo( "testHolder" );
		assertThat( binder.get( "cities" ).getValue() ).isEqualTo( Arrays.asList( new City( "Antwerp" ), new City( "Brussels" ) ) );
		assertThat( (Map<String, Address>) binder.get( "addressMap" ).getValue() )
				.containsEntry( "one", new Address( "street 1" ) )
				.containsEntry( "two", new Address( "street 2" ) );

		ListEntityPropertyBinder cities = (ListEntityPropertyBinder) binder.get( "cities" );
		assertThat( cities.getItems().get( "0" ).getValue() ).isEqualTo( new City( "Antwerp" ) );
		assertThat( ( (SingleEntityPropertyBinder) cities.getItems().get( "0" ) ).getProperties().get( "name" ).getValue() ).isEqualTo( "Antwerp" );

		MapEntityPropertyBinder addressMap = (MapEntityPropertyBinder) binder.get( "addressMap" );
		assertThat( addressMap.getEntries().get( "0" ).getKey().getValue() ).isEqualTo( "one" );
		assertThat( addressMap.getEntries().get( "0" ).getValue().getValue() ).isEqualTo( new Address( "street 1" ) );
	}

	@Test
	public void mapEntryListAlwaysReturnsOrderedEntries() {
		CollectionsHolder collectionsHolder = new CollectionsHolder(
				"testHolder",
				Collections.emptyList(),
				ImmutableMap.of( "one", new Address( "street 1" ), "two", new Address( "street 2" ) )
		);

		EntityPropertiesBinder binder = new EntityPropertiesBinder( collectionsProperties );
		binder.setEntity( collectionsHolder );

		MapEntityPropertyBinder addressMap = (MapEntityPropertyBinder) binder.get( "addressMap" );
		MapEntityPropertyBinder.Entry one = addressMap.getEntries().get( "0" );
		assertThat( one.getKey().getValue() ).isEqualTo( "one" );
		assertThat( one.getEntryKey() ).isEqualTo( "0" );
		MapEntityPropertyBinder.Entry two = addressMap.getEntries().get( "1" );
		assertThat( two.getKey().getValue() ).isEqualTo( "two" );
		assertThat( two.getEntryKey() ).isEqualTo( "1" );

		assertThat( one.getSortIndex() ).isEqualTo( 0L );
		assertThat( two.getSortIndex() ).isEqualTo( 1L );
		assertThat( addressMap.getEntryList() ).containsExactly( one, two );

		two.setSortIndex( -1L );
		assertThat( addressMap.getEntryList() ).containsExactly( two, one );
	}
}
