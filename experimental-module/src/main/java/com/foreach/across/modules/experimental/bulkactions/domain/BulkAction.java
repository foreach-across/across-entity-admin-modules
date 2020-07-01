package com.foreach.across.modules.experimental.bulkactions.domain;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkAction {
    @NonNull
    private String action;

    @NonNull
    private String id;

    public static BulkAction of(String action, String id) {
        return new BulkAction(action, id);
    }
}
