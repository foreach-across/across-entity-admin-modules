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

package com.foreach.across.modules.entity.bind;

import com.foreach.across.modules.entity.registry.properties.EntityPropertyRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Arne Vandamme
 * @since 4.0.0
 */
@ExtendWith(MockitoExtension.class)
class TestEntityPropertiesBinderCache
{
	@Mock
	private EntityPropertyRegistry propertyRegistry;

	@Test
	void sameReadonlyBindingContextIsHandedOutForSameEntity() {
		EntityPropertiesBinderCache cache = EntityPropertiesBinderCache.builder()
		                                                               .propertyRegistry( propertyRegistry )
		                                                               .entities( Arrays.asList( "123", "456" ) )
		                                                               .build();

		EntityPropertiesBinder context = cache.getPropertiesBinder( "123" ).orElse( null );

		assertThat( context )
				.isNotNull()
				.isSameAs( cache.getPropertiesBinder( "123" ).orElse( null ) )
				.isNotSameAs( cache.getPropertiesBinder( "456" ).orElse( null ) );
		assertThat( context.isReadonly() ).isTrue();

		assertThat( cache.getPropertiesBinder( "456" ).orElse( null ) ).isNotNull();

		cache.remove( "123" );
		assertThat( cache.getPropertiesBinder( "123" ) ).isEmpty();
		assertThat( cache.getPropertiesBinder( "456" ).orElse( null ) )
				.isNotNull().isSameAs( cache.getPropertiesBinder( "456" ).orElse( null ) );

		cache.clear();
		assertThat( cache.getPropertiesBinder( "123" ) ).isEmpty();
		assertThat( cache.getPropertiesBinder( "456" ) ).isEmpty();
	}
}
