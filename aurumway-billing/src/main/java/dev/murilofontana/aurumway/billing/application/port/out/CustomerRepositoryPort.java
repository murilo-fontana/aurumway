package dev.murilofontana.aurumway.billing.application.port.out;

import dev.murilofontana.aurumway.billing.domain.model.Customer;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;

import java.util.Optional;

public interface CustomerRepositoryPort {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId id);

    boolean existsById(CustomerId id);
}
