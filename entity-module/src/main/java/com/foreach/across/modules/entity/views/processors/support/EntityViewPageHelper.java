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

import com.foreach.across.modules.entity.views.context.EntityViewContext;

/**
 * Helper for adding messages to the {@link com.foreach.across.modules.adminweb.ui.PageContentStructure} for a particular
 * {@link com.foreach.across.modules.entity.views.request.EntityViewRequest}.  Messages will be resolved against the configured
 * {@link com.foreach.across.modules.entity.views.support.EntityMessages} and will always get the {@link EntityViewContext#getEntityLabel()}
 * as the first argument.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class EntityViewPageHelper
{
	// EntityViewPageUtils.addGlobalFeedbackAfterRedirect( entityViewRequest, SUCCESS, "feedback.entityCreated" )
	// EntityViewPageUtils.throwOrAddExceptionFeedback( entityViewRequest, "feedback.entitySaveFailed", e );
	// EntityViewPageUtils.addExceptionFeedback( entityViewRequest, "feedback.entitySaveFailed", e );
	// EntityViewPageUtils.addGlobalFeedback( entityViewRequest, INFO, "feedback.entitySaved", e  );
}
