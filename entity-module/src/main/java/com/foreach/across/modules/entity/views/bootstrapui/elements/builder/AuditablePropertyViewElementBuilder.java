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
package com.foreach.across.modules.entity.views.bootstrapui.elements.builder;

import com.foreach.across.modules.entity.config.entities.AuditableEntityUiConfiguration;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.support.ValueFetcher;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolverStrategy;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.ConversionService;

import java.util.Date;

/**
 * <p>Custom {@link ViewElementBuilder} for created and last modified properties of any
 * {@link com.foreach.across.modules.hibernate.business.Auditable} entity.  Will combine both
 * the timestamp and the principal (if available) into one field.</p>
 * <p>Usually set for {@link ViewElementMode#LIST_VALUE}.</p>
 *
 * @author Arne Vandamme
 * @see AuditableEntityUiConfiguration
 */
public class AuditablePropertyViewElementBuilder implements ViewElementBuilder, ValueFetcher<Auditable>
{
	private boolean forLastModifiedProperty;
	private ConversionService conversionService;
	private SecurityPrincipalLabelResolverStrategy securityPrincipalLabelResolverStrategy;

	private MessageSource messageSource;

	/**
	 * Configure the builder for the last modified property instead of the creation property.
	 *
	 * @param forLastModifiedProperty true if the last modified property should be used
	 */
	public void setForLastModifiedProperty( boolean forLastModifiedProperty ) {
		this.forLastModifiedProperty = forLastModifiedProperty;
	}

	/**
	 * Set the {@link ConversionService} to be used for formatting dates.
	 * Can be {@code null} in case a {@link org.springframework.format.Printer} is provided.
	 *
	 * @param conversionService to use
	 */
	public void setConversionService( ConversionService conversionService ) {
		this.conversionService = conversionService;
	}

	/**
	 * Set the message source that should be used for creating the value.
	 * If none set, a default string will be generated.
	 *
	 * @param messageSource to use
	 */
	@Autowired
	public void setMessageSource( MessageSource messageSource ) {
		this.messageSource = messageSource;
	}

	/**
	 * Implementation to use for resolving a display label for a principal object.
	 *
	 * @param securityPrincipalLabelResolverStrategy to use for looking up security principal label
	 */
	@Autowired
	public void setSecurityPrincipalLabelResolverStrategy( SecurityPrincipalLabelResolverStrategy securityPrincipalLabelResolverStrategy ) {
		this.securityPrincipalLabelResolverStrategy = securityPrincipalLabelResolverStrategy;
	}

	@Override
	public ViewElement build( ViewElementBuilderContext builderContext ) {
		Auditable auditable = EntityViewElementUtils.currentEntity( builderContext, Auditable.class );
		String text = getValue( auditable );

		return text != null ? new TextViewElement( text ) : null;
	}

	@Override
	public String getValue( Auditable auditable ) {
		if ( auditable != null ) {
			Object principal = auditable.getCreatedBy();
			Date date = auditable.getCreatedDate();

			if ( forLastModifiedProperty ) {
				principal = auditable.getLastModifiedBy();
				date = auditable.getLastModifiedDate();
			}

			String principalString = getPrincipalString( principal );

			return buildMessage( date, principalString );
		}

		return null;
	}

	private String buildMessage( Date date, String principal ) {
		if ( messageSource != null ) {
			String message = messageSource.getMessage( determineMessageCode( principal ), new Object[] { date, principal }, "",
			                                           LocaleContextHolder.getLocale() );

			if ( StringUtils.isNotEmpty( message ) ) {
				return message;
			}
		}

		return convertToString( date ) + ( !StringUtils.isBlank( principal ) ? " by " + principal : "" );
	}

	private String determineMessageCode( String principal ) {
		if ( StringUtils.isEmpty( principal ) ) {
			return forLastModifiedProperty ? "Auditable.lastModifiedDate" : "Auditable.createdDate";
		}

		return forLastModifiedProperty ? "Auditable.lastModified" : "Auditable.created";
	}

	private String getPrincipalString( Object principal ) {
		try {
			return securityPrincipalLabelResolverStrategy.resolvePrincipalLabel( principal );
		}
		catch ( Exception e ) {
			return principal != null ? principal.toString() : null;
		}
	}

	private String convertToString( Date date ) {
		if ( conversionService != null && date != null ) {
			return conversionService.convert( date, String.class );
		}

		return "";
	}
}

