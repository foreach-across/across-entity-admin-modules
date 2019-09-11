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

package com.foreach.across.modules.entity.views.processors;

import com.foreach.across.modules.bootstrapui.elements.FormViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.context.EntityViewContext;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TestCssClassesOnViewProcessors
{
	@Mock
	private EntityViewRequest entityViewRequest;
	@Mock
	private EntityView entityView;

	@Test
	public void listViewFormHasCssClass() {
		ListFormViewProcessor processor = new ListFormViewProcessor();
		ViewElementBuilderMap map = new ViewElementBuilderMap();
		processor.createViewElementBuilders( entityViewRequest, entityView, map );
		assertThat( map ).isNotNull().containsKey( "entityListForm" );
		assertThat( map.get( "entityListForm" ) ).isInstanceOf( FormViewElementBuilder.class );
		FormViewElement formViewElement = (FormViewElement) map.get( "entityListForm" ).build();
		assertThat( formViewElement.hasCssClass( "em-list-form" ) ).isTrue();
	}

	@Test
	public void formViewFormHasCssClass() {
		EntityViewContext entityViewContext = mock( EntityViewContext.class );
		when( entityViewRequest.getWebRequest() ).thenReturn( mock( NativeWebRequest.class ) );
		when( entityViewRequest.getEntityViewContext() ).thenReturn( entityViewContext );
		when( entityViewRequest.getModel() ).thenReturn( mock( ModelMap.class ) );
		when( entityViewContext.getLinkBuilder() ).thenReturn( mock( EntityViewLinkBuilder.class ) );
		SingleEntityFormViewProcessor processor = new SingleEntityFormViewProcessor();
		ViewElementBuilderMap map = new ViewElementBuilderMap();
		processor.createViewElementBuilders( entityViewRequest, entityView, map );
		assertThat( map ).isNotNull().containsKey( "entityForm" );
		assertThat( map.get( "entityForm" ) ).isInstanceOf( FormViewElementBuilder.class );
		FormViewElement formViewElement = (FormViewElement) map.get( "entityForm" ).build();
		assertThat( formViewElement.hasCssClass( "em-form" ) ).isTrue();
	}
}
