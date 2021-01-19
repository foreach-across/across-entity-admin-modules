package com.foreach.across.modules.experimental.daterange.support;

import com.foreach.across.modules.entity.query.EQType;
import com.foreach.across.modules.entity.query.EQTypeConverter;
import com.foreach.across.modules.entity.query.EntityQueryFunctionHandler;
import com.foreach.across.modules.experimental.daterange.ui.DateRange;
import com.foreach.across.modules.experimental.daterange.ui.DateRangeFunctionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Handle all {@link com.foreach.across.modules.entity.query.EQFunction} related to {@link DateRangeFunctionRegistry}
 * and convert the result into a {@link DateRange} that will be picked up and handled further by the
 * {@link DateRangeEntityTranslator}
 *
 * @author Stijn Vanhoof
 * @author Marc Vanbrabant
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class DateRangeEqlFunctions implements EntityQueryFunctionHandler {

    private final DateRangeFunctionRegistry dateRangeFunctionRegistry;

    @Override
    public boolean accepts(String functionName, TypeDescriptor desiredType) {
        return dateRangeFunctionRegistry.forName(functionName) != null;
    }

    @Override
    public Object apply(String functionName, EQType[] arguments, TypeDescriptor desiredType, EQTypeConverter argumentConverter) {
        //TODO: support other date types, see EntityQueryDateFunctions
        LocalDateTime[] boundaries = Arrays.stream(argumentConverter.convertAll(desiredType, false, arguments)).toArray(LocalDateTime[]::new);
        return dateRangeFunctionRegistry.createDateRange(functionName, boundaries);
    }
}
