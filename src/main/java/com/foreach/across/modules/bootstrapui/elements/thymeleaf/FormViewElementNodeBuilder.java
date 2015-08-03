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
package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.spring4.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arne Vandamme
 */
public class FormViewElementNodeBuilder extends NestableNodeBuilderSupport<FormViewElement>
{
	public static final String VAR_CURRENT_BOOTSTRAP_FORM = "_currentBootstrapForm";

	@Override
	public List<Node> buildNodes( FormViewElement form,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = createNode( form, arguments, viewElementNodeFactory );
		attribute( node, "id", retrieveHtmlId( arguments, form ) );

		applyProperties( form, arguments, node );

		viewElementNodeFactory.setAttributes( node, form.getAttributes() );

		Arguments newArguments = buildFormArguments( form, arguments );

		for ( ViewElement child : form ) {
			viewElementNodeFactory.buildNodes( child, newArguments ).forEach( node::addChild );
		}

		return Collections.singletonList( (Node) node );
	}

	private Arguments buildFormArguments( FormViewElement form, Arguments original ) {
		Configuration configuration = original.getConfiguration();
		IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser( configuration );

		VariableExpression varExpression =
				(VariableExpression) expressionParser.parseExpression(
						original.getConfiguration(), original, commandAttributeName( form.getCommandAttribute() )
				);

		Map<String, Object> newVariables = new HashMap<>();
		newVariables.put( VAR_CURRENT_BOOTSTRAP_FORM, form );
		newVariables.put( SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION, varExpression );

		Arguments newArguments = original.addLocalVariables( newVariables );
		newArguments.getExpressionObjects().putAll( original.getExpressionObjects() );
		newArguments.getExpressionObjects().putAll(
				SpelVariableExpressionEvaluator.INSTANCE
						.computeExpressionObjects( newArguments.getConfiguration(), newArguments )
		);

		return newArguments;
	}

	private String commandAttributeName( String attributeName ) {
		return StringUtils.startsWith( attributeName, "${" ) ? attributeName : "${" + attributeName + "}";
	}

	@Override
	protected Element createNode( FormViewElement control,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = createElement( "form" );

		// Support request data value processing - CSRF tokens
		final Map<String, String> extraHiddenFields =
				RequestDataValueProcessorUtils.getExtraHiddenFields( arguments.getConfiguration(), arguments );

		if ( extraHiddenFields != null && extraHiddenFields.size() > 0 ) {

			for ( final Map.Entry<String, String> extraHiddenField : extraHiddenFields.entrySet() ) {
				final Element extraHiddenElement = new Element( "input" );
				extraHiddenElement.setAttribute( "type", "hidden" );
				extraHiddenElement.setAttribute( "name", extraHiddenField.getKey() );
				extraHiddenElement.setAttribute( "value", extraHiddenField.getValue() );

				node.insertChild( node.numChildren(), extraHiddenElement );
			}
		}

		return node;
	}
}
