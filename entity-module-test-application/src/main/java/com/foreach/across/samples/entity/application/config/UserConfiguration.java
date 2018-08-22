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

package com.foreach.across.samples.entity.application.config;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.entity.EntityAttributes;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.samples.entity.application.business.User;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Configuration
public class UserConfiguration implements EntityConfigurer
{
	@Override
	public void configure( EntitiesConfigurationBuilder entities ) {
		entities.withType( User.class )
		        .properties(
				        props ->
						        // display name as not required
						        props.property( "name" ).attribute( EntityAttributes.PROPERTY_REQUIRED, false ).and()
						             .property( "profilePicture" )
						             .viewElementBuilder( ViewElementMode.CONTROL, BootstrapUiBuilders.file().controlName( "entity.profilePicture" ) )
						             .attribute( EntityAttributes.FORM_ENCTYPE, FormViewElement.ENCTYPE_MULTIPART )
		        );
	}
}
