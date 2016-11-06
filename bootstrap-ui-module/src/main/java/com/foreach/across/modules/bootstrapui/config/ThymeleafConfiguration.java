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
import com.foreach.across.modules.web.ui.thymeleaf.ViewElementModelWriterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
@Configuration
@ConditionalOnBean(ViewElementModelWriterRegistry.class)
public class ThymeleafConfiguration
{
	@Autowired
	public void registerViewElements( ViewElementModelWriterRegistry modelWriterRegistry ) {
		modelWriterRegistry.registerModelWriter( IconViewElement.ELEMENT_TYPE,
		                                         new IconViewElementModelWriter() );
		modelWriterRegistry.registerModelWriter( ButtonViewElement.class,
		                                         new ButtonViewElementModelWriter() );
		modelWriterRegistry.registerModelWriter( FormViewElement.class,
		                                         new FormViewElementModelWriter() );
		modelWriterRegistry.registerModelWriter( TextboxFormElement.class,
		                                         new TextboxFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( TextareaFormElement.class,
		                                         new TextareaFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( CheckboxFormElement.class,
		                                         new CheckboxFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( RadioFormElement.class,
		                                         new RadioFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( SelectFormElement.class,
		                                         new SelectFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( SelectFormElement.Option.class,
		                                         new SelectFormElementModelWriter.OptionBuilder() );
		modelWriterRegistry.registerModelWriter( SelectFormElement.OptionGroup.class,
		                                         new SelectFormElementModelWriter.OptionGroupBuilder() );
		modelWriterRegistry.registerModelWriter( StaticFormElement.class,
		                                         new StaticFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( LabelFormElement.class,
		                                         new LabelFormElementModelWriter() );
		modelWriterRegistry.registerModelWriter( FormGroupElement.class,
		                                         new FormGroupElementModelWriter() );
		modelWriterRegistry.registerModelWriter( TableViewElement.class,
		                                         new TableViewElementModelBuilder() );
		modelWriterRegistry.registerModelWriter( TableViewElement.Row.class,
		                                         new TableViewElementModelBuilder.RowElementThymeleafBuilder() );
		modelWriterRegistry.registerModelWriter( TableViewElement.Cell.class,
		                                         new TableViewElementModelBuilder.CellElementThymeleafBuilder() );
	}
}
