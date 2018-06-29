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
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.annotations.EntityValidator;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyController;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.registry.properties.GenericEntityPropertyController;
import com.foreach.across.modules.entity.validators.EntityValidatorSupport;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.EmbeddedCollectionElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityQueryFilterProcessor;
import com.foreach.across.modules.hibernate.jpa.repositories.config.EnableAcrossJpaRepositories;
import com.foreach.across.samples.entity.EntityModuleTestApplication;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

	private final Map<Object, Integer> stockCounts = new HashMap<>();
	private final Map<Object, Manager> categoryManagers = new HashMap<>();
	private final Map<Object, List<Brand>> brands = new HashMap<>();

	@EntityValidator
	private Validator validator;

	/**
	 * Builds the initial category repository.
	 */
	public CategoryEntityConfiguration() {
		Map<String, Object> tv = new HashMap<>();
		tv.put( "id", "tv" );
		tv.put( "name", "Televisions" );

		Map<String, Object> smartphone = new HashMap<>();
		smartphone.put( "id", "smartphone" );
		smartphone.put( "name", "Smartphones" );

		categoryRepository.add( tv );
		categoryRepository.add( smartphone );

		stockCounts.put( "tv", 5 );
		categoryManagers.put( "tv", new Manager( "John Doe", "john@doe.com" ) );
		brands.put( "tv", Arrays.asList( new Brand( "SAM", "Samsung" ), new Brand( "PH", "Philips" ) ) );
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
						        .attribute( EntityAttributes.CONTROL_NAME, "entity[id]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .attribute( Sort.Order.class, new Sort.Order( "id" ) )
						        .attribute(
								        OptionIterableBuilder.class,
								        builderContext -> categoryRepository.stream()
								                                            .map( c -> (String) c.get( "id" ) )
								                                            .map( id -> new OptionFormElementBuilder().label( id )
								                                                                                      .value( id )
								                                                                                      .rawValue( id ) )
								                                            .collect( Collectors.toList() )
						        )
						        .attribute( EntityQueryFilterProcessor.ENTITY_QUERY_OPERAND, EntityQueryOps.EQ )
						        .viewElementType( ViewElementMode.FILTER_CONTROL.forMultiple(), BootstrapUiElements.SELECT )
						        .writable( true )
						        .spelValueFetcher( "get('id')" )
						        .order( 1 )
						        .and()
						        .property( "name" )
						        .displayName( "Name" )
						        .propertyType( String.class )
						        .attribute( EntityAttributes.CONTROL_NAME, "entity[name]" )
						        .attribute( TextboxFormElement.Type.class, TextboxFormElement.Type.TEXT )
						        .attribute( Sort.Order.class, new Sort.Order( "name" ) )
						        .writable( true )
						        .<Map>valueFetcher( map -> map.get( "name" ) )
						        .order( 2 )
						        .and( registerStockCountProperty() )
						        .and( registerGenerateIdProperty() )
						        .and( registerManagerProperty() )
						        .and( registerBrandsProperty() )
		        )
		        .entityModel(
				        model -> model
						        .entityFactory( new CategoryEntityFactory() )
						        .entityInformation( new CategoryEntityInformation() )
						        .labelPrinter( ( o, locale ) -> (String) o.get( "name" ) )
						        .findOneMethod( id -> categoryRepository.stream()
						                                                .filter( m -> id.equals(
								                                                m.get( "id" ) ) )
						                                                .findFirst().orElse( null ) )
						        .saveMethod(
								        category -> {
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
		        .listView(
				        lvb -> lvb.defaultSort( "name" )
				                  .entityQueryFilter( cfg -> cfg.showProperties( "id", "name" ).multiValue( "id" ) )
		        )
		        .createFormView( fvb -> fvb.showProperties( "id", "generateId", "name", "manager", "stockCount", "brands" ) )
		        .updateFormView( fvb -> fvb.showProperties( "name", "stockCount", "manager", "brands" ) )
		        .deleteFormView( dvb -> dvb.showProperties( "." ) )
		        .show()
		        .attribute( ( configuration, attributes ) ->
				                    attributes.setAttribute( EntityQueryExecutor.class,
				                                             new CollectionEntityQueryExecutor<>( categoryRepository, configuration.getPropertyRegistry() ) )
		        );
	}

	/**
	 * Add a custom integer property: the stock count.
	 */
	private Consumer<EntityPropertyRegistryBuilder> registerStockCountProperty() {
		return props ->
				props.property( "stockCount" )
				     .displayName( "Stock count" )
				     .propertyType( Integer.class )
				     .readable( true )
				     .writable( true )
				     .hidden( false )
						.<Map<String, Object>, Integer>controller(
								c -> c.valueFetcher( category -> stockCounts.get( category.get( "id" ) ) )
								      .addValidator( ( category, stockCount, errors, validationHints ) -> {
									      // stock count must be positive number`
									      if ( stockCount == null || stockCount < 0 ) {
										      errors.rejectValue( "", "must-be-positive", "Must be a positive number" );
									      }
								      } )
								      .saveConsumer( ( category, stockCount ) -> stockCounts.put( category.get( "id" ), stockCount ) )

						);
	}

	/**
	 * Add a custom checkbox, that when checked will generate an id.
	 * If checked it will first validate that id is empty, and when applied will generate a UUID as id.
	 * <p>
	 * If there is a validation error and the checkbox is checked, it should still be checked on the re-render.
	 */
	private Consumer<EntityPropertyRegistryBuilder> registerGenerateIdProperty() {
		return props ->
				props.property( "generateId" )
				     .displayName( "Generate Identity" )
				     .propertyType( Boolean.class )
				     .readable( false )
				     .writable( true )
				     .hidden( false )
						.<Map<String, Object>, Boolean>controller(
								c -> c.order( EntityPropertyController.BEFORE_ENTITY )
								      .valueFetcher( category -> false )
								      .addValidator( ( category, shouldGenerate, errors, validationHints ) -> {
									      // category name must not yet be filled in
									      if ( Boolean.TRUE.equals( shouldGenerate ) && !StringUtils.isEmpty( (String) category.get( "id" ) ) ) {
										      errors.rejectValue( "", "id-not-empty", "Unable to generate identity when already set." );
									      }
								      } )
								      .applyValueConsumer( ( category, shouldGenerate ) -> {
									      if ( Boolean.TRUE.equals( shouldGenerate ) ) {
										      category.put( "id", UUID.randomUUID().toString() );
									      }
								      } )

						);

	}

	/**
	 * Add a custom Manager property, a simple object with name and email representing the manager of a category.
	 * This is a single embedded entity that should only be saved after the category itself is saved.
	 * A reference using the unique category id is inserted in the categoryManagers map.
	 */
	private Consumer<EntityPropertyRegistryBuilder> registerManagerProperty() {
		return props -> {
			val controller = new GenericEntityPropertyController<Map<String, Object>, Manager>();
			controller.valueFetcher( category -> categoryManagers.getOrDefault( category.get( "id" ), new Manager() ) );
			controller.addValidator( validator );
			controller.saveConsumer( ( category, manager ) -> categoryManagers.put( category.get( "id" ), manager ) );

			props.property( "manager" )
			     .displayName( "Manager" )
			     .propertyType( Manager.class )
			     .readable( true )
			     .writable( true )
			     .hidden( false )
			     .controller( controller )
			     .attribute( Printer.class, (Printer<Manager>) ( manager, locale ) -> manager.getName() )
			     .viewElementType( ViewElementMode.FORM_WRITE, BootstrapUiElements.FIELDSET )
			     .attribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.of( "manager.*" ) )
			     .and()
			     .property( "manager.email" )
					.<TextboxFormElement>viewElementPostProcessor( ViewElementMode.CONTROL,
					                                               ( builderContext, textbox ) -> textbox.addCssClass( "custom-email" ) );
		};
	}

	/**
	 * Add a custom brands property: a list of Brand entities for a category.
	 * A single brand has a name and a code.
	 * When the entity is saved, an entry will be added to the categoryBrands map.
	 */
	private Consumer<EntityPropertyRegistryBuilder> registerBrandsProperty() {
		return props -> {
			val controller = new GenericEntityPropertyController<Map<String, Object>, List<Brand>>();
			controller.valueFetcher( category -> brands.getOrDefault( category.get( "id" ), Collections.emptyList() ) );
			controller.saveConsumer( ( category, list ) -> brands.put( category.get( "id" ), list ) );

			val memberController = new GenericEntityPropertyController<Map<String, Object>, Brand>();
			memberController.valueFetcher( category -> new Brand() );
			memberController.addValidator( validator );

			props.property( "brands" )
			     .displayName( "Brands" )
			     .propertyType( TypeDescriptor.collection( ArrayList.class, TypeDescriptor.valueOf( Brand.class ) ) )
			     .readable( true )
			     .writable( true )
			     .hidden( false )
			     .controller( controller )
			     .viewElementType( ViewElementMode.FORM_WRITE, EmbeddedCollectionElementBuilderFactory.ELEMENT_TYPE )
			     .attribute( EntityAttributes.FIELDSET_PROPERTY_SELECTOR, EntityPropertySelector.of( "brands[].*" ) )
			     .and()
			     .property( "brands[]" )
			     .controller( memberController );
		};
	}

	@Bean
	protected CategoryValidator categoryValidator() {
		return new CategoryValidator();
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Manager
	{
		@Length(max = 100)
		@NotBlank
		private String name;

		@Length(max = 250)
		@Email
		private String email;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Brand
	{
		@Length(max = 5)
		@NotBlank
		private String code;

		@Length(max = 100)
		@NotBlank
		private String name;
	}

	private static class CategoryValidator extends EntityValidatorSupport<Map<String, Object>>
	{
		@Override
		public boolean supports( Class<?> aClass ) {
			return Map.class.equals( aClass );
		}

		@Override
		protected void postValidation( Map<String, Object> entity, Errors errors, Object... validationHints ) {
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
