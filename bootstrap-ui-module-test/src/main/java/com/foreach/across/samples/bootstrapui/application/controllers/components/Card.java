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

package com.foreach.across.samples.bootstrapui.application.controllers.components;

import com.foreach.across.modules.web.menu.PathBasedMenuBuilder;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.samples.bootstrapui.application.controllers.ExampleController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import static com.foreach.across.modules.bootstrapui.elements.entry.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * @author Arne Vandamme
 * @since 3.0.0
 */
@Controller
class Card extends ExampleController
{
	@Override
	protected void menuItems( PathBasedMenuBuilder menu ) {
		menu.item( "/components/card", "Card" );
	}

	@GetMapping("/components/card")
	String render() {
		return render(
				panel( "Simple cards",
				       html.builders.div( css.card, css.margin.bottom.s2 )
				                    .add( html.builders.div( css.card.body )
				                                       .add( html.text( "This is some text within a card body." ) ) ),
				       html.builders.div( css.card ).add(
						       html.builders.div( css.card.body )
						                    .add( html.h5( css.card.title, html.unescapedText( "Card title" ) ) )
						                    .add( html.h6( css.card.subTitle, css.margin.bottom.s2, css.text.muted, html.unescapedText( "Card subtitle" ) ) )
						                    .add( html.p( css.card.text, html.unescapedText(
								                    "Some quick example text to build on the card title and make up the bulk of the card's content." ) )
						                    )
						                    .add( bootstrap.builders.link( css.card.link ).text( "Card link" ) )
						                    .add( bootstrap.builders.link( css.card.link ).text( "Another link" ) )
				       )
				)
		);
	}
}
