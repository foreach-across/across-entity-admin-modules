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
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.EntityQueryExecutor;
import com.foreach.across.modules.entity.query.collections.CollectionEntityQueryExecutor;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.OptionsFormElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.options.OptionIterableBuilder;
import com.foreach.across.samples.entity.application.business.BooleanDummy;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Steven Gentens
 * @since 2.2.0
 */
@Configuration
public class BooleanDummyEntityConfiguration implements EntityConfigurer
{
	private final List<BooleanDummy> booleanDummyRepository = new ArrayList<>();

	public BooleanDummyEntityConfiguration() {
		booleanDummyRepository.add(
				BooleanDummy.builder().id( -1 )
				            .booleanCheckbox( true ).booleanRadio( true ).booleanSelect( true ).booleanSelectNonNull( true )
				            .primitiveBooleanCheckbox( true ).primitiveBooleanRadio( true ).primitiveBooleanSelect( true ).primitiveBooleanSelectNonNull( true )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( -2 )
				            .booleanCheckbox( false ).booleanRadio( false ).booleanSelect( false ).booleanSelectNonNull( false )
				            .primitiveBooleanCheckbox( false ).primitiveBooleanRadio( false ).primitiveBooleanSelect( false )
				            .primitiveBooleanSelectNonNull( false )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( -3 )
				            .booleanCheckbox( true ).booleanRadio( false ).booleanSelect( true ).booleanSelectNonNull( false )
				            .primitiveBooleanCheckbox( false ).primitiveBooleanRadio( true ).primitiveBooleanSelect( false )
				            .primitiveBooleanSelectNonNull( true )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( -4 )
				            .booleanSelectNonNull( false ).primitiveBooleanSelectNonNull( true )
				            .build()
		);
	}

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.create().as( BooleanDummy.class )
		        .name( "booleanDummy" )
		        .displayName( "Boolean dummy" )
		        .entityType( BooleanDummy.class, false )
		        .entityModel(
				        model -> model
						        .entityFactory( new BooleanDummyEntityConfiguration.BooleanDummyEntityFactory() )
						        .entityInformation( new BooleanDummyEntityConfiguration.BooleanDummyEntityInformation() )
						        .labelPrinter( ( o, locale ) -> "" + o.getId() )
						        .findOneMethod( id -> booleanDummyRepository.stream()
						                                                    .filter( m -> Objects.equals( m.getId(), Integer.valueOf( id.toString() ) ) )
						                                                    .findFirst().orElse( null ) )
						        .saveMethod(
								        booleanDummy -> {
									        Optional<BooleanDummy> existing = booleanDummyRepository
											        .stream()
											        .filter( m -> m.getId().equals( booleanDummy.getId() ) )
											        .findFirst();

									        if ( existing.isPresent() ) {
										        existing.ifPresent( e -> {
											                            e.setBooleanCheckbox( booleanDummy.getBooleanCheckbox() );
											                            e.setBooleanRadio( booleanDummy.getBooleanRadio() );
											                            e.setBooleanSelect( booleanDummy.getBooleanSelect() );
											                            e.setBooleanSelectNonNull( booleanDummy.getBooleanSelectNonNull() );
											                            e.setBooleanDefaultControl( booleanDummy.getBooleanDefaultControl() );
											                            e.setPrimitiveBooleanCheckbox( booleanDummy.isPrimitiveBooleanCheckbox() );
											                            e.setPrimitiveBooleanRadio( booleanDummy.isPrimitiveBooleanRadio() );
											                            e.setPrimitiveBooleanSelect( booleanDummy.isPrimitiveBooleanSelect() );
											                            e.setPrimitiveBooleanSelectNonNull( booleanDummy.isPrimitiveBooleanSelectNonNull() );
											                            e.setPrimitiveBooleanDefaultControl( booleanDummy.isPrimitiveBooleanDefaultControl() );
										                            }

										        );
									        }
									        else {
										        Integer nextId = booleanDummyRepository.stream()
										                                               .mapToInt( BooleanDummy::getId )
										                                               .min().orElse( 0 ) - 1;

										        booleanDummy.setId( nextId );
										        booleanDummyRepository.add( booleanDummy );
									        }
									        return booleanDummy;
								        }
						        )
						        .deleteMethod( booleanDummyRepository::remove )
		        )
		        .attribute(
				        ( config, attributes ) ->
						        attributes.setAttribute( EntityQueryExecutor.class,
						                                 new CollectionEntityQueryExecutor<>( booleanDummyRepository, config.getPropertyRegistry() ) )
		        )
		        .properties( props -> props
				        .property( "id" )
				        .viewElementType( ViewElementMode.FILTER_CONTROL, OptionsFormElementBuilderFactory.OPTIONS )
				        .attribute(
						        OptionIterableBuilder.class,
						        builderContext -> booleanDummyRepository.stream()
						                                                .map( dummy ->
								                                                      new OptionFormElementBuilder().label( "" + dummy.getId() )
								                                                                                    .value( dummy.getId() )
								                                                                                    .rawValue( dummy.getId() )
						                                                )
						                                                .collect( Collectors.toList() )
				        )
				        //.attribute( EntityAttributes.PROPERTY_REQUIRED, true )
				        .and()
				        .property( "primitiveBooleanCheckbox" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.CHECKBOX )
				        .and().property( "primitiveBooleanRadio" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.RADIO )
				        .and().property( "primitiveBooleanSelect" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.SELECT )
				        .and().property( "primitiveBooleanSelectNonNull" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.SELECT )
				        .and().property( "booleanCheckbox" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.CHECKBOX )
				        .and().property( "booleanRadio" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.RADIO )
				        .and().property( "booleanSelect" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.SELECT )
				        .and().property( "booleanSelectNonNull" )
				        .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.RADIO )
		        )
		        .listView( lvb -> lvb.entityQueryFilter( cfg -> cfg.showProperties( "id", "booleanRadio", "booleanSelect" ).multiValue( "booleanSelect" ) ) )
		        .createFormView( fvb -> fvb.showProperties( "booleanSelectNonNull", "primitiveBooleanSelectNonNull" ) )
		        .updateFormView( fvb -> fvb.showProperties( "*" ) )
		        .deleteFormView( dvb -> dvb.showProperties( "." ) )
		        .show();
	}

	public class BooleanDummyEntityFactory implements com.foreach.across.modules.entity.registry.EntityFactory<BooleanDummy>
	{
		@Override
		public BooleanDummy createNew( Object... args ) {
			return BooleanDummy.builder().build();
		}

		@Override
		public BooleanDummy createDto( BooleanDummy entity ) {
			return entity.toBuilder().build();
		}
	}

	public class BooleanDummyEntityInformation implements org.springframework.data.repository.core.EntityInformation<BooleanDummy, Integer>
	{
		@Override
		public boolean isNew( BooleanDummy entity ) {
			return entity.getId() == null;
		}

		@Override
		public Integer getId( BooleanDummy entity ) {
			return entity.getId();
		}

		@Override
		public Class<Integer> getIdType() {
			return Integer.class;
		}

		@Override
		public Class<BooleanDummy> getJavaType() {
			return BooleanDummy.class;
		}
	}
}
