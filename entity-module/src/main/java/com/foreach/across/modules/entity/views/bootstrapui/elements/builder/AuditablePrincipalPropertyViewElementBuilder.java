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
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.infrastructure.services.SecurityPrincipalLabelResolverStrategy;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>Custom {@link ViewElementBuilder} for createdBy and lastModifiedBy properties of any
 * {@link com.foreach.across.modules.hibernate.business.Auditable} entity.
 * Will lookup the label for an assumed principal name using the.</p>
 * <p>Usually set for {@link ViewElementMode#LIST_VALUE} and {@link ViewElementMode#VALUE}.</p>
 *
 * @author Arne Vandamme
 * @see AuditableEntityUiConfiguration
 * @since 2.0.0
 */
public class AuditablePrincipalPropertyViewElementBuilder implements ViewElementBuilder
{
	private boolean forLastModifiedByProperty;
	private SecurityPrincipalLabelResolverStrategy securityPrincipalLabelResolverStrategy;

	/**
	 * Configure the builder for the lastModifiedBy property instead of the createdBy property.
	 *
	 * @param forLastModifiedByProperty true if the lastModifiedBy property should be used
	 */
	public void setForLastModifiedByProperty( boolean forLastModifiedByProperty ) {
		this.forLastModifiedByProperty = forLastModifiedByProperty;
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

		if ( auditable != null ) {
			Object principal = auditable.getCreatedBy();

			if ( forLastModifiedByProperty ) {
				principal = auditable.getLastModifiedBy();
			}

			return new TextViewElement( securityPrincipalLabelResolverStrategy.resolvePrincipalLabel( principal ) );
		}

		return null;
	}
}
