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
package com.foreach.across.modules.bootstrapui.config;

import com.foreach.across.modules.bootstrapui.elements.*;
import com.foreach.across.modules.bootstrapui.elements.thymeleaf.*;
import com.foreach.across.modules.web.ui.thymeleaf.ViewElementNodeBuilderRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @author Arne Vandamme
 */
@Configuration
// todo: check Thymeleaf is enabled
public class ThymeleafConfiguration
{
	@Autowired
	private ViewElementNodeBuilderRegistry viewElementNodeBuilderRegistry;

	@PostConstruct
	public void registerViewElements() {
		viewElementNodeBuilderRegistry.registerNodeBuilder( IconViewElement.ELEMENT_TYPE,
		                                                    new IconViewElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( ButtonViewElement.class,
		                                                    new ButtonViewElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( FormViewElement.class,
		                                                    new FormViewElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TextboxFormElement.class,
		                                                    new TextboxFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TextareaFormElement.class,
		                                                    new TextareaFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( CheckboxFormElement.class,
		                                                    new CheckboxFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( RadioFormElement.class,
		                                                    new RadioFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.class,
		                                                    new SelectFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.Option.class,
		                                                    new SelectFormElementThymeleafBuilder.OptionBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.OptionGroup.class,
		                                                    new SelectFormElementThymeleafBuilder.OptionGroupBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( StaticFormElement.class,
		                                                    new StaticFormElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( LabelFormElement.class,
		                                                    new LabelFormElementNodeBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( FormGroupElement.class,
		                                                    new FormGroupElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.class,
		                                                    new TableViewElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.Row.class,
		                                                    new TableViewElementThymeleafBuilder.RowElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.Cell.class,
		                                                    new TableViewElementThymeleafBuilder.CellElementThymeleafBuilder() );
	}
}
