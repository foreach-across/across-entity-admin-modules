/*
 * Copyright 2019 the original author or authors
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
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.*;
import org.thymeleaf.spring5.context.IThymeleafBindStatus;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.util.FieldUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.foreach.across.modules.bootstrapui.elements.thymeleaf.FormViewElementModelWriter.VAR_CURRENT_BOOTSTRAP_FORM;

/**
 * The FormGroupElementModelWriter is core to default Bootstrap based form rendering.
 * It supports a combination of control, label and optional help text.
 * It will automatically set attributes based on the presence of the different elements and
 * an optional form layout.  For most cases this will work perfectly but if you want total control
 * of the generated markup, you might want to create the form group markup manually.
 *
 * @author Arne Vandamme
 * @since 1.0.0
 */
@Slf4j
public class FormGroupElementModelWriter extends AbstractHtmlViewElementModelWriter<FormGroupElement>
{
	private final Collection<String> CONTROL_ELEMENTS = Arrays.asList( "input", "textarea", "select" );

	@Override
	public void writeModel( FormGroupElement group, ThymeleafModelBuilder model ) {
		super.writeOpenElement( group, model );

		model.addAttributeValue( "class", "form-group" );

		if ( group.isRequired() ) {
			model.addAttributeValue( "class", "required" );
		}

		boolean boxGroup = false;

		if ( isCheckboxGroup( group ) ) {
			boxGroup = true;
		}

		FormLayout layout = determineFormLayout( group, model );

		Consumer<ThymeleafModelBuilder> errorBuilder = null;

		ViewElement control = group.getControl();
		FormControlElement formControl = BootstrapElementUtils.getFormControl( group );

		boolean isWrappedBoxGroup = boxGroup && ( (CheckboxFormElement) control ).isWrapped();

		if ( formControl != null ) {
			errorBuilder = createFieldErrorsBuilder(
					group.isDetectFieldErrors() ? formControl : null, group.getFieldErrorsToShow(), model.getTemplateContext()
			);
		}

		IModel controlModel = control != null ? model.createViewElementModel( group.getControl() ) : null;

		if ( controlModel != null && errorBuilder != null ) {
			setIsInvalidOnControl( model.getModelFactory(), controlModel );

			if ( isWrappedBoxGroup ) {
				attachFeedbackToControl( model, controlModel, errorBuilder );
			}
		}

		ViewElement helpBlock = group.getHelpBlock();
		IModel helpBlockModel = helpBlock != null ? model.createViewElementModel( helpBlock ) : null;

		ViewElement descriptionBlock = group.getDescriptionBlock();
		IModel descriptionBlockModel = descriptionBlock != null ? model.createViewElementModel( descriptionBlock ) : null;

		ViewElement label = group.getLabel();
		IModel labelModel = label != null ? model.createViewElementModel( label ) : null;

		ViewElement tooltip = group.getTooltip();
		IModel tooltipModel = tooltip != null ? model.createViewElementModel( tooltip ) : null;

		if ( labelModel != null && group.isRequired() ) {
			addRequiredIndicatorToLabel( model.getModelFactory(), labelModel );
		}

		if ( tooltipModel != null ) {
			if ( labelModel != null ) {
				addTooltipToLabel( tooltipModel, labelModel );
			}
			else if ( boxGroup && controlModel != null ) {
				addTooltipToLabel( tooltipModel, controlModel );
			}
		}

		if ( formControl != null && controlModel != null ) {
			String controlId = model.retrieveHtmlId( formControl );

			if ( controlId != null && !formControl.hasAttribute( "aria-describedby" ) ) {
				String helpId = retrieveDescribedByIds( model.getModelFactory(), controlId, descriptionBlockModel, helpBlockModel, tooltipModel );

				if ( !helpId.isEmpty() ) {
					setDescribedByAttributeOnControl( model.getModelFactory(), controlModel, helpId );
				}
			}

			if ( layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
				String labelText = label instanceof LabelFormElement ? ( (LabelFormElement) label ).getText() : null;

				if ( labelText != null ) {
					setPlaceholderAttributeOnControl( model.getModelFactory(), controlModel, labelText );
				}
			}
		}

		if ( layout.getType() == FormLayout.Type.INLINE ) {
			if ( descriptionBlockModel != null ) {
				makeBlockScreenReaderOnly( model.getModelFactory(), descriptionBlockModel );
			}
			if ( helpBlockModel != null ) {
				makeBlockScreenReaderOnly( model.getModelFactory(), helpBlockModel );
			}
		}

		if ( errorBuilder != null ) {
			model.addAttributeValue( "class", "is-invalid" );
		}

		if ( labelModel != null ) {
			addFormLayoutToLabel( model.getModelFactory(), labelModel, layout );
			model.addModel( labelModel );
		}

		// open wrapper
		if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
			model.addOpenElement( "div" );
			model.addAttributeValue( "class", layout.getGrid().get( 1 ).toString() );

			if ( labelModel == null ) {
				model.addAttributeValue( "class", layout.getGrid().get( 0 ).asOffset().toString() );
			}
		}

		if ( descriptionBlockModel != null ) {
			model.addModel( descriptionBlockModel );
		}

		if ( controlModel != null ) {
			model.addModel( controlModel );
		}

		if ( helpBlockModel != null ) {
			model.addModel( helpBlockModel );
		}

		writeChildren( group, model );

		if ( errorBuilder != null && !boxGroup ) {
			errorBuilder.accept( model );
		}

		// close wrapper
		if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
			model.addCloseElement();
		}

		writeCloseElement( group, model );
	}

	private void attachFeedbackToControl( ThymeleafModelBuilder model, IModel controlModel, Consumer<ThymeleafModelBuilder> errorBuilder ) {
		ThymeleafModelBuilder childModelBuilder = model.createChildModelBuilder();
		errorBuilder.accept( childModelBuilder );
		controlModel.insertModel( controlModel.size() - 1, childModelBuilder.retrieveModel() );
	}

	private FormLayout determineFormLayout( FormGroupElement group, ThymeleafModelBuilder model ) {
		FormLayout layout = group.getFormLayout();

		if ( layout == null ) {
			FormViewElement form = (FormViewElement) model.getTemplateContext().getVariable( VAR_CURRENT_BOOTSTRAP_FORM );

			if ( form != null ) {
				layout = form.getFormLayout();
			}

			if ( layout == null ) {
				layout = FormLayout.normal();
			}
		}

		if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
			if ( layout.getGrid().size() != 2 ) {
				throw new IllegalStateException( "Horizontal form requires a grid layout of 2 positions." );
			}
		}
		return layout;
	}

	/**
	 * First element will get sr-only class added.
	 */
	private void makeBlockScreenReaderOnly( IModelFactory modelFactory, IModel helpBlockModel ) {
		for ( int i = 0; i < helpBlockModel.size(); i++ ) {
			ITemplateEvent event = helpBlockModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;

				String currentClass = StringUtils.defaultString( elementTag.getAttributeValue( "class" ) );
				String newClass = StringUtils.strip( currentClass + " sr-only" );
				helpBlockModel.replace( i, modelFactory.setAttribute( elementTag, "class", newClass ) );

				return;
			}
		}
	}

	/**
	 * Add layout class to first element of the label model.
	 */
	private void addFormLayoutToLabel( IModelFactory modelFactory, IModel labelModel, FormLayout layout ) {
		for ( int i = 0; i < labelModel.size(); i++ ) {
			ITemplateEvent event = labelModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;
				if ( layout.getType() == FormLayout.Type.HORIZONTAL ) {
					String currentClass = StringUtils.defaultString( elementTag.getAttributeValue( "class" ) );
					String newClass = StringUtils.strip( currentClass + " " + layout.getGrid().get( 0 ).toString() );
					labelModel.replace( i, modelFactory.setAttribute( elementTag, "class", newClass ) );
				}
				else if ( layout.getType() == FormLayout.Type.INLINE && !layout.isShowLabels() ) {
					String currentClass = StringUtils.defaultString( elementTag.getAttributeValue( "class" ) );
					String newClass = StringUtils.strip( currentClass + " sr-only" );
					labelModel.replace( i, modelFactory.setAttribute( elementTag, "class", newClass ) );
				}
				return;
			}
		}
	}

	/**
	 * We add the indicator before the first &lt;/label&gt; tag.
	 */
	private void addRequiredIndicatorToLabel( IModelFactory modelFactory, IModel labelModel ) {
		for ( int i = 0; i < labelModel.size(); i++ ) {
			ITemplateEvent event = labelModel.get( i );
			if ( event instanceof ICloseElementTag ) {
				ICloseElementTag closeElementTag = (ICloseElementTag) event;

				if ( "label".equalsIgnoreCase( closeElementTag.getElementCompleteName() ) ) {
					labelModel.insert( i, modelFactory.createOpenElementTag( "sup", "class", "required" ) );
					labelModel.insert( i + 1, modelFactory.createText( "*" ) );
					labelModel.insert( i + 2, modelFactory.createCloseElementTag( "sup" ) );
					return;
				}
			}
		}
	}

	private void addTooltipToLabel( IModel tooltipModel, IModel labelModel ) {
		for ( int i = 0; i < labelModel.size(); i++ ) {
			ITemplateEvent event = labelModel.get( i );
			if ( event instanceof ICloseElementTag ) {
				ICloseElementTag closeElementTag = (ICloseElementTag) event;

				if ( "label".equalsIgnoreCase( closeElementTag.getElementCompleteName() ) ) {
					labelModel.insertModel( i, tooltipModel );
					return;
				}
			}
		}
	}

	/**
	 * Find the very first control (input, select or textarea) and set the aria-describedby attribute.
	 */
	private void setDescribedByAttributeOnControl( IModelFactory modelFactory, IModel controlModel, String helpId ) {
		for ( int i = 0; i < controlModel.size(); i++ ) {
			ITemplateEvent event = controlModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;

				if ( CONTROL_ELEMENTS.contains( StringUtils.lowerCase( elementTag.getElementCompleteName() ) ) ) {
					controlModel.replace( i, modelFactory.setAttribute( elementTag, "aria-describedby", helpId ) );
					return;
				}
			}
		}
	}

	private void setPlaceholderAttributeOnControl( IModelFactory modelFactory,
	                                               IModel controlModel,
	                                               String placeholder ) {
		for ( int i = 0; i < controlModel.size(); i++ ) {
			ITemplateEvent event = controlModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;

				if ( CONTROL_ELEMENTS.contains( StringUtils.lowerCase( elementTag.getElementCompleteName() ) ) ) {
					if ( !"select".equals( StringUtils.lowerCase( elementTag.getElementCompleteName() ) )
							&& !elementTag.hasAttribute( "placeholder" ) ) {
						controlModel.replace( i, modelFactory.setAttribute( elementTag, "placeholder", placeholder ) );
					}

					return;
				}
			}
		}
	}

	/**
	 * Set is-invalid css class on the actual control.
	 */
	private void setIsInvalidOnControl( IModelFactory modelFactory, IModel controlModel ) {
		for ( int i = 0; i < controlModel.size(); i++ ) {
			ITemplateEvent event = controlModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;

				if ( CONTROL_ELEMENTS.contains( StringUtils.lowerCase( elementTag.getElementCompleteName() ) ) ) {
					String currentClass = StringUtils.defaultString( elementTag.getAttributeValue( "class" ) );
					String newClass = StringUtils.strip( currentClass + " is-invalid" );
					controlModel.replace( i, modelFactory.setAttribute( elementTag, "class", newClass ) );
				}
			}
		}
	}

	private String retrieveDescribedByIds( IModelFactory modelFactory, String controlId, IModel descriptionModel, IModel helpModel, IModel tooltipModel ) {
		StringBuilder concatenated = new StringBuilder();

		if ( descriptionModel != null ) {
			concatenated.append( setOrRetrieveBlockId( modelFactory, descriptionModel, controlId, "description" ) );
		}
		if ( helpModel != null ) {
			concatenated.append( setOrRetrieveBlockId( modelFactory, helpModel, controlId, "help" ) );
		}
		if ( tooltipModel != null ) {
			concatenated.append( setOrRetrieveBlockId( modelFactory, tooltipModel, controlId, "tooltip" ) );
		}

		return concatenated.toString().trim();
	}

	/**
	 * We assume the first open element to be the help block.  If an id is set on the first open element,
	 * we use that one.  Else we set one on.
	 */
	private String setOrRetrieveBlockId( IModelFactory modelFactory, IModel helpBlockModel, String controlId, String blockType ) {
		for ( int i = 0; i < helpBlockModel.size(); i++ ) {
			ITemplateEvent event = helpBlockModel.get( i );
			if ( event instanceof IOpenElementTag ) {
				IOpenElementTag elementTag = (IOpenElementTag) event;

				if ( elementTag.hasAttribute( "id" ) ) {
					return elementTag.getAttributeValue( "id" ) + " ";
				}
				else {
					String helpId = controlId + "." + blockType;
					helpBlockModel.replace( i, modelFactory.setAttribute( elementTag, "id", helpId ) );
					return helpId + " ";
				}
			}
		}

		return "";
	}

	private Consumer<ThymeleafModelBuilder> createFieldErrorsBuilder( FormControlElement formControl,
	                                                                  String[] fieldErrors,
	                                                                  ITemplateContext templateContext ) {

		if ( ( formControl != null || fieldErrors.length > 0 )
				&& templateContext.containsVariable( SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION ) ) {
			Set<String> errorMessages = new LinkedHashSet<>( fieldErrors.length + 1 );

			if ( formControl != null ) {
				String controlName = formControl.getControlName();
				String propertyName = StringUtils.startsWith( controlName, "_" ) ? StringUtils.substring( controlName, 1 ) : controlName;

				IThymeleafBindStatus bindStatus = FieldUtils.getBindStatus( templateContext, true, "*{" + propertyName + "}" );

				if ( bindStatus != null && bindStatus.isError() ) {
					errorMessages.addAll( Arrays.asList( bindStatus.getErrorMessages() ) );

					if ( formControl instanceof TextboxFormElement ) {
						// Set the original value that caused the binding error on the textbox
						Object inputValue = bindStatus.getValue();
						if ( inputValue != null ) {
							formControl.setAttribute( TextboxFormElementModelWriter.TRANSIENT_ERROR_VALUE_ATTRIBUTE, inputValue.toString() );
						}
					}
				}
			}

			Stream.of( fieldErrors )
			      .map( propertyName -> FieldUtils.getBindStatus( templateContext, true, "*{" + propertyName + "}" ) )
			      .filter( Objects::nonNull )
			      .filter( IThymeleafBindStatus::isError )
			      .flatMap( bs -> Stream.of( bs.getErrorMessages() ) )
			      .forEach( errorMessages::add );

			if ( !errorMessages.isEmpty() ) {
				return model -> {
					model.addOpenElement( "div" );
					model.addAttributeValue( "class", "invalid-feedback" );
					model.addHtml( StringUtils.join( errorMessages, " " ) );
					model.addCloseElement();
				};
			}
		}

		return null;
	}

	private boolean isCheckboxGroup( FormGroupElement group ) {
		return group.getControl() instanceof CheckboxFormElement;
	}
}
