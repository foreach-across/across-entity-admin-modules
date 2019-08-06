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
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;
import org.springframework.http.HttpMethod;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.support.RequestContext;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.IEngineContext;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.requestdata.RequestDataValueProcessorUtils;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.expression.VariableExpression;
import org.thymeleaf.util.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class FormViewElementModelWriter extends AbstractHtmlViewElementModelWriter<FormViewElement>
{
	public static final String VAR_CURRENT_BOOTSTRAP_FORM = "_currentBootstrapForm";
	private static final String VAR_BOUND_ERRORS = "_temporaryErrorsAttribute";

	@Override
	protected void writeOpenElement( FormViewElement form, ThymeleafModelBuilder model ) {
		super.writeOpenElement( form, model );

		// set the command attribute
		ITemplateContext templateContext = model.getTemplateContext();

		if ( templateContext instanceof IEngineContext ) {
			IEngineContext engineContext = (IEngineContext) templateContext;
			engineContext.setVariable( VAR_CURRENT_BOOTSTRAP_FORM, form );

			registerBoundObject( form, engineContext );
		}
	}

	private void registerBoundObject( FormViewElement form, IEngineContext engineContext ) {
		String commandAttribute = form.getCommandAttribute();

		RequestContext requestContext = (RequestContext) engineContext.getVariable( SpringContextVariableNames.SPRING_REQUEST_CONTEXT );

		if ( form.getErrors() != null ) {
			Map<String, Object> requestContextModel = requestContext.getModel();

			String beanName = UUID.randomUUID().toString();
			String bindingResultName = BindingResult.MODEL_KEY_PREFIX + beanName;
			if ( requestContextModel != null ) {
				requestContextModel.put( bindingResultName, form.getErrors() );
			}
			else {
				engineContext.setVariable( bindingResultName, form.getErrors() );
			}
			commandAttribute = beanName;
			engineContext.setVariable( VAR_BOUND_ERRORS, bindingResultName );
		}

		if ( commandAttribute == null && form.getCommandObject() != null ) {
			commandAttribute = resolveCommandAttributeName( form.getCommandObject(), engineContext );
		}

		if ( commandAttribute != null ) {
			IEngineConfiguration configuration = engineContext.getConfiguration();
			IStandardExpressionParser expressionParser = StandardExpressions.getExpressionParser( configuration );

			VariableExpression varExpression = (VariableExpression) expressionParser.parseExpression( engineContext, commandAttributeName( commandAttribute ) );
			engineContext.setVariable( SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION, varExpression );
		}
	}

	private String commandAttributeName( String attributeName ) {
		return StringUtils.startsWith( attributeName, "${" ) ? attributeName : "${" + attributeName + "}";
	}

	private String resolveCommandAttributeName( Object commandObject, IEngineContext engineContext ) {
		for ( String variableName : engineContext.getVariableNames() ) {
			if ( engineContext.getVariable( variableName ) == commandObject
					&& engineContext.getVariable( BindingResult.MODEL_KEY_PREFIX + variableName ) != null ) {
				return variableName;
			}
		}
		return null;
	}

	@Override
	protected void writeCloseElement( FormViewElement viewElement, ThymeleafModelBuilder writer ) {
		// Support request data value processing - CSRF tokens
		final Map<String, String> extraHiddenFields =
				RequestDataValueProcessorUtils.getExtraHiddenFields( writer.getTemplateContext() );

		if ( extraHiddenFields != null && extraHiddenFields.size() > 0 ) {
			extraHiddenFields.forEach( ( fieldName, fieldValue ) -> {
				if ( shouldWriteExtraField( fieldName, viewElement.getMethod() ) ) {
					writer.addOpenElement( "input" );
					writer.addAttribute( "type", "hidden" );
					writer.addAttribute( "name", fieldName );
					writer.addAttribute( "value", fieldValue );
					writer.addCloseElement();
				}
			} );
		}

		super.writeCloseElement( viewElement, writer );

		ITemplateContext templateContext = writer.getTemplateContext();

		if ( templateContext instanceof IEngineContext ) {
			removeBoundObject( (IEngineContext) templateContext );
		}
	}

	private void removeBoundObject( IEngineContext templateContext ) {
		String attr = (String) templateContext.getVariable( VAR_BOUND_ERRORS );

		if ( attr != null ) {
			templateContext.removeVariable( attr );

			RequestContext requestContext = (RequestContext) templateContext.getVariable( SpringContextVariableNames.SPRING_REQUEST_CONTEXT );
			if ( requestContext != null ) {
				Map<String, Object> requestContextModel = requestContext.getModel();
				if ( requestContextModel != null ) {
					requestContextModel.remove( attr );
				}
			}
		}
	}

	private boolean shouldWriteExtraField( String fieldName, HttpMethod httpMethod ) {
		// only write a CSRF token if http method is not get
		return httpMethod != HttpMethod.GET || !"_csrf".equals( fieldName );
	}
}
