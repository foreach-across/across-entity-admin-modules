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

package test;

import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.bootstrapui.BootstrapUiModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.entity.EntityModuleIcons;
import com.foreach.across.modules.entity.controllers.admin.GenericEntityViewController;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderFactory;
import com.foreach.across.modules.entity.views.bootstrapui.BootstrapUiElementTypeLookupStrategy;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.test.AcrossTestContext;
import org.junit.jupiter.api.Test;

import static com.foreach.across.test.support.AcrossTestBuilders.web;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
public class TestEntityModule
{
	@Test
	public void bootstrapWithoutBootstrapUiModule() {
		try (AcrossTestContext context = web( false )
				.modules( EntityModule.NAME )
				.build()) {
			assertThat( context.contextInfo().hasModule( BootstrapUiModule.NAME ) ).isFalse();
			assertThat( context.contextInfo().hasModule( AdminWebModule.NAME ) ).isFalse();

			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, GenericEntityViewController.class ) ).isEmpty();
			assertThat( context.getBeansOfType( EntityViewLinks.class ) ).isEmpty();
			assertThat( context.getBeansOfType( EntityViewElementBuilderFactory.class ) ).isEmpty();

			assertThatExceptionOfType( IllegalArgumentException.class )
					.isThrownBy( EntityModuleIcons.entityModuleIcons.formView::delete );
		}
	}

	@Test
	public void bootstrapWithoutAdminWebModule() {
		try (AcrossTestContext context = web( false )
				.modules( EntityModule.NAME, BootstrapUiModule.NAME )
				.build()) {
			assertThat( context.contextInfo().hasModule( AdminWebModule.NAME ) ).isFalse();

			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, GenericEntityViewController.class ) ).isEmpty();
			assertThat( context.getBeansOfType( EntityViewLinks.class ) ).isEmpty();
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, BootstrapUiElementTypeLookupStrategy.class ) ).isNotEmpty();

			assertThat( EntityModuleIcons.entityModuleIcons.formView.delete() ).isNotNull();
		}
	}

	@Test
	public void bootstrapWithAdminWebAndBootstrapUiModule() {
		try (AcrossTestContext context = web( false )
				.modules( EntityModule.NAME, AdminWebModule.NAME )
				.build()) {
			assertThat( context.findBeanOfTypeFromModule( EntityModule.NAME, GenericEntityViewController.class ) ).isNotEmpty();
			assertThat( context.getBeanOfType( EntityViewLinks.class ) ).isNotNull();

			assertThat( EntityModuleIcons.entityModuleIcons.formView.delete() ).isNotNull();
		}
	}
}
