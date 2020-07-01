package com.foreach.across.modules.experimental.bulkactions.domain;

import com.foreach.across.modules.experimental.bulkactions.support.BulkIdentifier;
import lombok.*;

import java.util.Collections;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class SimpleBulkIdentifier implements BulkIdentifier {
    private final String id;
    private final Map<String, Object> attributes;

    public static SimpleBulkIdentifier of(@NonNull String id) {
        return new SimpleBulkIdentifier(id, Collections.emptyMap());
    }

    public static SimpleBulkIdentifier of(@NonNull String id, Map<String, Object> attributes) {
        return new SimpleBulkIdentifier(id, attributes);
    }
}
