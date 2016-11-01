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
package com.foreach.across.modules.bootstrapui.elements.thymeleaf;

import com.foreach.across.modules.bootstrapui.elements.IconViewElement;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class IconViewElementModelWriter extends AbstractHtmlViewElementModelWriter<IconViewElement>
{
	@Override
	protected void writeOpenElement( IconViewElement viewElement, ThymeleafModelBuilder writer ) {
		super.writeOpenElement( viewElement, writer );

		if ( viewElement.getIconCss() != null ) {
			writer.addAttributeValue( "class", viewElement.getIconCss() );
		}
	}
}
