package com.foreach.across.modules.experimental.modals.ui.components;

import com.foreach.across.config.AcrossContextConfigurer;
import com.foreach.across.core.AcrossContext;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.experimental.modals.ModalModule;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.test.support.AbstractViewElementTemplateTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = TestModalViewElementBuilder.Config.class)
public class TestModalViewElementBuilder extends AbstractViewElementTemplateTest
{
	private DefaultViewElementBuilderContext builderContext;
	private ModalViewElementBuilder builder;

	@Before
	public void setUp() {
		builderContext = new DefaultViewElementBuilderContext();
		builder = new ModalViewElementBuilder();
	}

	@Test
	public void modalSections() {
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.body();
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'><div class='modal-body'></div></div></div></div>" );

		builder = new ModalViewElementBuilder();
		builder.header()
		       .body()
		       .footer();
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' aria-labelledby='Title' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'><div id='Title' class='modal-header'></div><div class='modal-body'></div><div class='modal-footer'></div></div></div></div>" );

	}

	@Test
	public void nameSetsIdIfUnset() {
		builder.name( "nameAndId" );
		renderAndExpect( builder.build(),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' id='nameAndId' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.id( "newId" );
		renderAndExpect( builder.build(),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' id='newId' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.name( "newName" );
		renderAndExpect( builder.build(),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' id='newId' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.header()
		       .body()
		       .footer();

		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' aria-labelledby='newIdTitle' tabindex='-1' aria-hidden='true' id='newId' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'><div id='newIdTitle' class='modal-header'></div><div class='modal-body'></div><div class='modal-footer'></div></div></div></div>" );
	}

	@Test
	public void scrollableBody() {
		builder.body()
		       .scrollableBody();

		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog modal-dialog-scrollable'><div class='modal-content'><div class='modal-body'></div></div></div></div>" );
	}

	@Test
	public void centeredModal() {
		builder.centered();
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog modal-dialog-centered'><div class='modal-content'></div></div></div>" );
	}

	@Test
	public void disableAnimations() {
		builder.animated( false );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' class='modal'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );
	}

	@Test
	public void modalTitleRegistersHeaderWithTitleAndCloseButton() {
		builder.title( "Modal title" );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' aria-labelledby='Title' tabindex='-1' aria-hidden='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'><div id='Title' class='modal-header'><div class='modal-title'><h3>Modal title</h3></div><button type='button' class='close' data-dismiss='modal' aria-label='Close'><i aria-hidden='true' class='fas fa-times axu-text-danger'></i></button></div></div></div></div>\n" );
	}

	@Test
	public void backdrop() {
		builder.staticBackdrop();
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' data-backdrop='static' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.backdrop( false );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' data-backdrop='false' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.backdrop( true );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' data-backdrop='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );
	}

	@Test
	public void keyboard() {
		builder.keyboard( false );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' data-keyboard='false' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

		builder.keyboard( true );
		renderAndExpect( builder.build( builderContext ),
		                 "<div role='dialog' tabindex='-1' aria-hidden='true' data-keyboard='true' class='modal fade'><div role='document' class='modal-dialog'><div class='modal-content'></div></div></div>" );

	}

	@Configuration
	protected static class Config implements AcrossContextConfigurer
	{
		@Override
		public void configure( AcrossContext context ) {
			context.addModule( new BootstrapUiModule() );
			context.addModule( new ModalModule() );
		}
	}
}
