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

package com.foreach.across.modules.entity.support;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

/**
 * {@link MessageSource} implementation using a {@link EntityMessageCodeResolver} with fallback
 * message lookups for every message.  Used by the {@link com.foreach.across.modules.entity.views.DefaultEntityViewFactory}
 * on the {@link com.foreach.across.modules.web.ui.ViewElementBuilderContext}.
 *
 * @author Arne Vandamme
 * @since 2.1.0
 */
@RequiredArgsConstructor
public class EntityViewMessageSource implements MessageSource
{
	private final EntityMessageCodeResolver messageCodeResolver;

	@Override
	public String getMessage( String code, Object[] args, String defaultMessage, Locale locale ) {
		return messageCodeResolver.getMessageWithFallback( code, args, defaultMessage, locale );
	}

	@Override
	public String getMessage( String code, Object[] args, Locale locale ) throws NoSuchMessageException {
		return messageCodeResolver.getMessageWithFallback( code, args, code, locale );
	}

	@Override
	public String getMessage( MessageSourceResolvable resolvable, Locale locale ) throws NoSuchMessageException {
		return messageCodeResolver.getMessage( resolvable, locale );
	}
}
