package com.foreach.across.modules.experimental.webutility.domain;

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.entity.web.links.EntityViewLinkBuilder;
import com.foreach.across.modules.experimental.webutility.viewprocessor.SortableTableBuilderAjaxLoadingViewProcessor;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;

import java.util.function.BiFunction;
import java.util.function.Consumer;

public class EntityViewAjax implements Consumer<EntityListViewFactoryBuilder> {
    public static EntityViewAjax ajaxSettings = new EntityViewAjax();

    private boolean enableAjaxPagination = false;
    private BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String> ajaxUrlProvider = new BiFunction<EntityViewLinkBuilder, ViewElementBuilderContext, String>() {
        @Override
        public String apply(EntityViewLinkBuilder linkBuilder, ViewElementBuilderContext builderContext) {
            return linkBuilder.listView().toUriString();
        }
    };

    public EntityViewAjax enableAjaxPagination() {
        enableAjaxPagination = true;
        return this;
    }

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
