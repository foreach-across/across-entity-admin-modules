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

package com.foreach.across.modules.entity.views.bootstrapui.processors.element;

import com.foreach.across.modules.bootstrapui.elements.FormGroupElement;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.TextViewElement;
import org.junit.Test;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestFormGroupRequiredPostProcessor
{
	private DefaultViewElementBuilderContext builderContext = new DefaultViewElementBuilderContext();
	private FormGroupElement group = new FormGroupElement();
	private FormGroupRequiredPostProcessor<ViewElement> postProcessor = new FormGroupRequiredPostProcessor<>();

	@Test
	public void nothingHappensIfNotAFormGroup() {
		postProcessor.postProcess( builderContext, bootstrap.builders.textbox().build( builderContext ) );
	}

	@Test
	public void nothingHappensIfNoControl() {
		group.setRequired( true );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.isRequired() ).isTrue();

		group.setRequired( false );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.isRequired() ).isFalse();

		group.setControl( TextViewElement.text( "hi" ) );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.isRequired() ).isFalse();

		group.setRequired( true );
		postProcessor.postProcess( builderContext, group );
		assertThat( group.isRequired() ).isTrue();
	}

	@Test
	public void groupIsSetAsRequiredIfTheControlIsRequired() {
		group.setControl( bootstrap.builders.textbox().required().build( builderContext ) );

		assertThat( group.isRequired() ).isFalse();
		postProcessor.postProcess( builderContext, group );

		assertThat( group.isRequired() ).isTrue();
	}

	@Test
	public void groupRemainsRequiredEvenIfControlIsNot() {
		group.setControl( bootstrap.builders.textbox().required( false ).build( builderContext ) );
		group.setRequired( true );

		postProcessor.postProcess( builderContext, group );
		assertThat( group.isRequired() ).isTrue();
	}
}
