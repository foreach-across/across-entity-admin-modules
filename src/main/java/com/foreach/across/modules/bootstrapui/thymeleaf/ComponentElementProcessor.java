package com.foreach.across.modules.bootstrapui.thymeleaf;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessor;
import com.foreach.across.modules.web.ui.thymeleaf.ThymeleafViewElementProcessorRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.standard.fragment.StandardFragment;
import org.thymeleaf.standard.fragment.StandardFragmentProcessor;

import java.util.Collections;
import java.util.List;

public class ComponentElementProcessor extends AbstractMarkupSubstitutionElementProcessor
{
	public static final String ELEMENT_NAME = "component";

	private static final String ATTRIBUTE_DATA = "data";

	public ComponentElementProcessor() {
		super( ELEMENT_NAME );
	}

	@Override
	protected List<Node> getMarkupSubstitutes( Arguments arguments, Element element ) {
		ViewElement viewElement = retrieveViewElementFromAttribute( arguments, element );

		return buildNodes( viewElement, arguments );
	}

	@SuppressWarnings("unchecked")
	public List<Node> buildNodes( ViewElement viewElement, Arguments arguments ) {
		if ( hasCustomTemplate( viewElement ) ) {
			return renderCustomTemplate( viewElement, arguments );
		}
		else {
			ThymeleafViewElementProcessor processor = findElementProcessor( viewElement, arguments );

			if ( processor != null ) {
				return processor.buildNodes( viewElement, arguments, this );
			}
		}

		throw new IllegalArgumentException( "Unable to render ViewElement of type " + viewElement.getClass() );
	}

	private ThymeleafViewElementProcessor findElementProcessor( ViewElement viewElement, Arguments arguments ) {
		ApplicationContext appCtx = ( (SpringWebContext) arguments.getContext() ).getApplicationContext();
		ThymeleafViewElementProcessorRegistry registry = appCtx.getBean( ThymeleafViewElementProcessorRegistry.class );

		return registry.getProcessor( viewElement );
	}

	private List<Node> renderCustomTemplate( ViewElement viewElement, Arguments arguments ) {
		Arguments newArguments = arguments.addLocalVariables(
				Collections.singletonMap( "component", (Object) viewElement )
		);

		StandardFragment fragment = StandardFragmentProcessor.computeStandardFragmentSpec(
				newArguments.getConfiguration(),
				newArguments,
				appendFragmentIfRequired( viewElement.getCustomTemplate() ),
				"th", "fragment" );

		return fragment.extractFragment( newArguments.getConfiguration(), newArguments,
		                                 newArguments.getTemplateRepository() );
	}

	/**
	 * Append the fragment to the custom template name if there is no fragment.
	 */
	private String appendFragmentIfRequired( String customTemplate ) {
		if ( !StringUtils.contains( customTemplate, "::" ) ) {
			return customTemplate + " :: render(${component})";
		}

		return customTemplate;
	}

	private boolean hasCustomTemplate( ViewElement viewElement ) {
		return viewElement.getCustomTemplate() != null;
	}

	private ViewElement retrieveViewElementFromAttribute( Arguments arguments, Element element ) {
		String expr = element.getAttributeValue( ATTRIBUTE_DATA );
		IStandardExpressionParser parser = StandardExpressions.getExpressionParser( arguments.getConfiguration() );
		IStandardExpression expression = parser.parseExpression( arguments.getConfiguration(), arguments, expr );

		Object viewElement = expression.execute( arguments.getConfiguration(), arguments );

		if ( viewElement instanceof ViewElement ) {
			return (ViewElement) viewElement;
		}

		throw new IllegalArgumentException(
				ELEMENT_NAME + " element requires a " + ATTRIBUTE_DATA + " attribute of type ViewElement"
		);
	}

	@Override
	public int getPrecedence() {
		return 1000;
	}
}
