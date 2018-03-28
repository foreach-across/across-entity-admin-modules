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

package com.foreach.across.modules.entity.views.settings;

import com.foreach.across.modules.bootstrapui.elements.Grid;
import com.foreach.across.modules.entity.config.builders.EntityViewFactoryBuilder;
import com.foreach.across.modules.entity.views.processors.SaveEntityViewProcessor;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.function.Consumer;

/**
 * Basic consumer that allows configuration of form related options.
 * Can set some basic form properties and re-configure a form view for a single extension form.
 * <p>
 * It supports configuration of the following processors:
 * <ul>
 * <li>{@link com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor}</li>
 * </ul>
 *
 * @author Arne Vandamme
 * @since 3.0.0
 */
@SuppressWarnings("unchecked")
@Accessors(fluent = true)
public class FormEntityViewSettings implements Consumer<EntityViewFactoryBuilder>
{
	/**
	 * -- SETTER --
	 * Should the default form buttons (save/cancel) be added to the form.
	 */
	@Setter
	private Boolean addFormButtons;

	/**
	 * -- SETTER --
	 * Set the custom form grid that should be used for the form body.
	 */
	@Setter
	private Grid formLayout;

	/**
	 * -- SETTER --
	 * Should the form view be configured for a single extension.
	 */
	@Setter
	private boolean forExtension;

	/**
	 * Apply the configuration to the view factory builder.
	 *
	 * @param builder for the view factory
	 */
	@Override
	public void accept( EntityViewFactoryBuilder builder ) {
		if ( addFormButtons != null || formLayout != null ) {
			builder.postProcess( SingleEntityFormViewProcessor.class, form -> {
				if ( addFormButtons != null ) {
					form.setAddDefaultButtons( addFormButtons );
				}
				if ( formLayout != null ) {
					form.setGrid( formLayout );
				}
			} );
		}

		if ( forExtension ) {
			builder.showProperties()
			       .removeViewProcessor( SaveEntityViewProcessor.class.getName() );
		}
	}
}
