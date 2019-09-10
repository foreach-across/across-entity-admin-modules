/*
 * Copyright 2019 the original author or authors
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

package com.foreach.across.samples.bootstrapui.application.controllers.form.controls;

import com.foreach.across.modules.bootstrapui.elements.NumericFormElement;
import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Currency;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * Generates a numeric control using a {@link NumericFormElement}.
 *
 * @author Vanhoof Stijn
 * @since 1.0.0
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/form-controls/numeric")
class Numeric extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/form-controls/numeric", "Numeric" );
	}

	@RequestMapping(method = RequestMethod.GET)
	String render() {
		return render(
				panel( "Number", simpleNumericElement() ),
				panel( "IntegerNumber", intgerNumericElement() ),
				panel( "Decimal Number", decimalNumericElement() ),
				panel( "Percentage number", percentageNumericElement() ),
				panel( "Currency number", currenencyNumericElement() )
		);
	}

	private NumericFormElement simpleNumericElement() {
		return bootstrap.builders.numeric()
		                         .simple()
		                         .build();
	}

	private NumericFormElement percentageNumericElement() {
		return bootstrap.builders.numeric()
		                         .percent()
		                         .build();
	}

	private NumericFormElement currenencyNumericElement() {
		return bootstrap.builders.numeric()
		                         .currency( Currency.getInstance( "EUR" ) )
		                         .build();
	}

	private NumericFormElement intgerNumericElement() {
		return bootstrap.builders.numeric()
		                         .integer()
		                         .build();
	}

	private NumericFormElement decimalNumericElement() {
		return bootstrap.builders.numeric()
		                         .decimal( 2 )
		                         .build();
	}

}
