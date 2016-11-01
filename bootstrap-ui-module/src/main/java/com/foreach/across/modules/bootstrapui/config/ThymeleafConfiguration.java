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
		                                                    new IconViewElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( ButtonViewElement.class,
		                                                    new ButtonViewElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( FormViewElement.class,
		                                                    new FormViewElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TextboxFormElement.class,
		                                                    new TextboxFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TextareaFormElement.class,
		                                                    new TextareaFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( CheckboxFormElement.class,
		                                                    new CheckboxFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( RadioFormElement.class,
		                                                    new RadioFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.class,
		                                                    new SelectFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.Option.class,
		                                                    new SelectFormElementModelWriter.OptionBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( SelectFormElement.OptionGroup.class,
		                                                    new SelectFormElementModelWriter.OptionGroupBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( StaticFormElement.class,
		                                                    new StaticFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( LabelFormElement.class,
		                                                    new LabelFormElementModelWriter() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( FormGroupElement.class,
		                                                    new FormGroupElementModelBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.class,
		                                                    new TableViewElementModelBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.Row.class,
		                                                    new TableViewElementModelBuilder.RowElementThymeleafBuilder() );
		viewElementNodeBuilderRegistry.registerNodeBuilder( TableViewElement.Cell.class,
		                                                    new TableViewElementModelBuilder.CellElementThymeleafBuilder() );
	}
}
