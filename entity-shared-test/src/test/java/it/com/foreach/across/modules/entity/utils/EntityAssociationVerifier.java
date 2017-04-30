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

import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;

import static org.junit.Assert.*;

/**
 * @author Arne Vandamme
 */
public class EntityAssociationVerifier
{
	private final EntityAssociation association;
	private final EntityVerifier parent;

	EntityAssociationVerifier( EntityVerifier parent, EntityConfiguration<?> configuration, String associationName ) {
		this.parent = parent;
		association = configuration.association( associationName );
		assertNotNull( "Association " + associationName + " not present", association );

		assertTrue( association.hasView( EntityView.LIST_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.CREATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.UPDATE_VIEW_NAME ) );
		assertTrue( association.hasView( EntityView.DELETE_VIEW_NAME ) );
	}

	public EntityAssociationVerifier from( String propertyName ) {
		if ( propertyName == null ) {
			assertNull( association.getSourceProperty() );
		}
		else {
			assertEquals( propertyName, association.getSourceProperty().getName() );
		}
		return this;
	}

	public EntityAssociationVerifier to( Class<?> targetClass ) {
		return to( targetClass, null );
	}

	public EntityAssociationVerifier to( Class<?> targetClass, String targetPropertyName ) {
		assertSame( parent.entityRegistry.getEntityConfiguration( targetClass ), association.getTargetEntityConfiguration() );
		if ( targetPropertyName == null ) {
			assertNull( "No target property was expected", association.getTargetProperty() );

		}
		else {
			assertSame(
					association.getTargetProperty(),
					association.getTargetEntityConfiguration().getPropertyRegistry().getProperty( targetPropertyName )
			);
		}
		return this;
	}

	public EntityAssociationVerifier visible( boolean visible ) {
		assertNotEquals( visible, association.isHidden() );
		return this;
	}

	public EntityVerifier and() {
		return parent;
	}
}
