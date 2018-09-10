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
import com.foreach.across.modules.entity.views.bootstrapui.elements.ViewElementFieldset;
import com.foreach.across.samples.entity.application.business.User;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.annotation.Configuration;

import static com.foreach.across.modules.entity.views.EntityViewCustomizers.basicSettings;
import static com.foreach.across.modules.entity.views.EntityViewCustomizers.formSettings;

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
						             .and()
						             // fieldset properties
						             .property( "actualFieldset" )
						             .propertyType( Owner.class )
						             .valueFetcher( x -> null )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .and()
						             .property( "bodyOnly" )
						             .propertyType( Owner.class )
						             .valueFetcher( x -> null )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_BODY_ONLY )
						             .and()
						             .property( "customFieldset" )
						             .propertyType( Owner.class )
						             .valueFetcher( x -> null )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.template( "custom-class", "fieldset/legend/div" ) )
						             .and()
						             .property( "sectionWithH1" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H1 )
						             .and()
						             .property( "sectionWithH2" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H2 )
						             .and()
						             .property( "sectionWithH3" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H3 )
						             .and()
						             .property( "sectionWithH4" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H4 )
						             .and()
						             .property( "sectionWithH5" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H5 )
						             .and()
						             .property( "sectionWithH6" )
						             .propertyType( Owner.class )
						             .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
						             .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H6 )
						             .and()
		        )
		        .formView(
				        "fieldsets",
				        basicSettings()
						        .adminMenu( "/fieldsets" )
						        .andThen( formSettings().forExtension( true ).addFormButtons( false ) )
						        .andThen( v -> v.showProperties( "actualFieldset", "customFieldset", "bodyOnly",
						                                         "sectionWithH1", "sectionWithH2", "sectionWithH3", "sectionWithH4", "sectionWithH5",
						                                         "sectionWithH6" ) )
						        /*.andThen(
								        layout()
										        .setColumns( 3 )
								                .withColumn( 1 )
								                .addFieldset( dsjdisjds )
								                .addToHeader("","")
								                .withElements("", "", "", "")
								                .showElements( "", "", "", "" )
						        )*/
		        );
	}

	@Data
	private static class Owner
	{
		@Length(max = 100)
		private String name;

		private int yearOfBirth;
	}
}
