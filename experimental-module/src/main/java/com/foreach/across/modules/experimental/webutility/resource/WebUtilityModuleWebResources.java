package com.foreach.across.modules.experimental.webutility.resource;

import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourcePackage;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;

import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;

public class WebUtilityModuleWebResources implements WebResourcePackage
{
	public static final String NAME = "experimental-web-utility";

	@Override
	public void install( WebResourceRegistry registry ) {
		registry.apply(
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/experimental-module.js" ) )
				               .withKey( "experimental-web-utilities" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/action-loader.js" ) )
				               .withKey( "experimental-action-loaders" )
				               .after( EntityModuleWebResources.NAME )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				// editable-value
				WebResourceRule.add(
						WebResource.javascript( "@static:/experimental/web/editable-value.js" ) )
				               .withKey( "experimental-editable-value-js" )
				               .after( "experimental-web-utilities" )
				               .toBucket( JAVASCRIPT_PAGE_END ),
				WebResourceRule.add(
						WebResource.css( "@static:/experimental/web/editable-value.css" ) )
				               .withKey( "experimental-editable-value-css" )
				               .toBucket( CSS )
		);
	}
}
