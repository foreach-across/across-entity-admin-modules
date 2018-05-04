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
import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.conditionals.ConditionalOnAdminWeb;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.ContainerViewElementBuilderSupport;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Checks if there is global feedback present on the model and renders it in the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure}.
 * Feedback should be registered as either model attribute or request parameter {@link #FEEDBACK_ATTRIBUTE_KEY}.  If both are present, first the feedback
 * defined as request parameter will be retrieved, followed by the feedback as model attribute.
 * <p/>
 * The feedback attribute is a formatted string of STYLE:MESSAGE_CODE.  Helper method {@link #addFeedbackMessage(Map, Style, String)}
 * is available for building the feedback values.
 * <p/>
 * Feedback messages will be added as dismissible alerts to the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure} feedback.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.processors.support.EntityViewPageHelper#addGlobalFeedbackAfterRedirect(EntityViewRequest, Style, String)
 * @since 2.0.0
 */
@ConditionalOnAdminWeb
@Component
@Exposed
public final class GlobalPageFeedbackViewProcessor extends EntityViewProcessorAdapter
{
	public static final String FEEDBACK_ATTRIBUTE_KEY = "feedbackMessageCodes";

	@Override
	protected void render( EntityViewRequest entityViewRequest,
	                       EntityView entityView,
	                       ContainerViewElementBuilderSupport<?, ?> containerBuilder,
	                       ViewElementBuilderMap builderMap,
	                       ViewElementBuilderContext builderContext ) {
		Map<String, Style> feedback = retrieveFeedbackMessage( entityViewRequest );

		if ( !isEmptyMap( feedback ) ) {
			// todo: move to EntityViewPageHelper
			PageContentStructure page = entityViewRequest.getPageContentStructure();
			feedback.forEach( ( message, style ) -> page.addToFeedback( BootstrapUiBuilders.alert()
			                                                                               .style( style )
			                                                                               .dismissible()
			                                                                               .text( message )
			                                                                               .build( builderContext ) ) );
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Style> retrieveFeedbackMessage( EntityViewRequest entityViewRequest ) {
		Map<String, Style> feedback = new HashMap<>();
		Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap( (HttpServletRequest) entityViewRequest.getWebRequest().getNativeRequest() );

		if ( flashMap != null && flashMap.containsKey( FEEDBACK_ATTRIBUTE_KEY ) ) {
			Map<String, Style> feedbackValues = (Map<String, Style>) flashMap.get( FEEDBACK_ATTRIBUTE_KEY );
			feedback.putAll( feedbackValues );
		}

		return feedback;
	}

	/**
	 * Add a feedback message (combination of message code and style) to an attribute.
	 *
	 * @param currentValue  map holding the current resolved messages and their style to add the feedback to.
	 * @param feedbackStyle style of the feedback
	 * @param message       resolved message
	 * @return new map holding the existing messages and the new message.
	 */
	public static Map<String, Style> addFeedbackMessage( Map<String, Style> currentValue, @NonNull Style feedbackStyle, @NonNull String message ) {
		Map<String, Style> feedback = new HashMap<>();
		if ( !isEmptyMap( currentValue ) ) {
			feedback.putAll( currentValue );
		}
		feedback.put( message, feedbackStyle );
		return feedback;
	}

	private static boolean isEmptyMap( Map<String, Style> map ) {
		return map == null || map.isEmpty();
	}
}
