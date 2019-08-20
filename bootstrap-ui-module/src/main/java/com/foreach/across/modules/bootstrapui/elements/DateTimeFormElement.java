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
package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.ConfigurableTextViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.i;

/**
 * Extension of an {@link InputGroupFormElement} that represents a date/time picker.
 * By default this is an input group with a calendar icon after the control element,
 * and a {@link TextboxFormElement} as control.
 *
 * @author Arne Vandamme
 */
public class DateTimeFormElement extends InputGroupFormElement
{
	public static final String ATTRIBUTE_DATA_DATEPICKER = "data-bootstrapui-datetimepicker";
	public static final String ATTRIBUTE_DATA_TARGET = "data-target";
	public static final String ATTRIBUTE_DATA_TOGGLE = "data-toggle";
	public static final String ATTRIBUTE_DATA_TARGET_INPUT = "data-target-input";

	public static final String CSS_JS_CONTROL = "js-form-datetimepicker";
	public static final String CSS_DATE = "date";
	public static final String CSS_DATETIMEPICKER_INPUT = "datetimepicker-input";

	private final HiddenFormElement hidden = new HiddenFormElement();

	private LocalDateTime value;

	public DateTimeFormElement() {
		setAppend( i( css.fa.solid( "calendar" ) ) );
		addCssClass( CSS_JS_CONTROL, CSS_DATE );
		setAttribute( ATTRIBUTE_DATA_DATEPICKER, new DateTimeFormElementConfiguration() );
		setAttribute( ATTRIBUTE_DATA_TARGET_INPUT, "nearest" );
		setAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "datetime" );
	}

	public DateTimeFormElementConfiguration getConfiguration() {
		return getAttribute( ATTRIBUTE_DATA_DATEPICKER, DateTimeFormElementConfiguration.class );
	}

	public void setConfiguration( @NonNull DateTimeFormElementConfiguration configuration ) {
		setAttribute( ATTRIBUTE_DATA_DATEPICKER, configuration );
	}

	@Override
	public String getControlName() {
		return hidden.getControlName();
	}

	@Override
	public DateTimeFormElement setControlName( String controlName ) {
		if ( StringUtils.isBlank( getHtmlId() ) && StringUtils.isNotBlank( controlName ) ) {
			this.setHtmlId( String.format( "_dp-controller--%s", controlName ) );
		}
		hidden.setControlName( controlName );
		return this;
	}

	@Deprecated
	public Date getValue() {
		return getConfiguration().localDateTimeToDate( value );
	}

	@Deprecated
	public DateTimeFormElement setValue( Date value ) {
		setLocalDateTime( getConfiguration().dateToLocalDateTime( value ) );
		return this;
	}

	public DateTimeFormElement setLocalDate( LocalDate value ) {
		setLocalDateTime( DateTimeFormElementConfiguration.localDateToLocalDateTime( value ) );
		return this;
	}

	public LocalDate getLocalDate() {
		return value.toLocalDate();
	}

	public DateTimeFormElement setLocalTime( LocalTime value ) {
		setLocalDateTime( DateTimeFormElementConfiguration.localTimeToLocalDateTime( value ) );
		return this;
	}

	public LocalTime getLocalTime() {
		return value.toLocalTime();
	}

	public DateTimeFormElement setLocalDateTime( LocalDateTime value ) {
		this.value = value;
		return this;
	}

	public LocalDateTime getLocalDateTime() {
		return value;
	}

	@Override
	public List<ViewElement> getChildren() {
		FormControlElement controlElement = getControl( FormControlElement.class );
		controlElement.removeAttribute( BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE );
		String controlName = hidden.getControlName();

		controlElement.setAttribute( ATTRIBUTE_DATA_TARGET, getTarget() );
		controlElement.addCssClass( CSS_DATETIMEPICKER_INPUT );

		if ( controlName != null ) {
			controlElement.setControlName( "_" + controlName );
			controlElement.setHtmlId( controlName );
		}
		else {
			controlElement.setControlName( null );
		}

		if ( value != null ) {
			String dateAsString = DateTimeFormElementConfiguration.JAVA_DATE_TIME_FORMATTER.format( value );
			hidden.setValue( dateAsString );

			if ( controlElement instanceof ConfigurableTextViewElement ) {
				( (ConfigurableTextViewElement) controlElement ).setText( dateAsString );
			}
		}

		List<ViewElement> elements = new ArrayList<>( super.getChildren() );
		// todo find a better way to do this
		elements.stream()
		        .filter( e -> NodeViewElement.class.isAssignableFrom( e.getClass() ) && ( (NodeViewElement) e ).hasCssClass( "input-group-append" ) )
		        .findFirst()
		        .ifPresent( e -> {
			        ( (NodeViewElement) e ).setAttribute( ATTRIBUTE_DATA_TOGGLE, "datetimepicker" );
			        ( (NodeViewElement) e ).setAttribute( ATTRIBUTE_DATA_TARGET, getTarget() );
		        } );
		elements.add( hidden );
		return elements;
	}

	/**
	 * Returns the selector for the target element that controls the datepicker.
	 */
	private String getTarget() {
		if ( StringUtils.isNotBlank( this.getHtmlId() ) ) {
			return "#" + getHtmlId();
		}
		return "";
	}

	@Override
	public DateTimeFormElement setPrepend( ViewElement prepend ) {
		super.setPrepend( prepend );
		return this;
	}

	@Override
	public DateTimeFormElement setAppend( ViewElement append ) {
		super.setAppend( append );
		return this;
	}

	@Override
	public DateTimeFormElement setControl( ViewElement control ) {
		super.setControl( control );
		return this;
	}

	@Override
	public DateTimeFormElement setPlaceholder( String placeholder ) {
		super.setPlaceholder( placeholder );
		return this;
	}

	@Override
	public DateTimeFormElement setDisabled( boolean disabled ) {
		super.setDisabled( disabled );
		return this;
	}

	@Override
	public DateTimeFormElement setReadonly( boolean readonly ) {
		super.setReadonly( readonly );
		return this;
	}

	@Override
	public DateTimeFormElement setRequired( boolean required ) {
		super.setRequired( required );
		return this;
	}

	@Override
	public DateTimeFormElement addCssClass( String... cssClass ) {
		super.addCssClass( cssClass );
		return this;
	}

	@Override
	public DateTimeFormElement removeCssClass( String... cssClass ) {
		super.removeCssClass( cssClass );
		return this;
	}

	@Override
	public DateTimeFormElement setAttributes( Map<String, Object> attributes ) {
		super.setAttributes( attributes );
		return this;
	}

	@Override
	public DateTimeFormElement setAttribute( String attributeName, Object attributeValue ) {
		super.setAttribute( attributeName, attributeValue );
		return this;
	}

	@Override
	public DateTimeFormElement addAttributes( Map<String, Object> attributes ) {
		super.addAttributes( attributes );
		return this;
	}

	@Override
	public DateTimeFormElement removeAttribute( String attributeName ) {
		super.removeAttribute( attributeName );
		return this;
	}

	@Override
	public DateTimeFormElement setName( String name ) {
		super.setName( name );
		return this;
	}

	@Override
	public DateTimeFormElement setCustomTemplate( String customTemplate ) {
		super.setCustomTemplate( customTemplate );
		return this;
	}

	@Override
	protected DateTimeFormElement setElementType( String elementType ) {
		super.setElementType( elementType );
		return this;
	}

	@Override
	public DateTimeFormElement addChild( ViewElement element ) {
		super.addChild( element );
		return this;
	}

	@Override
	public DateTimeFormElement addChildren( Collection<? extends ViewElement> elements ) {
		super.addChildren( elements );
		return this;
	}

	@Override
	public DateTimeFormElement addFirstChild( ViewElement element ) {
		super.addFirstChild( element );
		return this;
	}

	@Override
	public DateTimeFormElement clearChildren() {
		super.clearChildren();
		return this;
	}

	@Override
	public DateTimeFormElement apply( Consumer<ContainerViewElement> consumer ) {
		super.apply( consumer );
		return this;
	}

	@Override
	public <U extends ViewElement> DateTimeFormElement applyUnsafe( Consumer<U> consumer ) {
		super.applyUnsafe( consumer );
		return this;
	}

	@Override
	protected DateTimeFormElement setTagName( String tagName ) {
		super.setTagName( tagName );
		return this;
	}

	@Override
	public DateTimeFormElement setHtmlId( String htmlId ) {
		super.setHtmlId( htmlId );
		return this;
	}
}
