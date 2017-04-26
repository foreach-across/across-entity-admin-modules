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
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityFactory;
import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityViewModel;
import com.foreach.across.modules.web.AcrossWebModule;
import com.foreach.across.modules.web.template.WebTemplateInterceptor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.util.UriComponentsBuilder;

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
public class SaveEntityViewProcessor extends EntityViewProcessorAdapter
{
	private EntityViewPageHelper entityViewPageHelper;
	private ConversionService conversionService;

	// todo: listen to specific action only

	@SuppressWarnings("unchecked")
	@Override
	public void initializeCommandObject( EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder ) {
		// Set the dto of the entity on the command object
		EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
		EntityModel<Object, ?> entityModel = entityViewContext.getEntityModel();

		if ( entityViewContext.holdsEntity() ) {
			command.setEntity( entityModel.createDto( entityViewContext.getEntity( Object.class ) ) );
		}
		else {
			Object newDto = createNewDto( entityViewContext, entityModel );
			command.setEntity( newDto );
		}

		// set the dto as the entity
		entityViewRequest.getModel().addAttribute( EntityViewModel.ENTITY, command.getEntity() );
	}

	private Object createNewDto( EntityViewContext entityViewContext, EntityModel<Object, ?> entityModel ) {
		if ( entityViewContext.isForAssociation() ) {
			EntityAssociation entityAssociation = entityViewContext.getEntityAssociation();
			EntityFactory associatedEntityFactory = entityAssociation.getAttribute( EntityFactory.class );

			if ( associatedEntityFactory != null ) {
				return associatedEntityFactory.createNew( entityViewContext.getParentContext().getEntity() );
			}
			else {
				Object newDto = entityModel.createNew();

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

		return entityModel.createNew();
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
				boolean isNew = entityModel.isNew( entityToSave );
				Object savedEntity = entityModel.save( entityToSave );

				entityViewPageHelper.addGlobalFeedbackAfterRedirect( entityViewRequest, Style.SUCCESS,
				                                                     isNew ? "feedback.entityCreated" : "feedback.entityUpdated" );

				if ( entityViewRequest.hasPartialFragment() ) {
					entityView.setRedirectUrl(
							UriComponentsBuilder.fromUriString( entityViewContext.getLinkBuilder().update( savedEntity ) )
							                    .queryParam( WebTemplateInterceptor.PARTIAL_PARAMETER, entityViewRequest.getPartialFragment() )
							                    .toUriString()
					);
				}
				else {
					entityView.setRedirectUrl( entityViewContext.getLinkBuilder().update( savedEntity ) );
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
