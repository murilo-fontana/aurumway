package dev.murilofontana.aurumway.billing.application.usecase.query;

import dev.murilofontana.aurumway.billing.application.port.in.GetCustomerUseCase;
import dev.murilofontana.aurumway.billing.application.port.out.CustomerRepositoryPort;
import dev.murilofontana.aurumway.billing.domain.valueobject.CustomerId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetCustomerHandler implements GetCustomerUseCase {

    private final CustomerRepositoryPort repository;

    public GetCustomerHandler(CustomerRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public GetCustomerResult execute(String customerId) {
        var customer = repository.findById(new CustomerId(customerId))
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        return new GetCustomerResult(
                customer.id().value(), customer.name(), customer.email(),
                customer.taxId(), customer.createdAt()
        );
    }
}
