package com.foreach.across.modules.web.ui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.thymeleaf.ComponentElementProcessor;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessor;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class ContainerViewElementProcessor implements ThymeleafViewElementProcessor<ContainerViewElement>
{
	@Override
	public List<Node> buildNodes( ContainerViewElement container,
	                              Arguments arguments,
	                              ComponentElementProcessor componentElementProcessor ) {
		List<Node> list = new ArrayList<>();

		for ( ViewElement child : container ) {
			list.addAll( componentElementProcessor.buildNodes( child, arguments ) );
		}

		return list;
	}
}
