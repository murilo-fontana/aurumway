package dev.murilofontana.aurumway.billing.domain.model;

import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;

import java.time.Instant;
import java.util.Objects;

public final class Customer {

    private final CustomerId id;
    private final String name;
    private final String email;
    private final String taxId;
    private final Instant createdAt;

    private Customer(CustomerId id, String name, String email, String taxId, Instant createdAt) {
        this.id = Objects.requireNonNull(id, "id is required");
        this.name = Objects.requireNonNull(name, "name is required");
        this.email = Objects.requireNonNull(email, "email is required");
        this.taxId = taxId;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public static Customer create(String name, String email, String taxId) {
        return new Customer(CustomerId.newId(), name, email, taxId, Instant.now());
    }

    public static Customer rehydrate(CustomerId id, String name, String email, String taxId, Instant createdAt) {
        return new Customer(id, name, email, taxId, createdAt);
    }

    public CustomerId id() { return id; }
    public String name() { return name; }
    public String email() { return email; }
    public String taxId() { return taxId; }
    public Instant createdAt() { return createdAt; }
}
