/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.modules.bootstrapui.attributes;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.support.AttributeWitherFunction;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import org.junit.jupiter.api.Test;

import static com.foreach.across.modules.bootstrapui.attributes.BootstrapAttributes.attribute;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.3.0
 */
class TestBootstrapAttributes
{
	@Test
	void customAttribute() {
		assertAttribute( attribute.of( "someAttr" ) ).is( "someAttr" );
		assertAttribute( attribute.of( "someAttr" ).withValue( 123 ) ).is( "someAttr", 123 );
	}

	@Test
	void customDataAttribute() {
		assertAttribute( attribute.data( "name" ) ).is( "data-name" );
		assertAttribute( attribute.data( "name" ).withValue( 123 ) ).is( "data-name", 123 );
	}

	@Test
	void customAriaAttribute() {
		assertAttribute( attribute.aria( "name" ) ).is( "aria-name" );
		assertAttribute( attribute.aria( "name" ).withValue( 123 ) ).is( "aria-name", 123 );
	}

	@Test
	void role() {
		assertAttribute( attribute.role ).is( "role" );
		assertAttribute( attribute.role( "button" ) ).is( "role", "button" );
	}

	static AttributeDefaultValueMatcher assertAttribute( @NonNull DefaultValueAttributeWitherFunction function ) {
		return new AttributeDefaultValueMatcher( function );
	}

	static AttributeNameMatcher assertAttribute( @NonNull AttributeWitherFunction function ) {
		return new AttributeNameMatcher( function );
	}

	static AttributeSetterMatcher assertAttribute( @NonNull ViewElement.WitherSetter function ) {
		return new AttributeSetterMatcher( function );
	}

	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor
	static class AttributeNameMatcher
	{
		private final AbstractNodeViewElement node = mock( AbstractNodeViewElement.class );

		private final AttributeWitherFunction remover;

		public AttributeNameMatcher is( String attributeName ) {
			remover.removeFrom( node );
			verify( node ).removeAttribute( attributeName );
			reset( node );
			return this;
		}
	}

	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor
	static class AttributeSetterMatcher
	{
		private final AbstractNodeViewElement node = mock( AbstractNodeViewElement.class );

		private final ViewElement.WitherSetter setter;

		public AttributeSetterMatcher is( String attributeName, Object value ) {
			assertThat( setter ).isNotNull();
			setter.applyTo( node );
			verify( node ).setAttribute( eq( attributeName ), eq( value ) );
			reset( node );
			return this;
		}
	}

	@SuppressWarnings("unchecked")
	@RequiredArgsConstructor
	static class AttributeDefaultValueMatcher
	{
		private final AbstractNodeViewElement node = mock( AbstractNodeViewElement.class );

		private final DefaultValueAttributeWitherFunction function;

		public AttributeDefaultValueMatcher is( String attributeName, Object value ) {
			assertThat( function ).isNotNull();
			function.applyTo( node );
			verify( node ).setAttribute( eq( attributeName ), eq( value ) );
			reset( node );
			return this;
		}

		public AttributeDefaultValueMatcher is( String attributeName ) {
			function.removeFrom( node );
			verify( node ).removeAttribute( attributeName );
			reset( node );
			return this;
		}
	}
}
