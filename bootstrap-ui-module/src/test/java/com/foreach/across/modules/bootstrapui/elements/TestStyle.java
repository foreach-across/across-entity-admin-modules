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

package com.foreach.across.modules.bootstrapui.elements;

import com.foreach.across.modules.bootstrapui.elements.Style.Button;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class TestStyle
{
	@Test
	void shouldBeSerializable() {
		byte[] serialized = SerializationUtils.serialize( new Style( "custom-style" ) );
		assertNotNull( serialized );
		assertEquals( new Style( "custom-style" ), SerializationUtils.deserialize( serialized ) );

		assertEquals(
				Button.LINK,
				SerializationUtils.deserialize( SerializationUtils.serialize( Button.LINK ) )
		);
	}

	@Test
	void buttonRules() {
		assertThat( Button.toBootstrapStyleRule( Style.ACTIVE ) ).isNull();
		assertThat( Button.toBootstrapStyleRule( Button.DEFAULT ) ).isEqualTo( css.button.light );
		assertThat( Button.toBootstrapStyleRule( Button.LIGHT ) ).isEqualTo( css.button.light );
		assertThat( Button.toBootstrapStyleRule( Button.DARK ) ).isEqualTo( css.button.dark );
		assertThat( Button.toBootstrapStyleRule( Button.PRIMARY ) ).isEqualTo( css.button.primary );
		assertThat( Button.toBootstrapStyleRule( Button.SECONDARY ) ).isEqualTo( css.button.secondary );
		assertThat( Button.toBootstrapStyleRule( Button.SUCCESS ) ).isEqualTo( css.button.success );
		assertThat( Button.toBootstrapStyleRule( Button.INFO ) ).isEqualTo( css.button.info );
		assertThat( Button.toBootstrapStyleRule( Button.WARNING ) ).isEqualTo( css.button.warning );
		assertThat( Button.toBootstrapStyleRule( Button.DANGER ) ).isEqualTo( css.button.danger );
		assertThat( Button.toBootstrapStyleRule( Button.LINK ) ).isEqualTo( css.button.link );

		assertThat( Button.fromBootstrapStyleRule( css.button.block ) ).isNull();
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.white ) ).isNull();
		assertThat( Button.fromBootstrapStyleRule( css.button.light ) ).isEqualTo( Button.LIGHT );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.light ) ).isEqualTo( Button.LIGHT );
		assertThat( Button.fromBootstrapStyleRule( css.button.dark ) ).isEqualTo( Button.DARK );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.dark ) ).isEqualTo( Button.DARK );
		assertThat( Button.fromBootstrapStyleRule( css.button.primary ) ).isEqualTo( Button.PRIMARY );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.primary ) ).isEqualTo( Button.PRIMARY );
		assertThat( Button.fromBootstrapStyleRule( css.button.secondary ) ).isEqualTo( Button.SECONDARY );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.secondary ) ).isEqualTo( Button.SECONDARY );
		assertThat( Button.fromBootstrapStyleRule( css.button.success ) ).isEqualTo( Button.SUCCESS );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.success ) ).isEqualTo( Button.SUCCESS );
		assertThat( Button.fromBootstrapStyleRule( css.button.info ) ).isEqualTo( Button.INFO );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.info ) ).isEqualTo( Button.INFO );
		assertThat( Button.fromBootstrapStyleRule( css.button.warning ) ).isEqualTo( Button.WARNING );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.warning ) ).isEqualTo( Button.WARNING );
		assertThat( Button.fromBootstrapStyleRule( css.button.danger ) ).isEqualTo( Button.DANGER );
		assertThat( Button.fromBootstrapStyleRule( css.button.outline.danger ) ).isEqualTo( Button.DANGER );
		assertThat( Button.fromBootstrapStyleRule( css.button.link ) ).isEqualTo( Button.LINK );
	}
}
