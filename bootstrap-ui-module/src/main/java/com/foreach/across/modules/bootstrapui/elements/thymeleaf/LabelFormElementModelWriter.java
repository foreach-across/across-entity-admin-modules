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

import com.foreach.across.modules.bootstrapui.elements.LabelFormElement;
import com.foreach.across.modules.bootstrapui.utils.BootstrapElementUtils;
import com.foreach.across.modules.web.thymeleaf.ThymeleafModelBuilder;
import com.foreach.across.modules.web.ui.ViewElement;
import com.foreach.across.modules.web.ui.elements.thymeleaf.AbstractHtmlViewElementModelWriter;

import java.util.Optional;

/**
 * @author Arne Vandamme
 * @since 1.0.0
 */
public class LabelFormElementModelWriter extends AbstractHtmlViewElementModelWriter<LabelFormElement>
{
	@Override
	protected void writeOpenElement( LabelFormElement viewElement, ThymeleafModelBuilder writer ) {
		super.writeOpenElement( viewElement, writer );

		targetId( viewElement, writer ).ifPresent( id -> writer.addAttribute( "for", id ) );

		writer.addHtml( viewElement.getText() );
	}

	private Optional<String> targetId( LabelFormElement label, ThymeleafModelBuilder writer ) {
		if ( label.hasTarget() ) {
			if ( label.isTargetId() ) {
				return Optional.of( label.getTargetAsId() );
			}

			ViewElement target = BootstrapElementUtils.getFormControl( label.getTargetAsElement() );

			if ( target == null ) {
				target = label.getTargetAsElement();
			}

			return Optional.ofNullable( writer.retrieveHtmlId( target ) );
		}

		return Optional.empty();
	}
}
