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
package com.foreach.across.modules.entity.newviews.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.entity.support.EntityMessageCodeResolver;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.springframework.util.Assert;

/**
 * Implementation of {@link ViewElementPostProcessor} for a {@link LabelFormElement} that will set the value
 * of the label based on a messagecode that is being resolved.  The currently set text of the label will
 * be used as the fallback in case the code cannot be resolved.
 *
 * @author Arne Vandamme
 */
@Deprecated
public class LabelCodeResolverPostProcessor implements ViewElementPostProcessor<LabelFormElement>
{
	private final String messageCode;
	private EntityMessageCodeResolver defaultMessageCodeResolver;

	public LabelCodeResolverPostProcessor( String messageCode ) {
		Assert.notNull( messageCode );
		this.messageCode = messageCode;
	}

	public LabelCodeResolverPostProcessor( String messageCode,
	                                       EntityMessageCodeResolver defaultMessageCodeResolver ) {
		this( messageCode );
		this.defaultMessageCodeResolver = defaultMessageCodeResolver;
	}

	@Override
	public void postProcess( ViewElementBuilderContext builderContext, LabelFormElement label ) {
		EntityMessageCodeResolver codeResolver = builderContext.getAttribute( EntityMessageCodeResolver.class );

		if ( codeResolver == null ) {
			codeResolver = defaultMessageCodeResolver;
		}

		if ( codeResolver != null ) {
			label.setText( codeResolver.getMessageWithFallback( messageCode, label.getText() ) );
		}
	}
}
