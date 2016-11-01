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
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.spring4.expression.Fields;
import org.thymeleaf.spring4.expression.SpringStandardExpressionObjectFactory;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;

import java.util.function.Consumer;

/**
 * @author Arne Vandamme
 */
public class FormGroupElementModelBuilder extends AbstractHtmlViewElementModelWriter<FormGroupElement>
{
	@Override
	public void writeModel( FormGroupElement group, ThymeleafModelBuilder model ) {
		super.writeOpenElement( group, model );

		model.addAttributeValue( "class", "form-group" );

		if ( group.isRequired() ) {
			model.addAttributeValue( "class", "required" );
		}

		if ( isCheckboxGroup( group ) ) {
			model.addAttributeValue( "class", "checkbox" );
		}
		else if ( isRadioGroup( group ) ) {
			model.addAttributeValue( "class", "radio" );
		}

		FormLayout layout = group.getFormLayout();

		if ( layout == null ) {
			layout = FormLayout.normal();
		}
		else if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
			if ( layout.getGrid().size() != 2 ) {
				throw new IllegalStateException( "Horizontal form requires a grid layout of 2 positions." );
			}
		}

		Consumer<ThymeleafModelBuilder> errorBuilder = null;

		ViewElement control = group.getControl();
		FormControlElement formControl = BootstrapElementUtils.getFormControl( group );
		if ( formControl != null ) {
			String controlId = model.retrieveHtmlId( formControl );

			ViewElement helpBlock = group.getHelpBlock();

			String helpId = model.retrieveHtmlId( helpBlock );
			if ( helpId == null && helpBlock instanceof HtmlViewElement && controlId != null ) {
				( (HtmlViewElement) helpBlock ).setHtmlId( controlId + ".help" );
				helpId = model.retrieveHtmlId( helpBlock );
			}

			if ( helpId != null ) {
				formControl.setAttribute( "aria-describedby", helpId );
			}

			errorBuilder = createFieldErrorsBuilder( formControl.getControlName(), model.getTemplateContext() );
		}

		if ( errorBuilder != null ) {
			model.addAttributeValue( "class", "has-error" );
		}

		ViewElement label = group.getLabel();
		if ( label != null ) {
			if ( group.isRequired() && label instanceof LabelFormElement ) {
				NodeViewElement sup = new NodeViewElement( "sup" );
				sup.addCssClass( "required" );
				sup.addChild( new TextViewElement( "*" ) );
				( (LabelFormElement) label ).addChild( sup );
			}
			model.addViewElement( label );
		}

		if ( group.isRenderHelpBlockBeforeControl() ) {
			model.addViewElement( group.getHelpBlock() );
		}

		if ( control != null ) {
			model.addViewElement( control );
		}

		if ( !group.isRenderHelpBlockBeforeControl() ) {
			model.addViewElement( group.getHelpBlock() );
		}

		writeChildren( group, model );

		if ( errorBuilder != null ) {
			errorBuilder.accept( model );
		}

		writeCloseElement( group, model );
	}

	private Consumer<ThymeleafModelBuilder> createFieldErrorsBuilder( String controlName,
	                                                                  ITemplateContext templateContext ) {
		if ( controlName != null
				&& templateContext.containsVariable( SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION ) ) {
			Fields fields = (Fields) templateContext.getExpressionObjects().getObject(
					SpringStandardExpressionObjectFactory.FIELDS_EXPRESSION_OBJECT_NAME
			);

			String propertyName = StringUtils.startsWith( controlName, "_" )
					? StringUtils.substring( controlName, 1 )
					: controlName;

			if ( fields != null && fields.hasErrors( propertyName ) ) {
				return model -> {
					model.addOpenElement( "div" );
					model.addAttributeValue( "class", "small", "text-danger" );
					model.addText( "" + StringUtils.join( fields.errors( propertyName ), " " ) );
					model.addCloseElement();
				};
			}
		}

		return null;
	}

	//	@Override
//	protected Element createNode( FormGroupElement group,
//	                              Arguments arguments,
//	                              ViewElementNodeFactory viewElementNodeFactory ) {
//
//
//		ViewElement label = group.getLabel();
//		Element labelElement = null;
//		if ( label != null ) {
//			List<Node> labelNodes = viewElementNodeFactory.buildNodes( label, arguments );
//			labelElement = labelElement( labelNodes );
//
//			if ( labelElement != null ) {
//				if ( group.isRequired() ) {
//					addRequiredIndicator( labelElement );
//				}
//				if ( layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
//					labelElement.setAttribute( "class", "sr-only" );
//				}
//				else if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
//					attributeAppend( labelElement, "class", layout.getGrid().get( 0 ).toString() );
//				}
//			}
//
//			for ( Node childNode : labelNodes ) {
//				node.addChild( childNode );
//			}
//		}
//
//		Element controlParent = node;
//
//		if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
//			controlParent = createElement( "div" );
//			node.addChild( controlParent );
//			attribute( controlParent, "class", layout.getGrid().get( 1 ).toString() );
//
//			if ( labelElement == null ) {
//				attributeAppend( controlParent, "class", layout.getGrid().get( 0 ).asOffset().toString() );
//			}
//		}
//
//		ViewElement helpBlock = group.getHelpBlock();
//
//		List<Node> helpNodes;
//
//		if ( helpBlock != null ) {
//			helpNodes = viewElementNodeFactory.buildNodes( helpBlock, arguments );
//
//			if ( group.isRenderHelpBlockBeforeControl() ) {
//				for ( Node helpNode : helpNodes ) {
//					controlParent.addChild( helpNode );
//				}
//			}
//		}
//		else {
//			helpNodes = Collections.emptyList();
//		}
//
//		ViewElement control = group.getControl();
//		FormControlElement formControl = BootstrapElementUtils.getFormControl( group );
//		if ( control != null ) {
//			List<Node> controlNodes = viewElementNodeFactory.buildNodes( control, arguments );
//
//			Element controlElement = controlElement( controlNodes );
//
//			if ( controlElement != null ) {
//				if ( formControl instanceof TextboxFormElement
//						&& layout.getType() == FormLayout.Type.INLINE
//						&& !layout.isShowLabels() ) {
//					if ( label instanceof LabelFormElement ) {
//						String labelText = ( (LabelFormElement) label ).getText();
//
//						if ( labelText != null && !controlElement.hasAttribute( "placeholder" ) ) {
//							controlElement.setAttribute( "placeholder", labelText );
//						}
//					}
//				}
//
//				String controlId = controlElement.getAttributeValue( "id" );
//				String helpBlockId = controlId != null ? controlId + ".help" : null;
//
//				if ( helpBlockId != null ) {
//					Element helpElement = helpElement( helpNodes );
//
//					if ( helpElement != null ) {
//						helpElement.setAttribute( "id", helpBlockId );
//						controlElement.setAttribute( "aria-describedby", helpBlockId );
//
//						if ( layout.getType() == FormLayout.Type.INLINE ) {
//							attributeAppend( helpElement, "class", "sr-only" );
//						}
//					}
//				}
//			}
//
//			for ( Node childNode : controlNodes ) {
//				controlParent.addChild( childNode );
//			}
//
//			if ( formControl != null ) {
//				addFieldErrors( node, controlParent, formControl.getControlName(), arguments );
//			}
//
//			if ( !group.isRenderHelpBlockBeforeControl() ) {
//				for ( Node helpNode : helpNodes ) {
//					controlParent.addChild( helpNode );
//				}
//			}
//		}
//
//		return node;
//	}
//
//	private void addRequiredIndicator( Element labelElement ) {
//		Element indicator = new Element( "sup" );
//		indicator.setAttribute( "class", "required" );
//		indicator.addChild( new Text( "*" ) );
//
//		labelElement.addChild( indicator );
//	}
//

	//
//	private Element helpElement( List<Node> helpNodes ) {
//		if ( !helpNodes.isEmpty() ) {
//			Node node = helpNodes.get( 0 );
//			if ( node instanceof Element ) {
//				return (Element) node;
//			}
//		}
//		return null;
//	}
//
//	private Element labelElement( List<Node> labelNodes ) {
//		if ( !labelNodes.isEmpty() ) {
//			Node node = labelNodes.get( 0 );
//			if ( node instanceof Element ) {
//				Element candidate = (Element) node;
//
//				if ( StringUtils.equals( candidate.getNormalizedName(), "label" ) ) {
//					return candidate;
//				}
//			}
//		}
//		return null;
//	}
//
//	private Element controlElement( List<Node> controlNodes ) {
//		if ( !controlNodes.isEmpty() ) {
//			for ( Node node : controlNodes ) {
//				if ( node instanceof Element ) {
//					Element candidate = (Element) node;
//
//					if ( StringUtils.equals( candidate.getNormalizedName(), "input" )
//							|| StringUtils.equals( candidate.getNormalizedName(), "textarea" )
//							|| StringUtils.equals( candidate.getNormalizedName(), "select" ) ) {
//						return candidate;
//					}
//					else {
//						if ( candidate.hasChildren() ) {
//							candidate = controlElement( candidate.getChildren() );
//
//							if ( candidate != null ) {
//								return candidate;
//							}
//						}
//					}
//				}
//			}
//		}
//		return null;
//	}
//
	private boolean isCheckboxGroup( FormGroupElement group ) {
		return group.getControl() instanceof CheckboxFormElement;
	}

	private boolean isRadioGroup( FormGroupElement group ) {
		return group.getControl() instanceof RadioFormElement;
	}
}
