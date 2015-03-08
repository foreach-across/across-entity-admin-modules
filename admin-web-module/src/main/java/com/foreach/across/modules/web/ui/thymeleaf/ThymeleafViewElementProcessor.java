package com.foreach.across.modules.web.ui.thymeleaf;

import com.foreach.across.modules.bootstrapui.thymeleaf.ComponentElementProcessor;
import com.foreach.across.modules.web.ui.ViewElement;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;

import java.util.List;

public interface ThymeleafViewElementProcessor<T extends ViewElement>
{
	List<Node> buildNodes( T viewElement, Arguments arguments, ComponentElementProcessor componentElementProcessor );
}
