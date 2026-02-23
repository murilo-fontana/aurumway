package dev.murilofontana.aurumway.contracts.domain.valueobject;

import java.util.UUID;

public record ContractItemId(String value) {

    public static ContractItemId newId() {
        return new ContractItemId(UUID.randomUUID().toString());
    }
}
