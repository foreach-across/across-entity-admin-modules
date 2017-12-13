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
import com.foreach.across.modules.adminweb.ui.PageContentStructure;
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiFactory;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Checks if there is global feedback present on the model and renders it in the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure}.
 * Feedback should be registered as either model attribute or request parameter {@link #FEEDBACK_ATTRIBUTE_KEY}.  If both are present, first the feedback
 * defined as request parameter will be retrieved, followed by the feedback as model attribute.
 * <p/>
 * The feedback attribute is a formatted string of STYLE:MESSAGE_CODE.  Helper methods {@link #addFeedbackMessage(String, Style, String)} and
 * {@link #decodeFeedbackMessages(String)} are available for building and decoding the feedback values.
 * <p/>
 * Feedback messages will be added as dismissible alerts to the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure} feedback.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper#addGlobalFeedbackAfterRedirect(EntityViewRequest, Style, String)
 * @since 2.0.0
 */
@Component
@Exposed
public final class GlobalPageFeedbackViewProcessor extends EntityViewProcessorAdapter
{
	public static final String FEEDBACK_ATTRIBUTE_KEY = "feedbackMessageCodes";

	private BootstrapUiFactory bootstrapUiFactory;

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		Map<String, Style> feedback = retrieveFeedbackMessage( entityViewRequest );

		if ( !feedback.isEmpty() ) {
			EntityViewContext entityViewContext = entityViewRequest.getEntityViewContext();
			PageContentStructure page = entityViewRequest.getPageContentStructure();
			EntityMessages messages = entityViewContext.getEntityMessages();

			// todo: move to EntityViewPageHelper
			feedback.forEach( ( messageCode, style ) -> {
				page.addToFeedback(
						bootstrapUiFactory.alert()
						                  .style( style )
						                  .dismissible()
						                  .text( messages.withNameSingular( messageCode, entityViewContext.getEntityLabel() ) )
						                  .build( builderContext )
				);
			} );
		}
	}

	private Map<String, Style> retrieveFeedbackMessage( EntityViewRequest entityViewRequest ) {
		Map<String, Style> feedback = new LinkedHashMap<>();

		String[] parameterValues = entityViewRequest.getWebRequest().getParameterValues( FEEDBACK_ATTRIBUTE_KEY );

		if ( parameterValues != null ) {
			Stream.of( parameterValues )
			      .forEach( paramValue -> feedback.putAll( decodeFeedbackMessages( paramValue ) ) );
		}

		feedback.putAll( decodeFeedbackMessages( (String) entityViewRequest.getModel().get( FEEDBACK_ATTRIBUTE_KEY ) ) );

		return feedback;
	}

	@Autowired
	void setBootstrapUiFactory( BootstrapUiFactory bootstrapUiFactory ) {
		this.bootstrapUiFactory = bootstrapUiFactory;
	}

	/**
	 * Decode a string of feedback messages to a {@link java.util.LinkedHashMap} of message code along with their style.
	 *
	 * @param attributeValue string to decode
	 * @return map of message code with style value
	 */
	public static Map<String, Style> decodeFeedbackMessages( String attributeValue ) {
		Map<String, Style> decoded = new LinkedHashMap<>();

		Stream.of( StringUtils.defaultString( attributeValue ).split( "," ) )
		      .map( s -> s.split( ":" ) )
		      .filter( p -> p.length == 2 )
		      .forEach( p -> decoded.put( p[1], new Style( p[0] ) ) );

		return decoded;
	}

	/**
	 * Add a feedback message (combination of message code and style) to an attribute.
	 *
	 * @param currentValue  current attribute value to add the feedback to
	 * @param feedbackStyle style of the feedback
	 * @param messageCode   message
	 * @return new value string
	 */
	public static String addFeedbackMessage( String currentValue, @NonNull Style feedbackStyle, @NonNull String messageCode ) {
		String feedbackToken = ( feedbackStyle.isDefaultStyle() ? feedbackStyle.forPrefix( "alert" ) : feedbackStyle.getName() )
				+ ":" + messageCode;
		return StringUtils.defaultString( currentValue ).isEmpty() ? feedbackToken : currentValue + "," + feedbackToken;
	}
}
