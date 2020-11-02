package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

@Component
@RequiredArgsConstructor
public class TableLinker {

    private final EntityViewLinks entityViewLinks;

    public void createLinkOnProperty(SortableTableBuilder sortableTableBuilder, String property, LinkDestination linkDestination) {
        sortableTableBuilder.valueRowProcessor((ctx, row) ->
                row.getChildren().stream()
                        .filter(td -> StringUtils.equalsIgnoreCase(td.getName(), property))
                        .filter(TableViewElement.Cell.class::isInstance)
                        .map(TableViewElement.Cell.class::cast)
                        .findFirst()
                        .ifPresent(propertyCell -> addLink(getItem((IteratorViewElementBuilderContext) ctx), propertyCell, linkDestination)));
    }

    public Object getItem(IteratorViewElementBuilderContext ctx) {
        return ctx.getItem();
    }

    private void addLink(Object rowEntity, TableViewElement.Cell propertyCell, LinkDestination linkDestination) {
        if (propertyCell.getChildren().size() == 1 && rowEntity != null) {
            SingleEntityViewLinkBuilder.ForEntityConfiguration forEntityConfiguration = entityViewLinks.linkTo(rowEntity);
            LinkViewElement link = bootstrap.builders.link().url(linkDestination == LinkDestination.DETAIL ? forEntityConfiguration.toUriString() : forEntityConfiguration.updateView().toUriString()).build();
            ContainerViewElementUtils.move(propertyCell, propertyCell.getChildren().iterator().next(), link);
            propertyCell.addChild(link);
        }
    }

    public enum LinkDestination {
        DETAIL,
        UPDATE
    }
}