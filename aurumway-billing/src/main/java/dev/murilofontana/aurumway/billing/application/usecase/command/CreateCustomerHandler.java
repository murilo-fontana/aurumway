package dev.murilofontana.aurumway.billing.application.usecase.command;

import dev.murilofontana.aurumway.billing.application.port.in.CreateCustomerUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.CustomerRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.model.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateCustomerHandler implements CreateCustomerUseCase {

    private final CustomerRepositoryPort repository;

    public CreateCustomerHandler(CustomerRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public CreateCustomerResult execute(String name, String email, String taxId) {
        var customer = Customer.create(name, email, taxId);
        var saved = repository.save(customer);
        return new CreateCustomerResult(saved.id().value(), saved.name(), saved.email());
    }
}
