package com.foreach.across.modules.experimental.webutility.support;

import com.foreach.across.modules.bootstrapui.elements.LinkViewElement;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.registry.EntityAssociation;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinks;
import com.foreach.across.modules.entity.web.links.SingleEntityViewLinkBuilder;
import com.foreach.across.modules.spring.security.actions.AllowableAction;
import com.foreach.across.modules.web.ui.IteratorViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Function;

import static com.foreach.across.modules.bootstrapui.ui.factories.BootstrapViewElements.bootstrap;

/**
 * A helper class to convert a property in a {@link SortableTableBuilder} to an entity link.
 * Used by the {@link com.foreach.across.modules.experimental.webutility.viewprocessor.TablePropertyLinkViewProcessor}
 */
@RequiredArgsConstructor
public class TableLinker {

    private final EntityViewLinks entityViewLinks;

    public void createLinkOnProperty(SortableTableBuilder sortableTableBuilder, String property, Function<Object, String> targetLink) {
        sortableTableBuilder.valueRowProcessor((ctx, row) ->
                row.getChildren().stream()
                        .filter(td -> StringUtils.equalsIgnoreCase(td.getName(), property))
                        .filter(TableViewElement.Cell.class::isInstance)
                        .map(TableViewElement.Cell.class::cast)
                        .findFirst()
                        .ifPresent(propertyCell -> addLink(getItem((IteratorViewElementBuilderContext) ctx), propertyCell, targetLink)));
    }

    public Object getItem(IteratorViewElementBuilderContext ctx) {
        return ctx.getItem();
    }

    private void addLink(Object rowEntity, TableViewElement.Cell propertyCell, Function<Object, String> targetLink) {
        if (propertyCell.getChildren().size() == 1 && rowEntity != null) {
            LinkViewElement link = bootstrap.builders.link().url(targetLink.apply(rowEntity)).build();
            ContainerViewElementUtils.move(propertyCell, propertyCell.getChildren().iterator().next(), link);
            propertyCell.addChild(link);
        }
    }

    /**
     * When no targetLink is specified this is the default that is used to determine the target link
     */
    public Function<Object, String> buildTargetLink(EntityViewRequest entityViewRequest, Function<EntityViewRequest, LinkDestination> providedLinkDestination) {
        return (rowEntity -> {
            TableLinker.LinkDestination linkDestination = providedLinkDestination != null ? providedLinkDestination.apply(
                    entityViewRequest) : entityViewRequest.getEntityViewContext().getAllowableActions().contains(
                    AllowableAction.UPDATE) ? TableLinker.LinkDestination.UPDATE : TableLinker.LinkDestination.DETAIL;
            EntityAssociation entityAssociation =
                    entityViewRequest.getEntityViewContext().isForAssociation() ? entityViewRequest.getEntityViewContext().getEntityAssociation() : null;
            EntityViewLinkBuilder linkBuilder = null;

            if (entityAssociation != null && EntityAssociation.Type.EMBEDDED.equals(entityAssociation.getAssociationType())) {
                linkBuilder = entityViewRequest.getEntityViewContext().getLinkBuilder();
            }

            SingleEntityViewLinkBuilder.ForEntityConfiguration forEntityConfiguration = entityViewLinks.linkTo(rowEntity);
            String currentUrl = entityViewLinks.linkTo(rowEntity.getClass()).toUriString();
            String url =
                    linkDestination == TableLinker.LinkDestination.DETAIL ? forEntityConfiguration.withFromUrl(currentUrl).toUriString() : forEntityConfiguration
                            .updateView().withFromUrl(currentUrl).toUriString();

            if (linkBuilder != null) {
                currentUrl = linkBuilder.toUriString();
                url = linkDestination == TableLinker.LinkDestination.DETAIL ? linkBuilder.withFromUrl(currentUrl).toUriString() :
                        linkBuilder.forInstance(rowEntity).updateView().withFromUrl(currentUrl).toUriString();
            }

            return url;
        });
    }

    public enum LinkDestination {
        DETAIL,
        UPDATE
    }
}