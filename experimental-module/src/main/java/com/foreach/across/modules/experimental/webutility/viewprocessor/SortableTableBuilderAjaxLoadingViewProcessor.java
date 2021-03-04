package com.foreach.across.modules.experimental.webutility.viewprocessor;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.entity.web.EntityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;

@Accessors(fluent = true)
@Setter
@Getter
public class SortableTableBuilderAjaxLoadingViewProcessor extends EntityViewProcessorAdapter {
    private boolean enableAjaxPagination = false;

    @Override
    protected void registerWebResources(EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry) {
        webResourceRegistry.apply(
                WebResourceRule.add(
                        WebResource.javascript("@static:/experimental/web/experimental-module.js"))
                        .withKey("experimental-web-utilities")
                        .after(EntityModuleWebResources.NAME)
                        .toBucket(JAVASCRIPT_PAGE_END)
        );
    }

    @Override
    protected void postRender(EntityViewRequest entityViewRequest, EntityView entityView, ContainerViewElement container, ViewElementBuilderContext builderContext) {
        ContainerViewElementUtils.find(container, "itemsTable-table", TableViewElement.class)
                .ifPresent(table -> table.setAttribute("data-ajax-pagination", enableAjaxPagination));
    }
}
