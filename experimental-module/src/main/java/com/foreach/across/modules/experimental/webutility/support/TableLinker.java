package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * A helper class to convert a property in a {@link SortableTableBuilder} to an entity link.
 * Used by the {@link com.foreach.across.modules.experimental.webutility.viewprocessor.TablePropertyLinkViewProcessor}
 */
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
                        .ifPresent(propertyCell -> addLink(getItem((IteratorViewElementBuilderContext) ctx), propertyCell, linkDestination, null)));
    }

    public void createLinkOnProperty(SortableTableBuilder sortableTableBuilder, String property, LinkDestination linkDestination, EntityViewLinkBuilder linkBuilder) {
        sortableTableBuilder.valueRowProcessor((ctx, row) ->
                row.getChildren().stream()
                        .filter(td -> StringUtils.equalsIgnoreCase(td.getName(), property))
                        .filter(TableViewElement.Cell.class::isInstance)
                        .map(TableViewElement.Cell.class::cast)
                        .findFirst()
                        .ifPresent(propertyCell -> addLink(getItem((IteratorViewElementBuilderContext) ctx), propertyCell, linkDestination, linkBuilder)));
    }

    public Object getItem(IteratorViewElementBuilderContext ctx) {
        return ctx.getItem();
    }

    private void addLink(Object rowEntity, TableViewElement.Cell propertyCell, LinkDestination linkDestination, EntityViewLinkBuilder linkBuilder) {
        if (propertyCell.getChildren().size() == 1 && rowEntity != null) {
            SingleEntityViewLinkBuilder.ForEntityConfiguration forEntityConfiguration = entityViewLinks.linkTo(rowEntity);

            String currentUrl = entityViewLinks.linkTo(rowEntity.getClass()).toUriString();
            String url = linkDestination == LinkDestination.DETAIL ? forEntityConfiguration.withFromUrl(currentUrl).toUriString() : forEntityConfiguration.updateView().withFromUrl(currentUrl).toUriString();

            if (linkBuilder != null) {
                currentUrl = linkBuilder.toUriString();
                url = linkDestination == LinkDestination.DETAIL ? linkBuilder.withFromUrl(currentUrl).toUriString() : linkBuilder.forInstance(rowEntity).updateView().withFromUrl(currentUrl).toUriString();
            }

            LinkViewElement link = bootstrap.builders.link().url(url).build();
            ContainerViewElementUtils.move(propertyCell, propertyCell.getChildren().iterator().next(), link);
            propertyCell.addChild(link);
        }
    }

    public enum LinkDestination {
        DETAIL,
        UPDATE
    }
}