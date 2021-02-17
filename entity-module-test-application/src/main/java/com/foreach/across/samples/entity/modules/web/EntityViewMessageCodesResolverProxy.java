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

package com.foreach.across.samples.entity.modules.web;

import com.foreach.across.modules.entity.views.context.EntityViewContext;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.MessageCodesResolver;

/**
 * Proxy wrapping around the current message code resolver attached to the view context, which should never be {@code null}.
 */
@RequiredArgsConstructor
public class EntityViewMessageCodesResolverProxy implements MessageCodesResolver
{
	private final EntityViewContext viewContext;

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName ) {
		return viewContext.getMessageCodeResolver().resolveMessageCodes( errorCode, objectName );
	}

	@Override
	public String[] resolveMessageCodes( String errorCode, String objectName, String field, Class<?> fieldType ) {
		return viewContext.getMessageCodeResolver().resolveMessageCodes( errorCode, objectName, field, fieldType );
	}
}