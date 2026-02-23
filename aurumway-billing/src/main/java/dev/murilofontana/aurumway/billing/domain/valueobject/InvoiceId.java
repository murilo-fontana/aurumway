package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record InvoiceId(String value) {

    public static InvoiceId newId() {
        return new InvoiceId(UUID.randomUUID().toString());
    }
}
