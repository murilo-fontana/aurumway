package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record JournalLineId(String value) {

    public static JournalLineId newId() {
        return new JournalLineId(UUID.randomUUID().toString());
    }
}
