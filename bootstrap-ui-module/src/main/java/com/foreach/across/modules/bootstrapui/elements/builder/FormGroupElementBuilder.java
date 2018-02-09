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
package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.AbstractNodeViewElementBuilder;

public class FormGroupElementBuilder extends AbstractNodeViewElementBuilder<FormGroupElement, FormGroupElementBuilder>
{
	/**
	 * CSS class added to default description block.
	 */
	public static final String CSS_FORM_TEXT_DESCRIPTION = "form-text-description";

	/**
	 * CSS class added to default help block.
	 */
	public static final String CSS_FORM_TEXT_HELP = "form-text-help";

	private ElementOrBuilder label, control, helpBlock, descriptionBlock, tooltip;
	private FormLayout formLayout;
	private Boolean required;
	private Boolean detectFieldErrors;

	@Deprecated
	private boolean helpBlockBeforeControl;

	public Boolean isRequired() {
		return required;
	}

	public ElementOrBuilder getLabel() {
		return label;
	}

	public ElementOrBuilder getHelpBlock() {
		return helpBlock;
	}

	public boolean isHelpBlockBeforeControl() {
		return helpBlockBeforeControl;
	}

	/**
	 * Gets the label configured on this builder if it is of the target type specified.
	 * If no label is set or the label is not of the specified type, null will be returned.
	 *
	 * @param targetClass target type the label should match
	 * @param <V>         target type the label should match
	 * @return label instance or null
	 */
	public <V> V getLabel( Class<V> targetClass ) {
		if ( targetClass.isInstance( label.getSource() ) ) {
			return (V) label.getSource();
		}

		return null;
	}

	public ElementOrBuilder getControl() {
		return control;
	}

	/**
	 * Gets the control configured on this builder if it is of the target type specified.
	 * If no control is set or the control is not of the specified type, null will be returned.
	 *
	 * @param targetClass target type the control should match
	 * @param <V>         target type the control should match
	 * @return control instance or null
	 */
	@SuppressWarnings("unchecked")
	public <V> V getControl( Class<V> targetClass ) {
		if ( targetClass.isInstance( control.getSource() ) ) {
			return (V) control.getSource();
		}

		return null;
	}

	public FormLayout getFormLayout() {
		return formLayout;
	}

	public FormGroupElementBuilder label( ViewElement label ) {
		this.label = ElementOrBuilder.wrap( label );
		return this;
	}

	/**
	 * Quick create a basic label element and add it to the form group.
	 *
	 * @param text for the label
	 * @return current builder
	 */
	public FormGroupElementBuilder label( String text ) {
		label( BootstrapUiBuilders.label( text ) );
		return this;
	}

	public FormGroupElementBuilder label( ViewElementBuilder labelBuilder ) {
		this.label = ElementOrBuilder.wrap( labelBuilder );
		return this;
	}

	public FormGroupElementBuilder control( ViewElement control ) {
		this.control = ElementOrBuilder.wrap( control );
		return this;
	}

	public FormGroupElementBuilder control( ViewElementBuilder controlBuilder ) {
		this.control = ElementOrBuilder.wrap( controlBuilder );
		return this;
	}

	/**
	 * Quick create a basic help block and add it to the form group.
	 * Usually added after the control, providing contextual help.
	 *
	 * @param text for the help block
	 * @return current builder
	 */
	public FormGroupElementBuilder helpBlock( String text ) {
		helpBlock( BootstrapUiBuilders.helpBlock( text ).css( CSS_FORM_TEXT_HELP ) );
		return this;
	}

	/**
	 * Add a help block to the form group.
	 * Usually added after the control, providing contextual help.
	 *
	 * @param helpBlock the help block
	 * @return current builder
	 */
	public FormGroupElementBuilder helpBlock( ViewElement helpBlock ) {
		this.helpBlock = ElementOrBuilder.wrap( helpBlock );
		return this;
	}

	/**
	 * Add a help block to the form group.
	 * Usually added after the control, providing contextual help.
	 *
	 * @param helpBlock the help block
	 * @return current builder
	 */
	public FormGroupElementBuilder helpBlock( ViewElementBuilder helpBlock ) {
		this.helpBlock = ElementOrBuilder.wrap( helpBlock );
		return this;
	}

	/**
	 * Quick create a description text block, to show at the top of the form group.
	 *
	 * @param text for the description block
	 * @return current builder
	 */
	public FormGroupElementBuilder descriptionBlock( String text ) {
		descriptionBlock( BootstrapUiBuilders.helpBlock( text ).css( CSS_FORM_TEXT_DESCRIPTION ) );
		return this;
	}

	/**
	 * Add a description block to show at the top of the form group.
	 *
	 * @param descriptionBlock for the description block
	 * @return current builder
	 */
	public FormGroupElementBuilder descriptionBlock( ViewElement descriptionBlock ) {
		this.descriptionBlock = ElementOrBuilder.wrap( descriptionBlock );
		return this;
	}

	/**
	 * Add a description block to show at the top of the form group.
	 *
	 * @param descriptionBlock for the description block
	 * @return current builder
	 */
	public FormGroupElementBuilder descriptionBlock( ViewElementBuilder descriptionBlock ) {
		this.descriptionBlock = ElementOrBuilder.wrap( descriptionBlock );
		return this;
	}

	/**
	 * Add a tooltip with the given (html supporting) text.
	 * Tooltip will be added after the label text.
	 *
	 * @param text for the tooltip
	 * @return current builder
	 */
	public FormGroupElementBuilder tooltip( String text ) {
		tooltip( BootstrapUiBuilders.helpBlock( text ) );
		return this;
	}

	/**
	 * Add a tooltip to the form group.
	 * Tooltip will be added after the label text.
	 *
	 * @param tooltip element
	 * @return current builder
	 */
	public FormGroupElementBuilder tooltip( ViewElement tooltip ) {
		this.tooltip = ElementOrBuilder.wrap( tooltip );
		return this;
	}

	/**
	 * Add a tooltip to the form group.
	 * Tooltip will be added after the label text.
	 *
	 * @param tooltip element
	 * @return current builder
	 */
	public FormGroupElementBuilder tooltip( ViewElementBuilder tooltip ) {
		this.tooltip = ElementOrBuilder.wrap( tooltip );
		return this;
	}

	@Deprecated
	public FormGroupElementBuilder helpBlockRenderedBeforeControl( boolean helpBlockBeforeControl ) {
		this.helpBlockBeforeControl = helpBlockBeforeControl;
		return this;
	}

	public FormGroupElementBuilder formLayout( FormLayout formLayout ) {
		this.formLayout = formLayout;
		return this;
	}

	public FormGroupElementBuilder required() {
		return required( true );
	}

	public FormGroupElementBuilder required( boolean required ) {
		this.required = required;
		return this;
	}

	/**
	 * Should field errors for the control present in this form group be detected automatically.
	 * If this is the case (default) a {@link org.springframework.validation.Errors} object will be searched
	 * for on the model and the control name will be used as the selector for possible errors.
	 *
	 * @param detectFieldErrors should an Errors model object be used
	 * @return current builder
	 */
	public FormGroupElementBuilder detectFieldErrors( boolean detectFieldErrors ) {
		this.detectFieldErrors = detectFieldErrors;
		return this;
	}

	@Override
	protected FormGroupElement createElement( ViewElementBuilderContext builderContext ) {
		FormGroupElement group = new FormGroupElement();

		if ( required != null ) {
			group.setRequired( required );
		}

		if ( label != null ) {
			group.setLabel( label.get( builderContext ) );
		}

		if ( control != null ) {
			group.setControl( control.get( builderContext ) );
		}

		if ( detectFieldErrors != null ) {
			group.setDetectFieldErrors( detectFieldErrors );
		}

		group.setRenderHelpBlockBeforeControl( helpBlockBeforeControl );

		if ( helpBlock != null ) {
			group.setHelpBlock( helpBlock.get( builderContext ) );
		}

		if ( descriptionBlock != null ) {
			group.setDescriptionBlock( descriptionBlock.get( builderContext ) );
		}

		if ( tooltip != null ) {
			group.setTooltip( tooltip.get( builderContext ) );
		}

		if ( group.getLabel() instanceof LabelFormElement ) {
			LabelFormElement label = group.getLabel( LabelFormElement.class );

			if ( !label.hasTarget() ) {
				FormControlElement formControlElement = BootstrapElementUtils.getFormControl( group.getControl() );

				if ( formControlElement != null ) {
					label.setTarget( formControlElement );
				}
			}
		}

		if ( formLayout != null ) {
			group.setFormLayout( formLayout );
		}

		return apply( group, builderContext );
	}
}
