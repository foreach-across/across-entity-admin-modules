package com.foreach.across.modules.experimental.modals.ui.processors;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;

import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;

public abstract class AbstractModalViewProcessor extends EntityViewProcessorAdapter
{
	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/experimental-module.js" ) )
				               .withKey( "experimental-module" )
				               .after( EntityModuleWebResources.NAME )
				               .before( "modal-loader-js" )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/action-executor.js" ) )
				               .withKey( "request-executor" )
				               .after( EntityModuleWebResources.NAME )
				               .before( "modal-loader-js" )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
		webResourceRegistry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/modal-loader.js" ) )
				               .withKey( "modal-loader-js" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END )
		);
	}
}
