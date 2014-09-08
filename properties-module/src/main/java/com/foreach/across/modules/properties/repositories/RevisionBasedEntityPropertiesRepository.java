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
package com.foreach.across.modules.properties.repositories;

import com.foreach.across.core.revision.Revision;
import com.foreach.across.core.revision.RevisionBasedEntity;
import com.foreach.across.modules.properties.business.StringPropertiesSource;
import com.foreach.across.modules.properties.config.EntityPropertiesDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Entity properties that supports revisions. repository supporting revisions.
 *
 * @author Arne Vandamme
 */
public class RevisionBasedEntityPropertiesRepository<T, P extends Revision>
{
	private static final String FILTER_FOR_REVISION = "first_revision >= 0 and first_revision <= ? " +
			"and (last_revision = 0 or last_revision > ?)";
	private static final String FILTER_FOR_LATEST = "first_revision >= 0 and last_revision = 0";
	private static final String FILTER_FOR_LATEST_AND_DRAFTS = "first_revision = -1 or last_revision = 0";

	private final String SQL_INSERT_PROPERTY;
	private final String SQL_SELECT_PROPERTIES;
	private final String SQL_DROP_PROPERTIES;
	private final String SQL_UPDATE_PROPERTY;
	private final String SQL_DELETE_PROPERTY;

	private final JdbcTemplate jdbcTemplate;

	public RevisionBasedEntityPropertiesRepository( EntityPropertiesDescriptor configuration ) {
		jdbcTemplate = new JdbcTemplate( configuration.dataSource() );

		String table = configuration.tableName();
		String keyColumn = configuration.keyColumnName();

		SQL_INSERT_PROPERTY = String.format(
				"INSERT INTO %s (%s,property_name,property_value,first_revision,last_revision,deleted) " +
						"VALUES (?,?,?,?,?,?)", table, keyColumn );
		SQL_SELECT_PROPERTIES = String.format(
				"SELECT property_name, property_value,first_revision,last_revision,deleted " +
						"FROM %s WHERE %s = ?", table, keyColumn ) + " AND %s";

		SQL_UPDATE_PROPERTY = String.format(
				"UPDATE %s SET property_value = ?, first_revision = ?, last_revision = ?, deleted = ? " +
						"WHERE %s = ? AND property_name = ? AND first_revision = ? AND last_revision = ?",
				table, keyColumn
		);

		SQL_DELETE_PROPERTY = String.format(
				"DELETE FROM %s WHERE %s = ? AND property_name = ? AND first_revision = ? AND last_revision = ?",
				table, keyColumn
		);

		SQL_DROP_PROPERTIES = String.format( "DELETE FROM %s WHERE %s = ?", table, keyColumn );
	}

	@Transactional(readOnly = true)
	public StringPropertiesSource loadProperties( T entityId, P revision ) {
		List<Map<String, Object>> records;

		if ( revision.isLatestRevision() ) {
			records = jdbcTemplate.queryForList(
					String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_LATEST ),
					entityId
			);
		}
		else if ( revision.isDraftRevision() ) {
			records = jdbcTemplate.queryForList(
					String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_LATEST_AND_DRAFTS ),
					entityId
			);
		}
		else {
			records = jdbcTemplate.queryForList(
					String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_REVISION ),
					entityId, revision.getRevisionId(), revision.getRevisionId()
			);
		}

		Collection<PropertyRevision> revisionProperties = buildProperties( records );

		Map<String, String> sourceMap = new HashMap<>();

		if ( revision.isDraftRevision() ) {
			Map<Object, RevisionPair<PropertyRevision>> pairs = getRevisionPairs( revisionProperties );

			for ( RevisionPair<PropertyRevision> pair : pairs.values() ) {
				PropertyRevision use = pair.draft != null ? pair.draft : pair.nonDraft;
				sourceMap.put( use.getName(), use.getValue() );
			}
		}
		else {
			for ( PropertyRevision property : revisionProperties ) {
				sourceMap.put( property.getName(), property.getValue() );
			}
		}

		return new StringPropertiesSource( sourceMap );
	}

	@Transactional
	public void saveProperties( T entityId, P revision, StringPropertiesSource properties ) {
		List<Map<String, Object>> records = jdbcTemplate.queryForList(
				String.format( SQL_SELECT_PROPERTIES, FILTER_FOR_LATEST_AND_DRAFTS ), entityId
		);

		Collection<PropertyRevision> revisionProperties = buildProperties( records );

		Map<Object, RevisionPair<PropertyRevision>> pairs = getRevisionPairs( revisionProperties );

		for ( Map.Entry<String, ?> entry : properties.getProperties().entrySet() ) {
			PropertyRevision candidate = new PropertyRevision();
			candidate.setFirstRevision( Revision.DRAFT );
			candidate.setLastRevision( Revision.DRAFT );
			candidate.setName( entry.getKey() );

			Object value = entry.getValue();
			candidate.setValue( value != null ? value.toString() : null );

			RevisionPair<PropertyRevision> pair = pairs.get( candidate.getEntityIdentifier() );
			PropertyRevision current = pair != null ? pair.nonDraft : null;
			PropertyRevision draft = pair != null ? pair.draft : null;

			if ( current == null || candidate.isDifferentVersionOf( current ) ) {
				if ( draft == null ) {
					// insert this item as a new draft
					createProperty( entityId, candidate );
				}
				else if ( candidate.isDifferentVersionOf( draft ) ) {
					// update the draft item if different
					updateProperty( entityId, candidate );
				}
			}
			else if ( draft != null ) {
				// remove this draft as the new version is the same as the previous
				deleteProperty( entityId, draft );
			}

			pairs.remove( candidate.getEntityIdentifier() );
		}

		// All pairs left should be removed
		for ( RevisionPair<PropertyRevision> remaining : pairs.values() ) {
			if ( remaining.draft == null ) {
				// new draft that needs to delete the item
				PropertyRevision candidate = new PropertyRevision();
				candidate.setName( remaining.nonDraft.getName() );
				candidate.setFirstRevision( Revision.DRAFT );
				candidate.setLastRevision( Revision.DRAFT );
				candidate.setDeleted( true );

				createProperty( entityId, candidate );
			}
			else if ( remaining.nonDraft == null ) {
				// draft should be removed
				deleteProperty( entityId, remaining.draft );
			}
			else if ( !remaining.draft.isDeleted() ) {
				// update draft for deletion
				remaining.draft.setDeleted( true );
				updateProperty( entityId, remaining.draft );
			}
		}
	}

	private Collection<PropertyRevision> buildProperties( List<Map<String, Object>> properties ) {
		List<PropertyRevision> props = new LinkedList<>();

		for ( Map<String, Object> entry : properties ) {
			PropertyRevision propertyRevision = new PropertyRevision();
			propertyRevision.setName( (String) entry.get( "property_name" ) );
			propertyRevision.setValue( (String) entry.get( "property_value" ) );
			propertyRevision.setFirstRevision( toInteger( entry.get( "first_revision" ) ) );
			propertyRevision.setLastRevision( toInteger( entry.get( "last_revision" ) ) );
			propertyRevision.setDeleted( toBoolean( entry.get( "deleted" ) ) );

			props.add( propertyRevision );
		}

		return props;
	}

	private boolean toBoolean( Object instance ) {
		if ( instance instanceof Boolean ) {
			return (Boolean) instance;
		}

		if ( instance instanceof Number ) {
			return ( (Number) instance ).intValue() == 1;
		}

		return false;
	}

	private int toInteger( Object numericObject ) {
		if ( numericObject instanceof Number ) {
			return ( (Number) numericObject ).intValue();
		}

		return numericObject != null ? Integer.valueOf( numericObject.toString() ) : 0;
	}

	private void createProperty( T entityId, PropertyRevision revision ) {
		jdbcTemplate.update( SQL_INSERT_PROPERTY,
		                     entityId,
		                     revision.getName(),
		                     revision.getValue(),
		                     revision.getFirstRevision(),
		                     revision.getLastRevision(),
		                     revision.isDeleted() );
	}

	private void updateProperty( T entityId, PropertyRevision revision ) {
		jdbcTemplate.update( SQL_UPDATE_PROPERTY,
		                     revision.getValue(),
		                     revision.getFirstRevision(),
		                     revision.getLastRevision(),
		                     revision.isDeleted(),
		                     entityId,
		                     revision.getName(),
		                     revision.getFirstRevision(),
		                     revision.getLastRevision() );
	}

	private void deleteProperty( T entityId, PropertyRevision revision ) {
		jdbcTemplate.update( SQL_DELETE_PROPERTY, entityId, revision.getName(), revision.getFirstRevision(),
		                     revision.getLastRevision() );
	}

	protected Map<Object, RevisionPair<PropertyRevision>> getRevisionPairs( Collection<PropertyRevision> items ) {
		Map<Object, RevisionPair<PropertyRevision>> map = new HashMap<>();

		// Create the pairs of draft/non-draft instances
		for ( PropertyRevision item : items ) {
			// Get the key of the item
			Object id = item.getEntityIdentifier();

			// Get the revision pair
			RevisionPair<PropertyRevision> pair = map.get( id );

			if ( pair == null ) {
				pair = new RevisionPair<>();
				pair.id = pair;

				map.put( id, pair );
			}

			// Assign to the correct element
			if ( item.isDraft() ) {
				pair.draft = item;
			}
			else {
				pair.nonDraft = item;
			}
		}

		return map;
	}

	@Transactional
	public void deleteProperties( T entityId ) {
		jdbcTemplate.update( SQL_DROP_PROPERTIES, entityId );
	}

	private static final class RevisionPair<V extends RevisionBasedEntity>
	{
		private Object id;
		private V draft, nonDraft;
	}

	private static class PropertyRevision implements RevisionBasedEntity<PropertyRevision>
	{
		private String name, value;
		private int firstRevision, lastRevision;
		private boolean deleted;

		public void setName( String name ) {
			this.name = name;
		}

		public void setValue( String value ) {
			this.value = value;
		}

		public void setFirstRevision( int firstRevision ) {
			this.firstRevision = firstRevision;
		}

		public void setLastRevision( int lastRevision ) {
			this.lastRevision = lastRevision;
		}

		public void setDeleted( boolean deleted ) {
			this.deleted = deleted;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}

		public int getFirstRevision() {
			return firstRevision;
		}

		public int getLastRevision() {
			return lastRevision;
		}

		public boolean isDeleted() {
			return deleted;
		}

		public boolean isDraft() {
			return firstRevision == Revision.DRAFT;
		}

		@Override
		public Object getEntityIdentifier() {
			return getName();
		}

		@Override
		public boolean isDifferentVersionOf( PropertyRevision other ) {
			return !StringUtils.equals( value, other.value );
		}
	}
}
