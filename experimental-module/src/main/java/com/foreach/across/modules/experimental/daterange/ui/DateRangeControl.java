package com.foreach.across.modules.experimental.daterange.ui;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiViewElementAttributes;
import com.foreach.across.modules.bootstrapui.elements.DateTimeFormElementConfiguration;
import com.foreach.across.modules.bootstrapui.elements.builder.OptionFormElementBuilder;
import com.foreach.across.modules.entity.config.builders.EntityPropertyRegistryBuilder;
import com.foreach.across.modules.entity.query.EntityQueryConditionTranslator;
import com.foreach.across.modules.entity.query.EntityQueryOps;
import com.foreach.across.modules.entity.registry.properties.EntityPropertyDescriptor;
import com.foreach.across.modules.entity.util.EntityUtils;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.processors.query.EntityQueryFilterControlUtils;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.experimental.daterange.support.DateRangeEntityTranslator;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.HtmlViewElement;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

/**
 * Build the date range filter control
 *
 * @author Stijn Vanhoof
 */
@RequiredArgsConstructor
public class DateRangeControl implements ViewElementBuilder<HtmlViewElement> {

    public static final List<DateRange.DateRangeItem> DEFAULT_DATE_RANGES = Arrays.asList(
            DateRange.DATE_RANGE, DateRange.TODAY, DateRange.YESTERDAY, DateRange.LAST_WEEK, DateRange.LAST_MONTH, DateRange.LAST_YEAR
    );

    private final List<DateRange.DateRangeItem> dateRanges;

    /**
     * A utility method to construct the date range filter control
     *
     * @param dateRangeEntityTranslator the Translator to use from this filter controler {@link DateRangeEntityTranslator}
     * @param dateRanges                the dateRanges to display for this control
     */
    public static Consumer<EntityPropertyRegistryBuilder.PropertyDescriptorBuilder> build(@NonNull DateRangeEntityTranslator dateRangeEntityTranslator, List<DateRange.DateRangeItem> dateRanges) {
        return (propertyDescriptorBuilder) -> propertyDescriptorBuilder
                .viewElementBuilder(ViewElementMode.FILTER_CONTROL, new DateRangeControl(dateRanges))
                .attribute(EntityQueryConditionTranslator.class, dateRangeEntityTranslator);
    }

    @Override
    public HtmlViewElement build(ViewElementBuilderContext builderContext) {
        WebResourceRegistry webResourceRegistry = builderContext.getAttribute(WebResourceRegistry.class);
        EntityPropertyDescriptor descriptor = EntityViewElementUtils.currentPropertyDescriptor(builderContext);
        String propertyName = descriptor.getName();

        webResourceRegistry.apply(
                WebResourceRule.add(WebResource.javascript("@static:/experimental/web/daterange-loader.js"))
                        .withKey("dateRangePicker")
                        .toBucket(WebResource.JAVASCRIPT_PAGE_END),
                WebResourceRule.add(WebResource.css("@static:/experimental/web/daterange-loader.css"))
                        .withKey("dateRangePicker")
                        .toBucket(WebResource.CSS)
        );

        String LAST_MODIFIED_SELECTOR = propertyName + "Selector";
        DateTimeFormElementConfiguration dateTimeFormElementConfiguration = new DateTimeFormElementConfiguration(DateTimeFormElementConfiguration.Format.DATE);
        DateRange datepickerOptionValue = EntityViewElementUtils.currentPropertyValue(builderContext, DateRange.class);
        boolean customDateRangeIsSelected = isCustomDateRangeIsSelected(datepickerOptionValue);

        List<OptionFormElementBuilder> dateRangeViewElements = new ArrayList<>();
        for (DateRange.DateRangeItem dateRange : dateRanges) {
            OptionFormElementBuilder option;
            if (Objects.equals("dateRange", dateRange.getName())) {
                option = bootstrap.builders.option().value("custom")
                        .controlName(LAST_MODIFIED_SELECTOR)
                        .text("#{controls.dateRange[selectDate]=Select date range}")
                        .selected(customDateRangeIsSelected);
            } else {
                String text = builderContext.resolveText("#{properties." + propertyName + ".dateRange[" + dateRange.getName() + "]}", "");
                if (Objects.equals("", text)) {
                    text = builderContext.resolveText("#{controls.dateRange[" + dateRange.getName() + "]=" + dateRange.getName() + "}");
                }

                option = bootstrap.builders.option().value(dateRange.getName() + "()")
                        .controlName(LAST_MODIFIED_SELECTOR)
                        .text(Objects.equals(text, dateRange.getName()) ? EntityUtils.generateDisplayName(dateRange.getName()) : text)
                        .selected(isSelected(datepickerOptionValue, dateRange.getName()));
            }

            dateRangeViewElements.add(option);
        }

        Class<?> propertyType = descriptor.getPropertyType();
        DateTimeFormElementConfiguration.Format format = DateTimeFormElementConfiguration.Format.DATETIME;
        if (ChronoLocalDate.class.isAssignableFrom(propertyType)) {
            format = DateTimeFormElementConfiguration.Format.DATE;
        } else if (LocalTime.class.isAssignableFrom(propertyType)) {
            format = DateTimeFormElementConfiguration.Format.TIME;
        }

        return html.div(css.of("custom-date-range-picker"))
                .setAttribute(BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "date-range-picker")
                .setAttribute("data-" + EntityQueryFilterControlUtils.FilterControlAttributes.TYPE, "EQValue")
                .setAttribute("data-" + EntityQueryFilterControlUtils.FilterControlAttributes.OPERAND, EntityQueryOps.EQ.name())
                .setAttribute("data-" + EntityQueryFilterControlUtils.FilterControlAttributes.PROPERTY_NAME, propertyName)
                .addChild(bootstrap.builders.select(css.of("js-custom-datepicker-options"))
                        .add(bootstrap.builders.option().value("")
                                .controlName(LAST_MODIFIED_SELECTOR)
                                .text("All")
                                .selected(datepickerOptionValue == null)
                        )
                        .add(dateRangeViewElements.toArray(new ViewElementBuilder[0]))
                        .build()
                )
                .addChild(html.div(css.of(
                        "js-custom-date-picker-dates",
                        "custom-date-range-picker-dates",
                        !customDateRangeIsSelected ? "d-none" : ""
                        ))
                                .addChild(
                                        html.div().addChild(
                                                bootstrap.builders.datetime()
                                                        .configuration(dateTimeFormElementConfiguration)
                                                        .controlName(propertyName + "DateFrom")
                                                        .htmlId(propertyName + "DateFrom")
                                                        .data("daterange", "from")
                                                        .format(format)
                                                        .attribute(BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "datetime")
                                                        .value(customDateRangeIsSelected ? datepickerOptionValue.getDateFrom().getLocaleDateTime()
                                                                : null).build()
                                        )
                                )
                                .addChild(
                                        html.div().addChild(
                                                bootstrap.builders.datetime()
                                                        .controlName(propertyName + "DateTo")
                                                        .htmlId(propertyName + "DateTo")
                                                        .data("daterange", "to")
                                                        .format(format)
                                                        .configuration(dateTimeFormElementConfiguration)
                                                        .attribute(BootstrapUiViewElementAttributes.CONTROL_ADAPTER_TYPE, "datetime")
                                                        .value(customDateRangeIsSelected ? datepickerOptionValue.getDateTo().getLocaleDateTime()
                                                                : null).build()
                                        )
                                )
                );
    }

    private static boolean isCustomDateRangeIsSelected(DateRange datepickerOptionValue) {
        if (datepickerOptionValue != null) {
            return Objects.equals("dateRange", datepickerOptionValue.getType());
        }
        return false;
    }

    private static boolean isSelected(DateRange datepickerOptionValue, String type) {
        if (datepickerOptionValue == null) {
            return false;
        }
        return Objects.equals(type, datepickerOptionValue.getType());
    }

}
