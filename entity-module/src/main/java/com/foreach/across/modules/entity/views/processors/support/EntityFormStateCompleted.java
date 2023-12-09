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

import com.foreach.across.core.events.NamedAcrossEvent;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.support.EntityMessages;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * State object which holds the state after a typical entity form has completed.
 * Allows the user to customize the redirect or feedback messages, without having to
 * revert to a separate view processor.
 * <p/>
 * Can also be published as an event and handled globally.
 *
 * @author Arne Vandamme
 * @see com.foreach.across.modules.entity.views.processors.SaveEntityViewProcessor
 * @see com.foreach.across.modules.entity.views.processors.DeleteEntityViewProcessor
 * @since 4.0.0
 */
@RequiredArgsConstructor
public class EntityFormStateCompleted<T> implements NamedAcrossEvent, ResolvableTypeProvider
{
	public static final String ENTITY_CREATED = "ENTITY_CREATED";
	public static final String ENTITY_UPDATED = "ENTITY_UPDATED";
	public static final String ENTITY_DELETED = "ENTITY_DELETED";

	@Getter
	private final List<FeedbackMessage> feedbackMessages = new ArrayList<>();

	/**
	 * Name of the form state.
	 */
	@Getter
	private final String stateName;

	/**
	 * The entity on which the form was completed.
	 * Depending on the type of form this could hold either a dto, saved entity or
	 */
	@Getter
	private final T entity;

	/**
	 * The original entity view request.
	 */
	@Getter
	private final EntityViewRequest entityViewRequest;

	/**
	 * The view being generated.
	 */
	@Getter
	private final EntityView entityView;

	@Override
	public String getEventName() {
		return getEntityViewContext().getEntityConfiguration().getName();
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics( EntityFormStateCompleted.class, getEntityViewContext().getEntityConfiguration().getEntityType() );
	}

	public EntityViewContext getEntityViewContext() {
		return entityViewRequest.getEntityViewContext();
	}

	public EntityViewLinkBuilder getLinkBuilder() {
		return getEntityViewContext().getLinkBuilder();
	}

	public EntityMessages getEntityMessages() {
		return getEntityViewContext().getEntityMessages();
	}

	/**
	 * Check if this event is for the given state.
	 *
	 * @param stateName requested state
	 * @return true if available
	 */
	public boolean isForState( @NonNull String stateName ) {
		return StringUtils.equals( this.stateName, stateName );
	}

	/**
	 * Set the redirect that should be done.
	 *
	 * @param url for the redirect
	 */
	public void setRedirectUrl( String url ) {
		entityView.setRedirectUrl( url );
	}

	/**
	 * Remove any set redirect url.
	 */
	public void clearRedirect() {
		entityView.setRedirectUrl( null );
	}

	/**
	 * Add feedback message.
	 */
	public void addFeedbackMessage( @NonNull FeedbackMessage message ) {
		feedbackMessages.add( message );
	}

	/**
	 * Clear previously set feedback.
	 */
	public void clearFeedback() {
		feedbackMessages.clear();
	}

	public static FeedbackMessage.FeedbackMessageBuilder feedback( Style style ) {
		return FeedbackMessage.builder().style( style );
	}

	/**
	 * Represents a feedback message that should be sent to the user.
	 * If a redirect is set, these feedback messages will be sent after the redirect.
	 * Else they will be added to the page immediately.
	 */
	@Data
	@Builder
	public static class FeedbackMessage
	{
		@NonNull
		private Style style;

		/**
		 * Original message code that was used to resolve the message.
		 */
		private String messageCode;

		/**
		 * Actual message to display.
		 */
		private String message;

		/**
		 * Should this message only be added if a redirect will happen?
		 */
		private boolean onlyForRedirect;

		public String getMessageOrCode() {
			return message == null ? messageCode : message;
		}
	}
}
