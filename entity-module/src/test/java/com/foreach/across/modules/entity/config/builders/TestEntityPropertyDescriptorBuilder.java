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

package com.foreach.across.modules.entity.config.builders;

import com.foreach.across.modules.entity.registry.properties.*;
import com.foreach.across.modules.entity.views.ViewElementLookupRegistry;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.TypeDescriptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

//import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class TestEntityPropertyDescriptorBuilder
{
	private EntityPropertyDescriptorBuilder builder;

	private EntityPropertyDescriptor descriptor;

	@BeforeEach
	public void before() {
		builder = new EntityPropertyDescriptorBuilder( "myprop" );
		descriptor = null;
	}

	@Test
	public void nameIsRequired() {
		assertThatThrownBy( () -> builder = new EntityPropertyDescriptorBuilder( null ) )
				.isInstanceOf( IllegalArgumentException.class );
	}

	@Test
	public void builderMethodOnInterfaceShouldReturnEmptyBuilder() {
		EntityPropertyDescriptorBuilder newBuilder = EntityPropertyDescriptor.builder( "myprop" );
		assertThat( newBuilder ).isNotNull();
		descriptor = newBuilder.build();
		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
	}

	@Test
	public void defaultPropertiesOnBlankDescriptor() {
		build();

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		assertThat( descriptor.getDisplayName() ).isEqualTo( "Myprop" );
		assertThat( descriptor );
		assertThat( descriptor.getPropertyType() ).isNull();
		assertThat( descriptor.getPropertyTypeDescriptor() ).isNull();
		assertThat( descriptor.getPropertyRegistry() ).isNull();
		assertThat( descriptor.isHidden() ).isFalse();
		assertThat( descriptor.isWritable() ).isFalse();
		assertThat( descriptor.isReadable() ).isTrue();
		assertThat( descriptor.attributeMap() ).hasSize( 1 );
		assertThat( descriptor.hasAttribute( ViewElementLookupRegistry.class ) ).isTrue();
		assertThat( descriptor.isNestedProperty() ).isFalse();
		assertThat( descriptor.getParentDescriptor() ).isNull();
		assertThat( descriptor.getController() ).isInstanceOf( GenericEntityPropertyController.class );

		GenericEntityPropertyController controller = (GenericEntityPropertyController) descriptor.getController();
		assertThat( controller.getValueFetcher() ).isNull();
	}

	@Test
	public void descriptorWithOriginal() {
		EntityPropertyDescriptor original = mock( EntityPropertyDescriptor.class );
		builder.original( original );

		build();

		when( original.getDisplayName() ).thenReturn( "parentDisplayName" );

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		assertThat( descriptor.getDisplayName() ).isEqualTo( "parentDisplayName" );
	}

	@Test
	public void nestedDescriptor() {
		EntityPropertyDescriptor parent = mock( EntityPropertyDescriptor.class );
		builder.parent( parent );

		build();

		assertThat( descriptor.isNestedProperty() ).isTrue();
		assertThat( descriptor.getParentDescriptor() ).isEqualTo( parent );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void customProperties() {
		ValueFetcher vf = mock( ValueFetcher.class );
		ViewElementBuilder veb = mock( ViewElementBuilder.class );

		builder.displayName( "My Property" )
		       .valueFetcher( vf )
		       .propertyType( String.class )
		       .propertyType( TypeDescriptor.valueOf( Long.class ) )
		       .hidden( true )
		       .writable( true )
		       .readable( false )
		       .viewElementType( ViewElementMode.CONTROL, "testControl" )
		       .viewElementBuilder( ViewElementMode.FORM_READ, veb )
		       .viewElementModeCaching( ViewElementMode.FORM_READ, false )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .controller( c -> c.order( 5 ) );

		build();

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		assertThat( descriptor.getDisplayName() ).isEqualTo( "My Property" );
		assertThat( descriptor.getPropertyType() ).isEqualTo( Long.class );
		assertThat( descriptor.getPropertyTypeDescriptor() ).isEqualTo( TypeDescriptor.valueOf( Long.class ) );
		assertThat( descriptor.getPropertyRegistry() ).isNull();
		assertThat( descriptor.isHidden() ).isTrue();
		assertThat( descriptor.isWritable() ).isTrue();
		assertThat( descriptor.isReadable() ).isFalse();
		assertThat( descriptor.getAttribute( "someAttribute" ) ).isEqualTo( "someAttributeValue" );

		ViewElementLookupRegistry lookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		assertThat( lookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "testControl" );
		assertThat( lookupRegistry.getViewElementBuilder( ViewElementMode.FORM_READ ) ).isSameAs( veb );
		assertThat( lookupRegistry.isCacheable( ViewElementMode.FORM_READ ) ).isFalse();

		assertThat( descriptor.getController().getOrder() ).isEqualTo( 5 );
		descriptor.getController().fetchValue( EntityPropertyBindingContext.forReading( "x" ) );
		verify( vf ).getValue( "x" );
	}

	@Test
	@SuppressWarnings("unchecked")
	public void updateExistingDescriptor() {
		SimpleEntityPropertyDescriptor existing = new SimpleEntityPropertyDescriptor( "otherprop" );
		existing.setAttribute( "originalAttribute", "originalAttributeValue" );

		ValueFetcher vf = mock( ValueFetcher.class );
		ViewElementBuilder veb = mock( ViewElementBuilder.class );

		builder.displayName( "My Property" )
		       .valueFetcher( vf )
		       .propertyType( String.class )
		       .propertyType( TypeDescriptor.valueOf( Long.class ) )
		       .hidden( true )
		       .writable( true )
		       .readable( false )
		       .viewElementType( ViewElementMode.CONTROL, "testControl" )
		       .viewElementBuilder( ViewElementMode.FORM_READ, veb )
		       .viewElementModeCaching( ViewElementMode.FORM_READ, false )
		       .attribute( "someAttribute", "someAttributeValue" )
		       .apply( existing );

		// name cannot be updated
		assertThat( existing.getName() ).isEqualTo( "otherprop" );
		assertThat( existing.getDisplayName() ).isEqualTo( "My Property" );

		assertThat( existing.getPropertyType() ).isEqualTo( Long.class );
		assertThat( existing.getPropertyTypeDescriptor() ).isEqualTo( TypeDescriptor.valueOf( Long.class ) );
		assertThat( existing.getPropertyRegistry() ).isNull();
		existing.getPropertyValue( "x" );
		verify( vf ).getValue( "x" );
		assertThat( existing.isHidden() ).isTrue();
		assertThat( existing.isWritable() ).isTrue();
		assertThat( existing.isReadable() ).isFalse();
		assertThat( existing.getAttribute( "originalAttribute" ) ).isEqualTo( "originalAttributeValue" );
		assertThat( existing.getAttribute( "someAttribute" ) ).isEqualTo( "someAttributeValue" );

		ViewElementLookupRegistry lookupRegistry = existing.getAttribute( ViewElementLookupRegistry.class );
		assertThat( lookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "testControl" );
		assertThat( lookupRegistry.getViewElementBuilder( ViewElementMode.FORM_READ ) ).isSameAs( veb );
		assertThat( lookupRegistry.isCacheable( ViewElementMode.FORM_READ ) ).isFalse();
	}

	@Test
	public void onlySimplePropertyType() {
		builder.propertyType( Long.class );

		build();

		assertThat( descriptor.getPropertyType() ).isEqualTo( Long.class );
		assertThat( descriptor.getPropertyTypeDescriptor() ).isEqualTo( TypeDescriptor.valueOf( Long.class ) );
	}

	@Test
	public void onlyTypeDescriptorPropertyType() {
		builder.propertyType( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Long.class ) ) );

		build();

		assertThat( descriptor.getPropertyType() ).isEqualTo( List.class );
		assertThat( descriptor.getPropertyTypeDescriptor() ).isEqualTo( TypeDescriptor.collection( List.class, TypeDescriptor.valueOf( Long.class ) ) );
	}

	@Test
	public void viewElementLookupRegistryIsInheritedIfNotCustomized() {
		MutableEntityPropertyDescriptor existing = new EntityPropertyDescriptorBuilder( "myprop" )
				.viewElementType( ViewElementMode.CONTROL, "someControlType" )
				.build();
		builder.original( existing );

		build();

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		ViewElementLookupRegistry existingLookupRegistry = existing.getAttribute( ViewElementLookupRegistry.class );

		assertThat( existingLookupRegistry ).isNotNull();
		assertThat( existingLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someControlType" );

		ViewElementLookupRegistry descriptorLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		assertThat( descriptorLookupRegistry ).isNotNull()
		                                      .isEqualTo( existingLookupRegistry )
		                                      .isSameAs( existingLookupRegistry );
		assertThat( descriptorLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) )
				.isEqualTo( "someControlType" );
	}

	@Test
	public void viewElementLookupRegistryDiffersFromExistingIfCustomized() {
		MutableEntityPropertyDescriptor existing = new EntityPropertyDescriptorBuilder( "myprop" )
				.viewElementType( ViewElementMode.CONTROL, "someControlType" )
				.viewElementType( ViewElementMode.VALUE, "someValueType" )
				.build();

		builder.original( existing )
		       .viewElementType( ViewElementMode.CONTROL, "someOtherControlType" );
		build();

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		ViewElementLookupRegistry existingLookupRegistry = existing.getAttribute( ViewElementLookupRegistry.class );

		assertThat( existingLookupRegistry ).isNotNull();
		assertThat( existingLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someControlType" );
		assertThat( existingLookupRegistry.getViewElementType( ViewElementMode.VALUE ) ).isEqualTo( "someValueType" );

		ViewElementLookupRegistry descriptorLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		assertThat( descriptorLookupRegistry ).isNotNull()
		                                      .isNotSameAs( existingLookupRegistry )
		                                      .isNotEqualTo( existingLookupRegistry );
		assertThat( descriptorLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someOtherControlType" );
		assertThat( descriptorLookupRegistry.getViewElementType( ViewElementMode.VALUE ) ).isEqualTo( "someValueType" );

		MutableEntityPropertyDescriptor parent = new EntityPropertyDescriptorBuilder( "myprop" )
				.original( existing )
				.build();

		builder.original( parent )
		       .viewElementType( ViewElementMode.CONTROL, "someOtherControlType" );
		build();

		assertThat( descriptor.getName() ).isEqualTo( "myprop" );
		existingLookupRegistry = existing.getAttribute( ViewElementLookupRegistry.class );

		assertThat( existingLookupRegistry ).isNotNull();
		assertThat( existingLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someControlType" );
		assertThat( existingLookupRegistry.getViewElementType( ViewElementMode.VALUE ) ).isEqualTo( "someValueType" );

		ViewElementLookupRegistry parentLookupRegistry = parent.getAttribute( ViewElementLookupRegistry.class );
		assertThat( parentLookupRegistry ).isNotNull()
		                                  .isEqualTo( existingLookupRegistry )
		                                  .isSameAs( existingLookupRegistry );
		assertThat( parentLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someControlType" );
		assertThat( parentLookupRegistry.getViewElementType( ViewElementMode.VALUE ) ).isEqualTo( "someValueType" );

		descriptorLookupRegistry = descriptor.getAttribute( ViewElementLookupRegistry.class );
		assertThat( descriptorLookupRegistry ).isNotNull()
		                                      .isNotSameAs( existingLookupRegistry )
		                                      .isNotEqualTo( existingLookupRegistry )
		                                      .isNotSameAs( parentLookupRegistry )
		                                      .isNotEqualTo( parentLookupRegistry );
		assertThat( descriptorLookupRegistry.getViewElementType( ViewElementMode.CONTROL ) ).isEqualTo( "someOtherControlType" );
		assertThat( descriptorLookupRegistry.getViewElementType( ViewElementMode.VALUE ) ).isEqualTo( "someValueType" );

	}

	private void build() {
		descriptor = builder.build();
	}

}
