package dev.murilofontana.aurumway.billing.domain.valueobject;

import java.util.UUID;

public record InvoiceLineId(String value) {

    public static InvoiceLineId newId() {
        return new InvoiceLineId(UUID.randomUUID().toString());
    }
}
