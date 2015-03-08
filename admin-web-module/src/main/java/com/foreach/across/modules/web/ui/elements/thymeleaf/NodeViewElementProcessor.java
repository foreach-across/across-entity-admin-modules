package com.foreach.across.modules.web.ui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.thymeleaf.ComponentElementProcessor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class NodeViewElementProcessor implements ThymeleafViewElementProcessor<NodeViewElement>
{
	@Override
	public List<Node> buildNodes( NodeViewElement element,
	                              Arguments arguments,
	                              ComponentElementProcessor componentElementProcessor ) {
		Element node = new Element( element.getTagName() );

		for ( Map.Entry<String, String> attribute : element.getAttributes().entrySet() ) {
			node.setAttribute( attribute.getKey(), attribute.getValue() );
		}

		for ( ViewElement child : element ) {
			for ( Node childNode : componentElementProcessor.buildNodes( child, arguments ) ) {
				node.addChild( childNode );
			}
		}

		return Collections.singletonList( (Node) node );
	}
}
