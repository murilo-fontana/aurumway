package dev.murilofontana.aurumway.contracts.domain.valueobject;

import java.util.UUID;

public record ContractId(String value) {

    public static ContractId newId() {
        return new ContractId(UUID.randomUUID().toString());
    }
}
