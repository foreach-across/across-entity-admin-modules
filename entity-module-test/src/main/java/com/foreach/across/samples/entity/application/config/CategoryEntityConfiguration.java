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

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.ViewCreationContext;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.samples.entity.EntityModuleTestApplication;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;

/**
 * Configures a dummy <strong>category</strong> entity.
 * This entity is completely fake and has no Spring data repository.  It is represented by a {@link Map} containing
 * all its properties. The entire entity is manually configured: configuration, properties, entity model and views.
 * <p>
 * <p/>
 * This is a test case for manual configuration of an entity, probably not much of a real life use case however.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Configuration
@EnableAcrossJpaRepositories(basePackageClasses = EntityModuleTestApplication.class)
public class CategoryEntityConfiguration implements EntityConfigurer
{
	private final List<Map<String, Object>> categoryRepository = new ArrayList<>();

	/**
	 * Builds the initial category repository.
	 */
	public CategoryEntityConfiguration() {
		Map<String, Object> tv = new HashMap<>();
		tv.put( "id", "tv" );
		tv.put( "name", "Televisions" );

		categoryRepository.add( tv );
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.create()
		        .as( Map.class )
		        .name( "category" )
		        .entityType( Map.class, false )
		        .displayName( "Category" )
		        .attribute( Validator.class, categoryValidator() )
		        .properties(
				        props -> props
						        .property( "id" )
						        .displayName( "Id" )
						        .propertyType( String.class )
						        .attribute( EntityAttributes.CONTROL_NAME, "[id]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .writable( true )
						        .spelValueFetcher( "get('id')" )
						        .order( 1 )
						        .and()
						        .property( "name" )
						        .displayName( "Name" )
						        .propertyType( String.class )
						        .attribute( EntityAttributes.CONTROL_NAME, "[name]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .writable( true )
						        .spelValueFetcher( "get('name')" )
						        .order( 2 )
		        )
		        .entityModel(
				        model -> model
						        .entityFactory( new CategoryEntityFactory() )
						        .entityInformation( new CategoryEntityInformation() )
						        .labelPrinter( ( o, locale ) -> (String) ( (Map) o ).get( "name" ) )
						        .findOneMethod( id -> categoryRepository.stream()
						                                                .filter( m -> id.equals(
								                                                m.get( "id" ) ) )
						                                                .findFirst().orElse( null ) )
						        .saveMethod(
								        category -> {
									        //Map<String, Object> category = (Map<String, Object>) cat;
									        Optional<Map<String, Object>> existing = categoryRepository
											        .stream()
											        .filter( m -> m.get( "id" ).equals( category.get( "id" ) ) )
											        .findFirst();

									        if ( existing.isPresent() ) {
										        existing.ifPresent( e -> e.putAll( category ) );
									        }
									        else {
										        categoryRepository.add( category );
									        }

									        return category;
								        }
						        )
						        .deleteMethod( categoryRepository::remove )
		        )
		        .listView( lvb -> lvb.pageFetcher( new EntityListViewPageFetcher()
		        {
			        @Override
			        public Page fetchPage( ViewCreationContext viewCreationContext,
			                               Pageable pageable,
			                               EntityView model ) {
				        return new PageImpl<>( categoryRepository );
			        }
		        } ) )
		        .createFormView( fvb -> fvb.showProperties( "id", "name" ) )
		        .updateFormView( fvb -> fvb.showProperties( "name" ) )
		        .deleteFormView( dvb -> dvb.showProperties( "." ) )
		        .show();

	}

	@Bean
	protected CategoryValidator categoryValidator() {
		return new CategoryValidator();
	}

	private static class CategoryValidator extends EntityValidatorSupport<Map<String, Object>>
	{
		@Override
		public boolean supports( Class<?> aClass ) {
			return Map.class.equals( aClass );
		}

		@Override
		protected void postValidation( Map<String, Object> entity, Errors errors ) {
			String prefix = StringUtils.removeEnd( errors.getNestedPath(), "." );
			errors.setNestedPath( "" );

			if ( StringUtils.defaultString( Objects.toString( entity.get( "id" ) ) ).length() == 0 ) {
				errors.rejectValue( prefix + "[id]", "NotBlank" );
			}
			if ( StringUtils.defaultString( Objects.toString( entity.get( "name" ) ) ).length() == 0 ) {
				errors.rejectValue( prefix + "[name]", "NotBlank" );
			}

			errors.pushNestedPath( "entity" );
		}
	}

	private static class CategoryEntityFactory implements EntityFactory<Map>
	{
		@Override
		public Map createNew( Object... args ) {
			return new HashMap<>();
		}

		@Override
		public Map createDto( Map entity ) {
			return new HashMap<>( (Map<?, ?>) entity );
		}
	}

	private static class CategoryEntityInformation implements EntityInformation<Map, String>
	{
		@Override
		public boolean isNew( Map map ) {
			return map.containsKey( "id" );
		}

		@Override
		public String getId( Map map ) {
			return (String) map.get( "id" );
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<Map> getJavaType() {
			return Map.class;
		}
	}
}
