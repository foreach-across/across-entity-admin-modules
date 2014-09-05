package com.foreach.across.modules.it.properties.definingmodule.business;

import com.foreach.across.core.revision.*;

/**
 * @author Arne Vandamme
 */
public class EntityRevision implements com.foreach.across.core.revision.Revision
{
	private int revisionId;
	private boolean draft, latest;

	public EntityRevision( int revisionId, boolean draft, boolean latest ) {
		this.revisionId = revisionId;
		this.draft = draft;
		this.latest = latest;
	}

	public int getRevisionId() {
		return revisionId;
	}

	@Override
	public boolean isDraftRevision() {
		return draft;
	}

	@Override
	public boolean isLatestRevision() {
		return latest;
	}
}
