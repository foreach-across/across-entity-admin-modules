package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextareaFormElement;
import com.foreach.across.modules.bootstrapui.elements.TextboxFormElement;
import com.foreach.across.modules.web.ui.ViewElementBuilderFactory;
import com.foreach.across.test.support.AbstractViewElementBuilderTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFormGroupElementBuilder extends AbstractViewElementBuilderTest<FormGroupElementBuilder, FormGroupElement>
{
	@Override
	protected FormGroupElementBuilder createBuilder( ViewElementBuilderFactory builderFactory ) {
		return new FormGroupElementBuilder();
	}

	@Test
	public void getControlAndLabel() {
		assertNull( builder.getControl() );
		assertNull( builder.getLabel() );

		TextboxFormElementBuilder textbox = new TextboxFormElementBuilder();
		LabelFormElementBuilder label = new LabelFormElementBuilder();

		builder.control( textbox ).label( label );
		assertNotNull( builder.getControl() );
		assertSame( textbox, builder.getControl().getSource() );
		assertSame( textbox, builder.getControl( TextboxFormElementBuilder.class ) );
		assertNull( builder.getControl( FormGroupElementBuilder.class ) );

		assertSame( label, builder.getLabel().getSource() );
		assertSame( label, builder.getLabel( LabelFormElementBuilder.class ) );
		assertNull( builder.getLabel( TextboxFormElementBuilder.class ) );

		TextareaFormElement textarea = new TextareaFormElement();
		builder.control( textarea );

		assertSame( textarea, builder.getControl( TextboxFormElement.class ) );
		assertSame( textarea, builder.getControl( TextareaFormElement.class ) );
		assertNull( builder.getControl( LabelFormElement.class ) );

		assertSame( label, builder.getLabel().getSource() );
		assertSame( label, builder.getLabel( LabelFormElementBuilder.class ) );
		assertNull( builder.getLabel( TextboxFormElementBuilder.class ) );
	}
}
