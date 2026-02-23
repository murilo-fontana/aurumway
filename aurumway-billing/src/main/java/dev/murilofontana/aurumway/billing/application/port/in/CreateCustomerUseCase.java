package dev.murilofontana.aurumway.billing.application.port.in;

public interface CreateCustomerUseCase {

    CreateCustomerResult execute(String name, String email, String taxId);

    record CreateCustomerResult(String customerId, String name, String email) {}
}
