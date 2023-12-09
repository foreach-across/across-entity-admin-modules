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

package admin.application.config;

import com.foreach.across.modules.adminweb.resource.AdminWebWebResources;
import com.foreach.across.modules.web.events.BuildTemplateWebResourcesEvent;
import com.foreach.across.modules.web.resource.WebResourceRule;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import static com.foreach.across.modules.web.resource.WebResource.CSS;
import static com.foreach.across.modules.web.resource.WebResource.css;

/**
 * @author Stijn Vanhoof
 */
@Configuration
@RequiredArgsConstructor
public class AdminUiConfiguration
{
	@EventListener(condition = "#template.templateName=='adminWeb'")
	public void registerCustomCss( BuildTemplateWebResourcesEvent template ) {
		template.applyResourceRules(
				WebResourceRule.add( css( "@static:/adminWeb/css/adminweb-logo.css" ) )
				               .withKey( AdminWebWebResources.NAME + "-logo" )
				               .replaceIfPresent( false )
				               .toBucket( CSS )
		);
	}
}
