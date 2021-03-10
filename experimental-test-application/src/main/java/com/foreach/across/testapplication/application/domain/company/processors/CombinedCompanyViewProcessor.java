package com.foreach.across.testapplication.application.domain.company.processors;

import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.entity.views.EntityView;
import com.foreach.across.modules.entity.views.processors.EntityViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import com.foreach.across.modules.experimental.webutility.resource.WebUtilityModuleWebResources;
import com.foreach.across.modules.web.resource.WebResource;
import com.foreach.across.modules.web.resource.WebResourceRegistry;
import com.foreach.across.modules.web.resource.WebResourceRule;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.elements.AbstractNodeViewElement;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import com.foreach.across.modules.web.ui.elements.HtmlViewElements;
import com.foreach.across.modules.web.ui.elements.NodeViewElement;
import com.foreach.across.modules.web.ui.elements.support.ContainerViewElementUtils;

import static com.foreach.across.modules.web.resource.WebResource.JAVASCRIPT_PAGE_END;
import static com.foreach.across.modules.web.ui.elements.HtmlViewElement.Functions.css;

public class CombinedCompanyViewProcessor extends EntityViewProcessorAdapter {

    @Override
    protected void registerWebResources(EntityViewRequest entityViewRequest, EntityView entityView, WebResourceRegistry webResourceRegistry) {
        webResourceRegistry.apply(
                WebResourceRule.add(
                        WebResource.javascript("@static:experimental/web/company-combined-view.js"))
                        .withKey("modal-loader-js")
                        .after(WebUtilityModuleWebResources.NAME)
                        .toBucket(JAVASCRIPT_PAGE_END)
        );
    }

    @Override
    protected void postRender(EntityViewRequest entityViewRequest, EntityView entityView, ContainerViewElement container, ViewElementBuilderContext builderContext) {
        container.addChild(HtmlViewElements.html.builders.div(css("js-lv2")).name("lv2").build(builderContext));

        ContainerViewElementUtils.find(container, "itemsTable-table", TableViewElement.class).ifPresent(companyTable -> {
            companyTable.setAttribute("data-tbl", "company-table");
        });

        ContainerViewElementUtils.find(container, "itemsTable-pager", NodeViewElement.class).ifPresent(pager -> {
            ContainerViewElementUtils.findAll(container, ve -> ve instanceof AbstractNodeViewElement && ((AbstractNodeViewElement) ve).hasAttribute("data-tbl"))
                    .forEach(link -> ((AbstractNodeViewElement) link).setAttribute("data-tbl", "company-table"));
        });
    }
}
