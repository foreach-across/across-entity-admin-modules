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

package com.foreach.across.modules.entity.support;

import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import lombok.NonNull;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.util.Assert;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * Helper for resolving message codes in the context of an EntityConfiguration.
 * Takes into account the current {@link Locale} for resolving the messages.
 *
 * @author Arne Vandamme
 */
public class EntityMessageCodeResolver implements MessageSourceAware, MessageCodesResolver
{
	private static final Logger LOG = LoggerFactory.getLogger( EntityMessageCodeResolver.class );
	private static final Object[] NO_ARGUMENTS = new Object[0];

	private MessageSource messageSource;
	private EntityConfiguration<?> entityConfiguration;

	private MessageCodesResolver errorCodesResolver;

	private String[] prefix = new String[0];
	private String[] subCollections = new String[0];
	private String[] fallbackCollections = new String[0];

	private String[] generatedPrefixes = new String[0];

	public EntityMessageCodeResolver( EntityMessageCodeResolver original ) {
		messageSource = original.messageSource;
		entityConfiguration = original.entityConfiguration;
		subCollections = original.subCollections;
		fallbackCollections = original.fallbackCollections;
		errorCodesResolver = original.errorCodesResolver;

		if ( original.prefix != null ) {
			setPrefixes( original.prefix );
		}
	}

	public EntityMessageCodeResolver() {
		DefaultMessageCodesResolver errorCodesResolver = new DefaultMessageCodesResolver();
		errorCodesResolver.setPrefix( "validation." );

		this.errorCodesResolver = errorCodesResolver;
	}

	/**
	 * If the item key is empty, only the prefixes will be generated.
	 */
	static String[] generateCodes( String[] rootCollections, String[] subCollections, String itemKey ) {
		Assert.notNull( rootCollections, "rootCollections must be specified" );
		Assert.notNull( subCollections, "subCollections must be specified" );
		Assert.notNull( itemKey, "itemKey must be specified" );

		if ( rootCollections.length == 0 && subCollections.length == 0 ) {
			return itemKey.isEmpty() ? new String[0] : new String[] { itemKey };
		}

		int index = 0;

		String[] codes;

		String suffix = itemKey.isEmpty() ? "" : "." + itemKey;

		List<String> messageCodes = new ArrayList<>( ( rootCollections.length + 1 ) * ( subCollections.length + 1 ) );

		if ( rootCollections.length == 0 ) {
			for ( String subCollection : subCollections ) {
				if ( !subCollection.isEmpty() ) {
					messageCodes.add( subCollection + suffix );
				}
			}
			if ( !itemKey.isEmpty() ) {
				messageCodes.add( itemKey );
			}
		}
		else {
			for ( String rootCollection : rootCollections ) {
				String prefix = rootCollection.isEmpty() ? "" : rootCollection + ".";
				for ( String subCollection : subCollections ) {
					if ( !subCollection.isEmpty() ) {
						messageCodes.add( prefix + subCollection + suffix );
					}
				}
				messageCodes.add(
						itemKey.isEmpty() ? rootCollection : ( rootCollection.isEmpty() ? itemKey : rootCollection + suffix ) );
			}
		}

		return messageCodes.toArray( new String[messageCodes.size()] );
	}

	/**
	 * Replace all prefixes.
	 *
	 * @param prefix list
	 */
	public void setPrefixes( @NonNull String... prefix ) {
		this.prefix = prefix;
		generatedPrefixes = buildMessageCodes( "", true );
	}

	/**
	 * Add a new (first) prefix to use for building message codes.
	 *
	 * @param prefix list
	 */
	public void addPrefixes( String... prefix ) {
		setPrefixes( ArrayUtils.addAll( prefix, this.prefix ) );
	}

	/**
	 * Set the prefixes that should be applied only if fallback generation is specified.
	 * These prefixes will be ordered after the primary prefixes.
	 *
	 * @param fallbackCollections to try
	 */
	public void setFallbackCollections( @NonNull String... fallbackCollections ) {
		this.fallbackCollections = fallbackCollections;
		generatedPrefixes = buildMessageCodes( "", true );
	}

	/**
	 * Set a specific custom resolver that should be used for error code resolving.
	 * By default the current resolver is used with a <strong>validation</strong> prefix.
	 * <p/>
	 * The error codes resolver will be used it this resolved is used for resolving error messages.
	 *
	 * @param errorCodesResolver to use
	 */
	public void setErrorCodesResolver( @NonNull MessageCodesResolver errorCodesResolver ) {
		this.errorCodesResolver = errorCodesResolver;
	}

	@Override
	public void setMessageSource( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	public void setEntityConfiguration( EntityConfiguration<?> entityConfiguration ) {
		this.entityConfiguration = entityConfiguration;
	}

	public String getNameSingular() {
		return getNameSingular( LocaleContextHolder.getLocale() );
	}

	public String getNameSingularInline() {
		return getNameSingularInline( LocaleContextHolder.getLocale() );
	}

	public String getNamePlural() {
		return getNamePlural( LocaleContextHolder.getLocale() );
	}

	public String getNamePluralInline() {
		return getNamePluralInline( LocaleContextHolder.getLocale() );
	}

	public String getNameSingular( Locale locale ) {
		return getMessage( "name.singular", entityConfiguration.getDisplayName(), locale );
	}

	public String getNameSingularInline( Locale locale ) {
		return getMessage( "name.singular.inline", StringUtils.uncapitalize( getNameSingular( locale ) ), locale );
	}

	public String getNamePlural( Locale locale ) {
		return getMessage( "name.plural", generatePlural( getNameSingular( locale ) ), locale );
	}

	private String generatePlural( String name ) {
		return English.plural( name );
	}

	public String getNamePluralInline( Locale locale ) {
		return getMessage( "name.plural.inline", StringUtils.uncapitalize( getNamePlural( locale ) ), locale );
	}

	public String getPropertyDisplayName( EntityPropertyDescriptor descriptor ) {
		return getPropertyDisplayName( descriptor, LocaleContextHolder.getLocale() );
	}

	public String getPropertyDescription( EntityPropertyDescriptor descriptor ) {
		return getPropertyDescription( descriptor, LocaleContextHolder.getLocale() );
	}

	public String getPropertyMessage( EntityPropertyDescriptor descriptor, String subKey, String defaultValue ) {
		return getPropertyMessage( descriptor, subKey, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getPropertyDisplayName( EntityPropertyDescriptor descriptor, Locale locale ) {
		return getPropertyMessage( descriptor, null, descriptor.getDisplayName(), locale );
	}

	public String getPropertyDescription( EntityPropertyDescriptor descriptor, Locale locale ) {
		return getPropertyMessage( descriptor, "description", "", locale );
	}

	public String getPropertyMessage( EntityPropertyDescriptor descriptor,
	                                  String subKey,
	                                  String defaultValue,
	                                  Locale locale ) {
		String code = "properties." + descriptor.getName() + ( subKey != null ? "[" + subKey + "]" : "" );
		return getMessageWithFallback( code, new Object[0], defaultValue, locale );
	}

	public String getMessage( String code, String defaultValue ) {
		return getMessage( code, NO_ARGUMENTS, defaultValue );
	}

	public String getMessage( String code, String defaultValue, Locale locale ) {
		return getMessage( code, NO_ARGUMENTS, defaultValue, locale );
	}

	public String getMessage( String code, Object[] arguments, String defaultValue ) {
		return getMessage( code, arguments, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getMessage( String code, Object[] arguments, String defaultValue, Locale locale ) {
		return messageSource.getMessage(
				buildMessageSourceResolvable( code, false, arguments, defaultValue ),
				locale
		);
	}

	public String getMessageWithFallback( String code, String defaultValue ) {
		return getMessageWithFallback( code, NO_ARGUMENTS, defaultValue );
	}

	public String getMessageWithFallback( String code, String defaultValue, Locale locale ) {
		return getMessageWithFallback( code, NO_ARGUMENTS, defaultValue, locale );
	}

	public String getMessageWithFallback( String code, Object[] arguments, String defaultValue ) {
		return getMessageWithFallback( code, arguments, defaultValue, LocaleContextHolder.getLocale() );
	}

	public String getMessageWithFallback( String code, Object[] arguments, String defaultValue, Locale locale ) {
		return messageSource.getMessage( buildMessageSourceResolvable( code, true, arguments, defaultValue ), locale );
	}

	public String getMessage( MessageSourceResolvable messageSourceResolvable ) {
		return messageSource.getMessage( messageSourceResolvable, LocaleContextHolder.getLocale() );
	}

	public String getMessage( MessageSourceResolvable messageSourceResolvable, Locale locale ) {
		return messageSource.getMessage( messageSourceResolvable, locale );
	}

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName ) {
		return addPrefixesToMessageCodes( errorCodesResolver.resolveMessageCodes( errorCode, objectName ), true );
	}

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName, String field, Class<?> fieldType ) {
		String[] codes = errorCodesResolver.resolveMessageCodes( errorCode, objectName, field, fieldType );

		return Stream.of( generatedPrefixes )
		             .flatMap( prefix -> Stream.of( codes ).map( code -> prefix.isEmpty() ? code : prefix + "." + code ) )
		             .toArray( String[]::new );
	}

	@Deprecated
	public String[] addPrefixesToMessageCodes( String[] codes, boolean includeFallback ) {
		return includeFallback
				? addPrefix( ArrayUtils.addAll( prefix, fallbackCollections ), codes )
				: addPrefix( prefix, codes );
	}

	/**
	 * Generate the actual codes to lookup based on the registered prefixes.
	 *
	 * @param code requested
	 * @return message codes to lookup
	 */
	public String[] buildMessageCodes( String code ) {
		return buildMessageCodes( code, false );
	}

	/**
	 * Generate the actual codes to lookup based on the registered prefixes.
	 * Will not include fallback collections.
	 *
	 * @param code            requested
	 * @param includeFallback should fallback collections be included
	 * @return message codes to lookup
	 */
	public String[] buildMessageCodes( String code, boolean includeFallback ) {
		return generateCodes( includeFallback ? ArrayUtils.addAll( prefix, fallbackCollections ) : prefix, subCollections, code );
	}

	/**
	 * Create a custom prefixed resolver where the additional prefixes will actually be applied as
	 * suffixes for the already registered prefixes.  This will generate an exponential number of codes depending
	 * on the number of original prefixes and additional prefixes.
	 * <p/>
	 * Used to specify scoped code lookups in for example view rendering, where the primary prefixes always stay the same.
	 *
	 * @param additionalPrefixes to add
	 * @return code resolver instance
	 */
	public EntityMessageCodeResolver prefixedResolver( String... additionalPrefixes ) {
		EntityMessageCodeResolver resolver = new EntityMessageCodeResolver();
		resolver.messageSource = messageSource;
		resolver.entityConfiguration = entityConfiguration;
		resolver.fallbackCollections = fallbackCollections;
		resolver.subCollections = additionalPrefixes;
		resolver.setPrefixes( prefix );

		return resolver;
	}

	private String[] addPrefix( String[] prefixes, String[] names ) {
		String[] prefixed = new String[prefixes.length * names.length];

		if ( prefixes.length == 0 ) {
			return names;
		}

		if ( names.length == 0 ) {
			return prefixes;
		}

		int ix = 0;
		for ( String prefix : prefixes ) {
			for ( String name : names ) {
				if ( StringUtils.isBlank( prefix ) ) {
					prefixed[ix++] = name;
				}
				else if ( StringUtils.isBlank( name ) ) {
					prefixed[ix++] = prefix;
				}
				else {
					prefixed[ix++] = prefix + "." + name;
				}
			}
		}

		return prefixed;
	}

	private MessageSourceResolvable buildMessageSourceResolvable( String code, boolean includeFallback, Object[] arguments, String defaultValue ) {
		String[] codes = buildMessageCodes( code, includeFallback );

		if ( LOG.isTraceEnabled() ) {
			LOG.trace( "Looking up message codes: {}", StringUtils.join( codes, ", " ) );
		}

		return new DefaultMessageSourceResolvable( codes, arguments, defaultValue != null ? defaultValue : codes[0] );
	}
}
