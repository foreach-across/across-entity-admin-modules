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
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.bind.EntityPropertiesBinder;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyValue;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Responsible for saving a single entity after a form submit. Will initialize the command object to bind to the current entity (either
 * a new one or a dto of the bound entity). Will redirect when saved and will only save if the {@link BindingResult} has no errors.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Component
@Exposed
@Scope("prototype")
@Accessors(chain = true)
public class SaveEntityViewProcessor extends EntityViewProcessorAdapter
{
	private EntityViewPageHelper entityViewPageHelper;
	private ConversionService conversionService;

	/**
	 * Set to {@code true} if only the properties should be saved but not the actual entity.
	 * This will still create a DTO but will end up not calling the save method on the entity itself.
	 * <p/>
	 * Only useful if the properties have a custom implementation for
	 * {@link com.foreach.across.modules.entity.registry.properties.EntityPropertyController#save(EntityPropertyBindingContext, EntityPropertyValue)}
	 */
	@Setter
	@Getter
	private boolean propertiesOnly;

	// todo: listen to specific action only

	@SuppressWarnings("unchecked")
	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		// Set the dto of the entity on the command object
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		EntityFactory entityFactory = resolveEntityFactory( entityViewContext );

		Object currentEntity = entityViewContext.getEntity( Object.class );

		if ( currentEntity != null ) {
			command.setEntity( entityFactory.createDto( currentEntity ) );
		}
		else {
			Object newDto = createNewDto( entityViewContext, entityFactory );
			command.setEntity( newDto );
		}

		// set the dto as the entity
		entityViewRequest.getModel().addAttribute( EntityViewModel.ENTITY, command.getEntity() );

		EntityPropertiesBinder propertiesBinder = createPropertiesBinder(
				currentEntity, command.getEntity(), entityViewContext.getPropertyRegistry()
		);

		if ( HttpMethod.POST.equals( entityViewRequest.getHttpMethod() ) ) {
			propertiesBinder.setBindingEnabled( true );
		}

		command.setProperties( propertiesBinder );
	}

	private EntityPropertiesBinder createPropertiesBinder( Object entity, Object dto, EntityPropertyRegistry propertyRegistry ) {
		EntityPropertiesBinder binder = new EntityPropertiesBinder( propertyRegistry );
		binder.setBinderPrefix( "properties" );
		binder.setEntity( entity );
		binder.setTarget( dto );
		binder.setConversionService( conversionService );
		return binder;
	}

	private Object createNewDto( EntityViewContext entityViewContext, EntityFactory entityFactory ) {
		if ( entityViewContext.isForAssociation() ) {
			EntityAssociation entityAssociation = entityViewContext.getEntityAssociation();
			EntityFactory associatedEntityFactory = entityAssociation.getAttribute( EntityFactory.class );

			if ( associatedEntityFactory != null ) {
				return associatedEntityFactory.createNew( entityViewContext.getParentContext().getEntity() );
			}
			else {
				Object newDto = entityFactory.createNew();

				BeanWrapper beanWrapper = new BeanWrapperImpl( newDto );
				EntityPropertyDescriptor propertyDescriptor = entityViewContext.getEntityAssociation().getTargetProperty();

				Object parentEntity = entityViewContext.getParentContext().getEntity();
				Object valueToSet = parentEntity != null
						? conversionService.convert( parentEntity, TypeDescriptor.forObject( parentEntity ), propertyDescriptor.getPropertyTypeDescriptor() )
						: null;

				if ( propertyDescriptor != null ) {
					beanWrapper.setPropertyValue( propertyDescriptor.getName(), valueToSet );
				}

				return newDto;
			}
		}

		return entityFactory.createNew();
	}

	private EntityFactory resolveEntityFactory( EntityViewContext entityViewContext ) {
		if ( entityViewContext.isForAssociation() ) {
			EntityFactory associatedEntityFactory = entityViewContext.getEntityAssociation().getAttribute( EntityFactory.class );

			if ( associatedEntityFactory != null ) {
				return associatedEntityFactory;
			}
		}

		return entityViewContext.getEntityModel();
	}

	@Override
	protected void prepareViewElementBuilderContext( EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderContext builderContext ) {
		builderContext.setAttribute( EntityViewModel.ENTITY, entityViewRequest.getCommand().getEntity() );
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost( EntityViewRequest entityViewRequest, EntityView entityView, EntityViewCommand command, BindingResult bindingResult ) {
		if ( !bindingResult.hasErrors() ) {
			try {
				EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();

				EntityModel<Object, ?> entityModel = entityViewContext.getEntityModel();

				Object entityToSave = command.getEntity();
				Object savedEntity = entityToSave;
				boolean isNew = entityModel.isNew( entityToSave );

				if ( !propertiesOnly ) {
					AtomicReference savedEntityHolder = new AtomicReference();

					command.getProperties()
					       .createController()
					       .addEntitySaveCallback( () -> savedEntityHolder.set( entityModel.save( entityToSave ) ) )
					       .save();

					savedEntity = savedEntityHolder.get();
				}
				else {
					command.getProperties().createController().save();
				}

				// clear the binder to force properties to be reloaded, in case redirect does not happen
				command.getProperties().clear();

				entityViewPageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.SUCCESS,
				                                                     isNew ? "feedback.entityCreated" : "feedback.entityUpdated" );

				if ( entityViewRequest.hasPartialFragment() ) {
					entityView.setRedirectUrl(
							entityViewContext.getLinkBuilder().forInstance( savedEntity )
							                 .updateView()
							                 .withPartial( entityViewRequest.getPartialFragment() )
							                 .toUriString()
					);
				}
				else {
					entityView.setRedirectUrl( entityViewContext.getLinkBuilder().forInstance( savedEntity ).updateView().toUriString() );
				}
			}
			catch ( RuntimeException e ) {
				entityViewPageHelper.throwOrAddExceptionFeedback( entityViewRequest, "feedback.entitySaveFailed", e );
			}
		}
	}

	@Autowired
	void setEntityViewPageHelper( EntityViewPageHelper entityViewPageHelper ) {
		this.entityViewPageHelper = entityViewPageHelper;
	}

	@Autowired
	void setConversionService( @Qualifier(AcrossWebModule.CONVERSION_SERVICE_BEAN) ConversionService conversionService ) {
		this.conversionService = conversionService;
	}
}
