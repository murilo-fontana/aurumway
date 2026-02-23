package dev.murilofontana.aurumway.billing.application.port.in;

import java.time.Instant;

public interface GetCustomerUseCase {

    GetCustomerResult execute(String customerId);

    record GetCustomerResult(String customerId, String name, String email, String taxId, Instant createdAt) {}
}
