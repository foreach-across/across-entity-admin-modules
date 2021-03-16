package com.foreach.across.modules.experimental.daterange.support;

import com.foreach.across.core.annotations.Exposed;
import com.foreach.across.modules.entity.query.*;
import com.foreach.across.modules.experimental.daterange.ui.DateRange;
import org.springframework.stereotype.Component;

/**
 * Translates a {@link DateRange} into a {@link EntityQuery}
 *
 * @author Stijn Vanhoof
 * @author Marc Vanbrabant
 */
@Component
@Exposed
public class DateRangeEntityTranslator implements EntityQueryConditionTranslator
{

	@Override
	public EntityQueryExpression translate( EntityQueryCondition condition ) {
		if ( shouldTranslate( condition ) ) {
			EntityQuery entityQuery = new EntityQuery();
			entityQuery.setOperand( EntityQueryOps.AND );

			DateRange valueHolder = (DateRange) condition.getFirstArgument();

			entityQuery.add( new EntityQueryCondition( condition.getProperty(), EntityQueryOps.GE, valueHolder.getDateFrom().getSource() ) );
			entityQuery.add( new EntityQueryCondition( condition.getProperty(), EntityQueryOps.LT, valueHolder.getDateTo().getSource() ) );

			return entityQuery;
		}

		return condition;
	}

	private static boolean shouldTranslate( EntityQueryCondition condition ) {
		return EntityQueryOps.EQ == condition.getOperand()
				&& condition.getArguments().length > 0
				&& condition.getFirstArgument() instanceof DateRange;
	}

}
