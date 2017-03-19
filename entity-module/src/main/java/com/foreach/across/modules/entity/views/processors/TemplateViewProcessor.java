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

import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Objects;

/**
 * Simple {@link com.foreach.across.modules.entity.views.EntityViewProcessor} that sets the template of an {@link EntityView}.
 *
 * @author Arne Vandamme
 * @since 2.0.0
 */
@AllArgsConstructor
public final class TemplateViewProcessor extends SimpleEntityViewProcessorAdapter
{
	@Getter
	@Setter
	@NonNull
	private String template;

	@Override
	public void render( EntityViewRequest entityViewRequest, EntityView entityView ) {
		entityView.setTemplate( template );
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		TemplateViewProcessor that = (TemplateViewProcessor) o;
		return Objects.equals( template, that.template );
	}

	@Override
	public int hashCode() {
		return Objects.hash( template );
	}
}
