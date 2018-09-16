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
						             // todo automaticall resolve to fieldset
						             .property( "address[]" ).viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE ).and()
//						             .property( "profilePicture" )
//						             .viewElementBuilder( ViewElementMode.CONTROL, BootstrapUiBuilders.file().controlName( "entity.profilePicture" ) )
//						             .attribute( EntityAttributes.FORM_ENCTYPE, FormViewElement.ENCTYPE_MULTIPART )
                                     // fieldset properties
                                     .property( "fieldset" )
                                     .propertyType( Owner.class )
                                     // todo: default controller for type should be created
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .and()
                                     .property( "bodyOnly" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_BODY_ONLY )
                                     .and()
                                     .property( "sectionWithH1" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H1 )
                                     .and()
                                     .property( "sectionWithH2" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H2 )
                                     .and()
                                     .property( "sectionWithH3" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_SECTION_H3 )
                                     .and()
                                     .property( "panelDefault" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_PANEL_DEFAULT )
                                     .and()
                                     .property( "panelDanger" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_PANEL_DANGER )
                                     .and()
                                     .property( "panelPrimary" )
                                     .propertyType( Owner.class )
                                     .controller( c -> c.createValueSupplier( Owner::new ) )
                                     .viewElementType( ViewElementMode.FORM_WRITE, ViewElementFieldset.ELEMENT_TYPE )
                                     .attribute( ViewElementFieldset.TEMPLATE, ViewElementFieldset.TEMPLATE_PANEL_PRIMARY )
                                     .and()
		        )
		        .formView(
				        "fieldsets",
				        basicSettings()
						        .adminMenu( "/fieldsets" )
						        .andThen( formSettings().forExtension( true ).addFormButtons( false ) )
						        .andThen( v -> v.showProperties( "fieldset", "bodyOnly",
						                                         "sectionWithH1", "sectionWithH2", "sectionWithH3",
						                                         "panelDefault", "panelDanger", "panelPrimary" ) )
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
