package com.foreach.across.modules.experimental.bulkactions.configurars;

import com.foreach.across.modules.experimental.bulkactions.domain.SimpleBulkIdentifier;
import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifierBuilder;
import com.foreach.across.modules.experimental.bulkactions.viewprocessors.SortableTableBulkActionProcessor;
import com.foreach.across.modules.entity.config.builders.EntityViewProcessorConfigurer;
import com.foreach.across.modules.entity.views.EntityViewProcessor;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import org.springframework.core.Ordered;

import java.util.Collections;

public class ListViewBulkActions
{
	public static void configureBulkActions( EntityViewProcessorConfigurer<? extends EntityViewProcessor> vb ) {
		vb.provideBean( new SortableTableBulkActionProcessor() ).order( Ordered.LOWEST_PRECEDENCE - 2 );
	}

	public static BulkIdentifierBuilder defaultBulkIdentifierBuilder() {
		return context -> new SimpleBulkIdentifier( EntityViewElementUtils.currentEntity( context, IdBasedEntity.class ).getId().toString(), Collections.emptyMap() );
	}
}
