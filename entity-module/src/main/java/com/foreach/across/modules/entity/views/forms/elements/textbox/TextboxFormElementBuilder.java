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
package com.foreach.across.modules.entity.views.forms.elements.textbox;

import com.foreach.across.modules.entity.views.forms.elements.FormElementBuilderSupport;

/**
 * @author Arne Vandamme
 */
public class TextboxFormElementBuilder extends FormElementBuilderSupport<TextboxFormElement>
{
	private Integer maxLength;

	public TextboxFormElementBuilder() {
		super( TextboxFormElement.class );
	}

	public Integer getMaxLength() {
		return maxLength;
	}

	public void setMaxLength( Integer maxLength ) {
		this.maxLength = maxLength;
	}

	@Override
	public boolean equals( Object o ) {
		if ( this == o ) {
			return true;
		}
		if ( !( o instanceof TextboxFormElementBuilder ) ) {
			return false;
		}
		if ( !super.equals( o ) ) {
			return false;
		}

		TextboxFormElementBuilder that = (TextboxFormElementBuilder) o;

		if ( maxLength != null ? !maxLength.equals( that.maxLength ) : that.maxLength != null ) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + ( maxLength != null ? maxLength.hashCode() : 0 );
		return result;
	}
}