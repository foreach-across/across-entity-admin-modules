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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.builder.ColumnViewElementBuilder;
import com.foreach.across.modules.entity.controllers.EntityViewCommand;
import com.foreach.across.modules.entity.controllers.EntityViewRequest;
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.EntityListViewPageFetcher;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.web.WebViewCreationContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.find;
import static com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils.remove;

/**
 * Processor and page executor for {@link com.foreach.across.modules.entity.query.EntityQuery} based approach.
 * For this filter to work, the class must have a valid {@link com.foreach.across.modules.entity.query.EntityQueryExecutor}
 * and {@link com.foreach.across.modules.entity.query.EntityQueryParser}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Deprecated
public class OldEntityQueryFilterProcessor extends WebViewProcessorAdapter implements EntityListViewPageFetcher<WebViewCreationContext>
{
	private static final String PARAM = "eqFilter";

	private BootstrapUiFactory bootstrapUi;

	@Autowired
	public void setBootstrapUiFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUi = bootstrapUiFactory;
	}

	@Override
	protected void registerCommandExtensions( EntityViewCommand command ) {
		command.addExtensions( PARAM, "" );
	}


	@Override
	protected void extendViewModel( EntityView view ) {
		EntityViewRequest request = view.getAttribute( "viewRequest", EntityViewRequest.class );
		String filter = (String) request.getExtensions().get( PARAM );

		ContainerViewElement elements = view.getViewElements();

		// move the original actions
		Optional<ContainerViewElement> header = find( elements, "entityForm-header", ContainerViewElement.class );

		ColumnViewElementBuilder filterForm
				= bootstrapUi.column()
				             .css( "col-md-10" )
				             .add(
						             bootstrapUi
								             .inputGroup(
										             bootstrapUi.textbox()
										                        .controlName( "extensions[" + PARAM + "]" )
										                        .text( filter )
								             )
								             .addonAfter(
										             bootstrapUi.button()
										                        .submit()
										                        .iconOnly( new GlyphIcon( GlyphIcon.SEARCH ) )
								             )
				             );

		header.ifPresent(
				h -> {
					NodeViewElementBuilder row = bootstrapUi.row()
					                                        .add( filterForm );

					Optional<ViewElement> actions = remove( h, "entityForm-header-actions" );
					actions.ifPresent( a -> {
						( (NodeViewElement) a ).addCssClass( "col-md-2", "text-right" );
						row.add( a );
					} );

					h.addChild( row.build() );

					String errorMessage = view.getAttribute( "filterError", String.class );

					if ( !StringUtils.isBlank( errorMessage ) ) {
						h.addChild(
								bootstrapUi.div()
								           .css( "alert", "alert-danger" )
								           .add( bootstrapUi.text( errorMessage ) )
								           .build()
						);
					}
				}
		);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Page fetchPage( WebViewCreationContext viewCreationContext, Pageable pageable, EntityView model ) {
		EntityViewRequest request = model.getAttribute( "viewRequest", EntityViewRequest.class );
		String filter = (String) request.getExtensions().get( PARAM );

		try {
			EntityQueryParser parser = viewCreationContext.getEntityConfiguration().getAttribute(
					EntityQueryParser.class );
			EntityQuery query = parser.parse( filter );
			EntityQueryExecutor executor = viewCreationContext.getEntityConfiguration().getAttribute(
					EntityQueryExecutor.class );

			if ( viewCreationContext.isForAssociation() ) {
				EntityAssociation association = viewCreationContext.getEntityAssociation();
				AssociatedEntityQueryExecutor associatedExecutor
						= new AssociatedEntityQueryExecutor<>( association.getTargetProperty(), executor );

				return associatedExecutor.findAll( model.getParentEntity(), query, pageable );
			}
			else {
				return executor.findAll( query, pageable );
			}
		}
		catch ( EntityQueryParsingException pe ) {
			String message = pe.getMessage();

			if ( pe.hasErrorExpressionPosition() ) {
				message += " ; position " + pe.getErrorExpressionPosition();
			}

			model.addAttribute( "filterError", message );

		}
		catch ( Exception e ) {
			model.addAttribute( "filterError", ExceptionUtils.getRootCauseMessage( e ) );
		}

		// Explicitly return null to avoid "0 users found" along with an exception
		return null;
	}

}
