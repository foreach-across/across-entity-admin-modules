package com.foreach.across.modules.experimental.bulkactions.viewprocessors;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.registry.EntityConfiguration;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.processors.SortableTableRenderingViewProcessor;
import com.foreach.across.modules.entity.views.processors.support.ViewElementBuilderMap;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkAction;
import com.foreach.across.modules.experimental.bulkactions.domain.BulkActionSet;
import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifier;
import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifierBuilder;
import com.foreach.across.modules.hibernate.business.IdBasedEntity;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilder;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.builder.VoidNodeViewElementBuilder;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

import java.util.ArrayList;
import java.util.List;

import static com.foreach.across.modules.bootstrapui.styles.BootstrapStyles.css;
import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElements.html;

public class SortableTableBulkActionProcessor extends EntityViewProcessorAdapter {
    public static final String BULK_ACTION_FORM_NAME = "bulkActionForm";

    @Override
    protected void registerWebResources(EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry) {
        webResourceRegistry.apply(
                WebResourceRule.add(WebResource.javascript("@static:/experimental/js/bulk-actions.js")).toBucket(WebResource.JAVASCRIPT_PAGE_END)
        );
    }

    @Override
    protected void createViewElementBuilders(EntityViewRequest entityViewRequest, EntityView entityView, ViewElementBuilderMap builderMap) {
        BulkIdentifierBuilder bulkIdentifierBuilder = entityViewRequest.getEntityViewContext()
                .getEntityConfiguration()
                .getAttribute(BulkIdentifierBuilder.class);
        SortableTableBuilder sortableTableBuilder = builderMap.get(SortableTableRenderingViewProcessor.TABLE_BUILDER, SortableTableBuilder.class);
        if (sortableTableBuilder != null && bulkIdentifierBuilder != null) {

            sortableTableBuilder.headerRowProcessor((viewElementBuilderContext, row) -> {
                row.addCssClass("position-relative");
                row.addFirstChild(bootstrap.builders.table.cell()
                        .css("position-relative")
                        .css("check-mark-column")
                        .add(html.builders.input()
                                .attribute("type", "checkbox")
                                .attribute("data-bootstrapui-adapter-type", "checkbox")
                                .css("check-mark", "js-bulk-select-all"))
                        .build(viewElementBuilderContext));
            });
            sortableTableBuilder.valueRowProcessor((context, row) -> {
                IdBasedEntity idBasedEntity = EntityViewElementUtils.currentEntity(context, IdBasedEntity.class);
                if (idBasedEntity != null) {
                    BulkIdentifier bulkIdentifier = bulkIdentifierBuilder.getBulkIdentifier(context);
                    VoidNodeViewElementBuilder checkbox = html.builders.input()
                            .attribute("type", "checkbox")
                            .attribute("data-bootstrapui-adapter-type", "checkbox")
                            .attribute("name", "selectedIds")
                            .attribute("value", bulkIdentifier.getId())
                            .css("check-mark", "js-bulk-select");
                    bulkIdentifier.getAttributes().forEach(checkbox::attribute);
                    row.addFirstChild(bootstrap.builders.table.cell()
                            .css("check-mark-column")
                            .css("position-relative")
                            .add(checkbox)
                            .build(context));
                }
            });
        }
    }

    @Override
    protected void postRender(EntityViewRequest entityViewRequest,
                              EntityView entityView,
                              ContainerViewElement container,
                              ViewElementBuilderContext builderContext) {
        EntityConfiguration entityConfiguration = entityViewRequest.getEntityViewContext()
                .getEntityConfiguration();

        ContainerViewElementUtils.find(container, "itemsTable-table", TableViewElement.class)
                .ifPresent(tableViewElement -> tableViewElement.setAttribute("data-bootstrapui-adapter-type", "container"));
        ContainerViewElementUtils.find(container, "itemsTable")
                .ifPresent(table -> ContainerViewElementUtils.findParent(container, table).ifPresent(tableParent -> {
                    tableParent.clearChildren();
                    tableParent.addChild(html.builders.form()
                            .htmlId(BULK_ACTION_FORM_NAME)
                            .data("em-entity", entityConfiguration.getName())
                            .add(table)
                            .build(builderContext));

                }));

        BulkActionSet bulkActionSet = entityConfiguration
                .getAttribute(BulkActionSet.class);

        if (bulkActionSet != null) {
            List<ViewElementBuilder> actionViewElements = new ArrayList<>();
            for (BulkAction bulkAction : bulkActionSet.getBulkActions()) {
                actionViewElements.add(
                        bootstrap.builders.button(css.button.light, css.text.muted, css.margin.right.s2)
                                .htmlId(bulkAction.getId())
                                .text(builderContext.getMessage("bulk.action." + bulkAction.getAction()))
                                .attribute("data-toggle", "tooltip")
                                .attribute("data-placement", "top")
                                .attribute("title", "")
                                .attribute("data-bulk-action", bulkAction.getAction())
                                .attribute("data-disabled-text",
                                        builderContext.getMessage("bulk.action." + bulkAction.getAction() + "[disabled]")
                                )
                );
            }

            container.addChild(new NodeViewElement("template")
                    .setHtmlId("bulk-action-set-template")
                    .addChild(html.builders.div(css.grid.row, css.of("h-100"), css.align.items.center, css.padding.horizontal.s3)
                            .add(html.builders.div(css.grid.column4)
                                    .add(html.builders.span(css.padding.right.none).css("selected-count"))
                                    .add(html.builders.span(css.padding.right.none)
                                            .add(html.builders.text(" "))
                                    )
                                    .add(html.builders.span(css.padding.none)
                                            .add(html.builders
                                                    .text(builderContext.getMessage(
                                                            "bulk.items.selected")
                                                    ))
                                    )
                            )
                            .add(html.builders.div(css.grid.column.offset4, css.grid.column4, css.display.flex,
                                    css.justifyContent.end)
                                    .addAll(actionViewElements))
                            .build(builderContext)
                    ));
        }
    }
}
