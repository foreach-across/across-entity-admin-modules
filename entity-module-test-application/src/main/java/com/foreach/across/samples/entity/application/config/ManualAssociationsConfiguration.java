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

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.support.EntityPropertyRegistrationHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.core.support.ReflectionEntityInformation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

import static com.foreach.across.modules.entity.support.EntityConfigurationCustomizers.registerEntityQueryExecutor;

/**
 * @author Arne Vandamme
 * @since 3.3.0
 */
@Configuration
@RequiredArgsConstructor
public class ManualAssociationsConfiguration implements EntityConfigurer
{
	private final Map<Serializable, Book> books = new HashMap<>();
	private final Map<Serializable, Author> authors = new HashMap<>();

	private final EntityPropertyRegistrationHelper propertyRegistrars;
	private final AutoSuggestDataAttributeRegistrar autoSuggestData;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		installData();

		configureBooks( entities );
		configureAuthors( entities );
		configureAuthorsOnBook( entities );
		configureBooksOnAuthor( entities );
	}

	private void configureBooks( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .name( "book2" )
		        .displayName( "Book" )
		        .entityType( Book.class, true )
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
		        .listView( lvb -> lvb.entityQueryFilter(
				        eqf -> eqf.showProperties( "author", "reviewers" )
				                  .multiValue( "reviewers", "author" )
		        ) )
		        .createFormView()
		        .updateFormView()
		        .deleteFormView()
		        .show()
		;
	}

	private void configureAuthors( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .name( "author2" )
		        .displayName( "Author" )
		        .entityType( Author.class, true )
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
		        .show()
		        .viewElementType( ViewElementMode.FILTER_CONTROL, BootstrapUiElements.AUTOSUGGEST )
		        .viewElementType( ViewElementMode.FILTER_CONTROL.forMultiple(), BootstrapUiElements.AUTOSUGGEST )
		        .attribute(
				        autoSuggestData.entityQuery( "name ilike '{0}%' order by name desc" )
				                       .control( ctl -> ctl.minLength( 2 ) )
		        )
		;
	}

	private void configureAuthorsOnBook( EntitiesConfigurationBuilder entities ) {
		entities.withType( Book.class )
		        .properties(
				        props -> props.property( propertyRegistrars.entityIdProxy( "author" ).entityType( Author.class ).targetPropertyName( "authorId" ) )
				                      .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST )
				                      .and()
				                      .property( propertyRegistrars.entityIdProxy( "reviewers" )
				                                                   .entityType( Author.class )
				                                                   .targetPropertyName( "reviewerIds" ) )
				                      .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST )
				                      .attribute(
						                      autoSuggestData.entityQuery( "name ilike '%{0}' order by name desc" )
						                                     .control( ctl -> ctl.minLength( 1 ) )
				                      )
		        )
		        .createFormView(
				        vb -> vb.properties(
						        props -> props
								        .property( "author" )
								        .attribute(
										        autoSuggestData.entityQuery( ds -> ds
												        .suggestionsEql( "name contains '{0}' order by name asc" )
												        .maximumResults( 1 )
										        )
								        )
				        )
		        )
		        .updateFormView(
				        vb -> vb.properties(
						        props -> props.property( "author" )
						                      .attribute( autoSuggestData.dataSetId( "property-book2_createView.author" )
						                                                 .control( ctl -> ctl.showHint( false ) ) )
				        )
		        )
		;
	}

	private void configureBooksOnAuthor( EntitiesConfigurationBuilder entities ) {
		entities.withType( Author.class )
		        .association(
				        as -> as.name( "booksAuthored" )
				                .targetEntityType( Book.class )
				                .targetProperty( "author" )
				                .listView()
				                .createFormView()
				                .updateFormView()
				                .deleteFormView()
				                .show()
		        )
		        .association(
				        as -> as.name( "booksReviewed" )
				                .sourceProperty( "id" )
				                .targetEntityType( Book.class )
				                .targetProperty( "reviewerIds" )
				                .listView()
				                .createFormView()
				                .updateFormView()
				                .deleteFormView()
				                .show()
		        )
		        .association(
				        as -> as.name( "booksAuthoredEmbedded" )
				                .targetEntityType( Book.class )
				                .targetProperty( "author" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .listView()
				                .createFormView()
				                .updateFormView()
				                .deleteFormView()
				                .show()
		        )
		        .association(
				        as -> as.name( "booksReviewedEmbedded" )
				                .sourceProperty( "id" )
				                .targetEntityType( Book.class )
				                .targetProperty( "reviewerIds" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .listView()
				                .createFormView()
				                .updateFormView()
				                .deleteFormView()
				                .show()
		        );
	}

	private void installData() {
		Book book = new Book();
		book.setId( 1L );
		book.setTitle( "My Book" );
		books.put( book.getId(), book );

		Author john = new Author();
		john.setId( AuthorId.from( 1L ) );
		john.setName( "John Doe" );
		authors.put( john.getId(), john );

		Author jane = new Author();
		jane.setId( AuthorId.from( 2L ) );
		jane.setName( "Jane Doe" );
		authors.put( jane.getId(), jane );

		Author fred = new Author();
		fred.setId( AuthorId.from( 3L ) );
		fred.setName( "Fred Astaire" );
		authors.put( fred.getId(), fred );

		book.setAuthorId( john.getId() );
		book.setReviewerIds( Arrays.asList( jane.getId(), fred.getId() ) );
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

		@NotNull
		private AuthorId authorId;

		@NotNull
		@Builder.Default
		private List<AuthorId> reviewerIds = new ArrayList<>();
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
