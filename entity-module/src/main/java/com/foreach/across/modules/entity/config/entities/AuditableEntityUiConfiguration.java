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
package com.foreach.across.modules.entity.config.entities;

import com.foreach.across.core.annotations.AcrossDepends;
import com.foreach.across.core.annotations.OrderInModule;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.config.builders.EntityConfigurationBuilder;
import com.foreach.across.modules.entity.views.EntityListView;
import com.foreach.across.modules.entity.views.EntityListViewFactory;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.elements.builder.AuditablePrincipalPropertyViewElementBuilder;
import com.foreach.across.modules.entity.views.bootstrapui.elements.builder.AuditablePropertyViewElementBuilder;
import com.foreach.across.modules.hibernate.business.Auditable;
import com.foreach.across.modules.spring.security.SpringSecurityModule;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;

/**
 * Configures the standard properties for any {@link Auditable} entity.
 *
 * @author Arne Vandamme
 */
@Configuration
@OrderInModule(2)
@AcrossDepends(required = SpringSecurityModule.NAME)
public class AuditableEntityUiConfiguration implements EntityConfigurer
{
	@Autowired
	private ConversionService mvcConversionService;

	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		EntityConfigurationBuilder<?> builder = entities
				.assignableTo( Auditable.class )
				.properties(
						props -> {
							// Auditable properties are set automatically and should not be set through the interface,
							// we make them hidden so they are only added explicitly, and we position them after other properties
							props.property( "createdBy" )
							     .writable( false ).hidden( true ).order( 1001 )
							     .viewElementBuilder( ViewElementMode.VALUE, createdByValueBuilder() )
							     .viewElementBuilder( ViewElementMode.LIST_VALUE, createdByValueBuilder() )
							     .and()
							     .property( "createdDate" )
							     .writable( false ).hidden( true ).order( 1002 )
							     .and()
							     .property( "lastModifiedBy" )
							     .writable( false ).hidden( true ).order( 1003 )
							     .viewElementBuilder( ViewElementMode.VALUE, lastModifiedByValueBuilder() )
							     .viewElementBuilder( ViewElementMode.LIST_VALUE, lastModifiedByValueBuilder() )
							     .and()
							     .property( "lastModifiedDate" )
							     .writable( false ).hidden( true ).order( 1004 );

							// Create aggregated properties that sort on the dates
							props.property( "created" )
							     .displayName( "Created" )
							     .writable( false ).readable( true ).hidden( true ).order( 1005 )
							     .attribute( Sort.Order.class, new Sort.Order( "createdDate" ) )
							     .viewElementBuilder( ViewElementMode.VALUE, createdValueBuilder() )
							     .viewElementBuilder( ViewElementMode.LIST_VALUE, createdValueBuilder() );

							props.property( "lastModified" )
							     .displayName( "Last modified" )
							     .writable( false ).readable( true ).hidden( true ).order( 1006 )
							     .attribute( Sort.Order.class, new Sort.Order( "lastModifiedDate" ) )
							     .viewElementBuilder( ViewElementMode.VALUE, lastModifiedValueBuilder() )
							     .viewElementBuilder( ViewElementMode.LIST_VALUE, lastModifiedValueBuilder() );
						}
				);

		// Add aggregated properties to views
		builder.listView( lvb -> lvb.showProperties( ".", "lastModified" ) )
		       .updateFormView( fvb -> fvb.showProperties( ".", "created", "lastModified" ) );

		// Add default sort to list views if no default sort configured
		builder.postProcessor( entityConfiguration -> {
			EntityListViewFactory listViewFactory = entityConfiguration.getViewFactory( EntityListView.VIEW_NAME );

			if ( listViewFactory != null && listViewFactory.getDefaultSort() == null ) {
				listViewFactory.setDefaultSort( new Sort( Sort.Direction.DESC, "lastModifiedDate" ) );
			}
		} );
	}

	@Bean
	public ViewElementBuilder createdByValueBuilder() {
		return new AuditablePrincipalPropertyViewElementBuilder();
	}

	@Bean
	public ViewElementBuilder createdValueBuilder() {
		AuditablePropertyViewElementBuilder builder = new AuditablePropertyViewElementBuilder();
		builder.setConversionService( mvcConversionService );

		return builder;
	}

	@Bean
	public ViewElementBuilder lastModifiedByValueBuilder() {
		AuditablePrincipalPropertyViewElementBuilder builder = new AuditablePrincipalPropertyViewElementBuilder();
		builder.setForLastModifiedByProperty( true );
		return builder;
	}

	@Bean
	public ViewElementBuilder lastModifiedValueBuilder() {
		AuditablePropertyViewElementBuilder builder = new AuditablePropertyViewElementBuilder();
		builder.setConversionService( mvcConversionService );
		builder.setForLastModifiedProperty( true );

		return builder;
	}
}
