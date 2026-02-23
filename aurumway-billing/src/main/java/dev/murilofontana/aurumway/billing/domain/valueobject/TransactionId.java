package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record TransactionId(String value) {

    public static TransactionId newId() {
        return new TransactionId(UUID.randomUUID().toString());
    }
}
