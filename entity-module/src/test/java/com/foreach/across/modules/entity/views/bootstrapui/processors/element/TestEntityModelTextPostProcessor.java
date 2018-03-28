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

import com.foreach.across.modules.entity.registry.EntityModel;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class TestEntityModelTextPostProcessor
{
	@Mock
	private EntityModel model;

	@Mock
	private EntityPropertyDescriptor descriptor;

	private EntityModelTextPostProcessor processor;

	@Before
	public void setUp() throws Exception {
		processor = new EntityModelTextPostProcessor( descriptor, model );
	}

	@Test
	public void printDispatchesToEntityModel() {
		when( model.getLabel( "123", Locale.CANADA ) ).thenReturn( "translated." );
		assertEquals( "translated.", processor.print( "123", Locale.CANADA ) );
	}
}
