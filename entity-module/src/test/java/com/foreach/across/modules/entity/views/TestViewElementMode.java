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

package com.foreach.across.modules.entity.views;

import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.entity.views.ViewElementMode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * @author Arne Vandamme
 * @since 2.2.0
 */
class TestViewElementMode
{
	private static final String NAME_SINGLE = "MY_NAME";
	private static final String NAME_MULTIPLE = "MY_NAME_MULTIPLE";

	@Test
	void toStringEqualsType() {
		assertThat( NAME_SINGLE ).isEqualTo( ViewElementMode.of( NAME_SINGLE ).toString() );
		assertThat( NAME_MULTIPLE ).isEqualTo( ViewElementMode.of( NAME_MULTIPLE ).toString() );
	}

	@Test
	void multipleSuffixIsAddedIfMissing() {
		ViewElementMode mode = ViewElementMode.of( NAME_SINGLE );
		assertThat( mode.isForMultiple() ).isFalse();

		ViewElementMode multiple = mode.forMultiple();
		assertThat( ViewElementMode.of( NAME_MULTIPLE ) ).isEqualTo( multiple );

		assertThat( multiple.isForMultiple() ).isTrue();
		assertSame( multiple, multiple.forMultiple() );
	}

	@Test
	void multipleSuffixIsRemovedForSingle() {
		ViewElementMode multiple = ViewElementMode.of( NAME_MULTIPLE );
		assertThat( multiple.isForMultiple() ).isTrue();

		ViewElementMode single = multiple.forSingle();
		assertThat( ViewElementMode.of( NAME_SINGLE ) ).isEqualTo( single );

		assertThat( single.isForMultiple() ).isFalse();
		assertThat( single ).isSameAs( single.forSingle() );
	}

	@Test
	void isListMode() {
		assertThat( isList( ViewElementMode.LIST_VALUE ) ).isTrue();
		assertThat( isList( ViewElementMode.LIST_LABEL ) ).isTrue();
		assertThat( isList( ViewElementMode.LIST_CONTROL ) ).isTrue();
		assertThat( isList( ViewElementMode.LIST_VALUE.forMultiple() ) ).isTrue();
		assertThat( isList( ViewElementMode.LIST_LABEL.forMultiple() ) ).isTrue();
		assertThat( isList( ViewElementMode.LIST_CONTROL.forMultiple() ) ).isTrue();
		assertThat( isList( ViewElementMode.CONTROL ) ).isFalse();
	}

	@Test
	void isLabelMode() {
		assertThat( isLabel( ViewElementMode.LABEL ) ).isTrue();
		assertThat( isLabel( ViewElementMode.LIST_LABEL ) ).isTrue();
		assertThat( isLabel( ViewElementMode.LABEL.forMultiple() ) ).isTrue();
		assertThat( isLabel( ViewElementMode.LIST_LABEL.forMultiple() ) ).isTrue();
		assertThat( isLabel( ViewElementMode.CONTROL ) ).isFalse();
	}

	@Test
	void isValueMode() {
		assertThat( isValue( ViewElementMode.VALUE ) ).isTrue();
		assertThat( isValue( ViewElementMode.LIST_VALUE ) ).isTrue();
		assertThat( isValue( ViewElementMode.VALUE.forMultiple() ) ).isTrue();
		assertThat( isValue( ViewElementMode.LIST_VALUE.forMultiple() ) ).isTrue();
		assertThat( isValue( ViewElementMode.CONTROL ) ).isFalse();
	}

	@Test
	void isControlMode() {
		assertThat( isControl( ViewElementMode.CONTROL ) ).isTrue();
		assertThat( isControl( ViewElementMode.LIST_CONTROL ) ).isTrue();
		assertThat( isControl( ViewElementMode.FILTER_CONTROL ) ).isTrue();
		assertThat( isControl( ViewElementMode.CONTROL.forMultiple() ) ).isTrue();
		assertThat( isControl( ViewElementMode.LIST_CONTROL.forMultiple() ) ).isTrue();
		assertThat( isControl( ViewElementMode.FILTER_CONTROL.forMultiple() ) ).isTrue();
		assertThat( isControl( ViewElementMode.LABEL ) ).isFalse();
	}

	@Test
	void exceptionThrownIfIllegalCharacters() {
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> ViewElementMode.of( "VALUE(Y" ) );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> ViewElementMode.of( "VALUE)Y" ) );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> ViewElementMode.of( "VALUE=Y" ) );
		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> ViewElementMode.of( "VALUE,Y" ) );
	}

	@Test
	void withChildModes() {
		ViewElementMode original = ViewElementMode.of( "original" );
		assertThat( original.getChildModes() ).isEmpty();
		ViewElementMode one = original.withChildMode( "one", ViewElementMode.of( "nestedOne" ) );
		assertThat( original.getChildModes() ).isEmpty();
		assertThat( one.getChildModes() )
				.containsEntry( "one", ViewElementMode.of( "nestedOne" ) )
				.hasSize( 1 );
		ViewElementMode two = one.withChildMode( "two", ViewElementMode.of( "nestedTwo" ) );
		assertThat( original.getChildModes() ).isEmpty();
		assertThat( one.getChildModes() )
				.containsEntry( "one", ViewElementMode.of( "nestedOne" ) )
				.hasSize( 1 );
		assertThat( original.getChildModes() ).isEmpty();
		assertThat( two.getChildModes() )
				.containsEntry( "one", ViewElementMode.of( "nestedOne" ) )
				.containsEntry( "two", ViewElementMode.of( "nestedTwo" ) )
				.hasSize( 2 );
		assertThat( two.withoutChildModes() )
				.isEqualTo( original )
				.isNotEqualTo( one );
		assertThat( two.withoutChildMode( "two" ) )
				.isEqualTo( one )
				.isNotEqualTo( original );
	}

	@Test
	void matchesTypeOfAny() {
		assertThat( ViewElementMode.of( "one" ).matchesTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesTypeOf( ViewElementMode.of( "two" ) ) ).isFalse();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .matchesTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isFalse();
		assertThat( ViewElementMode.of( "one" ).forMultiple().matchesTypeOf( ViewElementMode.of( "one" ) ) ).isFalse();
		assertThat( ViewElementMode.of( "one" ).forMultiple().matchesTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isTrue();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .forMultiple()
		                           .matchesTypeOf( ViewElementMode.of( "one" ) ) ).isFalse();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .forMultiple()
		                           .matchesTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isTrue();

		assertThat( ViewElementMode.of( "one" ).matchesTypeOfAny( ViewElementMode.of( "two" ), ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesTypeOfAny( ViewElementMode.of( "two" ), ViewElementMode.of( "three" ) ) ).isFalse();
	}

	@Test
	void matchesSingleTypeOfAny() {
		assertThat( ViewElementMode.of( "one" ).matchesSingleTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesSingleTypeOf( ViewElementMode.of( "two" ) ) ).isFalse();
		assertThat( ViewElementMode.of( "one" ).forMultiple().matchesSingleTypeOf( ViewElementMode.of( "two" ).forMultiple() ) ).isFalse();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .matchesSingleTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesSingleTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).forMultiple().matchesSingleTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" ).forMultiple().matchesSingleTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isTrue();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .forMultiple()
		                           .matchesSingleTypeOf( ViewElementMode.of( "one" ) ) ).isTrue();
		assertThat( ViewElementMode.of( "one" )
		                           .withChildMode( "sub", ViewElementMode.of( "subMode" ) )
		                           .forMultiple()
		                           .matchesSingleTypeOf( ViewElementMode.of( "one" ).forMultiple() ) ).isTrue();

		assertThat( ViewElementMode.of( "one" ).matchesSingleTypeOfAny( ViewElementMode.of( "two" ).forMultiple(), ViewElementMode.of( "one" ).forMultiple() ) )
				.isTrue();
		assertThat( ViewElementMode.of( "one" ).matchesSingleTypeOfAny( ViewElementMode.of( "two" ).forMultiple(), ViewElementMode.of( "three" ) ) ).isFalse();
	}

	@Test
	void toAndFromString() {
		assertThat( ViewElementMode.of( "one" ).toString() ).isEqualTo( "one" );
		assertThat( ViewElementMode.of( "one" ).forMultiple().toString() ).isEqualTo( "one_MULTIPLE" );
		assertThat( ViewElementMode.of( "one_MULTIPLE" ) ).isEqualTo( ViewElementMode.of( "one" ).forMultiple() );

		assertThat(
				ViewElementMode.of( "one" )
				               .withChildMode( "sub1", ViewElementMode.of( "control" ) )
				               .toString()
		).isEqualTo( "one(sub1=control)" );

		assertThat(
				ViewElementMode.of( "one" )
				               .withChildMode( "sub1", ViewElementMode.of( "control" ) )
				               .withChildMode( "sub2", ViewElementMode.of( "label" )
				                                                      .withChildMode( "subSub", ViewElementMode.of( ( "custom" ) ) ) )
				               .toString()
		).isEqualTo( "one(sub1=control,sub2=label(subSub=custom))" );

		assertThat( ViewElementMode.of( "one(sub1=control)" ) )
				.isEqualTo( ViewElementMode.of( "one" )
				                           .withChildMode( "sub1", ViewElementMode.of( "control" ) ) );
		assertThat( ViewElementMode.of( "one(sub1=control,sub2=label(subSub=custom))" ) )
				.isEqualTo( ViewElementMode.of( "one" )
				                           .withChildMode( "sub1", ViewElementMode.of( "control" ) )
				                           .withChildMode( "sub2", ViewElementMode.of( "label" )
				                                                                  .withChildMode( "subSub", ViewElementMode.of( ( "custom" ) ) ) ) );

		assertThatExceptionOfType( IllegalArgumentException.class )
				.isThrownBy( () -> ViewElementMode.of( "one(sub1=control,sub2=label(subSub=custom)" ) );
	}

	@Test
	void getChildModeReturnsDefault() {
		ViewElementMode mode = of( "one" )
				.withChildMode( "sub1", of( "control" ) )
				.withChildMode( "sub2", of( "label" ) );

		assertThat( mode.getChildMode( "sub3" ) ).isNull();
		assertThat( mode.getChildMode( "sub3", FORM_READ ) ).isEqualTo( FORM_READ );
		assertThat( mode.getChildMode( "sub1" ) ).isEqualTo( of( "control" ) );
		assertThat( mode.getChildMode( "sub2", FORM_READ ) ).isEqualTo( of( "label" ) );
	}

	@Test
	void hasChildModes() {
		assertThat( ViewElementMode.of("one").hasChildModes() ).isFalse();
		assertThat( ViewElementMode.of("one").withChildMode( "sub1", of( "control" ) ).hasChildModes() ).isTrue();
		assertThat( ViewElementMode.of("one").withChildMode( "sub1", of( "control" ) ).withoutChildMode( "sub1" ).hasChildModes() ).isFalse();
		assertThat( ViewElementMode.of("one").withChildMode( "sub1", of( "control" ) ).withoutChildModes().hasChildModes() ).isFalse();
	}
}
