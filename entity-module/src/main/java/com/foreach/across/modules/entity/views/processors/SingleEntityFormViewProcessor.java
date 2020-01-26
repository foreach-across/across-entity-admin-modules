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
import lombok.experimental.Accessors;
import lombok.val;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

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
 * If the current view attributes ({@link EntityViewRequest#getConfigurationAttributes()} contains a {@link #ATTR_ENCTYPE}, its value
 * will be used as the enctype for the form. If no value is set but a collection of property descriptors
 * is available ({@link PropertyRenderingViewProcessor#ATTRIBUTE_PROPERTY_DESCRIPTORS} on the view, the descriptors will be inspected for the same attribute.
 * This is especially useful to automatically change the form enctype to multipart when a property requiring it (usually a file) is present.
 * <p>
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
@Accessors(chain = true)
public class SingleEntityFormViewProcessor extends EntityViewProcessorAdapter
{
	/**
	 * Holds the form {@code enctype} value that should be used, either present on the
	 * {@link com.foreach.across.modules.entity.views.EntityViewFactory} attributes or
	 * on a {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor}.
	 */
	public static final String ATTR_ENCTYPE = "FormViewElement.encType";

	/**
	 * Name of the root form container that contains the rows.
	 */
	public static final String FORM = "entityForm";

	/**
	 * Name of the container that holds the default buttons.
	 */
	public static final String FORM_BUTTONS = "entityForm-buttons";

	/**
	 * Default name of the left column element in case of a default form with a 2 column grid.
	 */
	public static final String LEFT_COLUMN = "entityForm-column-0";

	/**
	 * Default name of the right column element in case of a default form with a 2 column grid.
	 */
	public static final String RIGHT_COLUMN = "entityForm-column-1";

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

	@Override
	protected void createViewElementBuilders( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap ) {
		FormViewElementBuilder form = bootstrap.builders
				.form()
				.name( FORM )
				.commandAttribute( "entityViewCommand" )
				.post()
				.css( "em-form" )
				.noValidate();

		String encType = determineFormEncType( entityViewRequest );

		if ( encType != null ) {
			form.postProcessor( ( ( builderContext, f ) -> f.setEncType( encType ) ) );
		}

		if ( addGlobalBindingErrors ) {
			addGlobalBindingErrors( entityViewRequest, form );
		}

		builderMap.put( FORM, form );

		val formColumns = buildFormColumns();

		NodeViewElementBuilder columnRow = bootstrap.builders.row();
		formColumns.forEach( ( name, column ) -> {
			columnRow.add( column );
			builderMap.put( name, column );
		} );
		form.add( columnRow );

		String fromUrl = resolveFromUrl( entityViewRequest, form );

		if ( addDefaultButtons ) {
			ContainerViewElementBuilderSupport buttonsContainer = buildButtonsContainer( entityViewRequest, entityView, fromUrl );

			form.add( buttonsContainer );
			builderMap.put( FORM_BUTTONS, buttonsContainer );
		}
	}

	@SuppressWarnings("all")
	private String determineFormEncType( EntityViewRequest entityViewRequest ) {
		String encType = (String) entityViewRequest.getConfigurationAttributes().get( ATTR_ENCTYPE );

		if ( encType == null && entityViewRequest.getModel().containsAttribute( PropertyRenderingViewProcessor.ATTRIBUTE_PROPERTY_DESCRIPTORS ) ) {
			encType = PropertyRenderingViewProcessor.propertyDescriptors( entityViewRequest )
			                                        .values()
			                                        .stream()
			                                        .map( d -> d.<String, String>getAttribute( ATTR_ENCTYPE, String.class ) )
			                                        .filter( Objects::nonNull )
			                                        .findFirst()
			                                        .orElse( null );
		}

		return encType;
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

			val alert = bootstrap.builders
					.alert()
					.danger()
					.text( messages.withNameSingular( "feedback.validationErrors", entityViewContext.getEntityLabel() ) );

			if ( errors.hasGlobalErrors() ) {
				val globalErrorList = html.builders.ul().css( "global-errors" );

				errors.getGlobalErrors().forEach(
						e -> globalErrorList.add( html.builders.unescapedText(
								"<strong>" + messageCodeResolver.getMessage( e ) + "</strong>"
						) )
				);

				alert.add( globalErrorList );
			}

			form.add( alert );
		}
	}

	private String resolveFromUrl( EntityViewRequest entityViewRequest, FormViewElementBuilder form ) {
		Optional<String> fromUrl = Optional.ofNullable( entityViewRequest.getWebRequest().getParameter( "from" ) );
		fromUrl.ifPresent( url -> form.add( bootstrap.builders.hidden().controlName( "from" ).value( url ) ) );

		return fromUrl.orElseGet( entityViewRequest.getEntityViewContext().getLinkBuilder()::overview );
	}

	protected ContainerViewElementBuilderSupport buildButtonsContainer( EntityViewRequest entityViewRequest,
	                                                                    EntityView entityView, String fromUrl ) {
		EntityMessages messages = entityViewRequest.getEntityViewContext().getEntityMessages();
		return html.builders
				.div()
				.name( "buttons" )
				.css( "em-form-actions" )
				.add( bootstrap.builders.button()
				                        .name( "btn-save" )
				                        .data( "em-button-role", "save" )
				                        .style( Style.PRIMARY )
				                        .submit()
				                        .text( messages.messageWithFallback( "actions.save" ) )
				)
				.add( bootstrap.builders.button()
				                        .name( "btn-cancel" )
				                        .data( "em-button-role", "cancel" )
				                        .link( fromUrl )
				                        .text( messages.messageWithFallback( "actions.cancel" ) )
				);
	}

	private Map<String, ColumnViewElementBuilder> buildFormColumns() {
		Map<String, ColumnViewElementBuilder> columns = new HashMap<>( grid.size() );

		for ( int i = 0; i < grid.size(); i++ ) {
			Grid.Position position = grid.get( i );
			String name = "entityForm-column-" + i;
			columns.put(
					name,
					bootstrap.builders
							.column( position.toArray( new Grid.DeviceGridLayout[position.size()] ) )
							.name( name )
			);
		}

		return columns;
	}
}
