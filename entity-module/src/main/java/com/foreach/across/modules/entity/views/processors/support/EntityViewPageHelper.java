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

package com.foreach.across.modules.entity.views.processors.support;

import com.foreach.across.core.development.AcrossDevelopmentMode;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.UUID;

import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.FEEDBACK_ATTRIBUTE_KEY;
import static com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor.addFeedbackMessage;

/**
 * Helper for adding messages to the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure} for a particular
 * {@link com.foreach.across.modules.entity.views.request.EntityViewRequest}.  Messages will be resolved against the configured
 * {@link com.foreach.across.modules.entity.views.support.EntityMessages} and will always get the {@link EntityViewContext#getEntityLabel()}
 * as the first argument.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@Slf4j
@Service
public class EntityViewPageHelper
{
	private AcrossDevelopmentMode developmentMode;
	private BootstrapUiFactory bootstrapUiFactory;

	/**
	 * Should flash attributes be used for redirect attributes.
	 */
	@Setter
	private boolean useFlashAttributesForRedirect = true;

	/**
	 * Registers a feedback message that should be shown after redirect.  Depending on the value of {@link #useFlashAttributesForRedirect},
	 * the attribute will be registered in the flash map or as a visible redirect attribute.  The message codes and styles will be registered
	 * as model attribute {@link com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor#FEEDBACK_ATTRIBUTE_KEY}.
	 * <p/>
	 * Only support message codes, use together with {@link com.foreach.across.modules.entity.views.processors.GlobalPageFeedbackViewProcessor}.
	 *
	 * @param viewRequest   view being requested
	 * @param feedbackStyle style for the feedback message
	 * @param messageCode   that should be resolved when rendering the message
	 */
	@SuppressWarnings("unchecked")
	public void addGlobalFeedbackAfterRedirect( @NonNull EntityViewRequest viewRequest, @NonNull Style feedbackStyle, @NonNull String messageCode ) {
		RedirectAttributes redirectAttributes = viewRequest.getRedirectAttributes();
		Map<String, Object> model = useFlashAttributesForRedirect ? (Map<String, Object>) redirectAttributes.getFlashAttributes() : redirectAttributes.asMap();
		model.compute( FEEDBACK_ATTRIBUTE_KEY, ( key, value ) -> addFeedbackMessage( (String) value, feedbackStyle, messageCode ) );
	}

	public void throwOrAddExceptionFeedback( EntityViewRequest viewRequest, String messageCode, Throwable exception ) {
		if ( developmentMode.isActive() ) {
			throw exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException( exception );
		}

		addExceptionFeedback( viewRequest, messageCode, exception );
	}

	public void addExceptionFeedback( EntityViewRequest viewRequest, String messageCode, Throwable exception ) {
		EntityViewContext entityViewContext = viewRequest.getEntityViewContext();
		UUID exceptionId = UUID.randomUUID();

		LOG.error( "Exception [{}] occurred when for view request on entity configuration {}",
		           exceptionId,
		           entityViewContext.getEntityConfiguration().getName(),
		           exception );

		EntityMessages messages = entityViewContext.getEntityMessages();

		viewRequest.getPageContentStructure().addToFeedback(
				bootstrapUiFactory
						.alert()
						.danger()
						.dismissible()
						.add(
								TextViewElement.html(
										messages.withNameSingular( messageCode, entityViewContext.getEntityLabel(), exception.toString(), exceptionId )
								)
						)
						.build()
		);
	}

	// EntityViewPageUtils.addGlobalFeedback( entityViewRequest, INFO, "feedback.entitySaved", e  );

	@Autowired
	void setBootstrapUiFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUiFactory = bootstrapUiFactory;
	}

	@Autowired
	void setDevelopmentMode( AcrossDevelopmentMode developmentMode ) {
		this.developmentMode = developmentMode;
	}
}
