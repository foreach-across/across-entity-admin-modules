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

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.ColumnViewElementBuilder;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementBuilder;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Creates the form based layout for an entity.  Will create a column form based structure for the body and will replace the default container
 * by the left form column.  The page title will be set for the selected entity, or the "create new" title will be used if no entity set.
 * The form itself will have no default action set, and will always submit to the current page.
 * <p/>
 * If {@link #setAddDefaultButtons(boolean)} is {@code true}, a default save and cancel button will be added at the bottom of the form.
 * The cancel button will return to the entity overview page, unless a specific <strong>from</strong> request attribute is present.
 * <p/>
 * If {@link #setAddGlobalBindingErrors(boolean)} is {@code true} (default), any global errors on the {@link org.springframework.validation.BindingResult}
 * will be added above the two form columns.
 * <p/>
 * The form will also contain a hidden form element named <strong>from</strong> if it was passed in originally as request parameter.
 * It is expected to contain the original url that should be redirected back to in case of cancellation.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
public class SingleEntityFormViewProcessor extends EntityViewProcessorAdapter
{
	private BootstrapUiFactory bootstrapUiFactory;

	/**
	 * Should default save and cancel buttons be added at the bottom of the form, below the column structure?  Defaults to {@code false}.
	 */
	@Setter
	private boolean addDefaultButtons;

	/**
	 * Should global errors present on the {@link org.springframework.validation.BindingResult} be added on top of the form?  Defaults to {@code true}.
	 */
	@Setter
	private boolean addGlobalBindingErrors = true;

	/**
	 * Grid for the form (represents the columns the form should have).  Defaults to 2 columns of the same width.
	 * Use for example <em>Grid.create( Grid.Width.FULL )</em> to get a single full with column.
	 * Every column container will be a {@link com.foreach.across.modules.web.ui.elements.ContainerViewElement} with the name
	 * <strong>entityForm-column-COLUMN_NUMBER</strong>.
	 */
	@Setter
	private Grid grid = Grid.create(
			Grid.position( Grid.Device.MD.width( Grid.Width.HALF ) ),
			Grid.position( Grid.Device.MD.width( Grid.Width.HALF ) )
	);

	protected BootstrapUiFactory bootstrapUiFactory() {
		return bootstrapUiFactory;
	}

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		EntityMessages messages = entityViewContext.getEntityMessages();

		FormViewElementBuilder form = bootstrapUiFactory
				.form()
				.name( "entityForm" )
				.commandAttribute( "entityViewCommand" )
				.post()
				.noValidate();

		if ( addGlobalBindingErrors ) {
			addGlobalBindingErrors( entityViewRequest, form );
		}

		ColumnViewElementBuilder leftColumn = addFormColumnsAndReturnFirst( form );
		builderMap.put( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTIES_CONTAINER_BUILDER, leftColumn );

		String fromUrl = resolveFromUrl( entityViewRequest, form );

		if ( addDefaultButtons ) {
			ContainerViewElementBuilderSupport buttonsContainer = buildButtonsContainer( entityViewRequest, entityView, fromUrl );

			form.add( buttonsContainer );
			builderMap.put( "entityForm-buttons", buttonsContainer );
		}

		builderMap.put( "entityForm", form );
	}

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		Optional.ofNullable( builderMap.get( "entityForm" ) )
		        .ifPresent( containerBuilder::add );
	}

	private void addGlobalBindingErrors( EntityViewRequest entityViewRequest, FormViewElementBuilder form ) {
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		EntityMessages messages = entityViewContext.getEntityMessages();
		EntityMessageCodeResolver messageCodeResolver = entityViewContext.getMessageCodeResolver();

		BindingResult errors = entityViewRequest.getBindingResult();

		if ( errors != null && errors.hasErrors() ) {

			val alert = bootstrapUiFactory
					.alert()
					.danger()
					.text( messages.withNameSingular( "feedback.validationErrors", entityViewContext.getEntityLabel() ) );

			if ( errors.hasGlobalErrors() ) {
				val globalErrorList = bootstrapUiFactory.node( "ul" ).css( "global-errors" );

				errors.getGlobalErrors().forEach(
						e -> globalErrorList.add( bootstrapUiFactory.html(
								"<strong>" + messageCodeResolver.getMessage( e ) + "</strong>"
						) )
				);

				alert.add( globalErrorList );
			}

			form.add( alert );
		}
	}

	private ColumnViewElementBuilder addFormColumnsAndReturnFirst( FormViewElementBuilder form ) {
		List<ColumnViewElementBuilder> columns = buildFormColumns();

		NodeViewElementBuilder columnRow = bootstrapUiFactory.row();
		columns.forEach( columnRow::add );
		form.add( columnRow );

		return columns.get( 0 );
	}

	private String resolveFromUrl( EntityViewRequest entityViewRequest, FormViewElementBuilder form ) {
		Optional<String> fromUrl = Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "form" ) );
		fromUrl.ifPresent( url -> form.add( bootstrapUiFactory.hidden().controlName( "from" ).value( url ) ) );

		return fromUrl.orElseGet( entityViewRequest.getEntityViewContext().getLinkBuilder()::overview );
	}

	protected ContainerViewElementBuilderSupport buildButtonsContainer( EntityViewRequest entityViewRequest,
	                                                                    EntityView entityView, String fromUrl ) {
		EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();
		return bootstrapUiFactory
				.container()
				.name( "buttons" )
				.add(
						bootstrapUiFactory.button()
						                  .name( "btn-save" )
						                  .style( Style.PRIMARY )
						                  .submit()
						                  .text( messages.messageWithFallback( "actions.save" ) )
				)
				.add(
						bootstrapUiFactory.button()
						                  .name( "btn-cancel" )
						                  .link( fromUrl )
						                  .text( messages.messageWithFallback( "actions.cancel" ) )
				);
	}

	private List<ColumnViewElementBuilder> buildFormColumns() {
		List<ColumnViewElementBuilder> columns = new ArrayList<>( grid.size() );

		for ( int i = 0; i < grid.size(); i++ ) {
			Grid.Position position = grid.get( i );
			columns.add(
					bootstrapUiFactory
							.column( position.toArray( new Grid.DeviceGridLayout[position.size()] ) )
							.name( "entityForm-column-" + i )
			);
		}

		return columns;
	}

	@Autowired
	void setBootstrapUiFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUiFactory = bootstrapUiFactory;
	}
}
