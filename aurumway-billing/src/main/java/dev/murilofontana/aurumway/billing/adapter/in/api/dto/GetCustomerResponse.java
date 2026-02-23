package dev.murilofontana.aurumway.billing.adapter.in.api.dto;

import java.time.Instant;

public record GetCustomerResponse(
        String customerId, String name, String email, String taxId, Instant createdAt
) {}
