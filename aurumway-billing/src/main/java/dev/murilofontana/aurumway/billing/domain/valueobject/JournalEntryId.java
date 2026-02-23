package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record JournalEntryId(String value) {

    public static JournalEntryId newId() {
        return new JournalEntryId(UUID.randomUUID().toString());
    }
}
