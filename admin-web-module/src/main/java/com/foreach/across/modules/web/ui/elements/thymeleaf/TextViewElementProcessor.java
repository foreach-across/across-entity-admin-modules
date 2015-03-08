package com.foreach.across.modules.web.ui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.thymeleaf.ComponentElementProcessor;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.unbescape.html.HtmlEscape;

import java.util.Collections;
import java.util.List;

public class TextViewElementProcessor implements ThymeleafViewElementProcessor<TextViewElement>
{
	@Override
	public List<Node> buildNodes( TextViewElement viewElement,
	                              Arguments arguments,
	                              ComponentElementProcessor componentElementProcessor ) {
		String html = viewElement.isEscapeXml()
				? HtmlEscape.escapeHtml4Xml( viewElement.getText() )
				: viewElement.getText();

		Text text = new Text( html, null, null, true );

		return Collections.singletonList( (Node) text );
	}
}
