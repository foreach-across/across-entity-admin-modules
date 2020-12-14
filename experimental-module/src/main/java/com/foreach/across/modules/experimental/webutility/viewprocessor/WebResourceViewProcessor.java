package com.foreach.across.modules.experimental.webutility.viewprocessor;

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.experimental.webutility.support.WebResourceType;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;

import java.util.HashMap;
import java.util.Map;

public class WebResourceViewProcessor extends EntityViewProcessorAdapter
{

	private final Map<String, WebResourceType> webResourceToAdd = new HashMap<>();

	public WebResourceViewProcessor( String webResource, WebResourceType webResourceType ) {
		webResourceToAdd.put( webResource, webResourceType );
	}

	public WebResourceViewProcessor( Map<String, WebResourceType> webResources ) {
		webResourceToAdd.putAll( webResources );
	}

	@Override
	protected void registerWebResources( EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry ) {
		webResourceToAdd.forEach( ( key, value ) -> {

			webResourceRegistry.apply(
					WebResourceRule.add( value.isCss() ?
							                     WebResource.css( String.format( "%s%s", value.getPrefix(),
							                                                     String.format( "%s%s", key, value.getExtension() ) ) ) :
							                     WebResource.javascript(
									                     String.format( "%s%s", value.getPrefix(), String.format( "%s%s", key, value.getExtension() ) ) ) )
					               .withKey( value + "-" + key )
					               .after( EntityModuleWebResources.NAME )
					               .toBucket( value.isCss() ? WebResource.CSS : WebResource.JAVASCRIPT_PAGE_END ) );
		} );
	}

}
