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

package it.com.foreach.across.modules.entity.utils;

import com.foreach.across.core.context.info.AcrossModuleInfo;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.registry.EntityRegistry;
import com.foreach.across.modules.entity.views.EntityView;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.support.RepositoryInvoker;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class EntityVerifier
{
	private final EntityConfiguration<?> configuration;
	final EntityRegistry entityRegistry;

	public EntityVerifier( EntityRegistry entityRegistry, Class<?> entityType ) {
		this.entityRegistry = entityRegistry;
		configuration = entityRegistry.getEntityConfiguration( entityType );
		assertNotNull( configuration );
	}

	public EntityVerifier isVisible( boolean visible ) {
		assertEquals( visible, !configuration.isHidden() );
		return this;
	}

	public EntityVerifier hasRepository() {
		assertTrue( "EntityModel not present", configuration.hasEntityModel() );
		assertTrue( "Repository not present", configuration.hasAttribute( Repository.class ) );
		assertTrue( "RepositoryInvoker not present", configuration.hasAttribute( RepositoryInvoker.class ) );
		return this;
	}

	public EntityVerifier hasAttribute( String attributeName, Object attributeValue ) {
		assertTrue( configuration.hasAttribute( attributeName ) );
		assertEquals( attributeValue, configuration.getAttribute( attributeName ) );
		return this;
	}

	public EntityAssociationVerifier hasAssociation( String associationName, boolean visible ) {
		return new EntityAssociationVerifier( this, configuration, associationName ).visible( visible );
	}

	public EntityVerifier hasAssociation( String associationName, Class<?> targetClass, boolean visible ) {
		EntityAssociation association = configuration.association( associationName );
		assertNotNull( "Association " + associationName + " not present", association );
		assertSame( entityRegistry.getEntityConfiguration( targetClass ), association.getTargetEntityConfiguration() );

		assertNotEquals( visible, association.isHidden() );
		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.CREATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.UPDATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.DELETE_VIEW_NAME ) );

		return this;
	}

	public EntityVerifier isFromModule( String moduleName ) {
		assertEquals( moduleName, configuration.getAttribute( AcrossModuleInfo.class ).getName() );
		return this;
	}
}
