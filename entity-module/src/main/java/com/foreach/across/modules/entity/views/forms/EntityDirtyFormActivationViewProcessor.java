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

package com.foreach.across.modules.entity.views.forms;

import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.bootstrapui.resource.JQueryWebResources;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SingleEntityFormViewProcessor;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.Setter;

import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.data;

/**
 * Registers a marker attribute on the {@link SingleEntityFormViewProcessor#FORM} as well as
 * registers {@link WebResource}s to support dirty form checking.
 * <p>
 * Configures the <a href="https://github.com/snikch/jquery.dirtyforms">dirtyforms plugin</a> on the entity form.
 */
public class EntityDirtyFormActivationViewProcessor extends EntityViewProcessorAdapter
{
	public static final String EM_DIRTY_FORMS_REGISTRATION = "dirty-forms-registration";
	public static final String DIRTY_FORMS = "dirty-forms";
	public static final String DIRTY_FORMS_DIALOG = "dirty-forms-dialog";

	@Getter
	@Setter
	private EntityDirtyFormsConfiguration dirtyFormsConfiguration;

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add( WebResource.javascript( "@webjars:/jquery.dirtyforms/2.0.0/jquery.dirtyforms.js" ) )
				               .withKey( DIRTY_FORMS )
				               .after( JQueryWebResources.NAME )
				               .before( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				WebResourceRule.add( WebResource.javascript( "@webjars:/jquery.dirtyforms.dialogs.bootstrap/2.0.0/jquery.dirtyforms.dialogs.bootstrap.js" ) )
				               .withKey( DIRTY_FORMS_DIALOG )
				               .after( DIRTY_FORMS )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				WebResourceRule.add( WebResource.javascript( "@static:/entity/js/entity-dirty-forms.js" ) )
				               .withKey( EM_DIRTY_FORMS_REGISTRATION )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
	}

	@Override
	protected void postRender( EntityViewRequest entityViewRequest,
	                           EntityView entityView,
	                           ContainerViewElement container,
	                           ViewElementBuilderContext builderContext ) {
		ContainerViewElementUtils.find( container, SingleEntityFormViewProcessor.FORM, FormViewElement.class )
		                         .ifPresent( fve -> fve.set( data( "em-dirty-form-check", dirtyFormsConfiguration ) ) );
	}

}
