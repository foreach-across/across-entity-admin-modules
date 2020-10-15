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

package test.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyBindingContext;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 3.2.0
 */
public class TestBindingContextSimpleResolving extends AbstractEntityPropertyBindingContextTest
{
	@Test
	public void readonly() {
		Address address = new Address( "some street" );
		EntityPropertyBindingContext addressContext = EntityPropertyBindingContext.forReading( address );

		EntityPropertyBindingContext streetContext = addressContext.resolvePropertyBindingContext( addressStreet );
		assertThat( streetContext ).isNotNull();
		assertThat( streetContext.isReadonly() ).isTrue();
		assertThat( streetContext.<String>getEntity() ).isEqualTo( "some street" );
		assertThat( streetContext.<String>getTarget() ).isEqualTo( "some street" );
	}

	@Test
	public void nonReadOnly() {
		User original = new User( new Address( "my street" ) );
		User user = new User( new Address( "your street" ) );
		EntityPropertyBindingContext userContext = EntityPropertyBindingContext.forUpdating( original, user );

		EntityPropertyBindingContext addressContext = userContext.resolvePropertyBindingContext( userAddress );
		assertThat( addressContext ).isNotNull();
		assertThat( addressContext.isReadonly() ).isFalse();
		assertThat( addressContext.<Address>getEntity() ).isEqualTo( new Address( "your street" ) );
		assertThat( addressContext.<Address>getTarget() ).isEqualTo( new Address( "your street" ) );

		EntityPropertyBindingContext streetContext = addressContext.resolvePropertyBindingContext( addressStreet );
		assertThat( streetContext ).isNotNull();
		assertThat( streetContext.isReadonly() ).isFalse();
		assertThat( streetContext.<String>getEntity() ).isEqualTo( "your street" );
		assertThat( streetContext.<String>getTarget() ).isEqualTo( "your street" );
	}
}
