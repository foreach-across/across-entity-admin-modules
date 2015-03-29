package com.foreach.across.modules.bootstrapui.elements.builder;

import com.foreach.across.modules.bootstrapui.elements.FormControlElementSupport;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.builder.NodeViewElementSupportBuilder;

public abstract class FormControlElementBuilderSupport<T extends FormControlElementSupport, SELF extends FormControlElementBuilderSupport<T, SELF>>
		extends NodeViewElementSupportBuilder<T, SELF>
{
	private Boolean disabled, readonly;
	private String controlName;

	@SuppressWarnings("unchecked")
	public SELF controlName( String controlName ) {
		this.controlName = controlName;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF disabled() {
		return disabled( true );
	}

	@SuppressWarnings("unchecked")
	public SELF disabled( boolean disabled ) {
		this.disabled = disabled;
		return (SELF) this;
	}

	@SuppressWarnings("unchecked")
	public SELF readonly() {
		return readonly( true );
	}

	@SuppressWarnings("unchecked")
	public SELF readonly( boolean readonly ) {
		this.readonly = readonly;
		return (SELF) this;
	}

	@Override
	protected T apply( T viewElement, ViewElementBuilderContext builderContext ) {
		T control = super.apply( viewElement, builderContext );

		if ( controlName != null ) {
			control.setControlName( controlName );
		}
		if ( disabled != null ) {
			control.setDisabled( disabled );
		}
		if ( readonly != null ) {
			control.setReadonly( readonly );
		}

		return control;
	}
}
