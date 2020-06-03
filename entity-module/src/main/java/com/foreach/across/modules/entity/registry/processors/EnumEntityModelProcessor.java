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

package com.foreach.across.modules.entity.registry.processors;

import com.foreach.across.modules.entity.registry.DefaultEntityConfigurationProvider;
import com.foreach.across.modules.entity.registry.DefaultEntityModel;
import com.foreach.across.modules.entity.registry.MutableEntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.MutableEntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.support.ConvertedValuePrinter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Checks if the entity type is an enum, and if so, builds a default entity model if there is none yet.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
final class EnumEntityModelProcessor implements DefaultEntityConfigurationProvider.PostProcessor
{
	private final ConversionService mvcConversionService;

	@Override
	public void accept( MutableEntityConfiguration<?> mutableEntityConfiguration ) {
		Class<?> entityType = mutableEntityConfiguration.getEntityType();

		if ( entityType != null && entityType.isEnum() ) {
			Class<? extends Enum> enumType = (Class<? extends Enum>) entityType;

			EntityMessageCodeResolver codeResolver = mutableEntityConfiguration.getEntityMessageCodeResolver();
			codeResolver.setPrefixes( "enums." + enumType.getSimpleName() );
			codeResolver.setFallbackCollections( "enums" );

			configureLabelProperty( mutableEntityConfiguration );

			if ( !mutableEntityConfiguration.hasEntityModel() ) {
				DefaultEntityModel model = new DefaultEntityModel();
				model.setEntityInformation( new EnumEntityInformation( enumType ) );

				// A plain lambda gives a unexpected compiler error, so this code is written as a function
				model.setFindOneMethod( new Function<String, Enum>()
				{
					@Override
					public Enum apply( String name ) {
						return Enum.valueOf( enumType, name );
					}
				} );
				model.setLabelPrinter( createLabelPrinter( mutableEntityConfiguration.getPropertyRegistry() ) );

				mutableEntityConfiguration.setEntityModel( model );
			}
		}
	}

	private void configureLabelProperty( MutableEntityConfiguration<?> entityConfiguration ) {
		MutableEntityPropertyRegistry propertyRegistry = entityConfiguration.getPropertyRegistry();
		MutableEntityPropertyDescriptor descriptor = propertyRegistry.getProperty( EntityPropertyRegistry.LABEL );

		if ( descriptor != null ) {
			descriptor.setValueFetcher( rawValue -> {
				EntityMessageCodeResolver codeResolver = entityConfiguration.getEntityMessageCodeResolver();
				Enum enumValue = (Enum) rawValue;
				String messageCode = enumValue.name();
				String defaultLabel = EntityUtils.generateDisplayName( enumValue.name() );

				return codeResolver.getMessageWithFallback( messageCode, defaultLabel );
			} );
		}
	}

	private Printer createLabelPrinter( EntityPropertyRegistry propertyRegistry ) {
		return new ConvertedValuePrinter(
				mvcConversionService, propertyRegistry.getProperty( EntityPropertyRegistry.LABEL )
		);
	}

	@RequiredArgsConstructor
	private static class EnumEntityInformation implements EntityInformation<Enum, String>
	{
		private final Class<? extends Enum> enumType;

		@Override
		public boolean isNew( Enum entity ) {
			return false;
		}

		@Override
		public String getId( Enum entity ) {
			return entity.name();
		}

		@Override
		public Class<String> getIdType() {
			return String.class;
		}

		@Override
		public Class<Enum> getJavaType() {
			return (Class<Enum>) enumType;
		}
	}
}
