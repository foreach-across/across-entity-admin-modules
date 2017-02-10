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

import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.entity.views.context.ConfigurableEntityViewContext;
import com.foreach.across.modules.entity.views.support.EntityMessages;

/**
 * Updates the message code resolver on the {@link com.foreach.across.modules.entity.views.context.EntityViewContext}
 * with one or more prefixes. By default adds <strong>entityViews</strong> as the prefix.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
public class MessagePrefixingViewProcessor extends EntityViewProcessorAdapter
{
	private final String[] messagePrefixes;

	public MessagePrefixingViewProcessor() {
		this( "entityViews" );
	}

	public MessagePrefixingViewProcessor( String... messagePrefixes ) {
		this.messagePrefixes = messagePrefixes.clone();
	}

	@Override
	public void prepareEntityViewContext( ConfigurableEntityViewContext entityViewContext ) {
		EntityMessageCodeResolver prefixedResolver = entityViewContext.getMessageCodeResolver().prefixedResolver( messagePrefixes );

		entityViewContext.setMessageCodeResolver( prefixedResolver );
		entityViewContext.setEntityMessages( new EntityMessages( prefixedResolver ) );
	}
}
