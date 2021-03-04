package com.foreach.across.modules.experimental.webutility.domain;

import com.foreach.across.modules.entity.config.builders.EntityListViewFactoryBuilder;
import com.foreach.across.modules.experimental.webutility.viewprocessor.SortableTableBuilderAjaxLoadingViewProcessor;

import java.util.function.Consumer;

public class EntityViewAjax implements Consumer<EntityListViewFactoryBuilder> {
    public static EntityViewAjax ajaxSettings = new EntityViewAjax();

    private boolean enableAjaxPagination = false;

    public EntityViewAjax enableAjaxPagination() {
        enableAjaxPagination = true;
        return this;
    }

    @Override
    public void accept(EntityListViewFactoryBuilder lvb) {
        lvb.viewProcessor(vp -> vp.createBean(SortableTableBuilderAjaxLoadingViewProcessor.class)
                .configure(c -> c.enableAjaxPagination(enableAjaxPagination))
        );
    }
}
