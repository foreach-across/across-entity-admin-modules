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

package com.foreach.across.modules.entity.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author Arne Vandamme
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class TestEntityMessageCodeResolver
{
	private EntityMessageCodeResolver codeResolver;

	@Before
	public void setUp() throws Exception {
		codeResolver = new EntityMessageCodeResolver();
	}

	@Test
	public void buildCodes() {
		codeResolver.setPrefixes( "my.entity", "my" );
		codeResolver.setFallbackCollections( "entities", "" );

		assertArrayEquals(
				new String[] { "my.entity.*", "my.*" },
				codeResolver.buildMessageCodes( "*" )
		);
		assertArrayEquals(
				new String[] { "my.entity.*", "my.*", "entities.*", "*" },
				codeResolver.buildMessageCodes( "*", true )
		);
	}

	@Test
	public void prefixedResolver() {
		codeResolver.setPrefixes( "my.entity", "my" );
		codeResolver.setFallbackCollections( "entities", "" );

		EntityMessageCodeResolver child = codeResolver.prefixedResolver( "views.listView", "views" );
		assertArrayEquals(
				new String[] { "my.entity.views.listView.*", "my.entity.views.*", "my.entity.*", "my.views.listView.*", "my.views.*", "my.*" },
				child.buildMessageCodes( "*" )
		);
		assertArrayEquals(
				new String[] {
						"my.entity.views.listView.*", "my.entity.views.*", "my.entity.*", "my.views.listView.*", "my.views.*", "my.*",
						"entities.views.listView.*", "entities.views.*", "entities.*", "views.listView.*", "views.*", "*"
				},
				child.buildMessageCodes( "*", true )
		);
	}

	@Test
	public void errorCodeResolver() {
		codeResolver.setPrefixes( "my.entity" );
		codeResolver.setFallbackCollections( "entities" );

		assertArrayEquals(
				new String[] {
						"my.entity.validation.error.command.name",
						"my.entity.validation.error.name",
						"my.entity.validation.error.java.lang.String",
						"my.entity.validation.error",
						"entities.validation.error.command.name",
						"entities.validation.error.name",
						"entities.validation.error.java.lang.String",
						"entities.validation.error"
				},
				codeResolver.resolveMessageCodes( "error", "command", "name", String.class )
		);
	}

	@Test
	public void prefixedErrorCodeResolver() {
		codeResolver.setPrefixes( "my.entity" );
		codeResolver.setFallbackCollections( "entities" );

		EntityMessageCodeResolver child = codeResolver.prefixedResolver( "views" );

		assertArrayEquals(
				new String[] {
						"my.entity.views.validation.error.command.name",
						"my.entity.views.validation.error.name",
						"my.entity.views.validation.error.java.lang.String",
						"my.entity.views.validation.error",
						"my.entity.validation.error.command.name",
						"my.entity.validation.error.name",
						"my.entity.validation.error.java.lang.String",
						"my.entity.validation.error",
						"entities.views.validation.error.command.name",
						"entities.views.validation.error.name",
						"entities.views.validation.error.java.lang.String",
						"entities.views.validation.error",
						"entities.validation.error.command.name",
						"entities.validation.error.name",
						"entities.validation.error.java.lang.String",
						"entities.validation.error"
				},
				child.resolveMessageCodes( "error", "command", "name", String.class )
		);
	}

	@Test
	public void staticGenerationMultiple() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void emptyRootCollection() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities", "" };
		String[] subCollections = new String[] { "views.listView", "views" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.views.properties.displayName",
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.views.listView.properties.displayName",
						"EntityModule.entities.views.properties.displayName",
						"EntityModule.entities.properties.displayName",
						"views.listView.properties.displayName",
						"views.properties.displayName",
						"properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationRootCollectionOnly() {
		String[] rootCollections = new String[] { "UserModule.entities.user", "EntityModule.entities" };
		String[] subCollections = new String[0];
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.properties.displayName",
						"EntityModule.entities.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationSingle() {
		String[] rootCollections = new String[] { "UserModule.entities.user" };
		String[] subCollections = new String[] { "views.listView" };
		String itemKey = "properties.displayName";

		String[] generated = EntityMessageCodeResolver.generateCodes( rootCollections, subCollections, itemKey );

		assertArrayEquals(
				new String[] {
						"UserModule.entities.user.views.listView.properties.displayName",
						"UserModule.entities.user.properties.displayName"
				},
				generated
		);
	}

	@Test
	public void staticGenerationPrefixes() {
		assertArrayEquals(
				new String[] { "my.entities", "my" },
				EntityMessageCodeResolver.generateCodes( new String[] { "my.entities", "my" }, new String[0], "" )
		);
		assertArrayEquals(
				new String[] { "my.entities", "my" },
				EntityMessageCodeResolver.generateCodes( new String[0], new String[] { "my.entities", "my" }, "" )
		);
		assertArrayEquals(
				new String[] { "my.entities.views.listView", "my.entities.views", "my.entities", "my.views.listView", "my.views", "my" },
				EntityMessageCodeResolver.generateCodes( new String[] { "my.entities", "my" }, new String[] { "views.listView", "views" }, "" )
		);
	}
}
