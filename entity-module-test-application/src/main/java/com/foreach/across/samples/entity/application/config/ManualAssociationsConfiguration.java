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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.ReflectionEntityInformation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.foreach.across.modules.entity.support.EntityConfigurationCustomizers.registerEntityQueryExecutor;

/**
 * @author Arne Vandamme
 * @since 3.3.0
 */
@Configuration
public class ManualAssociationsConfiguration implements EntityConfigurer
{
	private final Map<Serializable, Book> books = new HashMap<>();
	private final Map<Serializable, Author> authors = new HashMap<>();

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		installData();

		configureBooks( entities );
		configureAuthors( entities );
	}

	private void configureBooks( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .name( "book2" )
		        .displayName( "Book" )
		        .entityType( Book.class, true )
		        .as( Book.class )
		        .properties( props -> props.property( "id" ).hidden( true ) )
		        .entityModel(
				        model -> model.entityFactory( EntityFactory.of( Book::new ) )
				                      .entityInformation( new ReflectionEntityInformation<>( Book.class ) )
				                      .findOneMethod( books::get )
				                      .labelPrinter( Book::getTitle )
				                      .saveMethod( book -> {
					                      if ( book.getId() == null ) {
						                      book.setId( System.currentTimeMillis() );
					                      }
					                      books.put( book.getId(), book );
					                      return book;
				                      } )
				                      .deleteByIdMethod( books::remove )
		        )
		        .and( registerEntityQueryExecutor( books::values ) )
		        .detailView()
		        .listView()
		        .createFormView()
		        .updateFormView()
		        .deleteFormView()
		        .show();
	}

	private void configureAuthors( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .name( "author2" )
		        .displayName( "Author" )
		        .entityType( Author.class, true )
		        .as( Author.class )
		        .properties( props -> props.property( "id" ).hidden( true ) )
		        .entityModel(
				        model -> model.entityFactory( EntityFactory.of( Author::new ) )
				                      .entityInformation( new ReflectionEntityInformation<>( Author.class ) )
				                      .findOneMethod( authors::get )
				                      .labelPrinter( Author::getName )
				                      .saveMethod( author -> {
					                      if ( author.getId() == null ) {
						                      author.setId( AuthorId.from( System.currentTimeMillis() ) );
					                      }
					                      authors.put( author.getId(), author );
					                      return author;
				                      } )
				                      .deleteByIdMethod( authors::remove )
		        )
		        .and( registerEntityQueryExecutor( authors::values ) )
		        .detailView()
		        .listView()
		        .createFormView()
		        .updateFormView()
		        .deleteFormView()
		        .show();
	}

	private void installData() {
		Book book = new Book();
		book.setId( 1L );
		book.setTitle( "My Book" );
		books.put( book.getId(), book );

		Author author = new Author();
		author.setId( AuthorId.from( 1L ) );
		author.setName( "John Doe" );
		authors.put( author.getId(), author );

		book.setAuthorId( author.getId() );
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder(toBuilder = true)
	public static class Book
	{
		@Id
		private Long id;

		@NotBlank
		@Length(max = 255)
		private String title;

		private AuthorId authorId;
	}

	@Data
	@NoArgsConstructor
	@EqualsAndHashCode(of = "id")
	public static class Author
	{
		@Id
		private AuthorId id;

		@NotBlank
		@Length(max = 255)
		private String name;
	}

	@Getter
	@EqualsAndHashCode
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AuthorId implements Serializable
	{
		private static final long serialVersionUID = 42L;

		private final long id;

		public static AuthorId from( long id ) {
			return new AuthorId( id );
		}

		public static AuthorId from( String id ) {
			return from( Long.parseLong( id ) );
		}

		@Override
		public String toString() {
			return "" + id;
		}
	}
}
