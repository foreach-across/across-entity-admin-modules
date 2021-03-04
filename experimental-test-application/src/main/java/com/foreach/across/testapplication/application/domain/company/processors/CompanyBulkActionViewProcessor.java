package com.foreach.across.testapplication.application.domain.company.processors;

import com.foreach.across.modules.entity.views.processors.ExtensionViewProcessorAdapter;
import com.foreach.across.modules.entity.views.request.EntityViewCommand;
import com.foreach.across.modules.entity.views.request.EntityViewRequest;
import lombok.*;
import org.springframework.web.bind.WebDataBinder;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class CompanyBulkActionViewProcessor extends ExtensionViewProcessorAdapter<CompanyBulkActionViewProcessor.BulkActionsHolder> {

    @Override
    protected BulkActionsHolder createExtension(EntityViewRequest entityViewRequest, EntityViewCommand command, WebDataBinder dataBinder) {
        return new BulkActionsHolder();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class BulkActionsHolder {

        private Set<String> selectedItems = new HashSet<>();
    }
}
