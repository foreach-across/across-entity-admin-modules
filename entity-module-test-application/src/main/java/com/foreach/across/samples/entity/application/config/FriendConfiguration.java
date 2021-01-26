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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.autosuggest.AutoSuggestDataAttributeRegistrar;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.query.EQType;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQueryFunctionHandler;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.support.EntityPropertyRegistrationHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.samples.entity.application.business.Application;
import com.foreach.across.samples.entity.application.business.Friend;
import com.foreach.across.samples.entity.application.business.Hobby;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.data.util.Streamable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class FriendConfiguration implements EntityConfigurer
{
	private final AutoSuggestDataAttributeRegistrar autoSuggestData;
	private final EntityPropertyRegistrationHelper proxyPropertyRegistrar;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( Application.class ).listView(
				lvb -> lvb.showProperties( "*" ).entityQueryFilter( eqf -> eqf.showProperties( "*" ).advancedMode( true ) ) );
		entities.withType( Friend.class )
		        .properties(
				        props -> props.property( proxyPropertyRegistrar.entityIdProxy( "hobby" )
				                                                       .entityType( Hobby.class )
				                                                       .targetPropertyName( "hobbyId" ) )
				                      .and()
				                      .property( "hobbyId" )
				                      .hidden( true )
				                      .and()
				                      .property( "users" )
				                      .viewElementType( ViewElementMode.CONTROL, BootstrapUiElements.AUTOSUGGEST )
				                      .attribute( autoSuggestData.entityQuery( "name ilike '%{0}%'" ) )
		        )
		        .listView(
				        lvb -> lvb.showProperties( ".", "~hobby" )
		        );

		entities.withType( Hobby.class )
		        .association(
				        ab -> ab.name( "users" )
				                .associationType( EntityAssociation.Type.EMBEDDED )
				                .targetEntityType( Friend.class )
				                .targetProperty( "hobby" )
				                .listView()
				                .detailView()
				                .updateFormView()
				                .createFormView()
				                .show()
		        );
	}

	@Bean
	@Exposed
	public EntityQueryFunctionHandler entityQueryFunctionHandler( List<RepositoryFactoryInformation> repositoryFactoryInformation ) {
		return new HackedRepos( repositoryFactoryInformation );
	}

	@RequiredArgsConstructor
	public static class HackedRepos implements EntityQueryFunctionHandler
	{
		private final List<RepositoryFactoryInformation> repositoryFactoryInformation;

		@Override
		public boolean accepts( String functionName, TypeDescriptor desiredType ) {
			return findMethod( functionName ).isPresent();
		}

		private Optional<Method> findMethod( String functionName ) {
			String[] split = StringUtils.split( functionName, "#" );
			Optional<RepositoryFactoryInformation> repository = repositoryFactoryInformation.stream().filter(
					ri -> Objects.equals( ri.getRepositoryInformation().getRepositoryInterface().getSimpleName(), split[0] ) ).findFirst();
			if ( repository.isPresent() ) {
				Streamable<Method> queryMethods = repository.get().getRepositoryInformation().getQueryMethods();
				Optional<Method> method = queryMethods.stream().filter( qm -> Objects.equals( qm.getName(), split[1] ) ).findFirst();
				return method;
			}
			return Optional.empty();
		}

		@Override
		public Object apply( String functionName, EQType[] arguments, TypeDescriptor desiredType, EQTypeConverter argumentConverter ) {
			Optional<Method> method = findMethod( functionName );
			List<Object> objects = new ArrayList<>();
			if ( method.isPresent() ) {
				Method m = method.get();
				objects.add( m );
				Class<?>[] parameterTypes = m.getParameterTypes();
//				if( parameterTypes.length != arguments.length ) {
//					throw new RuntimeException("Argument length differs");
//				}
				for ( int i = 0; i < arguments.length; i++ ) {
					Object converted = argumentConverter.convert( TypeDescriptor.valueOf( parameterTypes[i] ), arguments[i] );
					objects.add( converted );
				}
				return objects.toArray( new Object[0] );
			}
			throw new RuntimeException( "Should not come here" );
		}
	}
}
