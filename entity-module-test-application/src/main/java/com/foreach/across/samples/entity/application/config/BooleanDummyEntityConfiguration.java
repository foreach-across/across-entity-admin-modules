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
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.samples.entity.application.business.BooleanDummy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
				BooleanDummy.builder().id( "-1" )
				            .booleanCheckbox( true ).booleanRadio( true ).booleanSelect( true ).booleanSelectNonNull( true )
				            .primitiveBooleanCheckbox( true ).primitiveBooleanRadio( true ).primitiveBooleanSelect( true ).primitiveBooleanSelectNonNull( true )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( "-2" )
				            .booleanCheckbox( false ).booleanRadio( false ).booleanSelect( false ).booleanSelectNonNull( false )
				            .primitiveBooleanCheckbox( false ).primitiveBooleanRadio( false ).primitiveBooleanSelect( false )
				            .primitiveBooleanSelectNonNull( false )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( "-3" )
				            .booleanCheckbox( true ).booleanRadio( false ).booleanSelect( true ).booleanSelectNonNull( false )
				            .primitiveBooleanCheckbox( false ).primitiveBooleanRadio( true ).primitiveBooleanSelect( false )
				            .primitiveBooleanSelectNonNull( true )
				            .build()
		);
		booleanDummyRepository.add(
				BooleanDummy.builder().id( "-4" )
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
						        .labelPrinter( ( o, locale ) -> o.getId().toString() )
						        .findOneMethod( id ->
								                        booleanDummyRepository.stream()
								                                              .filter( m -> id.equals(
										                                              m.getId() ) )
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
										        Long nextId = booleanDummyRepository.stream()
										                                            .mapToLong( e -> Long.parseLong( e.getId() ) )
										                                            .min().orElse( 0L ) - 1;

										        booleanDummy.setId( nextId.toString() );
										        booleanDummyRepository.add( booleanDummy );
									        }
									        return booleanDummy;
								        }
						        )
						        .deleteMethod( booleanDummyRepository::remove )
		        )
		        .properties( props -> props
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
		        .listView( lvb -> lvb.pageFetcher( pageable -> new PageImpl<>( booleanDummyRepository ) )
		                             .entityQueryFilter( cfg -> cfg.showProperties( "booleanRadio" ) )
		                             .viewProcessor( new BooleanDummyListViewConfiguration() ) )
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

	public class BooleanDummyEntityInformation implements org.springframework.data.repository.core.EntityInformation<BooleanDummy, String>
	{
		@Override
		public boolean isNew( BooleanDummy entity ) {
			return StringUtils.isEmpty( entity.getId() );
		}

		@Override
		public String getId( BooleanDummy entity ) {
			return entity.getId();
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<BooleanDummy> getJavaType() {
			return BooleanDummy.class;
		}
	}
}
