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

package com.foreach.across.modules.bootstrapui.utils;

import com.foreach.across.modules.bootstrapui.elements.HiddenFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 2.1.0
 */
public class TestControlNamePrefixAdjuster
{
	private ControlNamePrefixAdjuster<?> prefixer = new ControlNamePrefixAdjuster<>();

	private HiddenFormElement control;
	private ContainerViewElement containerWithControl;
	private TextboxFormElement textboxWithControl;

	@Before
	public void before() {
		control = new HiddenFormElement();
		control.setControlName( "ctl" );

		containerWithControl = new ContainerViewElement();
		containerWithControl.addChild( control );

		textboxWithControl = new TextboxFormElement();
		textboxWithControl.setControlName( "text" );
		textboxWithControl.addChild( control );
	}

	@Test
	public void notModifiedIfPrefixToAddIsNotSet() {
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "ctl" );
	}

	@Test
	public void notModifiedIfNoControlName() {
		control.setControlName( null );
		prefixer.prefixToAdd( "my" ).accept( control );
		assertThat( control.getControlName() ).isNull();
	}

	@Test
	public void simplePrefixingOfSingleControlAddsDot() {
		prefixer.prefixToAdd( "my" ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );
	}

	@Test
	public void membersAreAdjustedIsRecurseIsTrue() {
		prefixer.prefixToAdd( "my" ).accept( containerWithControl );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );
	}

	@Test
	public void containerItselfIsAdjustedAlongWithMembersIfRecurseIsTrue() {
		prefixer.prefixToAdd( "my" ).accept( textboxWithControl );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );
		assertThat( textboxWithControl.getControlName() ).isEqualTo( "my.text" );
	}

	@Test
	public void membersAreNotAdjustedIfRecurseIsFalse() {
		prefixer.prefixToAdd( "my" ).recurse( false ).accept( containerWithControl );
		assertThat( control.getControlName() ).isEqualTo( "ctl" );
	}

	@Test
	public void onlyContainerIsAdjustedIfRecurseIsFalse() {
		prefixer.prefixToAdd( "my" ).recurse( false ).accept( textboxWithControl );
		assertThat( control.getControlName() ).isEqualTo( "ctl" );
		assertThat( textboxWithControl.getControlName() ).isEqualTo( "my.text" );
	}

	@Test
	public void replaceRequiresTheControlNameToStartWithThePrefix() {
		prefixer.prefixToAdd( "replaced" ).prefixToReplace( "ct" ).accept( textboxWithControl );
		assertThat( control.getControlName() ).isEqualTo( "replaced.l" );
		assertThat( textboxWithControl.getControlName() ).isEqualTo( "text" );
	}

	@Test
	public void underscoreIsIgnoredByDefault() {
		control.setControlName( "_ctl" );
		prefixer.prefixToAdd( "my" ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "_my.ctl" );

		prefixer.prefixToReplace( "my" ).prefixToAdd( "yours" ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "_yours.ctl" );
	}

	@Test
	public void underscoreIsKeptIfIgnoringIsFalse() {
		control.setControlName( "_ctl" );
		prefixer.prefixToAdd( "my" ).ignoreUnderscore( false ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my._ctl" );

		prefixer.prefixToReplace( "my" ).prefixToAdd( "_yours" ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "_yours._ctl" );
	}

	@Test
	public void dotIsInsertedUnlessSpecificStartCharacters() {
		prefixer.prefixToAdd( "my." ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );

		control.setControlName( "ctl" );
		prefixer.prefixToAdd( "my" ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );

		control.setControlName( "[0]" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my[0]" );

		control.setControlName( "{key}" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my{key}" );

		control.setControlName( "(test)" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my(test)" );

		control.setControlName( ".name" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.name" );
	}

	@Test
	public void dotIsNeverInsertedIfExplicitlyDisabled() {
		prefixer.prefixToAdd( "my." ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );

		control.setControlName( "ctl" );
		prefixer.prefixToAdd( "my" ).insertDotSeparator( false ).accept( control );
		assertThat( control.getControlName() ).isEqualTo( "myctl" );

		control.setControlName( "[0]" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my[0]" );

		control.setControlName( "{key}" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my{key}" );

		control.setControlName( "(test)" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my(test)" );

		control.setControlName( ".name" );
		prefixer.accept( control );
		assertThat( control.getControlName() ).isEqualTo( "my.name" );
	}

	@Test
	public void controlNotProcessedIfNotMatchingCustomElementPredicate() {
		prefixer.prefixToAdd( "my" ).elementPredicate( e -> e != textboxWithControl ).accept( textboxWithControl );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );
		assertThat( textboxWithControl.getControlName() ).isEqualTo( "text" );
	}

	@Test
	public void controlNotProcessedIfNotMatchingCustomControlNamePredicate() {
		prefixer.prefixToAdd( "my" ).controlNamePredicate( controlName -> !controlName.equals( "text" ) ).accept( textboxWithControl );
		assertThat( control.getControlName() ).isEqualTo( "my.ctl" );
		assertThat( textboxWithControl.getControlName() ).isEqualTo( "text" );
	}

	@Test
	public void controlNamePredicateGetsControlNameWithoutUnderscoreIfIgnoreIsTrue() {
		control.setControlName( "_ctl" );
		prefixer.prefixToAdd( "my" ).controlNamePredicate( s -> s.startsWith( "ctl" ) ).accept( control );

		assertThat( control.getControlName() ).isEqualTo( "_my.ctl" );
	}

	@Test
	public void controlNamePredicateGetsOriginalControlNameIfUnderscoreIgnoreIsFalse() {
		control.setControlName( "_ctl" );
		prefixer.prefixToAdd( "my" ).ignoreUnderscore( false ).controlNamePredicate( s -> s.startsWith( "ctl" ) ).accept( control );

		assertThat( control.getControlName() ).isEqualTo( "_ctl" );
	}
}
