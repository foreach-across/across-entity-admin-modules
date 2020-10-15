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

package com.foreach.across.modules.entity.query.collections;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;

import java.util.Comparator;

import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryComparators.createComparator;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@ExtendWith(MockitoExtension.class)
public class TestCollectionEntityQueryComparators
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem<Object> itemOne;

	@Mock
	private CollectionEntityQueryItem<Object> itemTwo;

	@Test
	public void stringComparator() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( String.class ) );
		when( itemOne.getPropertyValue( "name" ) ).thenReturn( "abcdef" );
		when( itemTwo.getPropertyValue( "name" ) ).thenReturn( "fedcba" );

		Sort.Order order = new Sort.Order( Sort.Direction.ASC, "name" );
		Comparator<CollectionEntityQueryItem<Object>> comparator = createComparator( order, descriptor );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isLessThan( 0 );
		assertThat( comparator.compare( itemTwo, itemOne ) )
				.isGreaterThan( 0 );

		when( itemOne.getPropertyValue( "name" ) ).thenReturn( "AAA" );
		when( itemTwo.getPropertyValue( "name" ) ).thenReturn( "aaa" );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isLessThan( 0 );
		assertThat( comparator.compare( itemTwo, itemOne ) )
				.isGreaterThan( 0 );

		comparator = createComparator( order.ignoreCase(), descriptor );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isZero();

	}

	@Test
	public void numericComparator() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( Integer.class ) );
		when( itemOne.getPropertyValue( "integer" ) ).thenReturn( 5 );
		when( itemTwo.getPropertyValue( "integer" ) ).thenReturn( 10 );

		Sort.Order order = new Sort.Order( Sort.Direction.ASC, "integer" );
		Comparator<CollectionEntityQueryItem<Object>> comparator = createComparator( order, descriptor );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isLessThan( 0 );
		assertThat( comparator.compare( itemTwo, itemOne ) )
				.isGreaterThan( 0 );

		when( itemOne.getPropertyValue( "integer" ) ).thenReturn( 10 );
		when( itemTwo.getPropertyValue( "integer" ) ).thenReturn( 10 );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isZero();

		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( Short.class ) );
		when( itemOne.getPropertyValue( "short" ) ).thenReturn( (short) 5 );
		when( itemTwo.getPropertyValue( "short" ) ).thenReturn( (short) 10 );

		order = new Sort.Order( Sort.Direction.ASC, "short" );
		comparator = createComparator( order, descriptor );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isLessThan( 0 );
		assertThat( comparator.compare( itemTwo, itemOne ) )
				.isGreaterThan( 0 );

		when( itemOne.getPropertyValue( "short" ) ).thenReturn( (short) 10 );
		when( itemTwo.getPropertyValue( "short" ) ).thenReturn( (short) 10 );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isZero();
	}

	@Test
	public void defaultComparator() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( MySimpleEntity.class ) );
		when( itemOne.getPropertyValue( "object" ) ).thenReturn( new MySimpleEntity( 1L ) );
		when( itemTwo.getPropertyValue( "object" ) ).thenReturn( new MySimpleEntity( 2L ) );

		Comparator<CollectionEntityQueryItem<Object>> comparator = createComparator( new Sort.Order( Sort.Direction.ASC, "object" ), descriptor );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isLessThan( 0 );
		assertThat( comparator.compare( itemTwo, itemOne ) )
				.isGreaterThan( 0 );

		when( itemOne.getPropertyValue( "object" ) ).thenReturn( new MySimpleEntity( 2L ) );
		when( itemTwo.getPropertyValue( "object" ) ).thenReturn( new MySimpleEntity( 2L ) );
		assertThat( comparator.compare( itemOne, itemTwo ) )
				.isZero();
	}

	@Test
	public void notComparable() {
		when( descriptor.getPropertyTypeDescriptor() ).thenReturn( TypeDescriptor.valueOf( NotComparable.class ) );

		assertThatIllegalArgumentException()
				.isThrownBy( () -> createComparator( new Sort.Order( Sort.Direction.ASC, "notComparable" ), descriptor ) );
	}

	class MySimpleEntity implements Comparable<MySimpleEntity>
	{

		private Long id;

		MySimpleEntity( Long id ) {
			this.id = id;
		}

		public Long getId() {
			return id;
		}

		@SuppressWarnings("NullableProblems")
		@Override
		public int compareTo( MySimpleEntity o ) {
			return this.id.compareTo( o.getId() );
		}
	}

	class NotComparable
	{

	}
}
