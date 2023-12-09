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

package com.foreach.across.modules.entity.registry;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Locale;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
@SuppressWarnings("unchecked")
public class TestEntityModel
{
	private DefaultEntityModel<Object, Serializable> model;

	@BeforeEach
	public void before() {
		model = new DefaultEntityModel<>();
	}

	@Test
	public void printerCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setLabelPrinter( null );
		} );
	}

	@Test
	public void labelWithSpecificLocale() {
		Printer<Object> printer = mock( Printer.class );
		model.setLabelPrinter( printer );

		when( printer.print( "test", Locale.CANADA_FRENCH ) ).thenReturn( "hello" );
		assertEquals( "hello", model.getLabel( "test", Locale.CANADA_FRENCH ) );
	}

	@Test
	public void labelWithDefaultLocale() {
		Printer<Object> printer = mock( Printer.class );
		model.setLabelPrinter( printer );

		when( printer.print( "test", LocaleContextHolder.getLocale() ) ).thenReturn( "hello" );
		assertEquals( "hello", model.getLabel( "test" ) );
	}

	@Test
	public void entityFactoryCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setEntityFactory( null );
		} );
	}

	@Test
	public void createNew() {
		EntityFactory<Object> factory = mock( EntityFactory.class );
		model.setEntityFactory( factory );

		when( factory.createNew() ).thenReturn( "noargs" );
		when( factory.createNew( "one", "two" ) ).thenReturn( "twoargs" );

		assertEquals( "noargs", model.createNew() );
		assertEquals( "twoargs", model.createNew( "one", "two" ) );
	}

	@Test
	public void createDto() {
		EntityFactory<Object> factory = mock( EntityFactory.class );
		model.setEntityFactory( factory );

		when( factory.createDto( "existing" ) ).thenReturn( "dto" );

		assertEquals( "dto", model.createDto( "existing" ) );
	}

	@Test
	public void findOneMethodCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setFindOneMethod( null );
		} );
	}

	@Test
	public void findOne() {
		model.setFindOneMethod( id -> "go".equals( id ) ? "yes" : "no" );

		assertEquals( "no", model.findOne( 2 ) );
		assertEquals( "yes", model.findOne( "go" ) );
	}

	@Test
	public void saveMethodCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setSaveMethod( null );
		} );
	}

	@Test
	public void save() {
		model.setSaveMethod( id -> "go".equals( id ) ? "yes" : "no" );

		assertEquals( "no", model.save( 2 ) );
		assertEquals( "yes", model.save( "go" ) );
	}

	@Test
	public void deleteMethodCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setDeleteMethod( null );
		} );
	}

	@Test
	public void delete() {
		Consumer consumer = mock( Consumer.class );
		model.setDeleteMethod( consumer );

		model.delete( "entity" );

		verify( consumer ).accept( "entity" );
	}

	@Test
	public void entityInformationCannotBeNull() {
		assertThrows( IllegalArgumentException.class, () -> {
			model.setEntityInformation( null );
		} );
	}

	@Test
	public void isNew() {
		EntityInformation entityInformation = mock( EntityInformation.class );
		model.setEntityInformation( entityInformation );

		when( entityInformation.isNew( "new" ) ).thenReturn( true );

		assertTrue( model.isNew( "new" ) );
		assertFalse( model.isNew( "existing" ) );
	}

	@Test
	public void getId() {
		EntityInformation entityInformation = mock( EntityInformation.class );
		model.setEntityInformation( entityInformation );

		when( entityInformation.getId( "one" ) ).thenReturn( 1 );
		when( entityInformation.getId( "two" ) ).thenReturn( "2" );

		assertEquals( 1, model.getId( "one" ) );
		assertEquals( "2", model.getId( "two" ) );
	}

	@Test
	public void getIdType() {
		EntityInformation entityInformation = mock( EntityInformation.class );
		model.setEntityInformation( entityInformation );

		when( entityInformation.getIdType() ).thenReturn( BigDecimal.class );

		assertEquals( BigDecimal.class, model.getIdType() );
	}

	@Test
	public void getJavaType() {
		EntityInformation entityInformation = mock( EntityInformation.class );
		model.setEntityInformation( entityInformation );

		when( entityInformation.getJavaType() ).thenReturn( Date.class );

		assertEquals( Date.class, model.getJavaType() );
	}
}
