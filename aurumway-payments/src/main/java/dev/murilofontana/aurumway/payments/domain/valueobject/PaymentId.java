package dev.murilofontana.aurumway.payments.domain.valueobject;

import java.util.UUID;

public record PaymentId(String value) {
    public static PaymentId newId() {
        return new PaymentId(UUID.randomUUID().toString());
    }
}
