package com.foreach.across.modules.bootstrapui.elements.complex;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders.*;

public class TestFormPageView extends AbstractBootstrapViewElementTest
{
	@Test
	public void buildFormManually() {
		FormViewElement form = new FormViewElement();

		FormGroupElement nameGroup = new FormGroupElement();
		LabelFormElement nameLabel = new LabelFormElement();
		nameLabel.setText( "Name *" );
		TextboxFormElement name = new TextboxFormElement();
		name.setText( "John Doe" );
		nameGroup.setLabel( nameLabel );
		nameGroup.setControl( name );

		form.addChild( nameGroup );

		FormGroupElement buttons = new FormGroupElement();

		ButtonViewElement save = new ButtonViewElement();
		save.setType( ButtonViewElement.Type.BUTTON_SUBMIT );
		save.setStyle( Style.Button.PRIMARY );
		save.setText( "Save" );
		buttons.addChild( save );

		ButtonViewElement cancel = new ButtonViewElement();
		cancel.setType( ButtonViewElement.Type.LINK );
		cancel.setStyle( Style.Button.LINK );
		cancel.setText( "Cancel" );
		cancel.setUrl( "/goback" );
		buttons.addChild( cancel );

		form.addChild( buttons );

		verify( form );
	}

	@Test
	public void buildFormThroughBuilders() {
		FormViewElementBuilder formBuilder = form()
				.add(
						formGroup(
								label( "Name *" ),
								textbox().text( "John Doe" )
						)
				)
				.add(
						formGroup()
								.add(
										button().submit().style( Style.Button.PRIMARY ).text( "Save" ),
										button().link( "/goback" ).text( "Cancel" )
								)
				);

		verify( formBuilder.build( new DefaultViewElementBuilderContext() ) );
	}

	private void verify( ViewElement element ) {
		renderAndExpect(
				element,
				"<form role='form' method='post'>" +
						"<div class='form-group'>" +
						"<label class='control-label'>Name *</label>" +
						"<input data-bootstrapui-adapter-type='basic' type='text' class='form-control' value='John Doe' />" +
						"</div>" +
						"<div class='form-group'>" +
						"<button type='submit' class='btn btn-primary'>Save</button>" +
						"<a role='button' class='btn btn-link' href='/goback'>Cancel</a>" +
						"</div>" +
						"</form>"
		);
	}
}
