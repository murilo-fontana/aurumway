package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record StatementId(String value) {

    public static StatementId newId() {
        return new StatementId(UUID.randomUUID().toString());
    }
}
