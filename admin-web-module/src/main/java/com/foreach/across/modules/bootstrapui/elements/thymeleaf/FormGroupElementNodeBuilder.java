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

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.thymeleaf.ViewElementNodeFactory;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.NestableNodeBuilderSupport;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.spring4.expression.Fields;
import org.thymeleaf.spring4.expression.SpelVariableExpressionEvaluator;

import java.util.Collections;
import java.util.List;

/**
 * @author Arne Vandamme
 */
public class FormGroupElementNodeBuilder extends NestableNodeBuilderSupport<FormGroupElement>
{
	@Override
	protected Element createNode( FormGroupElement group,
	                              Arguments arguments,
	                              ViewElementNodeFactory viewElementNodeFactory ) {
		Element node = new Element( "div" );
		node.setAttribute( "class", "form-group" );

		if ( group.isRequired() ) {
			attributeAppend( node, "class", "required" );
		}

		if ( isCheckboxGroup( group ) ) {
			attributeAppend( node, "class", "checkbox" );
		}
		else if ( isRadioGroup( group ) ) {
			attributeAppend( node, "class", "radio" );
		}

		FormLayout layout = group.getFormLayout();

		if ( layout == null ) {
			layout = FormLayout.normal();
		}

		ViewElement label = group.getLabel();
		if ( label != null ) {
			List<Node> labelNodes = viewElementNodeFactory.buildNodes( label, arguments );
			Element labelElement = labelElement( labelNodes );

			if ( labelElement != null ) {
				if ( group.isRequired() ) {
					addRequiredIndicator( labelElement );
				}
				if ( layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
					labelElement.setAttribute( "class", "sr-only" );
				}
			}

			for ( Node childNode : labelNodes ) {
				node.addChild( childNode );
			}
		}

		ViewElement helpBlock = group.getHelpBlock();

		List<Node> helpNodes;

		if ( helpBlock != null ) {
			helpNodes = viewElementNodeFactory.buildNodes( helpBlock, arguments );

			if ( group.isRenderHelpBlockBeforeControl() ) {
				for ( Node helpNode : helpNodes ) {
					node.addChild( helpNode );
				}
			}
		}
		else {
			helpNodes = Collections.emptyList();
		}

		ViewElement control = group.getControl();
		FormControlElementSupport formControl = BootstrapElementUtils.getFormControl( group );
		if ( control != null ) {
			List<Node> controlNodes = viewElementNodeFactory.buildNodes( control, arguments );

			Element controlElement = controlElement( controlNodes );

			if ( controlElement != null ) {
				if ( formControl instanceof TextboxFormElement
						&& layout.getType() == FormLayout.Type.INLINE
						&& !layout.isShowLabels() ) {
					if ( label instanceof LabelFormElement ) {
						String labelText = ( (LabelFormElement) label ).getText();

						if ( labelText != null && !controlElement.hasAttribute( "placeholder" ) ) {
							controlElement.setAttribute( "placeholder", labelText );
						}
					}
				}

				String controlId = controlElement.getAttributeValue( "id" );
				String helpBlockId = controlId != null ? controlId + ".help" : null;

				if ( helpBlockId != null ) {
					Element helpElement = helpElement( helpNodes );

					if ( helpElement != null ) {
						helpElement.setAttribute( "id", helpBlockId );
						controlElement.setAttribute( "aria-describedby", helpBlockId );

						if ( layout.getType() == FormLayout.Type.INLINE ) {
							attributeAppend( helpElement, "class", "sr-only" );
						}
					}
				}
			}

			for ( Node childNode : controlNodes ) {
				node.addChild( childNode );
			}

			if ( control instanceof FormControlElementSupport ) {
				addFieldErrors( node, ( (FormControlElementSupport) control ).getControlName(), arguments );
			}

			if ( !group.isRenderHelpBlockBeforeControl() ) {
				for ( Node helpNode : helpNodes ) {
					node.addChild( helpNode );
				}
			}
		}

		return node;
	}

	private void addRequiredIndicator( Element labelElement ) {
		Element indicator = new Element( "sup" );
		indicator.setAttribute( "class", "required" );
		indicator.addChild( new Text( "*" ) );

		labelElement.addChild( indicator );
	}

	private void addFieldErrors( Element node, String controlName, Arguments arguments ) {
		if ( arguments.hasLocalVariable( FormViewElementNodeBuilder.VAR_CURRENT_BOOTSTRAP_FORM ) ) {
			FormViewElement form = (FormViewElement) arguments.getLocalVariable(
					FormViewElementNodeBuilder.VAR_CURRENT_BOOTSTRAP_FORM
			);

			if ( form.getCommandAttribute() != null ) {
				Fields fields = (Fields) arguments.getExpressionObjects()
				                                  .get( SpelVariableExpressionEvaluator.FIELDS_EVALUATION_VARIABLE_NAME );

				String fullControlName = form.getCommandAttribute() + "." + controlName;
				if ( fields != null && fields.hasErrors( fullControlName ) ) {
					attributeAppend( node, "class", "has-error" );

					Element errors = new Element( "div" );
					errors.setAttribute( "class", "small text-danger" );
					errors.addChild( new Text( "" + StringUtils.join( fields.errors( fullControlName ), " " ) ) );

					node.addChild( errors );
				}
			}
		}
	}

	private Element helpElement( List<Node> helpNodes ) {
		if ( !helpNodes.isEmpty() ) {
			Node node = helpNodes.get( 0 );
			if ( node instanceof Element ) {
				return (Element) node;
			}
		}
		return null;
	}

	private Element labelElement( List<Node> labelNodes ) {
		if ( !labelNodes.isEmpty() ) {
			Node node = labelNodes.get( 0 );
			if ( node instanceof Element ) {
				Element candidate = (Element) node;

				if ( StringUtils.equals( candidate.getNormalizedName(), "label" ) ) {
					return candidate;
				}
			}
		}
		return null;
	}

	private Element controlElement( List<Node> controlNodes ) {
		if ( !controlNodes.isEmpty() ) {
			for ( Node node : controlNodes ) {
				if ( node instanceof Element ) {
					Element candidate = (Element) node;

					if ( StringUtils.equals( candidate.getNormalizedName(), "input" )
							|| StringUtils.equals( candidate.getNormalizedName(), "textarea" )
							|| StringUtils.equals( candidate.getNormalizedName(), "select" ) ) {
						return candidate;
					}
					else {
						if ( candidate.hasChildren() ) {
							candidate = controlElement( candidate.getChildren() );

							if ( candidate != null ) {
								return candidate;
							}
						}
					}
				}
			}
		}
		return null;
	}

	private boolean isCheckboxGroup( FormGroupElement group ) {
		return group.getControl() instanceof CheckboxFormElement;
	}

	private boolean isRadioGroup( FormGroupElement group ) {
		return group.getControl() instanceof RadioFormElement;
	}
}
