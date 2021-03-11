package com.foreach.across.modules.experimental.webutility.domain;

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.webutility.viewprocessor.SortableTableBuilderAjaxLoadingViewProcessor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Main entrypoint to configure ajax related settings an a listVIew
 *
 * Example usage:
 *        lvb -> lvb.listView(EntityViewAjax.ajaxSettings.enableAjaxPagination()
 *                  .ajaxUrlProvider(((linkBuilder, builderContext) -> linkBuilder.listView().withQueryParam("custom", "true").toUriString())))
 *
 * The ajaxUrlProvider is not required and the default value is the listView of the requested entity.
 */
public class EntityViewAjax implements Consumer<EntityListViewFactoryBuilder> {
    public static EntityViewAjax ajaxSettings = new EntityViewAjax();

    private boolean enableAjaxPagination = false;
    private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> ajaxUrlProvider = new BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String>() {
        @Override
        public String apply(EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext) {
            return linkBuilder.listView().toUriString();
        }
    };

    /**
     * Enable ajax pagination on a view
     */
    public EntityViewAjax enableAjaxPagination() {
        enableAjaxPagination = true;
        return this;
    }

    /**
     * Enable or disable ajax pagination on a view
     */
    public EntityViewAjax enableAjaxPagination(Boolean enableAjaxPagination) {
        this.enableAjaxPagination = enableAjaxPagination;
        return this;
    }

    /**
     * Provide your own ajax url provider
     * @param ajaxUrl BiFunction
     */
    public EntityViewAjax ajaxUrlProvider(BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> ajaxUrl) {
        this.ajaxUrlProvider = ajaxUrl;
        return this;
    }

    @Override
    public void accept(EntityListViewFactoryBuilder lvb) {
        lvb.viewProcessor(vp -> vp.createBean(SortableTableBuilderAjaxLoadingViewProcessor.class)
                .configure(c -> c.enableAjaxPagination(enableAjaxPagination)
                        .ajaxUrlProvider(ajaxUrlProvider)
                )
        );
    }
}
