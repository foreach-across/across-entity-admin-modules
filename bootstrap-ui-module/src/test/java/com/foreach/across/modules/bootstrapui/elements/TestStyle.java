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
import com.foreach.across.modules.bootstrapui.elements.Style.Table;
import com.foreach.across.modules.bootstrapui.elements.Style.TableCell;
import org.junit.jupiter.api.Test;
import org.springframework.util.SerializationUtils;

import static com.foreach.across.modules.bootstrapui.elements.Style.ACTIVE;
import static com.foreach.across.modules.bootstrapui.elements.Style.SECONDARY;
import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		assertThat( Button.toBootstrapStyleRule( ACTIVE ) ).isNull();
		assertThat( Button.toBootstrapStyleRule( Button.DEFAULT ) ).isEqualTo( css.button );
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
		assertThat( Button.fromBootstrapStyleRule( css.button ) ).isEqualTo( Button.DEFAULT );
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

	@Test
	void tableRules() {
		assertThat( Table.toBootstrapStyleRule( ACTIVE ) ).isNull();
		assertThat( Table.toBootstrapStyleRule( Table.HOVER ) ).isEqualTo( css.table.hover );
		assertThat( Table.toBootstrapStyleRule( Table.STRIPED ) ).isEqualTo( css.table.striped );
		assertThat( Table.toBootstrapStyleRule( Table.CONDENSED ) ).isEqualTo( css.table.small );
		assertThat( Table.toBootstrapStyleRule( Table.BORDERED ) ).isEqualTo( css.table.bordered );

		assertThat( Table.fromBootstrapStyleRule( css.table ) ).isNull();
		assertThat( Table.fromBootstrapStyleRule( css.table.hover ) ).isEqualTo( Table.HOVER );
		assertThat( Table.fromBootstrapStyleRule( css.table.striped ) ).isEqualTo( Table.STRIPED );
		assertThat( Table.fromBootstrapStyleRule( css.table.small ) ).isEqualTo( Table.CONDENSED );
		assertThat( Table.fromBootstrapStyleRule( css.table.bordered ) ).isEqualTo( Table.BORDERED );
	}

	@Test
	void tableCellRules() {
		assertThat( TableCell.toBootstrapStyleRule( SECONDARY ) ).isNull();
		assertThat( TableCell.toBootstrapStyleRule( TableCell.ACTIVE ) ).isEqualTo( css.table.active );
		assertThat( TableCell.toBootstrapStyleRule( TableCell.DANGER ) ).isEqualTo( css.table.danger );
		assertThat( TableCell.toBootstrapStyleRule( TableCell.INFO ) ).isEqualTo( css.table.info );
		assertThat( TableCell.toBootstrapStyleRule( TableCell.SUCCESS ) ).isEqualTo( css.table.success );
		assertThat( TableCell.toBootstrapStyleRule( TableCell.WARNING ) ).isEqualTo( css.table.warning );

		assertThat( TableCell.fromBootstrapStyleRule( css.table ) ).isNull();
		assertThat( TableCell.fromBootstrapStyleRule( css.table.active ) ).isEqualTo( TableCell.ACTIVE );
		assertThat( TableCell.fromBootstrapStyleRule( css.table.danger ) ).isEqualTo( TableCell.DANGER );
		assertThat( TableCell.fromBootstrapStyleRule( css.table.info ) ).isEqualTo( TableCell.INFO );
		assertThat( TableCell.fromBootstrapStyleRule( css.table.success ) ).isEqualTo( TableCell.SUCCESS );
		assertThat( TableCell.fromBootstrapStyleRule( css.table.warning ) ).isEqualTo( TableCell.WARNING );
	}
}
