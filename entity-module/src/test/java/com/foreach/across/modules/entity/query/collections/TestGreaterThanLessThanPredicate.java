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

import com.foreach.across.modules.entity.query.EntityQueryCondition;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.function.Predicate;

import static com.foreach.across.modules.entity.query.EntityQueryOps.*;
import static com.foreach.across.modules.entity.query.collections.CollectionEntityQueryPredicates.createPredicate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author Steven Gentens
 * @since 3.1.0
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SuppressWarnings("unchecked")
public class TestGreaterThanLessThanPredicate
{
	@Mock
	private EntityPropertyDescriptor descriptor;

	@Mock
	private CollectionEntityQueryItem item;

	private LocalDateTime dateTime = LocalDateTime.of( 2018, 1, 2, 12, 23, 44 );

	@BeforeEach
	public void setUp() {
		when( item.getPropertyValue( "date" ) ).thenReturn( new Date( dateTime.toInstant( ZoneOffset.UTC ).toEpochMilli() ) );
		when( item.getPropertyValue( "localDate" ) ).thenReturn( dateTime.toLocalDate() );
		when( item.getPropertyValue( "localDateTime" ) ).thenReturn( dateTime );
		when( item.getPropertyValue( "integer" ) ).thenReturn( 15 );
		when( item.getPropertyValue( "long" ) ).thenReturn( 15L );
		when( item.getPropertyValue( "double" ) ).thenReturn( 22.7 );
		when( item.getPropertyValue( "byte" ) ).thenReturn( (byte) 2 );
		when( item.getPropertyValue( "short" ) ).thenReturn( (short) 16000 );
	}

	@Test
	public void greaterThanDateTime() {
		LocalDateTime past = LocalDateTime.of( 1990, 3, 20, 12, 23 );
		Predicate predicate = createPredicate( new EntityQueryCondition( "date", GT, new Date( past.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ),
		                                       descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		LocalDateTime future = LocalDateTime.of( 3490, 3, 20, 12, 23 );
		predicate = createPredicate( new EntityQueryCondition( "date", GT, new Date( future.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDate", GT, past.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDate", GT, future.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", GT, past ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", GT, future ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void greaterThanOrEqualDateTime() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "date", GE, new Date( dateTime.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ),
		                                       descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		LocalDateTime future = LocalDateTime.of( 3490, 3, 20, 12, 23 );
		predicate = createPredicate( new EntityQueryCondition( "date", GE, new Date( future.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDate", GE, dateTime.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDate", GE, future.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", GE, dateTime ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", GE, future ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void greaterThanNumeric() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "integer", GT, -234 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "integer", GT, 234 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "long", GT, -124L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", GT, 23421L ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "double", GT, -123.234 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", GT, 123.234 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "byte", GT, (byte) -67 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", GT, (byte) 67 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "short", GT, (short) -32000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", GT, (short) 32000 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void greaterThanOrEqualNumeric() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "integer", GE, 15 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "integer", GE, 14 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "integer", GE, 16 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "long", GE, 15L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", GE, 14L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", GE, 16L ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "double", GE, 22.7 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", GE, 22.5 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", GE, 22.72 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "byte", GE, (byte) -67 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", GE, (byte) 2 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", GE, (byte) 67 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "short", GE, (short) -32000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", GE, (short) 16000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", GE, (short) 32000 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void lessThanDateTime() {
		LocalDateTime past = LocalDateTime.of( 1990, 3, 20, 12, 23 );
		Predicate predicate = createPredicate( new EntityQueryCondition( "date", LT, new Date( past.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ),
		                                       descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		LocalDateTime future = LocalDateTime.of( 3490, 3, 20, 12, 23 );
		predicate = createPredicate( new EntityQueryCondition( "date", LT, new Date( future.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDate", LT, past.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDate", LT, future.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", LT, past ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", LT, future ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void lessThanOrEqualDateTime() {
		LocalDateTime future = LocalDateTime.of( 3490, 3, 20, 12, 23 );
		LocalDateTime past = LocalDateTime.of( 1990, 3, 20, 12, 23 );

		Predicate predicate = createPredicate( new EntityQueryCondition( "date", LE, new Date( dateTime.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ),
		                                       descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "date", LE, new Date( future.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "date", LE, new Date( past.toInstant( ZoneOffset.UTC ).toEpochMilli() ) ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDate", LE, dateTime.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDate", LE, future.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDate", LE, past.toLocalDate() ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", LE, dateTime ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", LE, future ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "localDateTime", LE, past ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();
	}

	@Test
	public void lessThanNumeric() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "integer", LT, -234 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "integer", LT, 234 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", LT, -124L ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "long", LT, 23421L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", LT, -123.234 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "double", LT, 123.234 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", LT, (byte) -67 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "byte", LT, (byte) 67 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", LT, (short) -32000 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "short", LT, (short) 32000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

	@Test
	public void lessThanOrEqualNumeric() {
		Predicate predicate = createPredicate( new EntityQueryCondition( "integer", LE, 22 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "integer", LE, 9 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "integer", LE, 15 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", LE, 28L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "long", LE, 13L ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "long", LE, 15L ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", LE, 22.5 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "double", LE, 22.9 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "double", LE, 22.7 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", LE, (byte) -67 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "byte", LE, (byte) 2 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "byte", LE, (byte) 67 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", LE, (short) -32000 ), descriptor );
		assertThat( predicate.test( item ) ).isFalse();

		predicate = createPredicate( new EntityQueryCondition( "short", LE, (short) 16000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();

		predicate = createPredicate( new EntityQueryCondition( "short", LE, (short) 32000 ), descriptor );
		assertThat( predicate.test( item ) ).isTrue();
	}

}
