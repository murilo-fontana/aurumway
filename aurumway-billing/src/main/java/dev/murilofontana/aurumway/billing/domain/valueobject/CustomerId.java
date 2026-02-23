package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record CustomerId(String value) {

    public static CustomerId newId() {
        return new CustomerId(UUID.randomUUID().toString());
    }
}
