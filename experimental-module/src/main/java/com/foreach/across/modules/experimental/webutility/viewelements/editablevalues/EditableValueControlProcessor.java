package com.foreach.across.modules.experimental.webutility.viewelements.editablevalues;

import com.foreach.across.modules.entity.bind.EntityPropertyControlName;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyHandlingType;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto.Error;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto.PropertyValue;
import com.foreach.across.modules.experimental.webutility.viewelements.editablevalues.dto.UpdateResponse;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.ui.MutableViewElement;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.WebExpressionContext;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Stream;

/**
 * Configures the view for supporting editable value controls.
 * If {@link EditableValuesHolder#properties} are specified and the form was posted,
 * this view processor will consider it an editable values post and will return the appropriate JSON response.
 * <p/>
 * The collection of {@link EditableValuesHolder#properties} contains the properties that should rendered
 * and whose errors should be returned. Separate {@code ViewElementModes} can be specified per property
 * for rendering output.
 *
 * @see EditableValueViewElementBuilderFactory
 */
@Slf4j
@RequiredArgsConstructor
public class EditableValueControlProcessor extends ExtensionViewProcessorAdapter<EditableValueControlProcessor.EditableValuesHolder>
{
	private final EntityViewElementBuilderService entityViewElementBuilderService;
	private final SpringTemplateEngine templateEngine;

	@Override
	protected String extensionName() {
		return "editableValues";
	}

	@Override
	protected EditableValuesHolder createExtension( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		return new EditableValuesHolder();
	}

	@Override
	protected void doPost( EditableValuesHolder editableValues, BindingResult bindingResult, EntityView entityView, EntityViewRequest entityViewRequest ) {
		if ( editableValues.wereValuesPosted() ) {
			entityView.setRedirectUrl( null );
			entityView.setShouldRender( false );

			UpdateResponse updateResponse = buildUpdateResponse( editableValues.properties, bindingResult, entityViewRequest );
			entityView.setResponseEntity( new ResponseEntity<>( updateResponse, HttpStatus.OK ) );
		}
	}

	private UpdateResponse buildUpdateResponse( Map<String, Set<ViewElementMode>> properties, Errors errors, EntityViewRequest entityViewRequest ) {
		if ( errors.hasErrors() ) {
			return new UpdateResponse( false, new HashMap<>(), new HashMap<>(), new HashMap<>(),
			                           buildPropertyErrors( properties.keySet(), errors, entityViewRequest ) );
		}
		return new UpdateResponse( true, new HashMap<>(), resolvePropertyValues( properties, entityViewRequest ), new HashMap<>(), new HashMap<>() );
	}

	private Map<String, Set<Error>> buildPropertyErrors( Collection<String> properties, Errors errors, EntityViewRequest entityViewRequest ) {
		Map<String, Set<Error>> errorsByProperty = new HashMap<>( properties.size() );

		properties.forEach(
				property -> errorsByProperty.put( property, resolveErrorsForProperty( property, errors, entityViewRequest.getEntityViewContext() ) )
		);
		return errorsByProperty;
	}

	private Set<Error> resolveErrorsForProperty( String property, Errors errors, EntityViewContext entityViewContext ) {
		EntityMessageCodeResolver messageCodeResolver = entityViewContext.getMessageCodeResolver();
		EntityPropertyDescriptor propertyDescriptor = entityViewContext.getPropertyRegistry().getProperty( property );

		Set<Error> propertyErrors = new LinkedHashSet<>();
		errors.getGlobalErrors()
		      .stream()
		      .map( globalError -> new Error( globalError.getCode(), messageCodeResolver.getMessage( globalError ) ) )
		      .forEach( propertyErrors::add );

		EntityPropertyControlName controlName = EntityPropertyControlName.root( "entity" );
		if ( propertyDescriptor != null ) {
			Stream.of( controlName.forChildProperty( propertyDescriptor ).forHandlingType( EntityPropertyHandlingType.DIRECT ),
			           controlName.forChildProperty( propertyDescriptor ).forHandlingType( EntityPropertyHandlingType.BINDER ) )
			      .flatMap( c -> errors.getFieldErrors( c.toString() ).stream() )
			      .map( fieldError -> new Error( fieldError.getCode(), messageCodeResolver.getMessage( fieldError ) ) )
			      .forEach( propertyErrors::add );
		}
		return propertyErrors;
	}

	private Map<String, PropertyValue> resolvePropertyValues( Map<String, Set<ViewElementMode>> properties, EntityViewRequest entityViewRequest ) {
		// added babysteps for working with associations, if you have errors down below or weird shit, this might be the cause
		// this primarily resolves to using the parent property registry to resolve unknown properties for the current context.
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		boolean forAssociation = entityViewContext.isForAssociation();
		EntityPropertyRegistry propertyRegistry = entityViewContext.getPropertyRegistry();
		EntityPropertyRegistry parentPropertyRegistry = getParentPropertyRegistry( forAssociation, entityViewContext );

		EntityViewCommand command = entityViewRequest.getCommand();

		// save should have happened, disable binding so collections would return the correct value
		// todo: investigate if this is an actual bug or desired behaviour, if no value is sent for the property
		//  it should not get reset?
		command.getProperties().setBindingEnabled( false );

		Map<String, PropertyValue> propertyValues = new HashMap<>( properties.size() );
		properties.forEach( ( propertyName, viewElementModes ) -> {
			EntityPropertyDescriptor descriptor = propertyRegistry.getProperty( propertyName );
			// if the descriptor is null and we are in an association context, then the requested property could be for the
			// entity in the parent context (for example, the name of the entity in the parent context as view title)
			if ( descriptor == null && forAssociation ) {
				descriptor = parentPropertyRegistry.getProperty( propertyName );
			}
			if ( descriptor != null ) {
				Map<ViewElementMode, String> labels = new HashMap<>( viewElementModes.size() );

				for ( ViewElementMode mode : viewElementModes ) {
					ViewElement labelElement = entityViewElementBuilderService.createElementBuilder( descriptor, mode ).build();
					if ( labelElement instanceof TextViewElement ) {
						String label = ( (TextViewElement) labelElement ).getText();
						labels.put( mode, label );
					}
					else if ( labelElement != null ) {
						LOG.trace( "Not a TextViewElement - attempting inline Thymeleaf render for " + labelElement.getClass() );
						labels.put( mode, renderViewElement( labelElement ) );
					}
					else {
						labels.put( mode, "" );
					}
				}

				propertyValues.put( descriptor.getName(), new PropertyValue( true, labels ) );
			}
		} );

		return propertyValues;
	}

	@Override
	protected void postRender( EditableValuesHolder extension,
	                           EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		Optional.ofNullable( entityViewRequest.getWebRequest().getNativeRequest( HttpServletRequest.class ) )
		        .ifPresent( request -> {
			                    String partialToRender = (String) request.getAttribute( WebTemplateInterceptor.RENDER_VIEW_ELEMENT );
			                    if ( StringUtils.startsWith( partialToRender, "editableValue-" ) ) {
				                    // if a partial is requested, find the element in the actual container and update the name to something unique
				                    // then change the partial that should be rendered.
				                    // this is ensures that even though multiple editable values for the same property might be rendered on the view,
				                    // only the one inside the main container will be used for the partial update
				                    String uuid = UUID.randomUUID().toString();
				                    container.find( partialToRender, MutableViewElement.class )
				                             .ifPresent( control -> {
					                             control.setName( uuid );
					                             request.setAttribute( WebTemplateInterceptor.RENDER_VIEW_ELEMENT, uuid );
				                             } );
			                    }
		                    }
		        );
	}

	/**
	 * Attempts to render a view element to a string. Does not build the full context which is available
	 * if a Thymeleaf page is being rendered so not everything will work. Initialization could be extended
	 * to correspond as much as possible of problems arise.
	 */
	private String renderViewElement( ViewElement viewElement ) {
		ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		IEngineConfiguration configuration = templateEngine.getConfiguration();
		WebExpressionContext context =
				new WebExpressionContext(
						configuration,
						ra.getRequest(),
						ra.getResponse(),
						ra.getRequest().getServletContext(),
						LocaleContextHolder.getLocale(),
						Collections.singletonMap( "element", viewElement )
				);

		return templateEngine.process( "th/experimental/inline-view-element", context );
	}

	private EntityPropertyRegistry getParentPropertyRegistry( boolean forAssociation, EntityViewContext entityViewContext ) {
		return forAssociation ? entityViewContext.getParentContext().getPropertyRegistry() : null;
	}

	@Data
	static class EditableValuesHolder
	{
		private Map<String, Set<ViewElementMode>> properties;

		boolean wereValuesPosted() {
			return properties != null && !properties.isEmpty();
		}
	}
}
