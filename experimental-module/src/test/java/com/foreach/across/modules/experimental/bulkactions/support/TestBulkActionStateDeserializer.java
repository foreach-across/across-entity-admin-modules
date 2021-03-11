package com.foreach.across.modules.experimental.bulkactions.support;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TestBulkActionStateDeserializer {
    @Test
    void testDecodingFails() {
        Set<Long> bulkActionState = BulkActionStateDeserializer.parseBulkActionState("WyItNCJd", Long.class);

        assertThat(bulkActionState.size()).isEqualTo(1);
        assertThat(bulkActionState.toArray()[0]).isEqualTo(-4L);
    }
}
