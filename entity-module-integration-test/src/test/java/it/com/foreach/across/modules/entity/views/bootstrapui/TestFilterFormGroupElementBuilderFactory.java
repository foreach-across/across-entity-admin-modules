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
package it.com.foreach.across.modules.entity.views.bootstrapui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.CheckboxFormElement;
import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderService;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.bootstrapui.FilterFormGroupElementBuilderFactory;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Arne Vandamme
 */
public class TestFilterFormGroupElementBuilderFactory extends ViewElementBuilderFactoryTestSupport<FormGroupElement>
{
	@Mock
	private EntityViewElementBuilderService viewElementBuilderService;

	@Override
	protected EntityViewElementBuilderFactory createBuilderFactory() {
		return new FilterFormGroupElementBuilderFactory( viewElementBuilderService );
	}

	@Override
	protected Class getTestClass() {
		return Instance.class;
	}

	@Test
	public void formGroupOfLabelAndFilterControlIsBuilt() {
		EntityPropertyDescriptor descriptor = properties.get( "text" );

		ViewElementBuilder label = BootstrapUiBuilders.text( "label" );
		ViewElementBuilder control = BootstrapUiBuilders.text( "control" );

		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.FILTER_CONTROL ) ).thenReturn( control );
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LABEL ) ).thenReturn( label );

		FormGroupElement formGroup = assembleAndVerify( "text", ViewElementMode.FILTER_FORM );
		Assertions.assertThat( formGroup.getControl( TextViewElement.class ).getText() ).isEqualTo( "control" );
		Assertions.assertThat( formGroup.getLabel( LabelFormElement.class ).getText() ).isEqualTo( "label" );
	}

	@Test
	public void multipleParameterIsPassedToControl() {
		EntityPropertyDescriptor descriptor = properties.get( "text" );

		ViewElementBuilder label = BootstrapUiBuilders.text( "label" );
		ViewElementBuilder control = BootstrapUiBuilders.text( "control" );

		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.FILTER_CONTROL.forMultiple() ) ).thenReturn( control );
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.LABEL ) ).thenReturn( label );

		FormGroupElement formGroup = assembleAndVerify( "text", ViewElementMode.FILTER_FORM.forMultiple() );
		Assertions.assertThat( formGroup.getControl( TextViewElement.class ).getText() ).isEqualTo( "control" );
		Assertions.assertThat( formGroup.getLabel( LabelFormElement.class ).getText() ).isEqualTo( "label" );
	}

	@Test
	public void labelIsNotAddedForCheckbox() {
		EntityPropertyDescriptor descriptor = properties.get( "text" );

		ViewElementBuilder control = BootstrapUiBuilders.checkbox();
		when( viewElementBuilderService.getElementBuilder( descriptor, ViewElementMode.FILTER_CONTROL ) ).thenReturn( control );

		FormGroupElement formGroup = assembleAndVerify( "text", ViewElementMode.FILTER_FORM );
		Assertions.assertThat( formGroup.getControl() ).isInstanceOf( CheckboxFormElement.class );
		Assertions.assertThat( formGroup.getLabel() ).isNull();

		verify( viewElementBuilderService, never() ).getElementBuilder( descriptor, ViewElementMode.LABEL );
	}

	@SuppressWarnings("unchecked")
	private FormGroupElement assembleAndVerify( String propertyName, ViewElementMode mode ) {
		FormGroupElement control = assemble( propertyName, mode );
		assertEquals( "formGroup-" + propertyName, control.getName() );

		return control;
	}

	@SuppressWarnings("unused")
	private static class Instance
	{
		private String text;
	}
}
