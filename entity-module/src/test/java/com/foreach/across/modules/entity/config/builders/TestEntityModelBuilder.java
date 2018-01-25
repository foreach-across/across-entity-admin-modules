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

import com.foreach.across.modules.entity.registry.DefaultEntityModel;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.registry.EntityModel;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@SuppressWarnings("unchecked")
public class TestEntityModelBuilder
{
	private EntityModelBuilder<Object> modelBuilder;
	private DefaultEntityModel<Object, Serializable> model;

	@Before
	public void before() {
		modelBuilder = new EntityModelBuilder<>();
		model = mock( DefaultEntityModel.class );
	}

	@Test
	public void newEntityModel() {
		Consumer delete = mock( Consumer.class );
		EntityModel newModel = modelBuilder.deleteMethod( delete ).build();

		assertNotNull( newModel );
		assertTrue( newModel instanceof DefaultEntityModel );

		newModel.delete( "test" );
		verify( delete ).accept( "test" );
	}

	@Test
	public void entityFactory() {
		EntityFactory value = mock( EntityFactory.class );
		assertSame( modelBuilder, modelBuilder.entityFactory( value ) );
		modelBuilder.apply( model );
		verify( model ).setEntityFactory( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void entityInformation() {
		EntityInformation value = mock( EntityInformation.class );
		assertSame( modelBuilder, modelBuilder.entityInformation( value ) );
		modelBuilder.apply( model );
		verify( model ).setEntityInformation( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void labelPrinter() {
		Printer value = mock( Printer.class );
		assertSame( modelBuilder, modelBuilder.labelPrinter( value ) );
		modelBuilder.apply( model );
		verify( model ).setLabelPrinter( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void findOneMethod() {
		Function value = mock( Function.class );
		assertSame( modelBuilder, modelBuilder.findOneMethod( value ) );
		modelBuilder.apply( model );
		verify( model ).setFindOneMethod( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void saveMethod() {
		UnaryOperator value = mock( UnaryOperator.class );
		assertSame( modelBuilder, modelBuilder.saveMethod( value ) );
		modelBuilder.apply( model );
		verify( model ).setSaveMethod( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void deleteMethod() {
		Consumer value = mock( Consumer.class );
		assertSame( modelBuilder, modelBuilder.deleteMethod( value ) );
		modelBuilder.apply( model );
		verify( model ).setDeleteMethod( value );
		verifyNoMoreInteractions( model );
	}

	@Test
	public void deleteMethodById() {
		Consumer value = mock( Consumer.class );
		AtomicReference<Consumer> actualConsumer = new AtomicReference<>();

		doAnswer( invocation -> {
			actualConsumer.set( invocation.getArgument( 0 ) );
			return null;
		} ).when( model ).setDeleteMethod( any( Consumer.class ) );

		assertSame( modelBuilder, modelBuilder.deleteByIdMethod( value ) );
		modelBuilder.apply( model );
		verify( model ).setDeleteMethod( any( Consumer.class ) );
		verifyNoMoreInteractions( model );

		assertNotNull( actualConsumer.get() );
		assertNotSame( value, actualConsumer.get() );
	}
}
