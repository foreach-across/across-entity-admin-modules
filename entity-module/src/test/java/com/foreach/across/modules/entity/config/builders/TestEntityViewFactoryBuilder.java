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

import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.views.*;
import com.foreach.across.modules.entity.views.processors.*;
import com.foreach.across.modules.entity.views.processors.support.EntityViewProcessorRegistry;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class TestEntityViewFactoryBuilder
{
	@Mock(lenient = true)
	private AutowireCapableBeanFactory beanFactory;

	@Mock(lenient = true)
	private DispatchingEntityViewFactory dispatchingViewFactory;

	private EntityViewFactoryBuilder builder;
	private EntityViewProcessorRegistry processors;

	@BeforeEach
	void before() {
		processors = new EntityViewProcessorRegistry();

		when( dispatchingViewFactory.getProcessorRegistry() ).thenReturn( processors );
		when( beanFactory.createBean( DispatchingEntityViewFactory.class ) ).thenReturn( dispatchingViewFactory );

		builder = new EntityViewFactoryBuilder( beanFactory ).factoryType( DispatchingEntityViewFactory.class );
	}

	@Test
	void andAppliesAdditionalConsumer() {
		Consumer<EntityViewFactoryBuilder> consumer = mock( Consumer.class );
		assertSame( builder, builder.and( consumer ) );
		verify( consumer ).accept( builder );
	}

	@Test
	void buildRequiresAFactoryToBeSet() {
		Assertions.assertThatExceptionOfType( IllegalArgumentException.class )
		          .isThrownBy( () -> builder.factoryType( null ).build() );
	}

	@Test
	void defaultCreatesSingleEntityViewFactory() {
		builder.factoryType( EntityViewFactory.class );

		EntityViewFactory factory = mock( EntityViewFactory.class );
		when( beanFactory.createBean( EntityViewFactory.class ) ).thenReturn( factory );

		assertSame( builder.build(), factory );
	}

	@Test
	void attributesAreRegistered() {
		builder.attribute( "key", "value" )
		       .attribute( String.class, "my string" )
		       .attribute( ( ( entityViewFactory, attributes ) -> {
			       attributes.setAttribute( "key", "updatedValue" );
			       attributes.setAttribute( "factory", entityViewFactory );
		       } ) );

		DispatchingEntityViewFactory factory = new DefaultEntityViewFactory();
		when( beanFactory.createBean( DispatchingEntityViewFactory.class ) ).thenReturn( factory );

		EntityViewFactory built = builder.build();
		assertSame( factory, built );
		assertEquals( "updatedValue", built.getAttribute( "key" ) );
		assertEquals( "my string", built.getAttribute( String.class ) );
		assertSame( factory, built.getAttribute( "factory" ) );
	}

	@Test
	void postProcessHasNullRegistryIfFactoryTypeNotDispatching() {
		AtomicReference<EntityViewFactory> factoryRef = new AtomicReference<>();
		AtomicReference<EntityViewProcessorRegistry> registryRef = new AtomicReference<>();

		builder.factoryType( EntityViewFactory.class )
		       .postProcess( ( factory, registry ) -> {
			       factoryRef.set( factory );
			       registryRef.set( registry );
		       } );

		EntityViewFactory factory = mock( EntityViewFactory.class );
		when( beanFactory.createBean( EntityViewFactory.class ) ).thenReturn( factory );

		assertSame( factory, builder.build() );
		assertSame( factory, factoryRef.get() );
		assertNull( registryRef.get() );
	}

	@Test
	void postProcessSingleProcessor() {
		AtomicReference<EntityViewProcessor> postProcessed = new AtomicReference<>();

		EntityViewProcessor viewProcessor = mock( EntityViewProcessor.class );
		builder.viewProcessor( viewProcessor )
		       .postProcess( viewProcessor.getClass(), postProcessed::set )
		       .build();

		assertSame( viewProcessor, postProcessed.get() );
	}

	@Test
	void postProcessSingleProcessorNotCalledIfProcessorNotPresent() {
		EntityViewProcessor viewProcessor = mock( EntityViewProcessor.class );
		builder
				.postProcess( viewProcessor.getClass(), p -> {
					throw new IllegalStateException( "should not get here" );
				} )
				.build();
	}

	@Test
	void postProcessSingleProcessorNotCalledIfNotDispatching() {
		EntityViewProcessor viewProcessor = mock( EntityViewProcessor.class );
		builder.factory( mock( EntityViewFactory.class ) )
		       .viewProcessor( viewProcessor )
		       .postProcess( viewProcessor.getClass(), p -> {
			       throw new IllegalStateException( "should not get here" );
		       } )
		       .build();
	}

	@Test
	void templateValue() {
		assertSame( builder, builder.template( "templateName" ) );
		assertSame( dispatchingViewFactory, builder.build() );

		Optional<TemplateViewProcessor> templateViewProcessor = processors.getProcessor( TemplateViewProcessor.class.getName(), TemplateViewProcessor.class );
		assertTrue( templateViewProcessor.isPresent() );
		templateViewProcessor.ifPresent( p -> assertEquals( new TemplateViewProcessor( "templateName" ), p ) );

		builder.template( "other" ).build();
		templateViewProcessor = processors.getProcessor( TemplateViewProcessor.class.getName(), TemplateViewProcessor.class );
		assertTrue( templateViewProcessor.isPresent() );
		templateViewProcessor.ifPresent( p -> assertEquals( new TemplateViewProcessor( "other" ), p ) );
	}

	@Test
	void messagePrefixes() {
		assertSame( builder, builder.messagePrefix( "one", "two" ) );
		assertSame( dispatchingViewFactory, builder.build() );

		Optional<MessagePrefixingViewProcessor> processor
				= processors.getProcessor( MessagePrefixingViewProcessor.class.getName(), MessagePrefixingViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertEquals( new MessagePrefixingViewProcessor( "one", "two" ), p ) );

		builder.messagePrefix( "other" ).build();
		processor
				= processors.getProcessor( MessagePrefixingViewProcessor.class.getName(), MessagePrefixingViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertEquals( new MessagePrefixingViewProcessor( "other" ), p ) );
	}

	@Test
	void allowableAction() {
		assertSame( builder, builder.requiredAllowableAction( AllowableAction.ADMINISTER ) );
		assertSame( dispatchingViewFactory, builder.build() );

		Optional<ActionAllowedAuthorizationViewProcessor> processor
				= processors.getProcessor( ActionAllowedAuthorizationViewProcessor.class.getName(), ActionAllowedAuthorizationViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertEquals( AllowableAction.ADMINISTER, p.getRequiredAllowableAction() ) );

		builder.requiredAllowableAction( AllowableAction.CREATE ).build();
		processor
				= processors.getProcessor( ActionAllowedAuthorizationViewProcessor.class.getName(), ActionAllowedAuthorizationViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertEquals( AllowableAction.CREATE, p.getRequiredAllowableAction() ) );
	}

	@Test
	void propertyRegistry() {
		MutableEntityPropertyRegistry propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		assertSame( builder, builder.propertyRegistry( propertyRegistry ) );
		assertSame( dispatchingViewFactory, builder.build() );

		Optional<EntityPropertyRegistryViewProcessor> processor
				= processors.getProcessor( EntityPropertyRegistryViewProcessor.class.getName(), EntityPropertyRegistryViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertEquals( new EntityPropertyRegistryViewProcessor( propertyRegistry ), p ) );
	}

	@Test
	void configureProperties() {
		MutableEntityPropertyRegistry propertyRegistry = mock( MutableEntityPropertyRegistry.class );
		assertSame( dispatchingViewFactory, builder.propertyRegistry( propertyRegistry ).build() );
		assertSame( builder, builder.properties( props -> props.property( "name" ).displayName( "test" ) ) );
		assertSame( dispatchingViewFactory, builder.build() );

		verify( propertyRegistry ).register( any() );
	}

	@Test
	void propertiesToShowAndViewElementMode() {
		PropertyRenderingViewProcessor renderingViewProcessor = new PropertyRenderingViewProcessor();
		when( beanFactory.createBean( PropertyRenderingViewProcessor.class ) ).thenReturn( renderingViewProcessor );

		assertSame( builder, builder.showProperties( "one", "two" ).showProperties( ".", "three" ) );
		assertSame( dispatchingViewFactory, builder.build() );

		Optional<PropertyRenderingViewProcessor> processor
				= processors.getProcessor( PropertyRenderingViewProcessor.class.getName(), PropertyRenderingViewProcessor.class );
		assertTrue( processor.isPresent() );
		processor.ifPresent( p -> assertSame( renderingViewProcessor, p ) );

		PropertyRenderingViewProcessor expected = new PropertyRenderingViewProcessor();
		expected.setSelector( EntityPropertySelector.of( "one", "two", "three" ) );
		assertEquals( expected, renderingViewProcessor );

		builder.viewElementMode( ViewElementMode.CONTROL ).build();
		expected.setViewElementMode( ViewElementMode.CONTROL );
		assertEquals( expected, renderingViewProcessor );

		builder.showProperties( "*" ).viewElementMode( ViewElementMode.VALUE ).build();
		expected.setSelector( EntityPropertySelector.of( "*" ) );
		expected.setViewElementMode( ViewElementMode.VALUE );
		assertEquals( expected, renderingViewProcessor );
	}

	@Test
	void processors() {
		EntityViewProcessor one = mock( EntityViewProcessor.class );
		EntityViewProcessor two = mock( SimpleEntityViewProcessorAdapter.class );

		assertSame(
				dispatchingViewFactory,
				builder.viewProcessor( one )
				       .viewProcessor( two, 0 )
				       .viewProcessor( "two", one )
				       .viewProcessor( "three", one, 10 )
				       .build()
		);

		Optional<EntityViewProcessorRegistry.EntityViewProcessorRegistration> actual = processors.getProcessorRegistration( one.getClass().getName() );
		assertTrue( actual.isPresent() );
		actual.ifPresent( r -> assertSame( one, r.getProcessor() ) );

		actual = processors.getProcessorRegistration( "two" );
		assertTrue( actual.isPresent() );
		actual.ifPresent( r -> assertSame( one, r.getProcessor() ) );

		actual = processors.getProcessorRegistration( "three" );
		assertTrue( actual.isPresent() );
		actual.ifPresent( r -> {
			assertSame( one, r.getProcessor() );
			assertEquals( 10, r.getOrder() );
		} );

		actual = processors.getProcessorRegistration( two.getClass().getName() );
		assertTrue( actual.isPresent() );
		actual.ifPresent( r -> {
			assertEquals( 0, r.getOrder() );
			assertSame( two, r.getProcessor() );
		} );

		before();

		builder.removeViewProcessor( one.getClass().getName() ).removeViewProcessor( "two" ).viewProcessor( one ).build();
		assertTrue( processors.contains( one.getClass().getName() ) );
		assertFalse( processors.contains( "two" ) );
	}

	@Test
	void factoryInstanceTakePrecedence() {
		EntityViewFactory expected = mock( EntityViewFactory.class );

		builder.factoryType( DispatchingEntityViewFactory.class )
		       .factory( expected );

		EntityViewFactory built = builder.build();
		assertSame( expected, built );
		verify( beanFactory, never() ).createBean( any() );
	}
}
