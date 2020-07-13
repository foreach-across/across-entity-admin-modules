package com.foreach.across.modules.experimental.application.controller;

import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.experimental.application.dto.CarResource;
import com.foreach.across.modules.experimental.application.dto.UserResource;
import com.foreach.across.modules.experimental.entitycontrols.domain.EntityControlFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class EntityControlFactoryDemoController
{
	private final EntityControlFactory entityControlFactory;

	@GetMapping(path = "/entity-controls")
	public String showRegisterForm( Model model,
	                                ViewElementBuilderContext ctx ) {

		UserResource userResource = UserResource.builder().firstName( "Jobs" ).lastName( "Peeters" ).email( "jos.peeters@forach.be" ).birthDate(
				LocalDateTime.of( 2000, 2, 20, 17, 00 ) ).build();

		Map<String, ViewElement> userCreateControls = entityControlFactory.createControlsForClass( UserResource.class )
		                                                                  .forInstance( userResource )
		                                                                  .defaultRenderMode( ViewElementMode.FORM_WRITE )
		                                                                  .showProperties( "email", "birthDate", "lastName", "fakeComment" )
		                                                                  .properties(
				                                                                  props -> props.property( "email" )
				                                                                                .attribute( TextboxFormElement.Type.class,
				                                                                                            TextboxFormElement.Type.TEXT )
				                                                                                .and()
				                                                                                .property( "fakeComment" )
				                                                                                .propertyType( String.class )
				                                                                                .readable( true )
				                                                                                .writable( true )
		                                                                  )
		                                                                  .renderModeForProperties( ViewElementMode.FORM_READ, "lastName" )
		                                                                  .build( ctx );

		CarResource car = new CarResource( "Chevrolet El Camino" );

		Map<String, ViewElement> carControls = entityControlFactory.createControlsForClass( CarResource.class )
		                                                           .forInstance( car )
		                                                           .loadViewProperties( "custom" )
		                                                           .build( ctx );

		NodeViewElement simpleUserForm = HtmlViewElements.html.builders.form()
		                                                               .addAll( userCreateControls.values() )
		                                                               .addAll( carControls.values() )
		                                                               .build( ctx );

		model.addAttribute( "simpleUserForm", simpleUserForm );

		return "th/experimental/entitycontrols/example";
	}
}
